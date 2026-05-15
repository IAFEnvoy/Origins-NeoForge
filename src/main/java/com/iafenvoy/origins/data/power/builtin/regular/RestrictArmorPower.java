package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

public class RestrictArmorPower extends Power {
    public static final MapCodec<RestrictArmorPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("head").forGetter(RestrictArmorPower::getHeadCondition),
            ItemCondition.optionalCodec("chest").forGetter(RestrictArmorPower::getChestCondition),
            ItemCondition.optionalCodec("legs").forGetter(RestrictArmorPower::getLegsCondition),
            ItemCondition.optionalCodec("feet").forGetter(RestrictArmorPower::getFeetCondition),
            Codec.INT.optionalFieldOf("tick_rate", 20).forGetter(RestrictArmorPower::getTickRate)
    ).apply(i, RestrictArmorPower::new));
    private final ItemCondition head;
    private final ItemCondition chest;
    private final ItemCondition legs;
    private final ItemCondition feet;
    private final int tickRate;
    private final EnumMap<EquipmentSlot, ItemCondition> conditions = new EnumMap<>(EquipmentSlot.class);

    public RestrictArmorPower(BaseSettings settings, ItemCondition head, ItemCondition chest, ItemCondition legs, ItemCondition feet, int tickRate) {
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
    public void activeTick(OriginDataHolder holder) {
        super.activeTick(holder);
        if (!(holder.getEntity() instanceof LivingEntity living)) return;
        this.conditions.forEach((slot, condition) -> checkSingle(living, slot, condition));
    }

    @Override
    public int tickInterval() {
        return this.tickRate;
    }

    private static void checkSingle(LivingEntity entity, EquipmentSlot slot, ItemCondition condition) {
        ItemStack stack = entity.getItemBySlot(slot);
        if (condition.test(entity.level(), stack))
            Block.popResource(entity.level(), entity.blockPosition(), stack.split(stack.getCount()));
    }
}
