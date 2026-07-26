package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.PowerHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Applies enchantment affinities that vanilla derives from its removed MobType API.
 */
@EventBusSubscriber
public final class EntityGroupEvents {
    private EntityGroupEvents() {
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

        ItemStack weapon = event.getSource().getWeaponItem();
        if (weapon == null || weapon.isEmpty()) weapon = attacker.getMainHandItem();
        Registry<Enchantment> enchantments = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        if (hasGroup(event.getEntity(), EntityGroupPower.Group.UNDEAD)) {
            int levelValue = weapon.getEnchantmentLevel(enchantments.getHolderOrThrow(Enchantments.SMITE));
            if (levelValue > 0) event.setAmount(event.getAmount() + levelValue * 2.5F);
        }
        if (hasGroup(event.getEntity(), EntityGroupPower.Group.ARTHROPOD)) {
            int levelValue = weapon.getEnchantmentLevel(enchantments.getHolderOrThrow(Enchantments.BANE_OF_ARTHROPODS));
            if (levelValue > 0) event.setAmount(event.getAmount() + levelValue * 2.5F);
        }
    }

    private static boolean hasGroup(LivingEntity entity, EntityGroupPower.Group group) {
        return PowerHelper.get(entity).anyActive(EntityGroupPower.class, power -> power.group() == group);
    }
}
