package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.fog.FogData;
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
    //修复::Use NotCondition instead of blacklist
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

    @SuppressWarnings("ConstantValue")
    public static boolean shouldPhaseThrough(Entity entity, Level level, BlockPos pos, boolean isAbove) {
        if (entity instanceof ServerPlayer player && player.connection == null)
            return false;// 修复玩家连接时崩溃的问题
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
        return this.blacklist ^ this.blockCondition.test(level, pos);
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
            if (OriginDataHolder.get(event.getPlayer()).hasActivePower(PhasingPower.class))
                event.setCanceled(true);
        }

        // 26.1 移植: 墙内致盲雾效果。旧的 RenderSystem.setShaderFog* 路径已被移除，
        // 但 NeoForge 的 ViewportEvent 仍然暴露了近/远平面和颜色设置器，
        // 因此通过这些重新实现了该效果。当摄像机实体以 BLINDNESS 渲染类型穿墙
        // 且其眼睛位于阻挡视线的方块内时，将雾拉近到 view_distance 并染成黑色以遮挡视野。
        @SubscribeEvent
        public static void renderFog(ViewportEvent.RenderFog event) {
            if (!(Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living)) return;
            if (getInWallBlockState(living) == null) return;
            getRenderMethod(living, PhasingRenderType.BLINDNESS).ifPresent(viewDistance -> {
                // 拉入所有雾通道（环境 + 渲染距离 + 天空/云）。仅设置环境
                // 近/远会使渲染距离雾保持在最大范围，因此地形保持完全可见且不显示雾霾。
                FogData data = event.getFogData();
                float start = viewDistance * 0.25F;
                data.environmentalStart = start;
                data.environmentalEnd = viewDistance;
                data.renderDistanceStart = start;
                data.renderDistanceEnd = viewDistance;
                data.skyEnd = viewDistance;
                data.cloudEnd = viewDistance;
            });
        }

        @SubscribeEvent
        public static void fogColor(ViewportEvent.ComputeFogColor event) {
            if (!(Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living)) return;
            if (getInWallBlockState(living) == null) return;
            if (hasRenderMethod(living, PhasingRenderType.BLINDNESS)) {
                event.setRed(0.0F);
                event.setGreen(0.0F);
                event.setBlue(0.0F);
            }
        }
    }
}
