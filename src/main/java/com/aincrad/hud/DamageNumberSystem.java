package com.aincrad.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import java.util.ArrayList;
import java.util.List;

public final class DamageNumberSystem {
    private static DamageNumberSystem instance;
    private final List<DamageNumber> numbers = new ArrayList<>();
    private boolean enabled = true;
    private static final int MAX_NUMBERS = 20;
    private static final int LIFETIME = 72;

    public enum DamageType {
        PLAYER_DAMAGE(0xFFFFFFFF, 1.0f, false),
        DAMAGE_TAKEN(0xFFFF4444, 1.0f, false),
        CRIT(0xFFFFDD44, 1.5f, true),
        HEAL(0xFF55EE88, 1.0f, false),
        MANA(0xFF3399FF, 1.0f, false),
        MISS(0xFF999999, 1.0f, false),
        BLOCK(0xFFAAAAAA, 1.0f, false),
        ABSORB(0xFF00E5FF, 1.0f, false),
        DOT(0xFFAA44FF, 1.0f, false);

        final int color;
        final float scale;
        final boolean jitter;

        DamageType(int color, float scale, boolean jitter) {
            this.color = color;
            this.scale = scale;
            this.jitter = jitter;
        }
    }

    private record DamageNumber(String text, float x, float y, int age, int maxAge, DamageType type) {}

    public static synchronized DamageNumberSystem getInstance() {
        if (instance == null) instance = new DamageNumberSystem();
        return instance;
    }

    public void spawn(String text, float x, float y, DamageType type) {
        if (numbers.size() >= MAX_NUMBERS) numbers.remove(0);
        numbers.add(new DamageNumber(text, x, y, 0, LIFETIME, type));
    }

    public void update() {
        for (int i = numbers.size() - 1; i >= 0; i--) {
            DamageNumber dn = numbers.get(i);
            numbers.set(i, new DamageNumber(dn.text(), dn.x(), dn.y() - 0.4f, dn.age() + 1, dn.maxAge(), dn.type()));
            if (dn.age() >= dn.maxAge()) numbers.remove(i);
        }
    }

    public void render(GuiGraphicsExtractor extractor) {
        if (!enabled) return;
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        for (DamageNumber dn : numbers) {
            float progress = (float) dn.age() / dn.maxAge();
            int alpha = (int)(255 * (1.0f - progress));
            int color = dn.type().color & 0x00FFFFFF | (alpha << 24);
            float jitterX = dn.type().jitter ? (float)(Math.sin(dn.age() * 0.5) * 2) : 0;
            extractor.text(font, dn.text(), (int)(dn.x() + jitterX), (int)dn.y(), color, true);
        }
    }

    public void toggle() { enabled = !enabled; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean e) { enabled = e; }
}
