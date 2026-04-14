package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.util.EntitySelectorCompiler;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record SelectorActionAction(String selector, BiEntityAction biEntityAction,
                                   BiEntityCondition biEntityCondition) implements EntityAction {
    public static final MapCodec<SelectorActionAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("selector").forGetter(SelectorActionAction::selector),
            BiEntityAction.optionalCodec("bientity_action").forGetter(SelectorActionAction::biEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(SelectorActionAction::biEntityCondition)
    ).apply(i, SelectorActionAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        MinecraftServer server = source.getServer();
        if (server == null) return;
        CommandSourceStack stack = new CommandSourceStack(CommandSource.NULL, source.position(), source.getRotationVector(), (ServerLevel) source.level(), 2, source.getScoreboardName(), source.getName(), server, source);
        try {
            EntitySelectorCompiler.compile(this.selector).findEntities(stack).stream()
                    .filter(e -> this.biEntityCondition().test(source, e))
                    .forEach(e -> this.biEntityAction().execute(source, e));
        } catch (CommandSyntaxException ignored) {
        }
    }
}
