package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data._common.helper.ResourceValueHelper;
import com.iafenvoy.origins.util.math.MathExpression;
import com.iafenvoy.origins.util.math.ResourceOperation;
import com.iafenvoy.origins.util.math.VariableSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class VariableChangeResourceAction implements EntityAction {
    public static final MapCodec<VariableChangeResourceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("resource").forGetter(VariableChangeResourceAction::resource),
            Codec.STRING.fieldOf("expression").forGetter(VariableChangeResourceAction::expression),
            VariableSerializer.CODEC.optionalFieldOf("variables", new VariableSerializer(Map.of())).forGetter(VariableChangeResourceAction::variables),
            ResourceOperation.CODEC.optionalFieldOf("operation", ResourceOperation.ADD).forGetter(VariableChangeResourceAction::operation)
    ).apply(i, VariableChangeResourceAction::new));
    private final ResourceLocation resource;
    private final String expression;
    private final VariableSerializer variables;
    private final ResourceOperation operation;
    private final MathExpression parsedExpression;

    public VariableChangeResourceAction(ResourceLocation resource, String expression, VariableSerializer variables, ResourceOperation operation) {
        this.resource = resource;
        this.expression = expression;
        this.variables = variables;
        this.operation = operation;
        this.parsedExpression = new MathExpression(expression, variables.variables().keySet());
    }

    public ResourceLocation resource() {
        return this.resource;
    }

    public String expression() {
        return this.expression;
    }

    public VariableSerializer variables() {
        return this.variables;
    }

    public ResourceOperation operation() {
        return this.operation;
    }

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (!(source instanceof LivingEntity)) return;
        double change = this.parsedExpression.evaluate(source, this.variables);
        if (this.operation == ResourceOperation.ADD)
            ResourceValueHelper.add(source, this.resource, change);
        else
            ResourceValueHelper.set(source, this.resource, change);
    }
}
