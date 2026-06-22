package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record AdvancementCondition(Identifier advancement) implements EntityCondition {
    public static final MapCodec<AdvancementCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("advancement").forGetter(AdvancementCondition::advancement)
    ).apply(i, AdvancementCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        if (!(entity instanceof Player player)) return false;
        if (player instanceof ServerPlayer serverPlayer) {
            AdvancementHolder advancementEntry = serverPlayer.level().getServer().getAdvancements().get(this.advancement);
            if (advancementEntry == null) {
                Origins.LOGGER.warn("Advancement \"{}\" did not exist, but was referenced in an \"advancement\" entity condition!", this.advancement);
                return false;
            } else return serverPlayer.getAdvancements().getOrStartProgress(advancementEntry).isDone();
        } else if (player instanceof LocalPlayer clientPlayer) {
            ClientAdvancements advancementManager = clientPlayer.connection.getAdvancements();
            AdvancementHolder advancement = advancementManager.get(this.advancement);
            if (advancement == null) {
                //  如果进度不存在，我们不希望在此处打印错误，
                //  因为在客户端，进度可能只是还没有从服务器接收
                return false;
            }
            AdvancementProgress progress = advancementManager.progress.get(advancement);
            return progress != null && progress.isDone();
        } else return false;
    }
}
