package com.aincrad.devtest;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Creates signs at a test area showing what to test.
 * Signs show: 'TEST: Boss F1', 'TEST: Mobs', 'TEST: Items', etc.
 */
public final class DevTestSigns {

    private DevTestSigns() {
    }

    /**
     * Places a sign at the given position with the specified text lines.
     *
     * @param level  The server level
     * @param pos    The position to place the sign
     * @param lines  The text lines for the sign (up to 4)
     */
    public static void placeSign(ServerLevel level, BlockPos pos, String... lines) {
        // Place the sign block
        BlockState signState = Blocks.OAK_SIGN.defaultBlockState();
        level.setBlockAndUpdate(pos, signState);

        // Set the sign text
        if (level.getBlockEntity(pos) instanceof SignBlockEntity signEntity) {
            List<Component> messages = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                messages.add(i < lines.length ? Component.literal(lines[i]) : Component.empty());
            }
            SignText signText = new SignText(
                List.copyOf(messages),
                List.of(Component.empty(), Component.empty(), Component.empty(), Component.empty()),
                DyeColor.BLACK,
                false
            );
            signEntity.setText(signText, SignTextSlot.FRONT);
            signEntity.setText(new SignText(
                List.of(Component.empty(), Component.empty(), Component.empty(), Component.empty()),
                List.of(Component.empty(), Component.empty(), Component.empty(), Component.empty()),
                DyeColor.BLACK,
                false
            ), SignTextSlot.BACK);
            level.sendBlockUpdated(pos, signState, signState, 3);
        }
    }

    /**
     * Places a sign with a title and description.
     *
     * @param level       The server level
     * @param pos         The position to place the sign
     * @param title       The sign title (line 1)
     * @param description The sign description (lines 2-4)
     */
    public static void placeSignWithDescription(ServerLevel level, BlockPos pos, String title, String... description) {
        String[] lines = new String[4];
        lines[0] = title;
        for (int i = 0; i < Math.min(description.length, 3); i++) {
            lines[i + 1] = description[i];
        }
        placeSign(level, pos, lines);
    }
}
