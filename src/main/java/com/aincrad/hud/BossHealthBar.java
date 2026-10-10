package com.aincrad.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class BossHealthBar extends HudElement {
    private static final int BAR_WIDTH = 500;
    private static final int BAR_HEIGHT = 20;
    private static final int PADDING = 8;
    private static final double LERP_FACTOR = 0.15;

    private boolean bossTargeted;
    private String bossName = "The Gleam Eyes";
    private int maxHp = 10000;
    private int currentHp = 7800;
    private int totalPhases = 3;
    private int currentPhase = 2;
    private float enrageTimer;
    private float displayedHp;
    private String nextAbility = "Fire Breath";
    private int partyDps = 1247;

    public BossHealthBar() {
        super(-BAR_WIDTH / 2, 30, BAR_WIDTH, BAR_HEIGHT + 40);
        this.anchor = Anchor.TOP_CENTER;
        this.displayedHp = 0.78f;
    }

    @Override
    public void update() {
        float targetHp = maxHp > 0 ? (float) currentHp / maxHp : 0;
        displayedHp = lerp(displayedHp, targetHp, LERP_FACTOR);
        if (enrageTimer > 0) enrageTimer = Math.max(0, enrageTimer - 0.05f);
    }

    @Override
    public void render(GuiGraphicsExtractor extractor) {
        if (!visible || !bossTargeted) return;
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;

        int panelX = x;
        int panelY = y;
        int panelW = width;
        int panelH = height;

        extractor.fill(panelX, panelY, panelX + panelW, panelY + panelH, HudPalette.PANEL_BG);
        extractor.outline(panelX, panelY, panelX + panelW - 1, panelY + panelH - 1, HudPalette.PANEL_BORDER);

        String nameText = "Floor 15 Boss: " + bossName;
        extractor.text(font, nameText, panelX + panelW / 2 - font.width(nameText) / 2, panelY + 2, HudPalette.TEXT_PRIMARY);

        int barX = panelX + PADDING;
        int barY = panelY + 18;
        int barW = panelW - PADDING * 2;

        extractor.fill(barX, barY, barX + barW, barY + BAR_HEIGHT, HudPalette.HP_BACKGROUND);
        int fillW = (int)(barW * clamp01(displayedHp));
        if (fillW > 0) extractor.fillGradient(barX, barY, barX + fillW, barY + BAR_HEIGHT, HudPalette.HP_GRADIENT_START, HudPalette.HP_GRADIENT_END);

        float phase2Marker = 0.33f;
        int marker2X = barX + (int)(barW * phase2Marker);
        extractor.verticalLine(marker2X, barY, barY + BAR_HEIGHT, 0xFFFFFFFF);
        float phase1Marker = 0.66f;
        int marker1X = barX + (int)(barW * phase1Marker);
        extractor.verticalLine(marker1X, barY, barY + BAR_HEIGHT, 0xFFFFFFFF);

        String hpText = String.format("%.0f%%", displayedHp * 100);
        extractor.text(font, hpText, barX + barW / 2 - font.width(hpText) / 2, barY + 6, HudPalette.TEXT_PRIMARY, true);

        int infoY = barY + BAR_HEIGHT + 4;
        String phaseText = "Phase " + currentPhase + "/" + totalPhases;
        extractor.text(font, phaseText, barX, infoY, HudPalette.TEXT_ACCENT);

        if (enrageTimer > 0) {
            String enrageText = String.format("Enrage in %d:%02d", (int)(enrageTimer / 60), (int)(enrageTimer % 60));
            extractor.text(font, enrageText, barX + 100, infoY, 0xFFFF4444);
        }

        String abilityText = "Next: " + nextAbility;
        extractor.text(font, abilityText, barX + barW - font.width(abilityText), infoY, HudPalette.TEXT_SECONDARY);

        String dpsText = "DPS: " + partyDps;
        extractor.text(font, dpsText, barX + barW - font.width(dpsText), infoY + 12, HudPalette.TEXT_TERTIARY);
    }

    public void setBossTargeted(boolean targeted) { this.bossTargeted = targeted; }
    public void setBossName(String name) { this.bossName = name; }
    public void setCurrentHp(int hp) { this.currentHp = hp; }
    public void setMaxHp(int hp) { this.maxHp = hp; }
    public void setEnrageTimer(float seconds) { this.enrageTimer = seconds; }
    public void setNextAbility(String ability) { this.nextAbility = ability; }
    public void setPartyDps(int dps) { this.partyDps = dps; }

    private static float lerp(float from, float to, double factor) { return (float)(from + (to - from) * factor); }
    private static float clamp01(float value) { return Math.max(0.0f, Math.min(1.0f, value)); }
}
