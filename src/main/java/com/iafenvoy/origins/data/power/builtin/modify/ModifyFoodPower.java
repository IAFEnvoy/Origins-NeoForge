package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.Modifier;
import com.iafenvoy.origins.util.ModifierUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record ModifyFoodPower(List<Modifier> foodModifiers, List<Modifier> saturationModifiers,
                               ItemCondition itemCondition, EntityAction entityAction,
                               Optional<ItemStack> replaceStack, ItemAction itemAction,
                               boolean alwaysEdible, boolean preventEffects) implements Power {

    public static final MapCodec<ModifyFoodPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ListConfiguration.modifierCodec("food_modifier").forGetter(ModifyFoodPower::foodModifiers),
            ListConfiguration.modifierCodec("saturation_modifier").forGetter(ModifyFoodPower::saturationModifiers),
            ItemCondition.optionalCodec("item_condition").forGetter(ModifyFoodPower::itemCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyFoodPower::entityAction),
            ItemStack.CODEC.optionalFieldOf("replace_stack").forGetter(ModifyFoodPower::replaceStack),
            ItemAction.optionalCodec("item_action").forGetter(ModifyFoodPower::itemAction),
            Codec.BOOL.fieldOf("always_edible").orElse(false).forGetter(ModifyFoodPower::alwaysEdible),
            Codec.BOOL.fieldOf("prevent_effects").orElse(false).forGetter(ModifyFoodPower::preventEffects)
    ).apply(i, ModifyFoodPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean test(Level level, ItemStack stack) {
        return this.itemCondition().test(level, stack);
    }

    public void execute(Entity player) {
        this.entityAction().execute(player);
    }

    public double applyFood(double baseValue) {
        return ModifierUtil.applyModifiers(this.foodModifiers, baseValue);
    }

    public double applySaturation(double baseValue) {
        return ModifierUtil.applyModifiers(this.saturationModifiers, baseValue);
    }
}
