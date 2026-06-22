package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.event.OriginsModifierCollectEvent;
import com.iafenvoy.origins.util.math.Modifier;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

//仅供能力使用
@FunctionalInterface
public interface ModifierPowerHelper {
    List<Modifier> getModifier();

    default int modify(OriginDataHolder holder, int baseValue) {
        return Modifier.applyModifiers(holder, this.collectModifiers(holder, baseValue), baseValue);
    }

    default float modify(OriginDataHolder holder, float baseValue) {
        return Modifier.applyModifiers(holder, this.collectModifiers(holder, baseValue), baseValue);
    }

    default double modify(OriginDataHolder holder, double baseValue) {
        return Modifier.applyModifiers(holder, this.collectModifiers(holder, baseValue), baseValue);
    }

    @SuppressWarnings("unchecked")
    default List<Modifier> collectModifiers(OriginDataHolder holder, double baseValue) {
        OriginsModifierCollectEvent event = new OriginsModifierCollectEvent(holder.getEntity(), (Class<? extends Power>) this.getClass(), baseValue, this.getModifier());
        NeoForge.EVENT_BUS.post(event);
        return event.getModifier();
    }
}
