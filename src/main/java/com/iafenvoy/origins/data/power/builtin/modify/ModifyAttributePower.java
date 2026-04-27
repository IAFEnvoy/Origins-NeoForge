package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.helper.ModifierPowerHelper;
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
public class ModifyAttributePower extends Power implements ModifierPowerHelper {
    public static final MapCodec<ModifyAttributePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Attribute.CODEC.fieldOf("attribute").forGetter(ModifyAttributePower::getAttribute),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyAttributePower::getModifier)
    ).apply(i, ModifyAttributePower::new));
    private final Holder<Attribute> attribute;
    private final List<Modifier> modifier;

    public ModifyAttributePower(BaseSettings settings, Holder<Attribute> attribute, List<Modifier> modifier) {
        super(settings);
        this.attribute = attribute;
        this.modifier = modifier;
    }

    public Holder<Attribute> getAttribute() {
        return this.attribute;
    }

    public List<Modifier> getModifier() {
        return this.modifier;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
