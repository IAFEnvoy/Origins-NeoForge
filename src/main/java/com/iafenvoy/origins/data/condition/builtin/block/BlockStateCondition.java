package com.iafenvoy.origins.data.condition.builtin.block;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public record BlockStateCondition(String property, Optional<Comparison> comparison, Optional<Boolean> booleanValue,
                                  Optional<String> stringValue) implements BlockCondition {
    public static final MapCodec<BlockStateCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("property").forGetter(BlockStateCondition::property),
            Comparison.OPTIONAL_CODEC.forGetter(BlockStateCondition::comparison),
            Codec.BOOL.optionalFieldOf("value").forGetter(BlockStateCondition::booleanValue),
            Codec.STRING.optionalFieldOf("enum").forGetter(BlockStateCondition::stringValue)
    ).apply(instance, BlockStateCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Collection<Property<?>> properties = state.getProperties();
        return properties.stream().filter(p -> this.property().equals(p.getName())).findFirst().map(property -> this.checkProperty(state.getValue(property))).orElse(false);
    }

    public boolean checkProperty(Object value) {
        boolean flag = false;
        if (this.stringValue().isPresent()) {
            if (value instanceof Enum<?> enumValue)
                flag = enumValue.name().equalsIgnoreCase(this.stringValue().get());
            if (value instanceof StringRepresentable stringIdentifiable)
                flag |= stringIdentifiable.getSerializedName().equalsIgnoreCase(this.stringValue().get());
        }
        if (this.booleanValue().isPresent() && value instanceof Boolean bool)
            return bool.booleanValue() == this.booleanValue().get();
        if (this.comparison().isPresent() && value instanceof Integer intValue)
            return this.comparison().get().compare(intValue);
        return flag;
    }
}
