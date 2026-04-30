package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.PositionedItemStackSettings;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.OptionalInt;

@EventBusSubscriber
public class StartingEquipmentPower extends Power {
    public static final MapCodec<StartingEquipmentPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.POSITIONED_ITEM_STACK.fieldOf("stack").forGetter(StartingEquipmentPower::getStack),
            Codec.BOOL.optionalFieldOf("recurrent", false).forGetter(StartingEquipmentPower::shouldRecurrent)
    ).apply(i, StartingEquipmentPower::new));
    private final List<PositionedItemStackSettings> stack;
    private final boolean recurrent;

    public StartingEquipmentPower(BaseSettings settings, List<PositionedItemStackSettings> stack, boolean recurrent) {
        super(settings);
        this.stack = stack;
        this.recurrent = recurrent;
    }

    public List<PositionedItemStackSettings> getStack() {
        return this.stack;
    }

    public boolean shouldRecurrent() {
        return this.recurrent;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void grant(@NotNull OriginDataHolder holder) {
        super.grant(holder);
        if (holder.getEntity() instanceof Player player)
            this.giveStacks(player);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        OriginDataHolder.get(player).streamActivePowers(StartingEquipmentPower.class).filter(StartingEquipmentPower::shouldRecurrent).forEach(x -> x.giveStacks(player));
    }

    private void giveStacks(Player player) {
        this.stack.forEach(x -> {
            Origins.LOGGER.info("Giving player {} stack: {}", player.getName().getString(), x.stack().toString());
            OptionalInt optional = x.slot();
            if (optional.isPresent()) {
                int slot = optional.getAsInt();
                Inventory inventory = player.getInventory();
                if (slot >= 0 && slot <= inventory.getContainerSize() && inventory.getItem(slot).isEmpty())
                    player.getInventory().setItem(slot, x.stack().copy());
                else
                    Origins.LOGGER.warn("Couldn't give player {} stack {} in slot {}, slot is not empty or invalid!", player.getName().getString(), x.stack().toString(), slot);
            } else player.addItem(x.stack().copy());
        });
    }
}
