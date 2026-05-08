package com.iafenvoy.origins.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import com.iafenvoy.origins.util.codec.CollectionCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record ItemPowersComponent(Multimap<EquipmentSlotGroup, Entry> powers) implements TooltipProvider {
    public static final Codec<ItemPowersComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            CollectionCodecs.multiMapCodec(EquipmentSlotGroup.CODEC, Entry.CODEC).fieldOf("powers").forGetter(ItemPowersComponent::powers)
    ).apply(i, ItemPowersComponent::new));
    public static final ItemPowersComponent EMPTY = new ItemPowersComponent(HashMultimap.create());

    @Override
    public void addToTooltip(Item.@NotNull TooltipContext context, @NotNull Consumer<Component> consumer, @NotNull TooltipFlag flag) {
        //TODO
    }

    public boolean isEmpty() {
        return this.powers.isEmpty();
    }

    public boolean contains(EquipmentSlot slot, Holder<Power> power) {
        return this.powers.entries().stream().filter(e -> e.getKey().test(slot)).anyMatch(e -> Objects.equals(e.getValue().power(), power));
    }

    public List<Entry> get(EquipmentSlot slot) {
        return this.powers.entries().stream().filter(e -> e.getKey().test(slot)).map(Map.Entry::getValue).toList();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Multimap<EquipmentSlotGroup, Entry> powers = HashMultimap.create();

        public Builder add(ItemStack stack) {
            return this.add(stack.getOrDefault(OriginsDataComponents.ITEM_POWERS, EMPTY));
        }

        public Builder add(ItemPowersComponent component) {
            this.powers.putAll(component.powers);
            return this;
        }

        public Builder add(EquipmentSlotGroup slot, Holder<Power> power, boolean hidden, boolean negative) {
            this.powers.put(slot, new Entry(power, hidden, negative));
            return this;
        }

        public Builder add(Iterable<EquipmentSlotGroup> slots, Holder<Power> power, boolean hidden, boolean negative) {
            slots.forEach(slot -> this.add(slot, power, hidden, negative));
            return this;
        }

        public Builder remove(EquipmentSlotGroup slot, Holder<Power> power, BiConsumer<EquipmentSlotGroup, Entry> callback) {
            for (Entry entry : this.powers.get(slot).stream().filter(e1 -> e1.power().equals(power)).toList()) {
                this.powers.remove(slot, entry);
                callback.accept(slot, entry);
            }
            return this;
        }

        public Builder remove(Iterable<EquipmentSlotGroup> slots, Holder<Power> power, BiConsumer<EquipmentSlotGroup, Entry> callback) {
            slots.forEach(slot -> this.remove(slot, power, callback));
            return this;
        }

        public ItemPowersComponent build() {
            return new ItemPowersComponent(this.powers);
        }
    }

    public record Entry(Holder<Power> power, boolean hidden, boolean negative) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Power.CODEC.fieldOf("power").forGetter(Entry::power),
                Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Entry::hidden),
                Codec.BOOL.optionalFieldOf("negative", false).forGetter(Entry::negative)
        ).apply(i, Entry::new));
    }
}
