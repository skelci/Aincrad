package com.aincrad.mob.impl;

import com.aincrad.mob.MobEntity;
import com.aincrad.mob.MobType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Cactus — passive mob that damages attackers with spikes on contact.
 * Floors 31-40.
 */
public class CactusMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "cactus_mob"));

    public static final EntityType<CactusMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(CactusMob::new, MobCategory.MONSTER)
            .sized(0.8f, 1.8f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 30.0f;
    private static final float BASE_DAMAGE = 3.0f;
    private static final float BASE_SPEED = 0.0f;
    private static final float SPIKE_DAMAGE = 2.0f;
    private static final double SPIKE_RANGE = 1.2;

    public CactusMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.PASSIVE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.15f;
        float dmgScale = 1.0f + (floor - 1) * 0.10f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) {
        boolean result = super.doHurtTarget(level, target);
        if (result && target instanceof net.minecraft.world.entity.LivingEntity living) {
            living.hurt(this.level().damageSources().thorns(this), SPIKE_DAMAGE);
        }
        return result;
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }
}
