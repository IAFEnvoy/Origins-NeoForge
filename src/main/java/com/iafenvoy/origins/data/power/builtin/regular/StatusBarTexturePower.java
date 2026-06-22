package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class StatusBarTexturePower extends Power implements Prioritized {
    public static final MapCodec<StatusBarTexturePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).fieldOf("texture_map").forGetter(StatusBarTexturePower::getTextureMap),
            Codec.INT.fieldOf("priority").forGetter(StatusBarTexturePower::getPriority)
    ).apply(i, StatusBarTexturePower::new));
    private final Map<Identifier, Identifier> textureMap;
    private final int priority;

    protected StatusBarTexturePower(BaseSettings settings, Map<Identifier, Identifier> textureMap, int priority) {
        super(settings);
        this.textureMap = textureMap;
        this.priority = priority;
    }

    public Map<Identifier, Identifier> getTextureMap() {
        return this.textureMap;
    }

    public static Identifier replaceTexture(@Nullable Player player, Identifier original) {
        if (player == null) return original;
        return OriginDataHolder.get(player).streamActivePowers(StatusBarTexturePower.class)
                .map(StatusBarTexturePower::getTextureMap)
                .filter(map -> map.containsKey(original))
                .map(map -> map.get(original))
                .findFirst()
                .orElse(original);
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
