package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public class ActionOnEquipPower extends Power {
    private static final EquipmentSlot[] TRACKED_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND
    };
    private static final Map<UUID, Map<EquipmentSlot, ItemStack>> EQUIPMENT = new ConcurrentHashMap<>();

    public static final MapCodec<ActionOnEquipPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("head").forGetter(ActionOnEquipPower::head),
            ItemCondition.optionalCodec("chest").forGetter(ActionOnEquipPower::chest),
            ItemCondition.optionalCodec("legs").forGetter(ActionOnEquipPower::legs),
            ItemCondition.optionalCodec("feet").forGetter(ActionOnEquipPower::feet),
            ItemCondition.optionalCodec("offhand").forGetter(ActionOnEquipPower::offhand),
            EntityAction.CODEC.fieldOf("action").forGetter(ActionOnEquipPower::action)
    ).apply(instance, ActionOnEquipPower::new));
    private final ItemCondition head;
    private final ItemCondition chest;
    private final ItemCondition legs;
    private final ItemCondition feet;
    private final ItemCondition offhand;
    private final EntityAction action;

    public ActionOnEquipPower(BaseSettings settings, ItemCondition head, ItemCondition chest, ItemCondition legs,
                              ItemCondition feet, ItemCondition offhand, EntityAction action) {
        super(settings);
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
        this.offhand = offhand;
        this.action = action;
    }

    public ItemCondition head() { return this.head; }
    public ItemCondition chest() { return this.chest; }
    public ItemCondition legs() { return this.legs; }
    public ItemCondition feet() { return this.feet; }
    public ItemCondition offhand() { return this.offhand; }
    public EntityAction action() { return this.action; }

    @Override
    public @NotNull MapCodec<? extends Power> codec() { return CODEC; }

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Map<EquipmentSlot, ItemStack> previous = EQUIPMENT.computeIfAbsent(player.getUUID(), unused -> snapshot(player));
        for (EquipmentSlot slot : TRACKED_SLOTS) {
            ItemStack current = player.getItemBySlot(slot);
            if (ItemStack.matches(previous.get(slot), current)) continue;
            previous.put(slot, current.copy());
            PowerHelper.get(player).execute(ActionOnEquipPower.class,
                    power -> power.matches(player, slot, current), (holder, power) -> power.action.execute(player));
        }
    }

    private boolean matches(ServerPlayer player, EquipmentSlot slot, ItemStack stack) {
        ItemCondition condition = switch (slot) {
            case HEAD -> this.head;
            case CHEST -> this.chest;
            case LEGS -> this.legs;
            case FEET -> this.feet;
            case OFFHAND -> this.offhand;
            default -> throw new IllegalArgumentException("Untracked equipment slot: " + slot);
        };
        return condition.test(player.level(), stack);
    }

    private static Map<EquipmentSlot, ItemStack> snapshot(ServerPlayer player) {
        Map<EquipmentSlot, ItemStack> snapshot = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : TRACKED_SLOTS) snapshot.put(slot, player.getItemBySlot(slot).copy());
        return snapshot;
    }
}
