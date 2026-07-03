package com.iafenvoy.origins.attachment;

import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.data.power.component.ComponentHolderProvider;
import com.iafenvoy.origins.util.function.TriFunction;
import com.iafenvoy.origins.util.math.Modifier;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record PowerHelperImpl(OriginDataHolder holder) implements PowerHelper {
    @Override
    public <T extends Power> Stream<T> streamActive(Class<T> clazz) {
        return this.holder.streamActivePowers(clazz);
    }

    @Override
    public <T extends Power> void execute(Class<T> clazz, Predicate<T> condition, BiConsumer<OriginDataHolder, T> action) {
        this.streamActive(clazz).filter(condition).forEach(t -> action.accept(this.holder, t));
    }

    @Override
    public <T extends Power & ModifierPowerHelper> int modify(Class<T> clazz, Predicate<T> condition, int baseValue) {
        return this.streamActive(clazz).filter(condition).reduce(baseValue, (value, power) -> power.modify(this.holder, value), Integer::sum);
    }

    @Override
    public <T extends Power & ModifierPowerHelper> float modify(Class<T> clazz, Predicate<T> condition, float baseValue) {
        return this.streamActive(clazz).filter(condition).reduce(baseValue, (value, power) -> power.modify(this.holder, value), Float::sum);
    }

    @Override
    public <T extends Power & ModifierPowerHelper> double modify(Class<T> clazz, Predicate<T> condition, double baseValue) {
        return this.streamActive(clazz).filter(condition).reduce(baseValue, (value, power) -> power.modify(this.holder, value), Double::sum);
    }

    @Override
    public <T extends Power, U> U reduce(Class<T> clazz, U baseValue, TriFunction<OriginDataHolder, U, T, U> accumulator, BinaryOperator<U> combiner) {
        return this.streamActive(clazz).reduce(baseValue, (value, power) -> accumulator.apply(this.holder, value, power), combiner);
    }

    @Override
    public <T> Optional<T> getComponent(ResourceLocation id, Class<T> clazz) {
        return Optional.ofNullable(this.holder.getData().getComponents().get(id)).map(x -> x.get(clazz)).filter(x -> clazz.isAssignableFrom(x.getClass())).map(clazz::cast);
    }

    @Override
    public <T> Optional<T> getComponentFor(Power power, Class<T> clazz) {
        return this.getComponent(power.getId(this.holder.getAccess()), clazz);
    }

    @Override
    public <H, T extends ComponentHolderProvider<H>> Optional<H> getComponentHolder(ResourceLocation id, Class<T> clazz) {
        return Optional.ofNullable(this.holder.getData().getComponents().get(id)).map(x -> x.get(clazz)).filter(x -> clazz.isAssignableFrom(x.getClass())).map(clazz::cast).map(x -> x.constructHolder(this.holder));
    }

    @Override
    public void toggle(String key) {
        this.holder.streamPowers(Toggleable.class).forEach(x -> x.toggle(this.holder, key));
    }

    @Override
    public int applyModifiers(List<Modifier> modifiers, int baseValue) {
        return Modifier.applyModifiers(this.holder, modifiers, baseValue);
    }

    @Override
    public float applyModifiers(List<Modifier> modifiers, float baseValue) {
        return Modifier.applyModifiers(this.holder, modifiers, baseValue);
    }

    @Override
    public double applyModifiers(List<Modifier> modifiers, double baseValue) {
        return Modifier.applyModifiers(this.holder, modifiers, baseValue);
    }
}
