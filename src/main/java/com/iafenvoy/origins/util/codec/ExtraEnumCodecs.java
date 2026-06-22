package com.iafenvoy.origins.util.codec;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;

import java.util.Locale;

public final class ExtraEnumCodecs {
    public static final Codec<Dist> DIST = enumCodec(Dist.class);
    public static final Codec<SoundSource> SOUND_SOURCE = enumCodec(SoundSource.class);
    public static final Codec<ClipContext.Block> CLIP_CONTEXT_BLOCK = enumCodec(ClipContext.Block.class);
    public static final Codec<ClipContext.Fluid> CLIP_CONTEXT_FLUID = enumCodec(ClipContext.Fluid.class);
    public static final Codec<LightLayer> LIGHT_LAYER = enumCodec(LightLayer.class);
    public static final Codec<InteractionHand> HAND = enumCodec(InteractionHand.class);
    public static final Codec<InteractionResult> INTERACTION_RESULT = Codec.STRING.xmap(ExtraEnumCodecs::interactionResult, ExtraEnumCodecs::interactionResultName);
    public static final Codec<FogType> FOG_TYPE = enumCodec(FogType.class);
    public static final Codec<Direction.Axis> AXIS = enumCodec(Direction.Axis.class);
    public static final Codec<ItemUseAnimation> USE_ANIM = enumCodec(ItemUseAnimation.class);
    public static final Codec<ClickAction> CLICK_ACTION = enumCodec(ClickAction.class);

    public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> clazz) {
        return Codec.stringResolver(x -> x.name().toLowerCase(Locale.ROOT), x -> Enum.valueOf(clazz, x.toUpperCase(Locale.ROOT)));
    }

    private static InteractionResult interactionResult(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "success" -> InteractionResult.SUCCESS;
            case "success_server" -> InteractionResult.SUCCESS_SERVER;
            case "consume", "consume_partial" -> InteractionResult.CONSUME;
            case "fail" -> InteractionResult.FAIL;
            case "try_with_empty_hand" -> InteractionResult.TRY_WITH_EMPTY_HAND;
            default -> InteractionResult.PASS;
        };
    }

    private static String interactionResultName(InteractionResult result) {
        if (result.equals(InteractionResult.SUCCESS)) return "success";
        if (result.equals(InteractionResult.SUCCESS_SERVER)) return "success_server";
        if (result.equals(InteractionResult.CONSUME)) return "consume";
        if (result.equals(InteractionResult.FAIL)) return "fail";
        if (result.equals(InteractionResult.TRY_WITH_EMPTY_HAND)) return "try_with_empty_hand";
        return "pass";
    }
}
