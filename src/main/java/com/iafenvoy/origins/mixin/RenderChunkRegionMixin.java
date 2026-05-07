package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data.power.builtin.modify.ModifyFluidRenderPower;
import com.iafenvoy.origins.render.LevelRenderHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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

@OnlyIn(Dist.CLIENT)
@Mixin(RenderChunkRegion.class)
public abstract class RenderChunkRegionMixin {
    @Shadow
    public abstract BlockState getBlockState(BlockPos pPos);

    @ModifyReturnValue(method = "getBlockState", at = @At("RETURN"))
    private BlockState modifyBlockRender(BlockState original, BlockPos pos) {
        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.player != null)
            return LevelRenderHelper.streamBlockRenderPowers()
                    .filter(x -> x.getBlockCondition().test(client.level, pos))
                    .map(x -> x.getBlock().defaultBlockState())
                    .findFirst().orElse(original);
        return original;
    }

    @ModifyReturnValue(method = "getFluidState", at = @At("RETURN"))
    private FluidState modifyFluidRender(FluidState original, BlockPos pos) {
        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.player != null)
            return LevelRenderHelper.streamFluidRenderPowers()
                    .filter(x -> x.getBlockCondition().test(client.level, pos) && x.getFluidCondition().test(original))
                    .map(ModifyFluidRenderPower::getFluid)
                    .findFirst().orElse(original);
        return original;
    }
}
