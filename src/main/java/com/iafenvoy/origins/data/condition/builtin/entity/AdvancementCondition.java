package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record AdvancementCondition(ResourceLocation advancement) implements EntityCondition {
    public static final MapCodec<AdvancementCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("advancement").forGetter(AdvancementCondition::advancement)
    ).apply(i, AdvancementCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        if (!(entity instanceof Player player)) return false;
        if (player instanceof ServerPlayer serverPlayer) {
            AdvancementHolder advancementEntry = serverPlayer.server.getAdvancements().get(this.advancement);
            if (advancementEntry == null) {
                Origins.LOGGER.warn("Advancement \"{}\" did not exist, but was referenced in an \"advancement\" entity condition!", this.advancement);
                return false;
            } else return serverPlayer.getAdvancements().getOrStartProgress(advancementEntry).isDone();
        } else if (player instanceof LocalPlayer clientPlayer) {
            ClientAdvancements advancementManager = clientPlayer.connection.getAdvancements();
            AdvancementHolder advancement = advancementManager.get(this.advancement);
            if (advancement == null) {
                //  We don't want to print an error here if the advancement does not exist,
                //  because on the client-side, the advancement could just not have been received from the server
                return false;
            }
            AdvancementProgress progress = advancementManager.progress.get(advancement);
            return progress != null && progress.isDone();
        } else return false;
    }
}
