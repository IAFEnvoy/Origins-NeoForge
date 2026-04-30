package com.iafenvoy.origins.util.codec;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;

import java.util.Locale;
import java.util.function.Function;

public final class ExtraEnumCodecs {
    public static final Codec<Dist> DIST = enumCodec(Dist::valueOf);
    public static final Codec<SoundSource> SOUND_SOURCE = enumCodec(SoundSource::valueOf);
    public static final Codec<ClipContext.Block> CLIP_CONTEXT_BLOCK = enumCodec(ClipContext.Block::valueOf);
    public static final Codec<ClipContext.Fluid> CLIP_CONTEXT_FLUID = enumCodec(ClipContext.Fluid::valueOf);
    public static final Codec<LightLayer> LIGHT_LAYER = enumCodec(LightLayer::valueOf);
    public static final Codec<InteractionHand> HAND = enumCodec(InteractionHand::valueOf);
    public static final Codec<InteractionResult> INTERACTION_RESULT = enumCodec(InteractionResult::valueOf);
    public static final Codec<FogType> FOG_TYPE = enumCodec(FogType::valueOf);
    public static final Codec<Direction.Axis> AXIS = enumCodec(Direction.Axis::valueOf);
    public static final Codec<UseAnim> USE_ANIM = enumCodec(UseAnim::valueOf);
    public static final Codec<GameEventListener.DeliveryMode> GAME_EVENT_DELIVERY_MODE = enumCodec(GameEventListener.DeliveryMode::valueOf);
    public static final Codec<ClickAction> CLICK_ACTION = enumCodec(ClickAction::valueOf);

    public static <T extends Enum<T>> Codec<T> enumCodec(Function<String, T> stringSolver) {//TODO::Class instead?
        return Codec.stringResolver(x -> x.name().toLowerCase(Locale.ROOT), x -> stringSolver.apply(x.toUpperCase(Locale.ROOT)));
    }
}
