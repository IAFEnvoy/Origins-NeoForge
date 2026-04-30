package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.builtin.InventoryComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public class KeepInventoryPower extends Power {
    public static final MapCodec<KeepInventoryPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(KeepInventoryPower::getSettings),
            ItemCondition.optionalCodec("item_condition").forGetter(KeepInventoryPower::getItemCondition),
            Codec.INT.listOf().optionalFieldOf("slots", List.of()).forGetter(KeepInventoryPower::getSlots)
    ).apply(i, KeepInventoryPower::new));
    private final ItemCondition itemCondition;
    private final List<Integer> slots;

    public KeepInventoryPower(BaseSettings settings, ItemCondition itemCondition, List<Integer> slots) {
        super(settings);
        this.itemCondition = itemCondition;
        this.slots = slots;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public List<Integer> getSlots() {
        return this.slots;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void createComponents(ComponentCollector collector) {
        super.createComponents(collector);
        collector.add(new InventoryComponent(41));
    }

    public boolean isApplicableTo(int slot, Level level, ItemStack stack) {
        if (this.slots != null && !this.slots.contains(slot)) return false;
        return this.itemCondition.test(level, stack);
    }

    public void captureItems(OriginDataHolder holder, Player player) {
        Optional<Container> optional = holder.getComponentFor(this, InventoryComponent.class).map(InventoryComponent::getContainer);
        if (optional.isPresent()) {
            Container container = optional.get();
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (this.isApplicableTo(i, player.level(), stack) && !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                    container.setItem(i, stack);
                    inventory.setItem(i, ItemStack.EMPTY);
                } else
                    container.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    public void restoreItems(OriginDataHolder holder, Player player) {
        Optional<Container> optional = holder.getComponentFor(this, InventoryComponent.class).map(InventoryComponent::getContainer);
        if (optional.isPresent()) {
            Container container = optional.get();
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize() && i < container.getContainerSize(); i++)
                if (!container.getItem(i).isEmpty()) {
                    inventory.setItem(i, container.getItem(i));
                    container.setItem(i, ItemStack.EMPTY);
                }
        }
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        OriginDataHolder holder = OriginDataHolder.get(event.getEntity());
        if (!event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
            holder.streamActivePowers(KeepInventoryPower.class).forEach(power -> power.restoreItems(holder, event.getEntity()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            OriginDataHolder holder = OriginDataHolder.get(player);
            if (!player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
                holder.streamActivePowers(KeepInventoryPower.class).forEach(power -> power.captureItems(holder, player));
        }
    }
}
