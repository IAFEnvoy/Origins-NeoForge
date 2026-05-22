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
     * Transforms a vector from local space to global space. The base inferred from its forward vector is orthogonal.
     *
     * @param baseForwardVector the base's forward (Z) vector
     * @param vector            the vector to transform
     * @param baseYaw           the yaw of the base (used in case the forward vector lacks information to infer the base)
     * @param normalizeBase     whether to normalize the base, if so all three vectors of the base will be normalized, otherwise they'll all have the length of the input forward vector
     * @author Alluysl
     */
    public static void transformVectorToBase(Vec3 baseForwardVector, Vector3f vector, float baseYaw, boolean normalizeBase) {
        double baseScaleD = baseForwardVector.length();
        if (baseScaleD <= 0.007D) vector.zero(); // tweak value if too high, may be a bit too aggressive
        else {
            float baseScale = (float) baseScaleD;
            Vec3 normalizedBase = baseForwardVector.normalize(); // the function called below assumes the base is normalized to simplify calculations (Y calculated as cross product of Z and X guaranteed to be normalized if X and Z are normalized)
            Matrix3f transformMatrix = getBaseTransformMatrixFromNormalizedDirectionVector(normalizedBase, baseYaw);
            if (!normalizeBase) // if the base wasn't supposed to get normalized, re-scale to compensate for the prior normalization
                transformMatrix.scale(baseScale, baseScale, baseScale);
            vector.mulTranspose(transformMatrix); // matrix multiplication, vector is now in the new base :D
        }
    }

    /**
     * Provides the matrix transform from the base specified by the input vector to the cardinal base.
     * The input vector is the Z (forward) axis of the base, while the calculated X axis is orthogonal to the "left" of Z. Y is such as Z is the cross product of X and Y.
     * If the input vector were to be vertical, the yaw is used to infer the X and Y vectors of the base.
     * After determining the vectors of the base it builds the transformation matrix by laying each into a column (if you consider vectors as being in columns for multiplications).
     *
     * @param vector the input vector the base is inferred from (forward vector of local space)
     * @param yaw    the yaw of local space
     * @return the transformation matrix from local to global space
     * @author Alluysl
     */
    private static Matrix3f getBaseTransformMatrixFromNormalizedDirectionVector(Vec3 vector, float yaw) {
        double xX, xZ, // X vector
                zX = 0.0D, zY = vector.y(), zZ = 0.0D; // Z vector

        if (Math.abs(zY) != 1.0F) { // Z not vertical, can infer X from it
            // Z
            zX = vector.x();
            zZ = vector.z();
            // X (orthogonal to the projection of Z on the global XZ plane)
            xX = vector.z();
            xZ = -vector.x();
            // Normalize X
            float xFactor = (float) (1 / Math.sqrt(xX * xX + xZ * xZ));
            xX *= xFactor;
            xZ *= xFactor;
        } else {
            // If the orientation vector points straight up or straight down, use the yaw to determine the X vector (it's "on the left")
            // The pitch doesn't affect the X vector as it's a rotation around that same vector
            float trigonometricYaw = -yaw * 0.0174532925F; // pi / 180 = 0.0174532925
            xX = Mth.cos(trigonometricYaw);
            xZ = -Mth.sin(trigonometricYaw);
        }

        Matrix3f res = new Matrix3f();
        // X
        res.set(0, 0, (float) xX);
        res.set(1, 0, 0.0F); // X vector is horizontal, set its Y components (a10 (mathematically a21)) to 0
        res.set(2, 0, (float) xZ);
        // Y (cross product of Z and X, simplified by the fact that X has a Y components of 0)
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
