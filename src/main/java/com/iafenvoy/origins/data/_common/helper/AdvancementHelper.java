package com.iafenvoy.origins.data._common.helper;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Set;

public interface AdvancementHelper {
    Identifier advancement();

    Mode selection();

    default Set<AdvancementHolder> getAdvancements(ServerAdvancementManager manager) {
        AdvancementHolder advancement = manager.get(this.advancement());
        if (advancement == null) return Set.of();
        AdvancementNode node = manager.tree().get(advancement);
        if (node == null) return Set.of(advancement);
        ImmutableSet.Builder<AdvancementHolder> builder = ImmutableSet.builder();
        if (this.selection().parents)
            for (AdvancementNode n = node.parent(); n != null; n = n.parent())
                builder.add(n.holder());
        builder.add(advancement);
        if (this.selection().children) addChildren(node, builder);
        return builder.build();
    }

    static void addChildren(AdvancementNode node, ImmutableSet.Builder<AdvancementHolder> builder) {
        for (AdvancementNode advancementnode : node.children()) {
            builder.add(advancementnode.holder());
            addChildren(advancementnode, builder);
        }
    }

    enum Mode implements StringRepresentable {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);
        public static final Codec<Mode> CODEC = StringRepresentable.fromValues(Mode::values);
        private final boolean parents;
        private final boolean children;

        Mode(boolean parents, boolean children) {
            this.parents = parents;
            this.children = children;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
