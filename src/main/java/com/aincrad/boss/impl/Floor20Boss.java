package com.aincrad.boss.impl;

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
 * Floor 20 boss — Ancient Treant.
 * A forest-themed tier boss with root snares, bark armor, and nature's wrath.
 */
public class Floor20Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor20_boss"));

    public static final EntityType<Floor20Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor20Boss::new, MobCategory.MONSTER)
            .sized(1.8f, 3.0f)
            .build(TYPE_KEY));

    private static final int FLOOR = 20;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor20Boss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
    }

    @Override
    protected void initPatterns() {
        // Tier boss pattern library
        var libPatterns = BossAttackLibrary.getTierBossPatterns(FLOOR);
        for (var p : libPatterns) {
            patterns.add(p);
            patternCooldowns.add(0);
        }

        // Root Snare — AoE slam with roots
        patterns.add(BossPatternFactory.createAoeSlam("RootSnare", BASE_DAMAGE * 1.5f, 7.0f, 85));
        patternCooldowns.add(0);

        // Bark Armor — buff armor
        patterns.add(BossPatternFactory.createBuff("BarkArmor",
            Attributes.ARMOR, 1.4f, 130));
        patternCooldowns.add(0);

        // Nature's Wrath — projectile barrage
        patterns.add(BossPatternFactory.createProjectile("NaturesWrath", BASE_DAMAGE * 1.1f, 20.0f, 65));
        patternCooldowns.add(0);

        // Summon Saplings — summon minions
        patterns.add(BossPatternFactory.createSummon("SummonSaplings", 2, BASE_DAMAGE * 0.25f, 170));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Ancient Treant";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;
        String msg = switch (phase) {
            case PHASE_2 -> "The Ancient Treant's roots dig deeper into the earth!";
            case PHASE_3 -> "The Ancient Treant's bark hardens like iron!";
            case PHASE_4 -> "The Ancient Treant calls upon nature's fury!";
            default -> null;
        };
        if (msg != null) {
            final String finalMsg = msg;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.DARK_GREEN + finalMsg)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.ARMOR).setBaseValue(15.0f);
            case PHASE_3 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.2f);
            case PHASE_4 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor20_ancient_treant", Floor20Boss.class, FLOOR);
    }
}
