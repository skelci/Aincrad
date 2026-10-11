package com.aincrad.armor;

import com.aincrad.combat.stat.PlayerStats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public final class ArmorSetBonusApplier {
    private ArmorSetBonusApplier() {}

    public static void apply(Player player, PlayerStats stats) {
        Map<ArmorSet, Integer> setCounts = new EnumMap<>(ArmorSet.class);
        Map<ArmorSet, ArmorPiece> setPieces = new EnumMap<>(ArmorSet.class);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof ArmorPiece armorPiece) {
                setCounts.merge(armorPiece.getSet(), 1, Integer::sum);
                setPieces.putIfAbsent(armorPiece.getSet(), armorPiece);
            }
        }

        for (Map.Entry<ArmorSet, Integer> entry : setCounts.entrySet()) {
            int count = entry.getValue();
            ArmorPiece piece = setPieces.get(entry.getKey());

            if (count >= 6 && piece.getBonus6() != null) {
                stats.modifyBase(piece.getBonus6().stat(), piece.getBonus6().bonus());
            }
            if (count >= 4 && piece.getBonus4() != null) {
                stats.modifyBase(piece.getBonus4().stat(), piece.getBonus4().bonus());
            }
            if (count >= 2 && piece.getBonus2() != null) {
                stats.modifyBase(piece.getBonus2().stat(), piece.getBonus2().bonus());
            }
        }
    }
}
