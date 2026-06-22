package com.iafenvoy.origins.data.action.builtin.item;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.util.codec.WildcardCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

//修复::更新文档
public record ModifyAction(Identifier modifier) implements ItemAction {
    public static final MapCodec<ModifyAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WildcardCodec.INSTANCE.fieldOf("modifier").forGetter(ModifyAction::modifier)
    ).apply(i, ModifyAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity source, @NotNull SlotAccess access) {
        if (level instanceof ServerLevel serverLevel) {
            ResourceKey<LootItemFunction> key = ResourceKey.create(Registries.ITEM_MODIFIER, this.modifier);
            LootItemFunction function = serverLevel.getServer().reloadableRegistries().lookup().get(key).map(Holder::value).orElse(null);
            if (function == null) {
                Origins.LOGGER.error("Unknown item modifier: {}", this.modifier);
                return;
            }
            LootParams lootContextParameterSet = new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, new Vec3(0, 0, 0)).create(LootContextParamSets.COMMAND);
            LootContext lootContext = new LootContext.Builder(lootContextParameterSet).create(Optional.empty());
            access.set(function.apply(access.get(), lootContext));
        }
    }
}
