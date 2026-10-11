package com.aincrad.boss.impl;

import com.aincrad.boss.BossEntity;
import com.aincrad.boss.BossPhase;
import com.aincrad.boss.BossRegistry;
import com.aincrad.boss.patterns.BossAttackLibrary;
import com.aincrad.boss.patterns.BossPatternFactory;
import com.aincrad.boss.theme.BossTheme;
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
 * Floor 45 boss — Frost Wolf.
 * An ice-themed standard boss with frost breath, freeze, and pack tactics.
 */
public class Floor45Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor45_boss"));

    public static final EntityType<Floor45Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor45Boss::new, MobCategory.MONSTER)
            .sized(1.2f, 1.5f)
            .build(TYPE_KEY));

    private static final int FLOOR = 45;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor45Boss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
    }

    @Override
    protected void initPatterns() {
        // Standard pattern library
        var libPatterns = BossAttackLibrary.getStandardPatterns(FLOOR);
        for (var p : libPatterns) {
            patterns.add(p);
            patternCooldowns.add(0);
        }

        // Frost Breath — cone of ice damage
        patterns.add(BossPatternFactory.createProjectile("FrostBreath", BASE_DAMAGE * 1.3f, 12.0f, 70));
        patternCooldowns.add(0);

        // Deep Freeze — heavy melee with slowing effect
        patterns.add(BossPatternFactory.createHeavyStrike("DeepFreeze", BASE_DAMAGE * 1.6f, 90));
        patternCooldowns.add(0);

        // Howl of the Pack — buff attack speed
        patterns.add(BossPatternFactory.createBuff("HowlOfThePack",
            Attributes.ATTACK_DAMAGE, 1.3f, 140));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Frost Wolf";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;
        String msg = switch (phase) {
            case PHASE_2 -> "The Frost Wolf's breath turns biting cold!";
            case PHASE_3 -> "The Frost Wolf howls, calling winter itself!";
            case PHASE_4 -> "The Frost Wolf enters a frozen rage!";
            default -> null;
        };
        if (msg != null) {
            final String finalMsg = msg;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.AQUA + finalMsg)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.45f);
            case PHASE_3 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.25f);
            case PHASE_4 -> this.getAttribute(Attributes.ARMOR).setBaseValue(15.0f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor45_frost_wolf", Floor45Boss.class, FLOOR);
    }
}
