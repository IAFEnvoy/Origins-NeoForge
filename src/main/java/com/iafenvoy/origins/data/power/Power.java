package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.util.annotation.Comment;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
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

    @NotNull
    public abstract MapCodec<? extends Power> codec();

    public BaseSettings getSettings() {
        return this.settings;
    }

    @Comment("Only one class each is allowed")
    public void createComponents(ComponentCollector collector) {
    }

    public boolean isActive(OriginDataHolder holder) {
        return this.getSettings().condition().test(holder.entity());
    }

    @Comment("Call after grant, server side only")
    public void grant(@NotNull Entity entity) {
    }

    @Comment("Call after revoke, server side only")
    public void revoke(@NotNull Entity entity) {
    }

    public void tick(@NotNull Entity entity) {
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
                               EntityCondition condition, int loadingPriority, List<Badge> badges) {
        public static final MapCodec<BaseSettings> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(BaseSettings::name),
                ComponentSerialization.CODEC.optionalFieldOf("description").forGetter(BaseSettings::description),
                Codec.BOOL.optionalFieldOf("hidden", false).forGetter(BaseSettings::hidden),
                EntityCondition.optionalCodec("condition").forGetter(BaseSettings::condition),
                Codec.INT.optionalFieldOf("loading_priority", 0).forGetter(BaseSettings::loadingPriority),
                Badge.CODEC.listOf().optionalFieldOf("badges", List.of()).forGetter(BaseSettings::badges)
        ).apply(i, BaseSettings::new));
    }
}
