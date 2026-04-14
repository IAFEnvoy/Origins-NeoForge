package com.iafenvoy.origins.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AdvancementUtil {
    public static List<AdvancementHolder> getAdvancements(ServerAdvancementManager manager, AdvancementHolder advancement, Mode mode) {
        AdvancementTree advancementtree = manager.tree();
        AdvancementNode advancementnode = advancementtree.get(advancement);
        if (advancementnode == null) return List.of(advancement);
        else {
            List<AdvancementHolder> list = new ArrayList<>();
            if (mode.parents)
                for (AdvancementNode advancementnode1 = advancementnode.parent(); advancementnode1 != null; advancementnode1 = advancementnode1.parent())
                    list.add(advancementnode1.holder());
            list.add(advancement);
            if (mode.children) addChildren(advancementnode, list);
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

        private final boolean parents;
        private final boolean children;
        public static final Codec<Mode> CODEC = StringRepresentable.fromValues(Mode::values);

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
