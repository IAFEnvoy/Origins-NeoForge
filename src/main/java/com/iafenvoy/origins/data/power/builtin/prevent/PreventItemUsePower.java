package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.config.OriginsConfig;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

//FIXME::Tooltip
@EventBusSubscriber
public class PreventItemUsePower extends Power {
    public static final MapCodec<PreventItemUsePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("item_condition").forGetter(PreventItemUsePower::getItemCondition)
    ).apply(i, PreventItemUsePower::new));
    private final ItemCondition itemCondition;

    public PreventItemUsePower(BaseSettings settings, ItemCondition itemCondition) {
        super(settings);
        this.itemCondition = itemCondition;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public static boolean isUsagePrevented(Entity entity, ItemStack stack) {
        return OriginDataHolder.get(entity).streamActivePowers(PreventItemUsePower.class).anyMatch(x -> x.itemCondition.test(entity.level(), stack));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preventBlockInteraction(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getItemStack().getItem() instanceof BlockItem) && isUsagePrevented(event.getEntity(), event.getItemStack()))
            event.setUseItem(TriState.FALSE);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preventItemUsage(PlayerInteractEvent.RightClickItem event) {
        if (isUsagePrevented(event.getEntity(), event.getItemStack())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void appendTooltips(ItemTooltipEvent event) {
        //TODO::Config
        Player player = event.getEntity();
        if (player == null) return;
        List<PreventItemUsePower> powers = OriginDataHolder.get(player).streamActivePowers(PreventItemUsePower.class).filter(x -> x.itemCondition.test(player.level(), event.getItemStack())).collect(Collectors.toList());
        int size = powers.size();
        if (!powers.isEmpty()) {
            RegistryAccess access = player.registryAccess();
            powers.removeIf(x -> x.getSettings().hidden());
            String key = String.format(Locale.ROOT, "tooltip.%s.unusable.%s", Origins.MOD_ID, event.getItemStack().getUseAnimation().name().toLowerCase(Locale.ROOT));
            ChatFormatting textColor = ChatFormatting.GRAY;
            ChatFormatting powerColor = ChatFormatting.RED;
            if (OriginsConfig.INSTANCE.general.compactUsabilityHints.getValue() || powers.isEmpty()) {
                if (powers.size() == 1) {
                    PreventItemUsePower power = powers.getFirst();
                    event.getToolTip().add(Component.translatable(key + ".single", power.getName(access).withStyle(powerColor)).withStyle(textColor));
                } else
                    event.getToolTip().add(Component.translatable(key + ".multiple", Component.literal((powers.isEmpty() ? size : powers.size()) + "").withStyle(powerColor)).withStyle(textColor));
            } else {
                MutableComponent component = powers.getFirst().getName(access).withStyle(powerColor);
                for (int i = 1; i < powers.size(); i++) {
                    component = component.append(Component.literal(", ").withStyle(textColor));
                    component = component.append(powers.get(i).getName(access).withStyle(powerColor));
                }
                MutableComponent preventText = Component.translatable(key + ".single", component).withStyle(textColor);
                event.getToolTip().add(preventText);
            }
        }
    }
}
