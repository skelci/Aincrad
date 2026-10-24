package com.aincrad.client;

import com.aincrad.network.SkillCastPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class SkillKeys {

    private static final int[] DEFAULTS = {
            InputConstants.KEY_F6, InputConstants.KEY_F6 + 1, InputConstants.KEY_F6 + 2, InputConstants.KEY_F6 + 3
    };
    private static final KeyMapping[] KEYS = new KeyMapping[4];

    private SkillKeys() {
    }

    public static void register() {
        SkillCastPayload.register();
        KeyMapping.Category category = new KeyMapping.Category(
                Identifier.fromNamespaceAndPath("aincrad", "keybinds"));
        for (int i = 0; i < 4; i++) {
            KEYS[i] = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                    "key.aincrad.skill." + (i + 1),
                    InputConstants.Type.KEYBOARD,
                    DEFAULTS[i],
                    category));
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.getConnection() == null) return;
            for (int i = 0; i < KEYS.length; i++) {
                while (KEYS[i].consumeClick()) {
                    SkillCastPayload.send(i);
                }
            }
        });
    }
}
