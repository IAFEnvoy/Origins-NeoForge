package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data._common.EffectEntry;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.mixin.accessor.MobEffectInstanceAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@EventBusSubscriber
public class PermanentEffectPower extends Power {
    public static final MapCodec<PermanentEffectPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EffectEntry.LIST_CODEC.fieldOf("effect").forGetter(PermanentEffectPower::getEffect),
            Codec.BOOL.optionalFieldOf("allow_higher_level", false).forGetter(PermanentEffectPower::allowHigher)
    ).apply(i, PermanentEffectPower::new));
    private final List<EffectEntry> effect;
    private final boolean allowHigherLevel;

    public PermanentEffectPower(BaseSettings settings, List<EffectEntry> effect, boolean allowHigherLevel) {
        super(settings);
        this.effect = effect;
        this.allowHigherLevel = allowHigherLevel;
    }

    public List<EffectEntry> getEffect() {
        return this.effect;
    }

    public boolean allowHigher() {
        return this.allowHigherLevel;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    // ==================== 激活 / 取消 / 重生 ====================

    @Override
    public void active(@NotNull OriginDataHolder holder) {
        super.active(holder);
        if (holder.getEntity() instanceof LivingEntity living)
            this.effect.stream().map(e -> e.create(-1)).forEach(living::addEffect);
    }

    @Override
    public void inactive(@NotNull OriginDataHolder holder) {
        super.inactive(holder);
        if (holder.getEntity() instanceof LivingEntity living)
            this.effect.stream().map(EffectEntry::effect).forEach(living::removeEffect);
    }

    @Override
    public void respawn(OriginDataHolder holder, boolean backFromEnd) {
        // 死后重生重新给一遍防止失效（末地传送门除外）
        if (!backFromEnd) this.active(holder);
    }

    // ==================== 每 tick 检查补回 ====================

    @Override
    public void activeTick(OriginDataHolder holder) {
        super.activeTick(holder);
        if (holder.getEntity() instanceof LivingEntity living)
            for (EffectEntry entry : this.effect) {
                MobEffectInstance instance = living.getEffect(entry.effect());
                if (instance == null) {
                    // 完全缺失 → 补上
                    living.addEffect(entry.create(-1));
                } else if (!this.allowHigherLevel) {
                    // 不允许覆盖：只要不是完全匹配，一律删掉补回
                    if (instance.isInfiniteDuration() && instance.getAmplifier() == entry.amplifier()) continue;
                    living.removeEffect(entry.effect());
                    living.addEffect(entry.create(-1));
                } else {
                    // 允许更高等级
                    if (instance.isInfiniteDuration()) {
                        // 无限时长且等级 ≥ entry → 保留（可能是更高等级的永久效果）
                        if (instance.getAmplifier() >= entry.amplifier()) continue;
                        // 无限时长但等级更低 → 删掉补回
                        living.removeEffect(entry.effect());
                        living.addEffect(entry.create(-1));
                    } else {
                        // 有限时长 → 被更高等级暂时覆盖，递归检查 hiddenEffect 链
                        if (!checkHiddenEffectChain(instance, entry)) {
                            // 链中没有匹配的永久效果 → 强制覆盖
                            living.removeEffect(entry.effect());
                            living.addEffect(entry.create(-1));
                        }
                        // 链中有 → 等待自动恢复
                    }
                }
            }
    }

    /**
     * 递归检查 hiddenEffect 链，看是否能最终还原回 entry 指定的永久效果（无限时长 + 等级匹配）。
     *
     * @return true 表示链中存在匹配的永久效果，可以等待自动恢复；false 表示需要强制覆盖
     */
    private static boolean checkHiddenEffectChain(@NotNull MobEffectInstance instance, EffectEntry entry) {
        MobEffectInstance next = ((MobEffectInstanceAccessor) instance).getHiddenEffect();
        while (next != null) {
            if (next.isInfiniteDuration() && next.getAmplifier() == entry.amplifier()
                    && Objects.equals(next.getEffect(), entry.effect()))
                return true;
            next = ((MobEffectInstanceAccessor) next).getHiddenEffect();
        }
        return false;
    }

    @Override
    public int tickInterval() {
        return 20;
    }

    // ==================== 添加效果事件：控制哪些效果可以放行 ====================

    @SubscribeEvent
    public static void handleReplace(MobEffectEvent.Applicable event) {
        MobEffectInstance instance = event.getEffectInstance();
        LivingEntity entity = event.getEntity();
        List<PermanentEffectPower> powers = PowerHelper.get(entity).listActive(PermanentEffectPower.class,
                p -> p.effect.stream().anyMatch(e -> Objects.equals(e.effect(), instance.getEffect())));
        if (powers.isEmpty()) return;

        // 放行1：同等级无限时长（power 自身施加/补回）
        if (instance.isInfiniteDuration()
                && powers.stream().anyMatch(p -> p.effect.stream().anyMatch(e -> e.amplifier() == instance.getAmplifier())))
            return;

        // 放行2：更高等级 + allowHigher=true
        MobEffectInstance existing = entity.getEffect(instance.getEffect());
        if (existing != null
                && instance.getAmplifier() > existing.getAmplifier()
                && powers.stream().anyMatch(PermanentEffectPower::allowHigher))
            return;

        // 其余一律拒绝
        event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
    }

    // ==================== 移除效果事件：保护 power 添加的效果 ====================

    @SubscribeEvent
    public static void handleRemove(MobEffectEvent.Remove event) {
        MobEffectInstance instance = event.getEffectInstance();
        // 只有"无限时长 + 等级匹配"的才视为 power 添加的效果，禁止移除
        if (instance != null && instance.isInfiniteDuration()
                && PowerHelper.get(event.getEntity()).anyActive(PermanentEffectPower.class,
                p -> p.effect.stream().anyMatch(e -> Objects.equals(e.effect(), event.getEffect())
                        && e.amplifier() == instance.getAmplifier())))
            event.setCanceled(true);
    }
}