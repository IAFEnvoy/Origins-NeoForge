package com.iafenvoy.origins.mixin.loot;

import com.iafenvoy.origins.accessor.LootContextTypeHolder;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(LootParams.class)
public abstract class LootParamsMixin implements LootContextTypeHolder {
    @Unique
    private LootContextParamSet origins$contextType;

    @Override
    public LootContextParamSet origins$getType() {
        return Objects.requireNonNull(this.origins$contextType, "Loot context parameters are not initialized properly!");
    }

    @Override
    public void origins$setType(LootContextParamSet type) {
        this.origins$contextType = type;
    }
}
