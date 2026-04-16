package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@NotImplementedYet
public class ModifyAttributePower extends Power {
    public static final MapCodec<ModifyAttributePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Attribute.CODEC.fieldOf("attribute").forGetter(ModifyAttributePower::getAttribute),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyAttributePower::getModifiers)
    ).apply(i, ModifyAttributePower::new));
    private final Holder<Attribute> attribute;
    private final List<Modifier> modifiers;

    public ModifyAttributePower(BaseSettings settings, Holder<Attribute> attribute, List<Modifier> modifiers) {
        super(settings);
        this.attribute = attribute;
        this.modifiers = modifiers;
    }

    public Holder<Attribute> getAttribute() {
        return this.attribute;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
