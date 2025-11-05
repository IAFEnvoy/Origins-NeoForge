package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ModifyFoodPower(ItemCondition itemCondition, EntityAction entityAction, Optional<ItemStack> replaceStack,
                              ItemAction itemAction, boolean alwaysEdible, boolean preventEffects) implements Power {

    public static final MapCodec<ModifyFoodPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.optionalCodec("item_condition").forGetter(ModifyFoodPower::itemCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyFoodPower::entityAction),
            ItemStack.CODEC.optionalFieldOf("replace_stack").forGetter(ModifyFoodPower::replaceStack),
            ItemAction.optionalCodec("item_action").forGetter(ModifyFoodPower::itemAction),
            Codec.BOOL.fieldOf("always_edible").orElse(false).forGetter(ModifyFoodPower::alwaysEdible),
            Codec.BOOL.fieldOf("prevent_effects").orElse(false).forGetter(ModifyFoodPower::preventEffects)
    ).apply(i, ModifyFoodPower::new));

// TODO ListConfiguration

//public static final Codec<ModifyFoodConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//        ListConfiguration.modifierCodec("food_modifier").forGetter(ModifyFoodConfiguration::foodModifiers),
//        ListConfiguration.modifierCodec("saturation_modifier").forGetter(ModifyFoodConfiguration::saturationModifiers),
//        ConfiguredItemCondition.optional("item_condition").forGetter(ModifyFoodConfiguration::itemCondition),
//        ConfiguredEntityAction.optional("entity_action").forGetter(ModifyFoodConfiguration::entityAction),
//        ExtraCodecs.strictOptionalField(SerializableDataTypes.ITEM_STACK, "replace_stack").forGetter(x -> Optional.ofNullable(x.replaceStack())),
//        ConfiguredItemAction.optional("item_action").forGetter(ModifyFoodConfiguration::itemAction),
//        ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "always_edible", false).forGetter(ModifyFoodConfiguration::alwaysEdible),
//        ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "prevent_effects", false).forGetter(ModifyFoodConfiguration::preventEffects)
//).apply(instance, (t1, t2, t3, t4, t5, t6, t7, t8) -> new ModifyFoodConfiguration(t1, t2, t3, t4, t5.orElse(null), t6, t7, t8)));
//
    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    // TODO
//
//    public static List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> getValidPowers(Entity source, ItemStack stack) {
//        return getValidPowers(source, source.level(), stack);
//    }
//
//    public static List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> getValidPowers(Entity source, Level level, ItemStack stack) {
//        return PowerContainer.getPowers(source, ApoliPowers.MODIFY_FOOD.get()).stream().map(Holder::value)
//                .filter(x -> x.getFactory().check(x, level, stack)).collect(Collectors.toList());
//    }
//
//    public static boolean isAlwaysEdible(Entity entity, Level level, ItemStack stack) {
//        return getValidPowers(entity, level, stack).stream().anyMatch(x -> x.getConfiguration().alwaysEdible());
//    }
//
//    public static double apply(List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> source, Level level, ItemStack stack, double baseValue, Function<ModifyFoodConfiguration, ListConfiguration<AttributeModifier>> access) {
//        List<AttributeModifier> modifiers = source.stream()
//                .filter(x -> x.getFactory().check(x, level, stack))
//                .flatMap(x -> access.apply(x.getConfiguration()).getContent().stream()).collect(Collectors.toList());
//        return AttributeUtil.applyModifiers(modifiers, baseValue);
//    }
//
//    public static void modifyStack(Iterable<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> powers, Level level, Mutable<ItemStack> input) {
//        for (ConfiguredPower<ModifyFoodConfiguration, ?> power : powers) {
//            if (power.getConfiguration().replaceStack() != null)
//                input.setValue(power.getConfiguration().replaceStack().copy());
//            ConfiguredItemAction.execute(power.getConfiguration().itemAction(), level, input);
//        }
//    }
//
//    public static void execute(List<ConfiguredPower<ModifyFoodConfiguration, ModifyFoodPower>> source, Entity entity, Level level, ItemStack stack) {
//        source.stream()
//                .filter(x -> x.getFactory().check(x, level, stack))
//                .forEach(x -> x.getFactory().execute(x, entity));
//    }


    public boolean test(Level level, ItemStack stack) {
        return itemCondition().test(level, stack);
    }

    public void execute(Entity player) {
        entityAction().execute(player);
    }
}
