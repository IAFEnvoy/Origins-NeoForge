package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyAttributePower(Holder<Attribute> attribute, List<Modifier> modifiers) implements Power {

    public static final MapCodec<ModifyAttributePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(ModifyAttributePower::attribute),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyAttributePower::modifiers)
    ).apply(i, ModifyAttributePower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
