package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

/**
 * Assigns the holder to a vanilla-style combat group without changing its real entity type.
 * Minecraft 1.21 no longer exposes MobType, so the group is consumed by the combat hooks that
 * still have a vanilla equivalent, such as Smite and Bane of Arthropods.
 */
public final class EntityGroupPower extends Power {
    public static final MapCodec<EntityGroupPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Group.CODEC.fieldOf("group").forGetter(EntityGroupPower::group)
    ).apply(instance, EntityGroupPower::new));
    private final Group group;

    public EntityGroupPower(BaseSettings settings, Group group) {
        super(settings);
        this.group = group;
    }

    public Group group() {
        return this.group;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public enum Group {
        UNDEAD,
        ARTHROPOD,
        ILLAGER,
        AQUATIC,
        DEFAULT;

        public static final Codec<Group> CODEC = Codec.STRING.xmap(
                value -> Group.valueOf(value.toUpperCase(Locale.ROOT)),
                value -> value.name().toLowerCase(Locale.ROOT)
        );
    }
}
