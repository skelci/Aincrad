package com.aincrad.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class MapKey {
    private static KeyMapping mapKey;

    private MapKey() {
    }

    public static void register() {
        KeyMapping.Category category = new KeyMapping.Category(
                Identifier.fromNamespaceAndPath("aincrad", "keybinds"));
        mapKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.aincrad.world_map",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_M,
                category));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.getConnection() == null) return;
            while (mapKey.consumeClick()) {
                if (client.gui.screen() == null) {
                    client.gui.setScreen(new WorldMapScreen());
                }
            }
        });
    }
}
