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
 * Floor 65 boss — Will-o'-Wisp.
 * A swamp-themed standard boss with ghostly fire, bog breath, and haunting presence.
 */
public class Floor65Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor65_boss"));

    public static final EntityType<Floor65Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor65Boss::new, MobCategory.MONSTER)
            .sized(0.6f, 0.8f)
            .build(TYPE_KEY));

    private static final int FLOOR = 65;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor65Boss(EntityType<? extends Monster> entityType, Level level) {
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

        // Ghostly Fire — ranged spectral damage
        patterns.add(BossPatternFactory.createProjectile("GhostlyFire", BASE_DAMAGE * 1.4f, 16.0f, 65));
        patternCooldowns.add(0);

        // Bog Breath — debuff reducing player healing
        patterns.add(BossPatternFactory.createDebuff("BogBreath", BASE_DAMAGE * 0.5f, 14, 120));
        patternCooldowns.add(0);

        // Haunting Presence — AoE fear effect
        patterns.add(BossPatternFactory.createAoeSlam("HauntingPresence", BASE_DAMAGE * 1.2f, 10.0f, 100));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Will-o'-Wisp";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;
        String msg = switch (phase) {
            case PHASE_2 -> "The Will-o'-Wisp flickers with eerie green light!";
            case PHASE_3 -> "Ghostly flames surround the Will-o'-Wisp!";
            case PHASE_4 -> "The Will-o'-Wisp's haunting intensifies!";
            default -> null;
        };
        if (msg != null) {
            final String finalMsg = msg;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.DARK_GREEN + finalMsg)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.55f);
            case PHASE_3 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.3f);
            case PHASE_4 -> this.getAttribute(Attributes.ARMOR).setBaseValue(12.0f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor65_will_o_wisp", Floor65Boss.class, FLOOR);
    }
}
