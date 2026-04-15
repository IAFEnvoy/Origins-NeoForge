package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifyJumpPower extends Power {
    public static final MapCodec<ModifyJumpPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyJumpPower::getModifiers),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyJumpPower::getEntityAction)
    ).apply(i, ModifyJumpPower::new));
    private final List<Modifier> modifiers;
    private final EntityAction entityAction;

    public ModifyJumpPower(BaseSettings settings, List<Modifier> modifiers, EntityAction entityAction) {
        super(settings);
        this.modifiers = modifiers;
        this.entityAction = entityAction;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public double apply(double baseValue) {
        return Modifier.applyModifiers(this.getModifiers(), baseValue);
    }

    public void execute(Entity player) {
        this.getEntityAction().execute(player);
    }
}
