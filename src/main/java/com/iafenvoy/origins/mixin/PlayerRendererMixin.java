package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data._common.ColorSettings;
import com.iafenvoy.origins.data.power.builtin.regular.ModelColorPower;
import com.iafenvoy.origins.render.layer.ElytraPowerLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void registerLayers(EntityRendererProvider.Context context, boolean useSlimModel, CallbackInfo ci) {
        this.addLayer(new ElytraPowerLayer<>(this, context.getModelSet()));
    }

    @Unique
    private static void origins$colorizeHand(ModelPart modelPart, PoseStack matrices, VertexConsumer vertices, int light, int overlay, MultiBufferSource vertexConsumers, AbstractClientPlayer player) {
        Optional<ColorSettings> color = ModelColorPower.getColor(player);
        if (color.isPresent())
            modelPart.render(matrices, vertexConsumers.getBuffer(RenderType.entityTranslucent(player.getSkin().texture())), light, overlay, color.get().getIntValue());
        else
            modelPart.render(matrices, vertices, light, overlay);
    }

    @OnlyIn(Dist.CLIENT)
    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V", ordinal = 0))
    private void makeArmTranslucent(ModelPart modelPart, PoseStack matrices, VertexConsumer vertices, int light, int overlay, PoseStack matrices2, MultiBufferSource vertexConsumers, int light2, AbstractClientPlayer player) {
        origins$colorizeHand(modelPart, matrices, vertices, light, overlay, vertexConsumers, player);
    }

    @OnlyIn(Dist.CLIENT)
    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V", ordinal = 1))
    private void makeSleeveTranslucent(ModelPart modelPart, PoseStack matrices, VertexConsumer vertices, int light, int overlay, PoseStack matrices2, MultiBufferSource vertexConsumers, int light2, AbstractClientPlayer player) {
        origins$colorizeHand(modelPart, matrices, vertices, light, overlay, vertexConsumers, player);
    }
}
