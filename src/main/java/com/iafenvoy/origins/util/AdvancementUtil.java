package com.iafenvoy.origins.util;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AdvancementUtil {
    public static List<AdvancementHolder> getAdvancements(ServerAdvancementManager manager, AdvancementHolder advancement, Mode mode) {
        AdvancementNode node = manager.tree().get(advancement);
        if (node == null) return List.of(advancement);
        else {
            List<AdvancementHolder> list = new ArrayList<>();
            if (mode.parents)
                for (AdvancementNode n = node.parent(); n != null; n = n.parent())
                    list.add(n.holder());
            list.add(advancement);
            if (mode.children) addChildren(node, list);
            return list;
        }
    }

    private static void addChildren(AdvancementNode node, List<AdvancementHolder> output) {
        for (AdvancementNode advancementnode : node.children()) {
            output.add(advancementnode.holder());
            addChildren(advancementnode, output);
        }
    }

    public enum Mode implements StringRepresentable {
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
