package com.aincrad.paper.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public final class BossFxNet {

    public static final String CHANNEL = "aincrad:vfx";

    public static final int LUNGE = 0;
    public static final int SWEEP = 1;
    public static final int TERROR = 2;
    public static final int AXIOM = 3;
    public static final int BLINK = 4;
    public static final int PHASE = 9;

    private BossFxNet() {
    }

    public static void send(Plugin plugin, Location loc, int type, float power) {
        try {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            DataOutputStream d = new DataOutputStream(bs);
            d.writeByte(type);
            d.writeDouble(loc.getX());
            d.writeDouble(loc.getY());
            d.writeDouble(loc.getZ());
            d.writeFloat(power);
            d.flush();
            byte[] data = bs.toByteArray();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().equals(loc.getWorld())
                        && p.getLocation().distanceSquared(loc) < 48.0 * 48.0) {
                    p.sendPluginMessage(plugin, CHANNEL, data);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
