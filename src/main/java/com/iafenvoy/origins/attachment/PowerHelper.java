package com.iafenvoy.origins.attachment;

import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public record PowerHelper(OriginDataHolder holder) {
    public <T extends Power> void execute(Class<T> clazz, Predicate<T> condition, Consumer<T> action) {
        this.holder.streamActivePowers(clazz).filter(condition).forEach(action);
    }

    public <T extends Power & ModifierPowerHelper> int modify(Class<T> clazz, int baseValue) {
        return this.modify(clazz, x -> true, baseValue);
    }

    public <T extends Power & ModifierPowerHelper> int modify(Class<T> clazz, Predicate<T> condition, int baseValue) {
        return this.holder.streamActivePowers(clazz).filter(condition).reduce(baseValue, (value, power) -> power.modify(this.holder, value), Integer::sum);
    }

    public <T extends Power & ModifierPowerHelper> float modify(Class<T> clazz, float baseValue) {
        return this.modify(clazz, x -> true, baseValue);
    }

    public <T extends Power & ModifierPowerHelper> float modify(Class<T> clazz, Predicate<T> condition, float baseValue) {
        return this.holder.streamActivePowers(clazz).filter(condition).reduce(baseValue, (value, power) -> power.modify(this.holder, value), Float::sum);
    }

    public <T extends Power & ModifierPowerHelper> double modify(Class<T> clazz, double baseValue) {
        return this.modify(clazz, x -> true, baseValue);
    }

    public <T extends Power & ModifierPowerHelper> double modify(Class<T> clazz, Predicate<T> condition, double baseValue) {
        return this.holder.streamActivePowers(clazz).filter(condition).reduce(baseValue, (value, power) -> power.modify(this.holder, value), Double::sum);
    }

    public void toggle(String key) {
        this.holder.streamPowers(Toggleable.class).forEach(x -> x.toggle(this.holder, key));
    }
}
