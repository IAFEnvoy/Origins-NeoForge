package com.iafenvoy.origins.attachment;

import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.ComponentHolderProvider;
import com.iafenvoy.origins.util.function.TriFunction;
import com.iafenvoy.origins.util.math.Modifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface PowerHelper {
    <T extends Power> Stream<T> streamActive(Class<T> clazz);

    default <T extends Power> List<T> listActive(Class<T> clazz) {
        return this.listActive(clazz, x -> true);
    }

    default <T extends Power> List<T> listActive(Class<T> clazz, Predicate<T> condition) {
        return this.streamActive(clazz).filter(condition).toList();
    }

    //execute&modify
    default <T extends Power> void execute(Class<T> clazz, BiConsumer<OriginDataHolder, T> action) {
        this.execute(clazz, x -> true, action);
    }

    <T extends Power> void execute(Class<T> clazz, Predicate<T> condition, BiConsumer<OriginDataHolder, T> action);

    default <T extends Power & ModifierPowerHelper> int modify(Class<T> clazz, int baseValue) {
        return this.modify(clazz, x -> true, baseValue);
    }

    <T extends Power & ModifierPowerHelper> int modify(Class<T> clazz, Predicate<T> condition, int baseValue);

    default <T extends Power & ModifierPowerHelper> float modify(Class<T> clazz, float baseValue) {
        return this.modify(clazz, x -> true, baseValue);
    }

    <T extends Power & ModifierPowerHelper> float modify(Class<T> clazz, Predicate<T> condition, float baseValue);

    default <T extends Power & ModifierPowerHelper> double modify(Class<T> clazz, double baseValue) {
        return this.modify(clazz, x -> true, baseValue);
    }

    <T extends Power & ModifierPowerHelper> double modify(Class<T> clazz, Predicate<T> condition, double baseValue);

    //query
    default <T extends Power> boolean anyActive(Class<T> clazz) {
        return this.anyActive(clazz, x -> true);
    }

    default <T extends Power> boolean anyActive(Class<T> clazz, Predicate<T> condition) {
        return this.streamActive(clazz).anyMatch(condition);
    }

    default <T extends Power> boolean noneActive(Class<T> clazz) {
        return this.noneActive(clazz, x -> true);
    }

    default <T extends Power> boolean noneActive(Class<T> clazz, Predicate<T> condition) {
        return this.streamActive(clazz).noneMatch(condition);
    }

    default <T extends Power> double reduce(Class<T> clazz, double baseValue, TriFunction<OriginDataHolder, Double, T, Double> accumulator) {
        return this.reduce(clazz, baseValue, accumulator, Double::sum);
    }

    default <T extends Power> double reduce(Class<T> clazz, Predicate<T> condition, double baseValue, TriFunction<OriginDataHolder, Double, T, Double> accumulator) {
        return this.reduce(clazz, condition, baseValue, accumulator, Double::sum);
    }

    default <T extends Power, U> U reduce(Class<T> clazz, U baseValue, TriFunction<OriginDataHolder, U, T, U> accumulator, BinaryOperator<U> combiner) {
        return this.reduce(clazz, x -> true, baseValue, accumulator, combiner);
    }

    <T extends Power, U> U reduce(Class<T> clazz, Predicate<T> condition, U baseValue, TriFunction<OriginDataHolder, U, T, U> accumulator, BinaryOperator<U> combiner);

    default <T extends Power> Optional<T> getFirst(Class<T> clazz) {
        return this.getFirst(clazz, x -> true);
    }

    default <T extends Power> Optional<T> getFirst(Class<T> clazz, Predicate<T> condition) {
        return this.streamActive(clazz).filter(condition).findFirst();
    }

    //Component
    <T> Optional<T> getComponent(ResourceLocation id, Class<T> clazz);

    <T> Optional<T> getComponentFor(Power power, Class<T> clazz);

    <H, T extends ComponentHolderProvider<H>> Optional<H> getComponentHolder(ResourceLocation id, Class<T> clazz);

    //misc
    void toggle(String key);

    int applyModifiers(List<Modifier> modifiers, int baseValue);

    float applyModifiers(List<Modifier> modifiers, float baseValue);

    double applyModifiers(List<Modifier> modifiers, double baseValue);

    static PowerHelper get(Entity entity) {
        return OriginDataHolder.optional(entity).<PowerHelper>map(PowerHelperImpl::new).orElse(Empty.INSTANCE);
    }

    enum Empty implements PowerHelper {
        INSTANCE;

        @Override
        public <T extends Power> Stream<T> streamActive(Class<T> clazz) {
            return Stream.empty();
        }

        @Override
        public <T extends Power> void execute(Class<T> clazz, Predicate<T> condition, BiConsumer<OriginDataHolder, T> action) {
        }

        @Override
        public <T extends Power & ModifierPowerHelper> int modify(Class<T> clazz, Predicate<T> condition, int baseValue) {
            return baseValue;
        }

        @Override
        public <T extends Power & ModifierPowerHelper> float modify(Class<T> clazz, Predicate<T> condition, float baseValue) {
            return baseValue;
        }

        @Override
        public <T extends Power & ModifierPowerHelper> double modify(Class<T> clazz, Predicate<T> condition, double baseValue) {
            return baseValue;
        }

        @Override
        public <T extends Power, U> U reduce(Class<T> clazz, Predicate<T> condition, U baseValue, TriFunction<OriginDataHolder, U, T, U> accumulator, BinaryOperator<U> combiner) {
            return baseValue;
        }

        @Override
        public <T> Optional<T> getComponent(ResourceLocation id, Class<T> clazz) {
            return Optional.empty();
        }

        @Override
        public <T> Optional<T> getComponentFor(Power power, Class<T> clazz) {
            return Optional.empty();
        }

        @Override
        public <H, T extends ComponentHolderProvider<H>> Optional<H> getComponentHolder(ResourceLocation id, Class<T> clazz) {
            return Optional.empty();
        }

        @Override
        public void toggle(String key) {
        }

        @Override
        public int applyModifiers(List<Modifier> modifiers, int baseValue) {
            return baseValue;
        }

        @Override
        public float applyModifiers(List<Modifier> modifiers, float baseValue) {
            return baseValue;
        }

        @Override
        public double applyModifiers(List<Modifier> modifiers, double baseValue) {
            return baseValue;
        }
    }
}
