package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifySlipperinessPower;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IBlockStateExtension.class)
public interface IBlockStateExtensionMixin {
    @ModifyReturnValue(method = "getFriction", at = @At("RETURN"))
    default float apoli$modifyFriction(float original, @Local(argsOnly = true) LevelReader reader, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) @Nullable Entity entity) {
        if (entity != null && reader instanceof Level level)
            return OriginDataHolder.get(entity).getHelper().modify(ModifySlipperinessPower.class, p -> p.getBlockCondition().test(level, pos), original);
        return original;
    }
}
