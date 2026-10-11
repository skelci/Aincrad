package com.aincrad.boss.impl;

import com.aincrad.boss.BossAttackPattern;
import com.aincrad.boss.BossEntity;
import com.aincrad.boss.BossPhase;
import com.aincrad.boss.BossRegistry;
import com.aincrad.boss.patterns.BossAttackLibrary;
import com.aincrad.boss.patterns.BossPatternFactory;
import com.aincrad.boss.util.BossStatScaler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * Floor 75 raid boss — Sea Serpent.
 * Ocean-themed raid boss with tidal waves, serpent strikes, and drowning debuff.
 */
public class Floor75Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor75_boss"));

    public static final EntityType<Floor75Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor75Boss::new, MobCategory.MONSTER)
            .sized(1.5f, 3.0f)
            .build(TYPE_KEY));

    private static final int FLOOR = 75;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor75Boss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
    }

    @Override
    protected void initPatterns() {
        // Raid patterns from library
        for (BossAttackPattern p : BossAttackLibrary.getRaidPatterns(FLOOR)) {
            patterns.add(p);
            patternCooldowns.add(0);
        }

        // Custom: Tidal Wave — expanding AoE
        patterns.add(BossPatternFactory.createAoeWave("TidalWave", BASE_DAMAGE * 1.5f, 120));
        patternCooldowns.add(0);

        // Custom: Serpent Strike — charge with knockback
        patterns.add(BossPatternFactory.createCharge("SerpentStrike", BASE_DAMAGE * 1.8f, 12.0f, 100));
        patternCooldowns.add(0);

        // Custom: Drowning — debuff
        patterns.add(BossPatternFactory.createDebuff("Drowning", BASE_DAMAGE * 0.6f, 10, 110));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Sea Serpent";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        switch (phase) {
            case PHASE_2 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.15f);
                broadcastPhaseMessage("Sea Serpent's scales shimmer with ocean power!");
            }
            case PHASE_3 -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5f);
                broadcastPhaseMessage("Sea Serpent moves like a crashing wave!");
            }
            case PHASE_4 -> {
                this.getAttribute(Attributes.ARMOR).setBaseValue(20.0f);
                broadcastPhaseMessage("Sea Serpent's hide becomes like coral armor!");
            }
            case PHASE_5 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.5f);
                broadcastPhaseMessage("Sea Serpent unleashes the fury of the deep!");
            }
            default -> {}
        }
    }

    private void broadcastPhaseMessage(String msg) {
        if (!this.level().isClientSide()) {
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(
                    ChatFormatting.BLUE + msg)));
        }
    }

    static {
        BossRegistry.register("floor75_sea_serpent", Floor75Boss.class, FLOOR);
    }
}
