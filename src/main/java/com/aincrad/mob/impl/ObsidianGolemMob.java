package com.aincrad.mob.impl;

import com.aincrad.mob.MobEntity;
import com.aincrad.mob.MobType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Obsidian Golem — very slow, very high HP, AOE slam attack.
 * Floors 41-50.
 */
public class ObsidianGolemMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "obsidian_golem_mob"));

    public static final EntityType<ObsidianGolemMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(ObsidianGolemMob::new, MobCategory.MONSTER)
            .sized(1.8f, 2.5f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 80.0f;
    private static final float BASE_DAMAGE = 10.0f;
    private static final float BASE_SPEED = 0.12f;
    private static final double AOE_RADIUS = 3.0;
    private static final int SLAM_COOLDOWN = 60;

    private int slamTimer;

    public ObsidianGolemMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.HOSTILE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.8, false));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.20f;
        float dmgScale = 1.0f + (floor - 1) * 0.12f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
        this.getAttribute(Attributes.ARMOR).setBaseValue(10.0f + floor * 0.5f);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.slamTimer > 0) this.slamTimer--;

        if (!this.level().isClientSide() && this.slamTimer <= 0
                && this.getTarget() != null && this.getTarget().isAlive()) {
            double dist = this.distanceTo(this.getTarget());
            if (dist < AOE_RADIUS) {
                this.aoeSlam();
                this.slamTimer = SLAM_COOLDOWN;
            }
        }
    }

    private void aoeSlam() {
        double radius = AOE_RADIUS;
        for (net.minecraft.world.entity.LivingEntity entity : this.level().getEntitiesOfClass(
                net.minecraft.world.entity.LivingEntity.class,
                this.getBoundingBox().inflate(radius))) {
            if (entity != this && entity.isAlive()) {
                entity.hurt(this.level().damageSources().mobAttack(this),
                    (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.7));
            }
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}
