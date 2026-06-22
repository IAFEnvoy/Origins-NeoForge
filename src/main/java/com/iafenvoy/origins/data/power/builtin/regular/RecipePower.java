package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data._common.helper.RecipeHelper;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.data.power.reference.PowerReference;
import com.iafenvoy.origins.event.GrantPowerEvent;
import com.iafenvoy.origins.event.RevokePowerEvent;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// 实际的注入发生在 RecipeManagerMixin#origins$injectPowerRecipes 中，
// 它会用每个已加载的 RecipePower 对应一个 PowerCraftingRecipe 来重建（不可变的，26.1）RecipeMap。
// 该包装器仅在合成玩家拥有此能量激活时才匹配，因此配方专属于授予该能量的起源。
@EventBusSubscriber
public class RecipePower extends Power implements Prioritized, RecipeHelper {
    public static final MapCodec<RecipePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            MiscCodecs.DATAPACK_RECIPES_ONLY_CODEC.fieldOf("recipe").forGetter(RecipePower::getRecipe),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(RecipePower::getPriority)
    ).apply(i, RecipePower::new));
    private final CraftingRecipe recipe;
    private final int priority;

    public RecipePower(BaseSettings settings, CraftingRecipe recipe, int priority) {
        super(settings);
        this.recipe = recipe;
        this.priority = priority;
    }

    @Override
    public CraftingRecipe getRecipe() {
        return this.recipe;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) syncRecipeBook(player);
    }

    @SubscribeEvent
    public static void onPowerGranted(GrantPowerEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) syncRecipeBook(player);
    }

    @SubscribeEvent
    public static void onPowerRevoked(RevokePowerEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) syncRecipeBook(player);
    }

    private static void syncRecipeBook(ServerPlayer player) {
        OriginDataHolder data = OriginDataHolder.get(player);
        PowerReference.listAllPowers(player.registryAccess())
                .filter(holder -> holder.power() instanceof RecipePower)
                .forEach(holder -> {
                    ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, holder.id());
                    RecipeHolder<?> recipe = player.level().getServer().getRecipeManager().byKey(key).orElse(null);
                    if (recipe == null) return;

                    boolean active = data.hasActivePower(holder.id(), RecipePower.class);
                    boolean known = player.getRecipeBook().contains(key);
                    if (active && !known) player.awardRecipes(List.of(recipe));
                    else if (!active && known) player.resetRecipes(List.of(recipe));
                });
    }
}
