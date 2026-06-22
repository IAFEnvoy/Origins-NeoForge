package com.iafenvoy.origins.util.codec;

import com.iafenvoy.origins.data._common.AttributeEntry;
import com.iafenvoy.origins.data._common.PositionedItemStackSettings;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

//这些 codec 可以识别单体形式和数组形式
public final class CombinedCodecs {
    public static final Codec<IntList> INT = combineCodec(Codec.INT).xmap(IntImmutableList::new, List::copyOf);
    public static final Codec<List<String>> STRING = combineCodec(Codec.STRING);
    public static final Codec<List<Holder<MobEffect>>> MOB_EFFECT = combineCodec(MobEffect.CODEC);
    public static final Codec<List<MobEffectInstance>> MOB_EFFECT_INSTANCE = combineCodec(MobEffectInstance.CODEC);
    public static final Codec<List<Holder<Enchantment>>> ENCHANTMENT = combineCodec(Enchantment.CODEC);
    public static final Codec<List<Component>> TEXT = combineCodec(MiscCodecs.TRANSLATE_FIRST);
    public static final Codec<List<Holder<Biome>>> BIOME = combineCodec(Biome.CODEC);
    public static final Codec<List<Holder<GameEvent>>> GAME_EVENT = combineCodec(GameEvent.CODEC);
    public static final Codec<List<Modifier>> MODIFIER = combineCodec(Modifier.CODEC);
    public static final Codec<List<AttributeEntry>> ATTRIBUTE = combineCodec(AttributeEntry.CODEC);
    public static final Codec<List<AttributeModifier>> ATTRIBUTE_MODIFIER = combineCodec(AttributeModifier.CODEC);
    public static final Codec<List<PositionedItemStackSettings>> POSITIONED_ITEM_STACK = combineCodec(PositionedItemStackSettings.COMBINED_CODEC);
    public static final Codec<List<EquipmentSlotGroup>> EQUIPMENT_SLOT_GROUP = combineCodec(EquipmentSlotGroup.CODEC);

    public static <T> Codec<List<T>> combineCodec(Codec<T> codec) {
        return Codec.either(codec, codec.listOf()).xmap(x -> x.map(List::of, l -> l), l -> l.size() == 1 ? Either.left(l.getFirst()) : Either.right(l));
    }
}
