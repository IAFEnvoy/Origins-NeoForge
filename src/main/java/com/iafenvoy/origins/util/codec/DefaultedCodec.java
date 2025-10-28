package com.iafenvoy.origins.util.codec;

import com.iafenvoy.origins.Constants;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import org.slf4j.Logger;

import java.util.function.Function;
import java.util.function.Supplier;

//This codec will handle decode phase so that game won't crash when loading failed
public class DefaultedCodec<A> implements Codec<A> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Codec<A> baseCodec;
    private final Supplier<A> defaultValue;
    private final String name;

    public DefaultedCodec(Codec<A> baseCodec, Supplier<A> defaultValue, String name) {
        this.baseCodec = baseCodec;
        this.defaultValue = defaultValue;
        this.name = name;
    }

    public static <T, A> DefaultedCodec<T> registryDispatch(Registry<A> registry, String typeKey, Function<? super T, ? extends A> type, Function<? super A, ? extends MapCodec<? extends T>> codec, Supplier<T> defaultValue) {
        return new DefaultedCodec<>(registry.byNameCodec().dispatch(typeKey, type, codec), defaultValue, registry.key().location().toString());
    }

    public static <T, A> DefaultedCodec<T> registryDispatch(Registry<A> registry, Function<? super T, ? extends A> type, Function<? super A, ? extends MapCodec<? extends T>> codec, Supplier<T> defaultValue) {
        return registryDispatch(registry, Constants.TYPE_KEY, type, codec, defaultValue);
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<Pair<A, T>> result = this.baseCodec.decode(ops, input);
        if (result instanceof DataResult.Error<Pair<A, T>> error) {
            LOGGER.error("Failed to decode {}", this.name, new IllegalStateException(error.message()));
            result = DataResult.success(new Pair<>(this.defaultValue.get(), input));
        }
        return result;
    }

    @Override
    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        DataResult<T> result = this.baseCodec.encode(input, ops, prefix);
        if (result instanceof DataResult.Error<T> error) {
            LOGGER.error("Failed to encode {}", this.name, new IllegalStateException(error.message()));
            result = DataResult.success(prefix);
        }
        return result;
    }
}
