package com.iafenvoy.origins.util.math;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.Locale;

public enum Space implements StringRepresentable {
    WORLD(false, false, false, false),
    LOCAL(true, false, false, false),
    LOCAL_HORIZONTAL(true, false, true, false),
    LOCAL_HORIZONTAL_NORMALIZED(true, false, true, true),
    VELOCITY(true, true, false, false),
    VELOCITY_NORMALIZED(true, true, false, true),
    VELOCITY_HORIZONTAL(true, true, true, false),
    VELOCITY_HORIZONTAL_NORMALIZED(true, true, true, true);
    public static final Codec<Space> CODEC = StringRepresentable.fromValues(Space::values);
    private final boolean process, velocity, horizontal, normalize;

    Space(boolean process, boolean velocity, boolean horizontal, boolean normalize) {
        this.process = process;
        this.velocity = velocity;
        this.horizontal = horizontal;
        this.normalize = normalize;
    }

    public void toGlobal(Vector3f vector, Entity entity) {
        if (this.process) {
            Vec3 vec3 = this.velocity ? entity.getDeltaMovement() : entity.getLookAngle();
            if (this.horizontal) vec3 = new Vec3(vec3.x(), 0, vec3.z());
            transformVectorToBase(vec3, vector, entity.getYRot(), this.normalize);
        }
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    /**
     * 将向量从局部空间变换到全局空间。由其前向向量推导出的基是正交的。
     *
     * @param baseForwardVector 基的前向（Z）向量
     * @param vector            要变换的向量
     * @param baseYaw           基的偏航角（当前向向量缺少信息无法推导基时使用）
     * @param normalizeBase     是否归一化基，如果是，则基的所有三个向量都将归一化；否则它们将具有输入前向向量的长度
     * @author Alluysl
     */
    public static void transformVectorToBase(Vec3 baseForwardVector, Vector3f vector, float baseYaw, boolean normalizeBase) {
        double baseScaleD = baseForwardVector.length();
        if (baseScaleD <= 0.007D) vector.zero(); // 调整值，如果太高可能有点过于激进
        else {
            float baseScale = (float) baseScaleD;
            Vec3 normalizedBase = baseForwardVector.normalize(); // 下面调用的函数假设基已归一化以简化计算（如果 X 和 Z 已归一化，则 Y 作为 Z 和 X 的叉积保证也是归一化的）
            Matrix3f transformMatrix = getBaseTransformMatrixFromNormalizedDirectionVector(normalizedBase, baseYaw);
            if (!normalizeBase) // 如果基不应该被归一化，重新缩放以补偿之前的归一化
                transformMatrix.scale(baseScale, baseScale, baseScale);
            vector.mulTranspose(transformMatrix); // 矩阵乘法，向量现在处于新基中 :D
        }
    }

    /**
     * 提供从输入向量指定的基到标准基的矩阵变换。
     * 输入向量是基的 Z（前向）轴，计算出的 X 轴与 Z 的"左侧"正交。Y 使得 Z 是 X 和 Y 的叉积。
     * 如果输入向量是垂直的，则使用偏航角来推导基的 X 和 Y 向量。
     * 确定基的各向量后，通过将每个向量放入一列来构建变换矩阵（假设在乘法中向量被视为列向量）。
     *
     * @param vector 用于推导基的输入向量（局部空间的前向向量）
     * @param yaw    局部空间的偏航角
     * @return 从局部空间到全局空间的变换矩阵
     * @author Alluysl
     */
    private static Matrix3f getBaseTransformMatrixFromNormalizedDirectionVector(Vec3 vector, float yaw) {
        double xX, xZ, // X 向量
                zX = 0.0D, zY = vector.y(), zZ = 0.0D; // Z 向量

        if (Math.abs(zY) != 1.0F) { // Z 不垂直，可以从它推导 X
            // Z
            zX = vector.x();
            zZ = vector.z();
            // X（与 Z 在全局 XZ 平面上的投影正交）
            xX = vector.z();
            xZ = -vector.x();
            // 归一化 X
            float xFactor = (float) (1 / Math.sqrt(xX * xX + xZ * xZ));
            xX *= xFactor;
            xZ *= xFactor;
        } else {
            // 如果方向向量指向正上方或正下方，使用偏航角来确定 X 向量（它在"左侧"）
            // 俯仰角不影响 X 向量，因为它是围绕该向量本身的旋转
            float trigonometricYaw = -yaw * 0.0174532925F; // pi / 180 = 0.0174532925
            xX = Mth.cos(trigonometricYaw);
            xZ = -Mth.sin(trigonometricYaw);
        }

        Matrix3f res = new Matrix3f();
        // X
        res.set(0, 0, (float) xX);
        res.set(1, 0, 0.0F); // X 向量是水平的，将其 Y 分量（a10，数学上为 a21）设为 0
        res.set(2, 0, (float) xZ);
        // Y（Z 和 X 的叉积，由于 X 的 Y 分量为 0 而简化）
        res.set(0, 1, (float) (zY * xZ));
        res.set(1, 1, (float) (zZ * xX - zX * xZ));
        res.set(2, 1, (float) (-zY * xX));
        // Z
        res.set(0, 2, (float) zX);
        res.set(1, 2, (float) zY);
        res.set(2, 2, (float) zZ);
        return res;
    }
}
