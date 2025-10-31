package com.iafenvoy.origins.network;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.network.payload.ChooseOriginC2SPayload;
import com.iafenvoy.origins.network.payload.ConfirmOriginS2CPayload;
import com.iafenvoy.origins.util.RLHelper;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Optional;

public final class ServerNetworkHandler {
    public static void onChooseOrigin(ChooseOriginC2SPayload packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        OriginDataHolder holder = OriginDataHolder.get(player);
        Holder<Layer> layer = packet.layer();
        if (holder.hasOrigin(layer)) {
            Origins.LOGGER.warn("Player {} tried to choose origin for layer \"{}\" while having one already.", player.getName().getString(), RLHelper.string(layer));
            return;
        }

        Optional<Holder<Origin>> optional = packet.origin();
        if (optional.isPresent()) {
            Holder<Origin> origin = optional.get();
            if (origin.value().unchoosable() || !origin.is(layer.value().origins())) {
                Origins.LOGGER.warn("Player {} tried to choose unchoosable origin \"{}\" from layer \"{}\"!", player.getName().getString(), RLHelper.string(origin), RLHelper.string(layer));
                holder.clearOrigin(layer);
            } else {
                holder.setOrigin(layer, origin);
                Origins.LOGGER.info("Player {} chose origin \"{}\" for layer \"{}\"", player.getName().getString(), RLHelper.string(origin), RLHelper.string(layer));

            }
        } else {
            List<Holder<Origin>> randomOriginIds = layer.value().collectRandomizableOrigins(player.registryAccess()).toList();
            if (!layer.value().allowRandom() || randomOriginIds.isEmpty()) {
                Origins.LOGGER.warn("Player {} tried to choose a random origin for layer \"{}\", which is not allowed!", player.getName().getString(), RLHelper.string(layer));
                holder.clearOrigin(layer);
            } else {
                Holder<Origin> origin = randomOriginIds.get(player.getRandom().nextInt(randomOriginIds.size()));
                holder.setOrigin(layer, origin);
                Origins.LOGGER.info("Player {} was randomly assigned the following origin: {}", player.getName().getString(), RLHelper.string(origin));
            }
        }
        PacketDistributor.sendToPlayer(player, new ConfirmOriginS2CPayload(layer, holder.getOrigin(layer)));
        holder.data().setSelecting(false);
        holder.sync();
    }

}
