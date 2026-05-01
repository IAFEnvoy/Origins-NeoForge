package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.AttributeEntry;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.Comment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Comment("Only for power, or will crash")
public interface AttributePowerHelper {
    List<AttributeEntry> getModifiers();

    boolean shouldUpdateHealth();

    default void modify(@NotNull OriginDataHolder holder, boolean grant) {
        Entity entity = holder.getEntity();
        if (!(entity instanceof LivingEntity living) || entity.level().isClientSide()) return;
        float previousMaxHealth = living.getMaxHealth();
        float previousHealthPercent = living.getHealth() / previousMaxHealth;
        this.getModifiers().stream().filter(x -> living.getAttributes().hasAttribute(x.attribute())).forEach(mod -> {
            AttributeInstance instance = living.getAttribute(mod.attribute());
            ResourceLocation id = holder.getPowerId((Power) this);// HERE
            if (instance != null)
                if (grant) {
                    if (!instance.hasModifier(id)) instance.addTransientModifier(mod.buildModifier(id));
                } else if (instance.hasModifier(id)) instance.removeModifier(id);
        });
        float afterMaxHealth = living.getMaxHealth();
        if (this.shouldUpdateHealth() && afterMaxHealth != previousMaxHealth)
            living.setHealth(afterMaxHealth * previousHealthPercent);
    }
}
