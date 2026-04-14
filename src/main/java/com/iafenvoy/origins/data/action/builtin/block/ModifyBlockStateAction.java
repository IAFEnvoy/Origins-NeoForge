package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.util.codec.OptionalCodecs;
import com.iafenvoy.origins.util.math.ResourceOperation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.OptionalInt;

public record ModifyBlockStateAction(String property, ResourceOperation operation, OptionalInt change,
                                     Optional<Boolean> value, Optional<String> enumValue,
                                     boolean cycle) implements BlockAction {
    public static final MapCodec<ModifyBlockStateAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("property").forGetter(ModifyBlockStateAction::property),
            ResourceOperation.CODEC.optionalFieldOf("operation", ResourceOperation.ADD).forGetter(ModifyBlockStateAction::operation),
            OptionalCodecs.integer("change").forGetter(ModifyBlockStateAction::change),
            Codec.BOOL.optionalFieldOf("value").forGetter(ModifyBlockStateAction::value),
            Codec.STRING.optionalFieldOf("enum").forGetter(ModifyBlockStateAction::enumValue),
            Codec.BOOL.optionalFieldOf("change", false).forGetter(ModifyBlockStateAction::cycle)
    ).apply(instance, ModifyBlockStateAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        BlockState state = level.getBlockState(pos);
        Collection<Property<?>> properties = state.getProperties();
        String desiredPropertyName = this.property();
        Property<?> property = null;
        for (Property<?> p : properties) {
            if (p.getName().equals(desiredPropertyName)) {
                property = p;
                break;
            }
        }
        if (property != null) {
            if (this.cycle()) {
                level.setBlockAndUpdate(pos, state.cycle(property));
            } else {
                Object value = state.getValue(property);
                switch (value) {
                    case Enum<?> ignored when this.enumValue().isPresent() ->
                            modifyEnumState(level, pos, state, property, this.enumValue().get());
                    case Boolean ignored when this.value().isPresent() ->
                            level.setBlockAndUpdate(pos, state.setValue((Property<Boolean>) property, this.value().get()));
                    case Integer ignored when this.change().isPresent() -> {
                        ResourceOperation op = this.operation();
                        int opValue = this.change().getAsInt();
                        int newValue = (int) value;
                        switch (op) {
                            case ADD -> newValue += opValue;
                            case SET -> newValue = opValue;
                        }
                        Property<Integer> integerProperty = (Property<Integer>) property;
                        if (integerProperty.getPossibleValues().contains(newValue)) {
                            level.setBlockAndUpdate(pos, state.setValue(integerProperty, newValue));
                        }
                    }
                    default -> {
                    }
                }
            }
        }
    }

    private static <T extends Comparable<T>> void modifyEnumState(Level world, BlockPos pos, BlockState originalState, Property<T> property, String value) {
        Optional<T> enumValue = property.getValue(value);
        enumValue.ifPresent(v -> world.setBlockAndUpdate(pos, originalState.setValue(property, v)));
    }
}
