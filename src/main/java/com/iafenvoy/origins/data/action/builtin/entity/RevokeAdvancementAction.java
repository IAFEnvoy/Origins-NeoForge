package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.AdvancementUtil;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
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
                                      AdvancementUtil.Mode selection) implements EntityAction {
    public static final MapCodec<RevokeAdvancementAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("advancement").forGetter(RevokeAdvancementAction::advancement),
            CombinedCodecs.STRING.optionalFieldOf("criterion", List.of()).forGetter(RevokeAdvancementAction::criterion),
            AdvancementUtil.Mode.CODEC.optionalFieldOf("selection", AdvancementUtil.Mode.ONLY).forGetter(RevokeAdvancementAction::selection)
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
            AdvancementHolder base = manager.get(this.advancement);
            List<AdvancementHolder> advancements = AdvancementUtil.getAdvancements(manager, base, this.selection);
            for (AdvancementHolder holder : advancements) {
                if (this.criterion.isEmpty()) {
                    AdvancementProgress advancementProgress = playerAdvancements.getOrStartProgress(holder);
                    if (advancementProgress.hasProgress())
                        for (String c : advancementProgress.getCompletedCriteria())
                            playerAdvancements.revoke(holder, c);
                } else for (String c : this.criterion) playerAdvancements.revoke(holder, c);
            }
        }
    }
}
