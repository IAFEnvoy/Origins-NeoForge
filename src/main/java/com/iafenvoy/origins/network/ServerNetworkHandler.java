package com.iafenvoy.origins.network;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.network.payload.ChooseOriginC2SPayload;
import com.iafenvoy.origins.network.payload.ConfirmOriginS2CPayload;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Optional;

public final class ServerNetworkHandler {
    public static void onChooseOrigin(ChooseOriginC2SPayload packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        EntityOriginAttachment component = EntityOriginAttachment.get(player);
        Holder<Layer> layer = packet.layer();

        if (component.hasOrigin(layer)) {
            Origins.LOGGER.warn("Player {} tried to choose origin for layer \"{}\" while having one already.", player.getName().getString(), id(layer));
            return;
        }

        Optional<Holder<Origin>> optional = packet.origin();

        if (optional.isPresent()) {
            Holder<Origin> origin = optional.get();
            if (origin.value().unchoosable() || !origin.is(layer.value().origins())) {
                Origins.LOGGER.warn("Player {} tried to choose unchoosable origin \"{}\" from layer \"{}\"!", player.getName().getString(), id(origin), id(layer));
                component.clearOrigin(layer, player);
            } else {
                component.setOrigin(layer, origin, player);
                Origins.LOGGER.info("Player {} chose origin \"{}\" for layer \"{}\"", player.getName().getString(), id(origin), id(layer));

            }
        } else {
            List<Holder<Origin>> randomOriginIds = layer.value().collectRandomizableOrigins(player.registryAccess()).toList();
            if (!layer.value().allowRandom() || randomOriginIds.isEmpty()) {
                Origins.LOGGER.warn("Player {} tried to choose a random origin for layer \"{}\", which is not allowed!", player.getName().getString(), id(layer));
                component.clearOrigin(layer, player);
            } else {
                Holder<Origin> origin = randomOriginIds.get(player.getRandom().nextInt(randomOriginIds.size()));
                component.setOrigin(layer, origin, player);
                Origins.LOGGER.info("Player {} was randomly assigned the following origin: {}", player.getName().getString(), id(origin));
            }
        }
        PacketDistributor.sendToPlayer(player, new ConfirmOriginS2CPayload(layer, component.getOrigin(layer)));
        component.setSelecting(false);
        component.sync(player);
    }

    public static String id(Holder<?> holder) {
        return holder.unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).orElse("???");
    }
}
