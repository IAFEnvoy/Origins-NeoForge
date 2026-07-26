package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/** Modifies the target-search range that matching mobs may use for this player. */
public final class ModifyTargetRangePower extends Power {
    public static final MapCodec<ModifyTargetRangePower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityCondition.optionalCodec("mob_condition").forGetter(ModifyTargetRangePower::mobCondition),
            Modifier.CODEC.fieldOf("modifier").forGetter(ModifyTargetRangePower::modifier)
    ).apply(instance, ModifyTargetRangePower::new));
    private final EntityCondition mobCondition;
    private final Modifier modifier;

    public ModifyTargetRangePower(BaseSettings settings, EntityCondition mobCondition, Modifier modifier) {
        super(settings);
        this.mobCondition = mobCondition;
        this.modifier = modifier;
    }

    public EntityCondition mobCondition() { return this.mobCondition; }
    public Modifier modifier() { return this.modifier; }

    public static double modify(Mob mob, Player player, double baseRange) {
        return OriginDataHolder.optional(player).map(holder -> {
            double range = baseRange;
            for (ModifyTargetRangePower power : PowerHelper.get(player).listActive(ModifyTargetRangePower.class,
                    candidate -> candidate.mobCondition.test(mob))) {
                range = Modifier.applyModifiers(holder, List.of(power.modifier), range);
            }
            return Math.max(0, range);
        }).orElse(baseRange);
    }

    public static boolean canTarget(Mob mob, Entity target) {
        if (!(target instanceof Player player)) return true;
        double base = mob.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE);
        return mob.distanceToSqr(player) <= Math.pow(modify(mob, player, base), 2);
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() { return CODEC; }
}
