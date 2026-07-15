package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class PreventBreedingPower extends Power {
    public static final MapCodec<PreventBreedingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(PreventBreedingPower::getBiEntityCondition),
            BiEntityAction.optionalCodec("bientity_action").forGetter(PreventBreedingPower::getBiEntityAction),
            Codec.BOOL.optionalFieldOf("prevent_follow", false).forGetter(PreventBreedingPower::preventsFollow)
    ).apply(instance, PreventBreedingPower::new));
    private final BiEntityCondition bientityCondition;
    private final BiEntityAction bientityAction;
    private final boolean preventFollow;

    public PreventBreedingPower(BaseSettings settings, BiEntityCondition bientityCondition,
                                BiEntityAction bientityAction, boolean preventFollow) {
        super(settings);
        this.bientityCondition = bientityCondition;
        this.bientityAction = bientityAction;
        this.preventFollow = preventFollow;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.bientityCondition;
    }

    public BiEntityAction getBiEntityAction() {
        return this.bientityAction;
    }

    public boolean preventsFollow() {
        return this.preventFollow;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onBreed(BabyEntitySpawnEvent event) {
        Player player = event.getCausedByPlayer();
        if (player == null) return;

        Mob firstParent = event.getParentA();
        Mob secondParent = event.getParentB();
        PowerHelper.get(player).streamActive(PreventBreedingPower.class).forEach(power -> {
            Mob target = power.bientityCondition.test(player, firstParent) ? firstParent
                    : power.bientityCondition.test(player, secondParent) ? secondParent : null;
            if (target == null) return;
            event.setCanceled(true);
            power.bientityAction.execute(player, target);
        });
    }
}
