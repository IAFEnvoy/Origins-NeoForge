package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.common.KeySettings;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.data.power.component.builtin.InventoryComponent;
import com.iafenvoy.origins.registry.OriginsKeyMappings;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

//FIXME::Back to vanilla screens or use custom screen?
public class InventoryPower extends Power implements Toggleable, MenuProvider {
    public static final MapCodec<InventoryPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ComponentSerialization.CODEC.optionalFieldOf("title", Component.translatable("container.inventory")).forGetter(InventoryPower::getTitle),
            Codec.BOOL.optionalFieldOf("drop_on_death", false).forGetter(InventoryPower::isDropOnDeath),
            ContainerType.CODEC.optionalFieldOf("container_type", ContainerType.DISPENSER).forGetter(InventoryPower::getContainerType),
            EntityCondition.optionalCodec("condition").forGetter(InventoryPower::getCondition),
            KeySettings.CODEC.forGetter(InventoryPower::getKey)
    ).apply(i, InventoryPower::new));
    private final Component title;
    private final boolean dropOnDeath;
    private final ContainerType containerType;
    private final EntityCondition condition;
    private final KeySettings key;

    public InventoryPower(BaseSettings settings, Component title, boolean dropOnDeath, ContainerType containerType, EntityCondition condition, KeySettings key) {
        super(settings);
        this.title = title;
        this.dropOnDeath = dropOnDeath;
        this.containerType = containerType;
        this.condition = condition;
        this.key = key;
    }

    public Component getTitle() {
        return this.title;
    }

    public boolean isDropOnDeath() {
        return this.dropOnDeath;
    }

    public ContainerType getContainerType() {
        return this.containerType;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    public KeySettings getKey() {
        return this.key;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public List<PowerComponent> createComponents() {
        return List.of(new InventoryComponent(this.containerType.getSize()));
    }

    @Override
    public void toggle(@NotNull OriginDataHolder holder, String key) {
        if (holder.entity() instanceof Player player && Objects.equals(this.key.key(), key))
            player.openMenu(this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return this.title;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return OriginDataHolder.get(player).getComponentFor(this, InventoryComponent.class).map(InventoryComponent::container).map(container -> this.containerType.getFactory().createMenu(id, inventory, container)).orElse(null);
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
