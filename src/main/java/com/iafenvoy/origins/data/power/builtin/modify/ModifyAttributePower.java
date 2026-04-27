package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ModifyAttributePower extends Power {
    public static final MapCodec<ModifyAttributePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Attribute.CODEC.fieldOf("attribute").forGetter(ModifyAttributePower::getAttribute),
            CombinedCodecs.ATTRIBUTE_MODIFIER.fieldOf("modifier").forGetter(ModifyAttributePower::getModifier)
    ).apply(i, ModifyAttributePower::new));
    private final Holder<Attribute> attribute;
    private final List<AttributeModifier> modifier;

    public ModifyAttributePower(BaseSettings settings, Holder<Attribute> attribute, List<AttributeModifier> modifier) {
        super(settings);
        this.attribute = attribute;
        this.modifier = modifier;
    }

    public Holder<Attribute> getAttribute() {
        return this.attribute;
    }

    public List<AttributeModifier> getModifier() {
        return this.modifier;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    private Optional<AttributeInstance> getAttribute(Entity entity) {
        if (entity instanceof LivingEntity player)
            return player.getAttributes().hasAttribute(this.attribute) ? Optional.ofNullable(player.getAttribute(attribute)) : Optional.empty();
        return Optional.empty();
    }

    @Override
    public void active(@NotNull OriginDataHolder holder) {
        this.getAttribute(holder.getEntity()).ifPresent(x -> this.modifier.stream().filter(mod -> !x.hasModifier(mod.id())).forEach(x::addTransientModifier));
    }

    @Override
    public void inactive(@NotNull OriginDataHolder holder) {
        this.getAttribute(holder.getEntity()).ifPresent(x -> this.modifier.stream().filter(mod -> x.hasModifier(mod.id())).forEach(x::removeModifier));
    }
}
