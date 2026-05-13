package com.iafenvoy.origins.content;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.iafenvoy.origins.network.payload.OpenChooseOriginScreenS2CPayload;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class OrbOfOriginItem extends Item {
    public OrbOfOriginItem() {
        super(new Properties().stacksTo(16).rarity(Rarity.RARE).component(OriginsDataComponents.ORB_LAYERS, List.of()));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer) {
            List<Holder<Layer>> layers = stack.getOrDefault(OriginsDataComponents.ORB_LAYERS, List.of());
            if (layers.isEmpty()) openGuiForLayer(serverPlayer, null);
            else for (Holder<Layer> layer : layers) openGuiForLayer(serverPlayer, layer);
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    //FIXME::idk why but this can work
    public static void openGuiForLayer(ServerPlayer target, @Nullable Holder<Layer> layer) {
        OriginDataHolder holder = OriginDataHolder.get(target);
        List<Holder<Layer>> layers = new ObjectArrayList<>();

        if (layer == null) LayerRegistries.streamAvailableLayers(target.registryAccess()).forEach(layers::add);
        else layers.add(layer);

        layers.stream().filter(x -> x.value().enabled()).forEach(holder::clearOrigin);

        boolean automaticallyAssigned = holder.fillAutoChoosing();
        int options = Optional.ofNullable(layer)
                .map(l -> l.value().getOriginOptionCount(target.registryAccess()))
                .orElseGet(() -> OriginRegistries.streamAvailableOrigins(target.registryAccess()).toList().size());

        holder.getData().setSelecting(!automaticallyAssigned || options > 0);
        holder.sync();

        if (holder.getData().isSelecting())
            PacketDistributor.sendToPlayer(target, new OpenChooseOriginScreenS2CPayload(false));
    }
}
