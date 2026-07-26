package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.mixin.accessor.LivingEntityAttackStrengthAccessor;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record ModifyAttackCooldownAction(Modifier modifier) implements EntityAction {
    public static final MapCodec<ModifyAttackCooldownAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Modifier.CODEC.fieldOf("modifier").forGetter(ModifyAttackCooldownAction::modifier)
    ).apply(instance, ModifyAttackCooldownAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity entity) {
        if (!(entity instanceof Player player)) {
            return;
        }
        LivingEntityAttackStrengthAccessor accessor = (LivingEntityAttackStrengthAccessor) player;
        int current = accessor.origins$getAttackStrengthTicker();
        int next = OriginDataHolder.optional(player)
                .map(holder -> Modifier.applyModifiers(holder, List.of(this.modifier), current))
                .orElse(current);
        accessor.origins$setAttackStrengthTicker(Math.max(0, next));
    }
}
