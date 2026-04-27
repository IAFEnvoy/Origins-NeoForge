package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.helper.ModifierPowerHelper;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class ModifyHealingPower extends Power implements ModifierPowerHelper {
    public static final MapCodec<ModifyHealingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Modifier.CODEC.listOf().fieldOf("modifier").forGetter(ModifyHealingPower::getModifier)
    ).apply(i, ModifyHealingPower::new));
    private final List<Modifier> modifier;

    protected ModifyHealingPower(BaseSettings settings, List<Modifier> modifier) {
        super(settings);
        this.modifier = modifier;
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
    public static void updateHealAmount(LivingHealEvent event) {
        event.setAmount(OriginDataHolder.get(event.getEntity()).getHelper().modify(ModifyHealingPower.class, event.getAmount()));
    }
}
