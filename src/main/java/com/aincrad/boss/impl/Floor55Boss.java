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
 * Floor 55 boss — Desert Wraith.
 * A desert-themed standard boss with sandstorm, mirage, and scorching attacks.
 */
public class Floor55Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor55_boss"));

    public static final EntityType<Floor55Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor55Boss::new, MobCategory.MONSTER)
            .sized(0.9f, 2.2f)
            .build(TYPE_KEY));

    private static final int FLOOR = 55;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor55Boss(EntityType<? extends Monster> entityType, Level level) {
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

        // Sandstorm — wide AoE damage
        patterns.add(BossPatternFactory.createAoeSlam("Sandstorm", BASE_DAMAGE * 1.4f, 8.0f, 85));
        patternCooldowns.add(0);

        // Mirage Strike — teleport behind target
        patterns.add(BossPatternFactory.createTeleport("MirageStrike", 70));
        patternCooldowns.add(0);

        // Scorching Heat — debuff reducing player damage output
        patterns.add(BossPatternFactory.createDebuff("ScorchingHeat", BASE_DAMAGE * 0.6f, 10, 110));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Desert Wraith";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;
        String msg = switch (phase) {
            case PHASE_2 -> "The Desert Wraith shimmers with heat haze!";
            case PHASE_3 -> "A sandstorm gathers around the Desert Wraith!";
            case PHASE_4 -> "The Desert Wraith's wrath scorches the air!";
            default -> null;
        };
        if (msg != null) {
            final String finalMsg = msg;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.GOLD + finalMsg)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5f);
            case PHASE_3 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.3f);
            case PHASE_4 -> this.getAttribute(Attributes.ARMOR).setBaseValue(18.0f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor55_desert_wraith", Floor55Boss.class, FLOOR);
    }
}
