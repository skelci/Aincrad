package com.aincrad.paper.listener;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.quest.QuestGui;
import com.aincrad.paper.skills.ShopGui;
import com.aincrad.paper.skills.SkillGui;
import com.aincrad.paper.world.BureauKiosk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TownMenuListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        var b = event.getClickedBlock();
        if (b == null) return;
        Player p = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (BureauKiosk.isSkillBlock(b)) {
            event.setCancelled(true);
            SkillGui.open(p, AincradPaperPlugin.getInstance().getSkillService());
        } else if (BureauKiosk.isQuestBlock(b)) {
            event.setCancelled(true);
            QuestGui.open(p);
        } else if (BureauKiosk.isShopBlock(b)) {
            event.setCancelled(true);
            ShopGui.open(p);
        } else if (isPotted(b) && inTown(b)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        if (isPotted(b) && inTown(b)) {
            event.setCancelled(true);
        }
    }

    private static boolean isPotted(Block b) {
        return b.getType().name().startsWith("POTTED_");
    }

    private static boolean inTown(Block b) {
        return AincradPaperPlugin.isFloorWorld(b.getWorld())
                && Math.hypot(b.getX(), b.getZ()) < 200;
    }
}
