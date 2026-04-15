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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class RestrictArmorPower extends IntervalPower {
    public static final MapCodec<RestrictArmorPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("head").forGetter(RestrictArmorPower::getHeadCondition),
            ItemCondition.optionalCodec("chest").forGetter(RestrictArmorPower::getChestCondition),
            ItemCondition.optionalCodec("legs").forGetter(RestrictArmorPower::getLegsCondition),
            ItemCondition.optionalCodec("feet").forGetter(RestrictArmorPower::getFeetCondition),
            Codec.INT.optionalFieldOf("tick_rate", 20).forGetter(RestrictArmorPower::getInterval)
    ).apply(i, RestrictArmorPower::new));
    private final ItemCondition head;
    private final ItemCondition chest;
    private final ItemCondition legs;
    private final ItemCondition feet;
    private final int tickRate;

    public RestrictArmorPower(BaseSettings settings, ItemCondition head, ItemCondition chest, ItemCondition legs, ItemCondition feet, int tickRate) {
        super(settings, tickRate);
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
        this.tickRate = tickRate;
    }

    @Override
    public int getInterval() {
        return this.tickRate;
    }

    @Override
    public void intervalTick(@NotNull Entity entity) {
        if (entity.level().isClientSide || !(entity instanceof LivingEntity living)) return;
        checkSingle(living, EquipmentSlot.HEAD, this.head);
        checkSingle(living, EquipmentSlot.CHEST, this.chest);
        checkSingle(living, EquipmentSlot.LEGS, this.legs);
        checkSingle(living, EquipmentSlot.FEET, this.feet);
    }

    private static void checkSingle(LivingEntity entity, EquipmentSlot slot, ItemCondition condition) {
        ItemStack stack = entity.getItemBySlot(slot);
        if (!condition.test(entity.level(), stack))
            Block.popResource(entity.level(), entity.blockPosition(), stack.split(stack.getCount()));
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public ItemCondition getHeadCondition() {
        return this.head;
    }

    public ItemCondition getChestCondition() {
        return this.chest;
    }

    public ItemCondition getLegsCondition() {
        return this.legs;
    }

    public ItemCondition getFeetCondition() {
        return this.feet;
    }
}
