package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data._common.helper.AdvancementHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.codec.WildcardCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RevokeAdvancementAction(ResourceLocation advancement, List<String> criterion,
                                      Mode selection) implements EntityAction, AdvancementHelper {
    public static final MapCodec<RevokeAdvancementAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("advancement").forGetter(RevokeAdvancementAction::advancement),
            CombinedCodecs.STRING.optionalFieldOf("criterion", List.of()).forGetter(RevokeAdvancementAction::criterion),
            Mode.CODEC.optionalFieldOf("selection", Mode.ONLY).forGetter(RevokeAdvancementAction::selection)
    ).apply(i, RevokeAdvancementAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (source instanceof ServerPlayer player) {
            ServerAdvancementManager manager = player.server.getAdvancements();
            PlayerAdvancements playerAdvancements = player.getAdvancements();
            for (AdvancementHolder holder : this.getAdvancements(manager)) {
                if (this.criterion.isEmpty()) {
                    AdvancementProgress progress = playerAdvancements.getOrStartProgress(holder);
                    if (progress.hasProgress())
                        for (String c : progress.getCompletedCriteria())
                            playerAdvancements.revoke(holder, c);
                } else for (String c : this.criterion) playerAdvancements.revoke(holder, c);
            }
        }
    }
}
