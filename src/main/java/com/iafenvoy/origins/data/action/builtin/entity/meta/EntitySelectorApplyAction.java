package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record EntitySelectorApplyAction(String selector, BiEntityAction biEntityAction,
                                        BiEntityCondition biEntityCondition) implements EntityAction {
    public static final MapCodec<EntitySelectorApplyAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("selector").forGetter(EntitySelectorApplyAction::selector),
            BiEntityAction.optionalCodec("bientity_action").forGetter(EntitySelectorApplyAction::biEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(EntitySelectorApplyAction::biEntityCondition)
    ).apply(i, EntitySelectorApplyAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        try {
            for (Entity entity : new EntitySelectorParser(new StringReader(this.selector), true).getSelector().findEntities(source.createCommandSourceStack()))
                if (this.biEntityCondition.test(source, entity))
                    this.biEntityAction.execute(source, entity);
        } catch (Exception e) {
            Origins.LOGGER.error("Failed to execute selector.", e);
        }
    }
}
