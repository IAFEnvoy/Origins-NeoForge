package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.iafenvoy.origins.util.wrapper.Mutable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ModifyFoodPower extends Power {
    public static final MapCodec<ModifyFoodPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.optionalFieldOf("food_modifier", List.of()).forGetter(ModifyFoodPower::getFoodModifier),
            CombinedCodecs.MODIFIER.optionalFieldOf("saturation_modifier", List.of()).forGetter(ModifyFoodPower::getSaturationModifier),
            ItemCondition.optionalCodec("item_condition").forGetter(ModifyFoodPower::getItemCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyFoodPower::getEntityAction),
            ItemStack.CODEC.optionalFieldOf("replace_stack").forGetter(ModifyFoodPower::getReplaceStack),
            ItemAction.optionalCodec("item_action").forGetter(ModifyFoodPower::getItemAction),
            Codec.BOOL.optionalFieldOf("always_edible", false).forGetter(ModifyFoodPower::isAlwaysEdible),
            Codec.BOOL.optionalFieldOf("prevent_effects", false).forGetter(ModifyFoodPower::shouldPreventEffects)
    ).apply(i, ModifyFoodPower::new));

    private final List<Modifier> foodModifier;
    private final List<Modifier> saturationModifier;
    private final ItemCondition itemCondition;
    private final EntityAction entityAction;
    private final Optional<ItemStack> replaceStack;
    private final ItemAction itemAction;
    private final boolean alwaysEdible;
    private final boolean preventEffects;

    public ModifyFoodPower(BaseSettings settings, List<Modifier> foodModifier, List<Modifier> saturationModifier, ItemCondition itemCondition, EntityAction entityAction, Optional<ItemStack> replaceStack, ItemAction itemAction, boolean alwaysEdible, boolean preventEffects) {
        super(settings);
        this.foodModifier = foodModifier;
        this.saturationModifier = saturationModifier;
        this.itemCondition = itemCondition;
        this.entityAction = entityAction;
        this.replaceStack = replaceStack;
        this.itemAction = itemAction;
        this.alwaysEdible = alwaysEdible;
        this.preventEffects = preventEffects;
    }

    public List<Modifier> getFoodModifier() {
        return this.foodModifier;
    }

    public List<Modifier> getSaturationModifier() {
        return this.saturationModifier;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public Optional<ItemStack> getReplaceStack() {
        return this.replaceStack;
    }

    public ItemAction getItemAction() {
        return this.itemAction;
    }

    public boolean isAlwaysEdible() {
        return this.alwaysEdible;
    }

    public boolean shouldPreventEffects() {
        return this.preventEffects;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public static void modifyStack(Level level, Entity entity, Mutable.Stack input) {
        PowerHelper.get(entity).execute(ModifyFoodPower.class, p -> p.itemCondition.test(level, input.get()), (h, p) -> {
            p.replaceStack.ifPresent(stack -> input.set(stack.copy()));
            p.itemAction.execute(level, entity, input.toSlotAccess());
        });
    }

    public static FoodProperties modifyFoodProperties(Level level, Entity entity, ItemStack stack, FoodProperties original) {
        PowerHelper helper = PowerHelper.get(entity);
        List<ModifyFoodPower> powers = helper.listActive(ModifyFoodPower.class, p -> p.itemCondition.test(level, stack));
        if (powers.isEmpty()) return original;
        int nutrition = helper.applyModifiers(powers.stream().flatMap(p -> p.foodModifier.stream()).toList(), original.nutrition());
        float saturation = helper.applyModifiers(powers.stream().flatMap(p -> p.saturationModifier.stream()).toList(), original.saturation());
        return new FoodProperties(nutrition, saturation, original.canAlwaysEat(), original.eatSeconds(), original.usingConvertsTo(), original.effects());
    }
}
