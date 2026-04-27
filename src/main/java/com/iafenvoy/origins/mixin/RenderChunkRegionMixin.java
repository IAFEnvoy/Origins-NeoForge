package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data.power.builtin.modify.ModifyFluidRenderPower;
import com.iafenvoy.origins.render.LevelRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderChunkRegion.class)
public abstract class RenderChunkRegionMixin {
    @Shadow
    public abstract BlockState getBlockState(BlockPos pPos);

    @Inject(method = "getBlockState", at = @At("RETURN"), cancellable = true)
    private void modifyBlockRender(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.player != null) LevelRenderHelper.streamBlockRenderPowers()
                .filter(x -> x.getBlockCondition().test(client.level, pos))
                .map(x -> x.getBlock().defaultBlockState())
                .findFirst().ifPresent(cir::setReturnValue);
    }

    @Inject(method = "getFluidState", at = @At("RETURN"), cancellable = true)
    private void modifyFluidRender(BlockPos pos, CallbackInfoReturnable<FluidState> cir) {
        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.player != null) LevelRenderHelper.streamFluidRenderPowers()
                .filter(x -> x.getBlockCondition().test(client.level, pos) && x.getFluidCondition().test(cir.getReturnValue()))
                .map(ModifyFluidRenderPower::getFluid)
                .findFirst().ifPresent(cir::setReturnValue);
    }
}
