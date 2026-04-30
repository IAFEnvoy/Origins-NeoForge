package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.KeySettings;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.builtin.InventoryComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@EventBusSubscriber
public class InventoryPower extends Power implements Toggleable, MenuProvider {
    public static final MapCodec<InventoryPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ComponentSerialization.CODEC.optionalFieldOf("title", Component.translatable("container.inventory")).forGetter(InventoryPower::getTitle),
            ContainerType.CODEC.optionalFieldOf("container_type", ContainerType.DISPENSER).forGetter(InventoryPower::getContainerType),
            Codec.BOOL.optionalFieldOf("drop_on_death", false).forGetter(InventoryPower::shouldDropOnDeath),
            ItemCondition.optionalCodec("drop_on_death_filter").forGetter(InventoryPower::getDropOnDeathFilter),
            Codec.BOOL.optionalFieldOf("recoverable", true).forGetter(InventoryPower::isRecoverable),
            KeySettings.CODEC.forGetter(InventoryPower::getKey)
    ).apply(i, InventoryPower::new));
    private final Component title;
    private final ContainerType containerType;
    private final boolean dropOnDeath;
    private final ItemCondition dropOnDeathFilter;
    private final boolean recoverable;
    private final KeySettings key;

    public InventoryPower(BaseSettings settings, Component title, ContainerType containerType, boolean dropOnDeath, ItemCondition dropOnDeathFilter, boolean recoverable, KeySettings key) {
        super(settings);
        this.title = title;
        this.containerType = containerType;
        this.dropOnDeath = dropOnDeath;
        this.dropOnDeathFilter = dropOnDeathFilter;
        this.recoverable = recoverable;
        this.key = key;
    }

    public Component getTitle() {
        return this.title;
    }

    public ContainerType getContainerType() {
        return this.containerType;
    }

    public boolean shouldDropOnDeath() {
        return this.dropOnDeath;
    }

    public ItemCondition getDropOnDeathFilter() {
        return this.dropOnDeathFilter;
    }

    public boolean isRecoverable() {
        return this.recoverable;
    }

    public KeySettings getKey() {
        return this.key;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void createComponents(ComponentCollector collector) {
        super.createComponents(collector);
        collector.add(new InventoryComponent(this.containerType.getSize()));
    }

    @Override
    public void toggle(@NotNull OriginDataHolder holder, String key) {
        if (holder.getEntity() instanceof Player player && Objects.equals(this.key.key(), key))
            player.openMenu(this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return this.title;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return OriginDataHolder.get(player).getComponentFor(this, InventoryComponent.class).map(InventoryComponent::getContainer).map(container -> this.containerType.getFactory().createMenu(id, inventory, container)).orElse(null);
    }

    public void tryDropItemsOnDeath(OriginDataHolder holder, Player player) {
        Optional<Container> optional = holder.getComponentFor(this, InventoryComponent.class).map(InventoryComponent::getContainer);
        if (optional.isPresent() && this.dropOnDeath) {
            Container container = optional.get();
            for (int i = 0; i < container.getContainerSize(); ++i) {
                ItemStack itemStack = container.getItem(i);
                if (this.dropOnDeathFilter.test(player.level(), itemStack)) {
                    if (!itemStack.isEmpty() && EnchantmentHelper.has(itemStack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP))
                        container.removeItemNoUpdate(i);
                    else {
                        player.drop(itemStack, true, false);
                        container.setItem(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            OriginDataHolder holder = OriginDataHolder.get(player);
            if (!player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
                holder.streamActivePowers(InventoryPower.class).forEach(p -> p.tryDropItemsOnDeath(holder, player));
        }
    }

    public enum ContainerType implements StringRepresentable {
        CHEST_1x9(9, (id, inventory, container) -> new ChestMenu(MenuType.GENERIC_9x1, id, inventory, container, 1)),
        CHEST_2x9(18, (id, inventory, container) -> new ChestMenu(MenuType.GENERIC_9x2, id, inventory, container, 2)),
        CHEST_3x9(27, ChestMenu::threeRows),
        CHEST_4x9(36, (id, inventory, container) -> new ChestMenu(MenuType.GENERIC_9x4, id, inventory, container, 4)),
        CHEST_5x9(45, (id, inventory, container) -> new ChestMenu(MenuType.GENERIC_9x5, id, inventory, container, 5)),
        CHEST_6x9(54, ChestMenu::sixRows),
        HOPPER(5, HopperMenu::new),
        DISPENSER(9, DispenserMenu::new);
        public static final Codec<ContainerType> CODEC = StringRepresentable.fromValues(ContainerType::values);
        private final int size;
        private final MenuFactory factory;

        ContainerType(int size, MenuFactory factory) {
            this.size = size;
            this.factory = factory;
        }

        public int getSize() {
            return this.size;
        }

        public MenuFactory getFactory() {
            return this.factory;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @FunctionalInterface
        public interface MenuFactory {
            AbstractContainerMenu createMenu(int id, Inventory inventory, Container container);
        }
    }
}
