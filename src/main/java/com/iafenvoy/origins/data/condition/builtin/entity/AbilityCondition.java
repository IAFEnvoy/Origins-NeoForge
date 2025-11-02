package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Predicate;

public record AbilityCondition(PlayerAbility ability) implements EntityCondition {
    public static final MapCodec<AbilityCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            PlayerAbility.CODEC.fieldOf("ability").forGetter(AbilityCondition::ability)
    ).apply(i, AbilityCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return entity instanceof Player player && this.ability.get(player);
    }

    public enum PlayerAbility implements StringRepresentable {
        FLYING(a -> a.flying),
        INSTABUILD(a -> a.instabuild),
        INVULNERABLE(a -> a.invulnerable),
        MAY_BUILD(a -> a.mayBuild),
        MAYFLY(a -> a.mayfly);
        public static final Codec<PlayerAbility> CODEC = StringRepresentable.fromValues(PlayerAbility::values);
        private final Predicate<Abilities> getter;

        PlayerAbility(Predicate<Abilities> getter) {
            this.getter = getter;
        }

        public boolean get(Player player) {
            return this.getter.test(player.getAbilities());
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
