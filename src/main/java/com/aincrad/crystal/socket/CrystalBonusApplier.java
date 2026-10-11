package com.aincrad.crystal.socket;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import com.aincrad.crystal.CrystalItem;
import com.aincrad.combat.stat.PlayerStats;
import com.aincrad.combat.stat.StatType;

public final class CrystalBonusApplier {
    private CrystalBonusApplier() {}

    public static void apply(Player player, PlayerStats stats) {
        SocketManager socketManager = new SocketManager();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            for (int i = 0; i < socketManager.getMaxSockets(stack); i++) {
                ItemStack crystal = socketManager.getCrystal(stack, i);
                if (crystal.isEmpty()) continue;
                applyCrystalBonus(crystal, stats);
            }
        }
    }

    private static void applyCrystalBonus(ItemStack crystal, PlayerStats stats) {
        // SWORD -> damage bonus, ARMOR -> defense, AGI -> evasion,
        // MAGIC -> INT, VITALITY -> VIT, SHIELD -> damage reduction
    }
}
