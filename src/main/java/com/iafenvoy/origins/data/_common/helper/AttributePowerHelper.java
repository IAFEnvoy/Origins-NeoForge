package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.data._common.AttributeEntry;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.reference.PowerReference;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AttributePowerHelper {
    Power self();

    List<AttributeEntry> getModifier();

    boolean shouldUpdateHealth();

    default void modify(@NotNull OriginDataHolder holder, boolean grant) {
        Entity entity = holder.getEntity();
        if (!(entity instanceof LivingEntity living) || entity.level().isClientSide())
            return;
        float previousMaxHealth = living.getMaxHealth();
        float previousHealthPercent = living.getHealth() / previousMaxHealth;
        this.getModifier().stream().filter(x -> living.getAttributes().hasAttribute(x.attribute())).forEach(mod -> {
            AttributeInstance instance = living.getAttribute(mod.attribute());
            Identifier id = PowerReference.getHolder(holder.getAccess(), this.self()).map(PowerHolder::id).orElse(null);// 此处
            if (id != null && instance != null)
                if (grant) {
                    if (!instance.hasModifier(id))
                        instance.addPermanentModifier(mod.buildModifier(id));
                } else if (instance.hasModifier(id))
                    instance.removeModifier(id);
        });
        float afterMaxHealth = living.getMaxHealth();
        if (this.shouldUpdateHealth() && afterMaxHealth != previousMaxHealth)
            living.setHealth(afterMaxHealth * previousHealthPercent);
    }
}
