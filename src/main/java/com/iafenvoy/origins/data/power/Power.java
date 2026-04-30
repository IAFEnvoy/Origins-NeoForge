package com.iafenvoy.origins.data.power;

import com.google.common.collect.ImmutableSet;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.builtin.ActiveComponent;
import com.iafenvoy.origins.util.annotation.Comment;
import com.iafenvoy.origins.util.codec.ComponentCodec;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class Power {
    public static final Codec<Power> DIRECT_CODEC = DefaultedCodec.registryDispatch(PowerRegistries.POWER_TYPE, Power::codec, Function.identity(), Power::createEmpty);
    public static final Codec<Holder<Power>> CODEC = RegistryFixedCodec.create(PowerRegistries.POWER_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Power>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PowerRegistries.POWER_KEY);
    private final BaseSettings settings;

    private static Power createEmpty() {
        return new EmptyPower();
    }

    protected Power(BaseSettings settings) {
        this.settings = settings;
    }

    public BaseSettings getSettings() {
        return this.settings;
    }

    @NotNull
    public abstract MapCodec<? extends Power> codec();

    @Comment("Only one class each is allowed")
    public void createComponents(ComponentCollector collector) {
        collector.add(new ActiveComponent(false));
    }

    public void collectBadges(ImmutableSet.Builder<Badge> builder) {
        this.settings.badges().stream().map(Holder::value).forEach(builder::add);
    }

    public boolean isActive(OriginDataHolder holder) {
        return this.settings.condition().test(holder.getEntity());
    }

    public void grant(@NotNull OriginDataHolder holder) {
    }

    public void revoke(@NotNull OriginDataHolder holder) {
        if (this.isActive(holder)) this.inactive(holder);
    }

    public void active(@NotNull OriginDataHolder holder) {
    }

    public void inactive(@NotNull OriginDataHolder holder) {
    }

    public void tick(@NotNull OriginDataHolder holder) {
        holder.getComponentFor(this, ActiveComponent.class).ifPresent(x -> x.tick(holder, this));
    }

    public ResourceLocation getId(RegistryAccess access) {
        return access.registryOrThrow(PowerRegistries.POWER_KEY).getKey(this);
    }

    public MutableComponent getName(RegistryAccess access) {
        return this.settings.name().map(Component::copy).orElse(Component.translatable(this.getId(access).toLanguageKey("power", "name")));
    }

    public MutableComponent getDescription(RegistryAccess access) {
        return this.settings.description().map(Component::copy).orElse(Component.translatable(this.getId(access).toLanguageKey("power", "description")));
    }

    public record BaseSettings(Optional<Component> name, Optional<Component> description, boolean hidden,
                               EntityCondition condition, int loadingPriority, List<Holder<Badge>> badges) {
        public static final MapCodec<BaseSettings> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ComponentCodec.TRANSLATE_FIRST.optionalFieldOf("name").forGetter(BaseSettings::name),
                ComponentCodec.TRANSLATE_FIRST.optionalFieldOf("description").forGetter(BaseSettings::description),
                Codec.BOOL.optionalFieldOf("hidden", false).forGetter(BaseSettings::hidden),
                EntityCondition.optionalCodec("condition").forGetter(BaseSettings::condition),
                Codec.INT.optionalFieldOf("loading_priority", 0).forGetter(BaseSettings::loadingPriority),
                Badge.CODEC.listOf().optionalFieldOf("badges", List.of()).forGetter(BaseSettings::badges)
        ).apply(i, BaseSettings::new));
    }
}
