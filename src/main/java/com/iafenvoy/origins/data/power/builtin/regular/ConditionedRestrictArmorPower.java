package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.IntervalPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

public class ConditionedRestrictArmorPower extends IntervalPower {
    public static final MapCodec<ConditionedRestrictArmorPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("head").forGetter(ConditionedRestrictArmorPower::getHead),
            ItemCondition.optionalCodec("chest").forGetter(ConditionedRestrictArmorPower::getChest),
            ItemCondition.optionalCodec("legs").forGetter(ConditionedRestrictArmorPower::getLegs),
            ItemCondition.optionalCodec("feet").forGetter(ConditionedRestrictArmorPower::getFeet),
            Codec.INT.optionalFieldOf("tick_rate", 80).forGetter(ConditionedRestrictArmorPower::getTickRate)
    ).apply(i, ConditionedRestrictArmorPower::new));
    private final ItemCondition head, chest, legs, feet;
    private final int tickRate;
    private final EnumMap<EquipmentSlot, ItemCondition> conditions = new EnumMap<>(EquipmentSlot.class);

    public ConditionedRestrictArmorPower(BaseSettings settings, ItemCondition head, ItemCondition chest, ItemCondition legs, ItemCondition feet, int tickRate) {
        super(settings);
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
        this.tickRate = tickRate;
        this.conditions.put(EquipmentSlot.HEAD, head);
        this.conditions.put(EquipmentSlot.CHEST, chest);
        this.conditions.put(EquipmentSlot.LEGS, legs);
        this.conditions.put(EquipmentSlot.FEET, feet);
    }

    public ItemCondition getHead() {
        return this.head;
    }

    public ItemCondition getChest() {
        return this.chest;
    }

    public ItemCondition getLegs() {
        return this.legs;
    }

    public ItemCondition getFeet() {
        return this.feet;
    }

    public int getTickRate() {
        return this.tickRate;
    }

    public EnumMap<EquipmentSlot, ItemCondition> getConditions() {
        return this.conditions;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public int getInterval() {
        return this.tickRate;
    }

    @Override
    public void intervalTick(@NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;
        this.conditions.forEach((slot, condition) -> {
            ItemStack equippedItem = living.getItemBySlot(slot);
            if (!equippedItem.isEmpty() && condition.test(living.level(), equippedItem)) {
                if (entity instanceof Player player) {
                    if (!player.getInventory().add(equippedItem))
                        player.drop(equippedItem, true);
                } else entity.spawnAtLocation(equippedItem);
                living.setItemSlot(slot, ItemStack.EMPTY);
            }
        });
    }
}
