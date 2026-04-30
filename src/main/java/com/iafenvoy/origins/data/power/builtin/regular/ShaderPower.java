package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.network.payload.ReapplyShadersS2CPayload;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class ShaderPower extends Power {
    public static final MapCodec<ShaderPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ResourceLocation.CODEC.fieldOf("shader").forGetter(ShaderPower::getShader),
            Codec.BOOL.optionalFieldOf("toggleable", true).forGetter(ShaderPower::isToggleable)
    ).apply(instance, ShaderPower::new));
    private final ResourceLocation shader;
    private final boolean toggleable;

    public ShaderPower(BaseSettings settings, ResourceLocation shader, boolean toggleable) {
        super(settings);
        this.shader = shader;
        this.toggleable = toggleable;
    }

    public ResourceLocation getShader() {
        return this.shader;
    }

    public boolean isToggleable() {
        return this.toggleable;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void active(@NotNull OriginDataHolder holder) {
        invokeReapplyShaders(holder);
    }

    @Override
    public void inactive(@NotNull OriginDataHolder holder) {
        invokeReapplyShaders(holder);
    }

    private static void invokeReapplyShaders(OriginDataHolder holder) {
        if (holder.getEntity() instanceof ServerPlayer player)
            PacketDistributor.sendToPlayer(player, ReapplyShadersS2CPayload.INSTANCE);
    }
}
