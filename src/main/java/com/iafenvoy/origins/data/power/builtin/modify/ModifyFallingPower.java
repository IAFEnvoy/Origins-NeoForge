package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.helper.ModifierPowerHelper;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class ModifyFallingPower extends Power implements ModifierPowerHelper {
    public static final MapCodec<ModifyFallingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("take_fall_damage", true).forGetter(ModifyFallingPower::shouldTakeFallDamage),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyFallingPower::getModifier)
    ).apply(i, ModifyFallingPower::new));
    private final boolean takeFallDamage;
    private final List<Modifier> modifier;

    public ModifyFallingPower(BaseSettings settings,boolean takeFallDamage, List<Modifier> modifier) {
        super(settings);
        this.takeFallDamage = takeFallDamage;
        this.modifier = modifier;
    }

    public boolean shouldTakeFallDamage() {
        return this.takeFallDamage;
    }

    @Override
    public List<Modifier> getModifier() {
        return this.modifier;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        LivingEntity living = event.getEntity();
        if (OriginDataHolder.get(living).streamActivePowers(ModifyFallingPower.class).anyMatch(x -> !x.shouldTakeFallDamage()))
            event.setDamageMultiplier(0.0F); //Disable fall damage without actually removing distance. This is to avoid breaking compatibility.
    }

    public static double apply(LivingEntity living, double originalValue) {
        AttributeInstance attribute = living.getAttribute(Attributes.GRAVITY);
        if (attribute != null) {
            double modifier = OriginDataHolder.get(living).getHelper().modify(ModifyFallingPower.class, originalValue);
            if (modifier != originalValue && modifier >= 0.0) return modifier;
        }
        return originalValue;
    }
}
