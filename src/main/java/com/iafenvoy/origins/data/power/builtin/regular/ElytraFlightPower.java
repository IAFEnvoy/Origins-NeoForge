package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

// 注意: 此能量之前依赖 Caelus API（滑翔飞行属性）来授予鞘翅式飞行。
// Caelus 目前没有 Minecraft 26.1 版本，因此不使用属性而是直接授予飞行能力：
// LivingEntityMixin#canGlide 在此能量激活时报告可以滑翔（参见 origins$powerGlide）。
// 这使客户端启动（LocalPlayer -> tryToStartFallFlying -> canGlide）和服务器端维持
// 都可以在没有鞘翅物品的情况下工作。一旦客户端渲染层移植完成，
// 下面的渲染字段将被使用；目前飞行可以工作，但不会绘制鞘翅。
public class ElytraFlightPower extends Power {
    public static final MapCodec<ElytraFlightPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("render_elytra", true).forGetter(ElytraFlightPower::shouldRenderElytra),
            Identifier.CODEC.optionalFieldOf("texture_location").forGetter(ElytraFlightPower::getTextureLocation)
    ).apply(i, ElytraFlightPower::new));
    private final boolean renderElytra;
    private final Optional<Identifier> textureLocation;

    public ElytraFlightPower(BaseSettings settings, boolean renderElytra, Optional<Identifier> textureLocation) {
        super(settings);
        this.renderElytra = renderElytra;
        this.textureLocation = textureLocation;
    }

    public boolean shouldRenderElytra() {
        return this.renderElytra;
    }

    public Optional<Identifier> getTextureLocation() {
        return this.textureLocation;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
