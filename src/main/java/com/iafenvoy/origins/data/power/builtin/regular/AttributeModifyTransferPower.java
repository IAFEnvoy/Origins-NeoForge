package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.reference.PowerReference;
import com.iafenvoy.origins.event.OriginsModifierCollectEvent;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@EventBusSubscriber
public class AttributeModifyTransferPower extends Power {
    public static final MapCodec<AttributeModifyTransferPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            PowerReference.CODEC.fieldOf("target").forGetter(AttributeModifyTransferPower::getTarget),
            BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(AttributeModifyTransferPower::getAttribute),
            Codec.DOUBLE.optionalFieldOf("multiplier", 1.0).forGetter(AttributeModifyTransferPower::getMultiplier)
    ).apply(i, AttributeModifyTransferPower::new));
    private final PowerReference target;
    private final Holder<Attribute> attribute;
    private final double multiplier;

    public AttributeModifyTransferPower(BaseSettings settings, PowerReference target, Holder<Attribute> attribute, double multiplier) {
        super(settings);
        this.target = target;
        this.attribute = attribute;
        this.multiplier = multiplier;
    }

    public PowerReference getTarget() {
        return this.target;
    }

    public Holder<Attribute> getAttribute() {
        return this.attribute;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void registerCollectModifiersCallback(OriginsModifierCollectEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity livingEntity)) return;
        Class<? extends Power> powerClass = event.getPowerClass();
        List<Modifier> modifiers = event.getModifier();
        OriginDataHolder holder = OriginDataHolder.get(livingEntity);
        for (AttributeModifyTransferPower p : holder.streamActivePowers(AttributeModifyTransferPower.class).toList()) {
            if (p.target.get(holder.getAccess()).map(x -> !Objects.equals(powerClass, x.power().getClass())).orElse(true))
                continue;
            AttributeInstance attributeInstance = livingEntity.getAttributes().getInstance(p.attribute);
            if (attributeInstance == null) continue;
            attributeInstance.getModifiers().stream().map(attributeModifier -> Modifier.fromAttributeModifier(new AttributeModifier(attributeModifier.id(), attributeModifier.amount() * p.multiplier, attributeModifier.operation()))).forEach(modifiers::add);
        }
    }
}
