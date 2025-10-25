package com.iafenvoy.origins.render.layer;

import com.iafenvoy.origins.event.client.ElytraTextureEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ElytraPowerLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends ElytraLayer<T, M> {
    private Optional<ResourceLocation> textureCache = Optional.empty();

    public ElytraPowerLayer(RenderLayerParent<T, M> renderer, EntityModelSet modelSet) {
        super(renderer, modelSet);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.textureCache = NeoForge.EVENT_BUS.post(new ElytraTextureEvent(livingEntity)).getTexture();
        super.render(poseStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public @NotNull ResourceLocation getElytraTexture(@NotNull ItemStack stack, @NotNull T entity) {
        return this.textureCache.orElse(super.getElytraTexture(stack, entity));
    }

    @Override
    public boolean shouldRender(@NotNull ItemStack stack, @NotNull T entity) {
        if (super.shouldRender(stack, entity)) return false;
        return this.textureCache.isPresent();
    }
}
