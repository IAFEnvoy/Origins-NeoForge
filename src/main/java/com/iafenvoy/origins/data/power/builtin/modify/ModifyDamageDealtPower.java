package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.helper.ModifierPowerHelper;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class ModifyDamageDealtPower extends Power implements ModifierPowerHelper {
    public static final MapCodec<ModifyDamageDealtPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyDamageDealtPower::getModifier),
            DamageCondition.optionalCodec("damage_condition").forGetter(ModifyDamageDealtPower::getDamageCondition),
            EntityCondition.optionalCodec("target_condition").forGetter(ModifyDamageDealtPower::getTargetCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ModifyDamageDealtPower::getBiEntityCondition),
            EntityAction.optionalCodec("self_action").forGetter(ModifyDamageDealtPower::getSelfAction),
            EntityAction.optionalCodec("target_action").forGetter(ModifyDamageDealtPower::getTargetAction),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ModifyDamageDealtPower::getBiEntityAction)
    ).apply(i, ModifyDamageDealtPower::new));

    private final List<Modifier> modifier;
    private final DamageCondition damageCondition;
    private final EntityCondition targetCondition;
    private final BiEntityCondition biEntityCondition;
    private final EntityAction selfAction, targetAction;
    private final BiEntityAction biEntityAction;

    public ModifyDamageDealtPower(BaseSettings settings, List<Modifier> modifier, DamageCondition damageCondition, EntityCondition targetCondition, BiEntityCondition biEntityCondition, EntityAction selfAction, EntityAction targetAction, BiEntityAction biEntityAction) {
        super(settings);
        this.modifier = modifier;
        this.damageCondition = damageCondition;
        this.targetCondition = targetCondition;
        this.biEntityCondition = biEntityCondition;
        this.selfAction = selfAction;
        this.targetAction = targetAction;
        this.biEntityAction = biEntityAction;
    }

    public List<Modifier> getModifier() {
        return this.modifier;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
    }

    public EntityCondition getTargetCondition() {
        return this.targetCondition;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    public EntityAction getSelfAction() {
        return this.selfAction;
    }

    public EntityAction getTargetAction() {
        return this.targetAction;
    }

    public BiEntityAction getBiEntityAction() {
        return this.biEntityAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        Entity source = event.getSource().getEntity(), target = event.getEntity();
        if (source == null) return;
        OriginDataHolder.get(source).streamActivePowers(ModifyDamageDealtPower.class).forEach(power -> {
            float baseValue = event.getNewDamage();
            DamageSource s = event.getSource();
            if (power.damageCondition.test(s, baseValue) && power.targetCondition.test(target) && power.biEntityCondition.test(source, target)) {
                event.setNewDamage(power.modify(baseValue));
                power.selfAction.execute(source);
                power.targetAction.execute(target);
                power.biEntityAction.execute(source, target);
            }
        });
    }
}
