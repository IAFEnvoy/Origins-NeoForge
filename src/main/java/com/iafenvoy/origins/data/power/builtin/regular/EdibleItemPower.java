package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@NotImplementedYet
public class EdibleItemPower extends Power implements Prioritized {
    public static final MapCodec<EdibleItemPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityAction.optionalCodec("entity_action").forGetter(EdibleItemPower::getEntityAction),
            ItemAction.optionalCodec("item_action").forGetter(EdibleItemPower::getItemAction),
            ItemAction.optionalCodec("result_item_action").forGetter(EdibleItemPower::getResultItemAction),
            ItemCondition.optionalCodec("item_condition").forGetter(EdibleItemPower::getItemCondition),
            FoodProperties.DIRECT_CODEC.fieldOf("food_properties").forGetter(EdibleItemPower::getFoodProperties),
            ItemStack.CODEC.optionalFieldOf("result_stack").forGetter(EdibleItemPower::getResultStack),
            ExtraEnumCodecs.USE_ANIM.optionalFieldOf("consume_animation", UseAnim.EAT).forGetter(EdibleItemPower::getConsumeAnimation),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("consume_sound", SoundEvents.GENERIC_EAT).forGetter(EdibleItemPower::getConsumeSound),
            CombinedCodecs.MODIFIER.optionalFieldOf("consuming_time_modifier", List.of()).forGetter(EdibleItemPower::getConsumingTimeModifier),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(EdibleItemPower::getPriority)
    ).apply(i, EdibleItemPower::new));
    private final EntityAction entityAction;
    private final ItemAction itemAction, resultItemAction;
    private final ItemCondition itemCondition;
    private final FoodProperties foodProperties;
    private final Optional<ItemStack> resultStack;
    private final UseAnim consumeAnimation;
    private final SoundEvent consumeSound;
    private final List<Modifier> consumingTimeModifier;
    private final int priority;

    public EdibleItemPower(BaseSettings settings, EntityAction entityAction, ItemAction itemAction, ItemAction resultItemAction, ItemCondition itemCondition, FoodProperties foodProperties, Optional<ItemStack> resultStack, UseAnim consumeAnimation, SoundEvent consumeSound, List<Modifier> consumingTimeModifier, int priority) {
        super(settings);
        this.entityAction = entityAction;
        this.itemAction = itemAction;
        this.resultItemAction = resultItemAction;
        this.itemCondition = itemCondition;
        this.foodProperties = foodProperties;
        this.resultStack = resultStack;
        this.consumeAnimation = consumeAnimation;
        this.consumeSound = consumeSound;
        this.consumingTimeModifier = consumingTimeModifier;
        this.priority = priority;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public ItemAction getItemAction() {
        return this.itemAction;
    }

    public ItemAction getResultItemAction() {
        return this.resultItemAction;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public FoodProperties getFoodProperties() {
        return this.foodProperties;
    }

    public Optional<ItemStack> getResultStack() {
        return this.resultStack;
    }

    public UseAnim getConsumeAnimation() {
        return this.consumeAnimation;
    }

    public SoundEvent getConsumeSound() {
        return this.consumeSound;
    }

    public List<Modifier> getConsumingTimeModifier() {
        return this.consumingTimeModifier;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
