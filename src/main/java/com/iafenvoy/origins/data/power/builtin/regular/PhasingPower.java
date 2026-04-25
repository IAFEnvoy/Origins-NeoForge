package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public class PhasingPower extends Power {
    public static final MapCodec<PhasingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("blacklist", false).forGetter(PhasingPower::isBlacklist),
            BlockCondition.optionalCodec("block_condition").forGetter(PhasingPower::getBlockCondition),
            PhasingRenderType.CODEC.optionalFieldOf("render_type", PhasingRenderType.BLINDNESS).forGetter(PhasingPower::getRenderType),
            Codec.FLOAT.optionalFieldOf("view_distance", 10F).forGetter(PhasingPower::getViewDistance),
            EntityCondition.optionalCodec("phase_down_condition").forGetter(PhasingPower::getPhaseDownCondition)
    ).apply(i, PhasingPower::new));
    //FIXME::Use NotCondition instead of blacklist
    private final boolean blacklist;
    private final BlockCondition blockCondition;
    private final PhasingRenderType renderType;
    private final float viewDistance;
    private final EntityCondition phaseDownCondition;

    public PhasingPower(BaseSettings settings, boolean blacklist, BlockCondition blockCondition, PhasingRenderType renderType, float viewDistance, EntityCondition phaseDownCondition) {
        super(settings);
        this.blacklist = blacklist;
        this.blockCondition = blockCondition;
        this.renderType = renderType;
        this.viewDistance = viewDistance;
        this.phaseDownCondition = phaseDownCondition;
    }

    public boolean isBlacklist() {
        return this.blacklist;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public PhasingRenderType getRenderType() {
        return this.renderType;
    }

    public float getViewDistance() {
        return this.viewDistance;
    }

    public EntityCondition getPhaseDownCondition() {
        return this.phaseDownCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public static boolean shouldPhaseThrough(Entity entity, Level level, BlockPos pos, boolean isAbove) {
        if (entity instanceof ServerPlayer player && player.connection == null)
            return false;//FIXME::Fixes a crash when the player is connecting
        return OriginDataHolder.get(entity).streamActivePowers(PhasingPower.class).anyMatch(x -> (!isAbove || x.canPhaseDown(entity)) && x.canPhaseThrough(level, pos));
    }

    public static boolean shouldPhaseThrough(Entity entity, Level level, BlockPos pos) {
        return shouldPhaseThrough(entity, level, pos, false);
    }

    public static boolean shouldPhaseThrough(Entity entity, BlockPos pos) {
        return shouldPhaseThrough(entity, entity.level(), pos);
    }

    public static boolean hasRenderMethod(Entity entity, PhasingRenderType renderType) {
        return OriginDataHolder.get(entity).streamActivePowers(PhasingPower.class).anyMatch(x -> x.renderType == renderType);
    }

    public static Optional<Float> getRenderMethod(Entity entity, PhasingRenderType renderType) {
        return OriginDataHolder.get(entity).streamActivePowers(PhasingPower.class).filter(x -> x.renderType == renderType).map(x -> x.viewDistance).min(Float::compareTo);
    }

    public boolean canPhaseDown(Entity entity) {
        return this.phaseDownCondition == AlwaysTrueCondition.INSTANCE ? entity.isCrouching() : this.phaseDownCondition.test(entity);
    }

    public boolean canPhaseThrough(Level level, BlockPos pos) {
        return this.blacklist ^ this.getBlockCondition().test(level, pos);
    }

    public static BlockState getInWallBlockState(LivingEntity playerEntity) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 8; ++i) {
            double d = playerEntity.getX() + (double) (((float) (i % 2) - 0.5F) * playerEntity.getBbWidth() * 0.8F);
            double e = playerEntity.getEyeY() + (double) (((float) ((i >> 1) % 2) - 0.5F) * 0.1F);
            double f = playerEntity.getZ() + (double) (((float) ((i >> 2) % 2) - 0.5F) * playerEntity.getBbWidth() * 0.8F);
            mutable.set(d, e, f);
            BlockState blockState = playerEntity.level().getBlockState(mutable);
            if (blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.isViewBlocking(playerEntity.level(), mutable)) {
                return blockState;
            }
        }

        return null;
    }

    public enum PhasingRenderType implements StringRepresentable {
        BLINDNESS, REMOVE_BLOCKS, NONE;
        public static final Codec<PhasingRenderType> CODEC = StringRepresentable.fromValues(PhasingRenderType::values);

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onBlockOverlay(RenderBlockScreenEffectEvent event) {
            if (OriginDataHolder.get(event.getPlayer()).hasPower(PhasingPower.class, true))
                event.setCanceled(true);
        }

        //Replaces redirectFogStart & redirectFogEnd in BackgroundRendererMixin
        @SubscribeEvent
        public static void renderFog(ViewportEvent.RenderFog event) {
            if (event.getCamera().getEntity() instanceof LivingEntity living) {
                Optional<Float> renderMethod = PhasingPower.getRenderMethod(living, PhasingRenderType.BLINDNESS);
                if (renderMethod.isPresent() && getInWallBlockState(living) != null) {
                    float view = renderMethod.get();
                    float s;
                    float v;
                    if (event.getMode() == FogRenderer.FogMode.FOG_SKY) {
                        s = 0F;
                        v = view * 0.8F;
                    } else {
                        s = view * 0.25F;
                        v = view;
                    }
                    RenderSystem.setShaderFogStart(s);
                    RenderSystem.setShaderFogEnd(v);
                }
            }
        }

        //Replaces modifyD in BackgroundRendererMixin
        @SubscribeEvent
        public static void fogColor(ViewportEvent.ComputeFogColor event) {
            if (event.getCamera().getEntity() instanceof LivingEntity living && hasRenderMethod(living, PhasingRenderType.BLINDNESS) && getInWallBlockState(living) != null) {
                event.setBlue(0);
                event.setGreen(0);
                event.setRed(0);
            }
        }
    }
}
