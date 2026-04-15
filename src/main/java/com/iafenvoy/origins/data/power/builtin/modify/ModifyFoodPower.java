package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ModifyFoodPower extends Power {
    public static final MapCodec<ModifyFoodPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("food_modifier").forGetter(ModifyFoodPower::getFoodModifiers),
            CombinedCodecs.MODIFIER.fieldOf("saturation_modifier").forGetter(ModifyFoodPower::getSaturationModifiers),
            ItemCondition.optionalCodec("item_condition").forGetter(ModifyFoodPower::getItemCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyFoodPower::getEntityAction),
            ItemStack.CODEC.optionalFieldOf("replace_stack").forGetter(ModifyFoodPower::getReplaceStack),
            ItemAction.optionalCodec("item_action").forGetter(ModifyFoodPower::getItemAction),
            Codec.BOOL.fieldOf("always_edible").orElse(false).forGetter(ModifyFoodPower::isAlwaysEdible),
            Codec.BOOL.fieldOf("prevent_effects").orElse(false).forGetter(ModifyFoodPower::isPreventEffects)
    ).apply(i, ModifyFoodPower::new));

    private final List<Modifier> foodModifiers;
    private final List<Modifier> saturationModifiers;
    private final ItemCondition itemCondition;
    private final EntityAction entityAction;
    private final Optional<ItemStack> replaceStack;
    private final ItemAction itemAction;
    private final boolean alwaysEdible;
    private final boolean preventEffects;

    public ModifyFoodPower(BaseSettings settings, List<Modifier> foodModifiers, List<Modifier> saturationModifiers, ItemCondition itemCondition, EntityAction entityAction, Optional<ItemStack> replaceStack, ItemAction itemAction, boolean alwaysEdible, boolean preventEffects) {
        super(settings);
        this.foodModifiers = foodModifiers;
        this.saturationModifiers = saturationModifiers;
        this.itemCondition = itemCondition;
        this.entityAction = entityAction;
        this.replaceStack = replaceStack;
        this.itemAction = itemAction;
        this.alwaysEdible = alwaysEdible;
        this.preventEffects = preventEffects;
    }

    public List<Modifier> getFoodModifiers() {
        return this.foodModifiers;
    }

    public List<Modifier> getSaturationModifiers() {
        return this.saturationModifiers;
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

    public boolean isPreventEffects() {
        return this.preventEffects;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean test(Level level, ItemStack stack) {
        return this.getItemCondition().test(level, stack);
    }

    public void execute(Entity player) {
        this.getEntityAction().execute(player);
    }

    public double applyFood(double baseValue) {
        return Modifier.applyModifiers(this.foodModifiers, baseValue);
    }

    public double applySaturation(double baseValue) {
        return Modifier.applyModifiers(this.saturationModifiers, baseValue);
    }
}
