package com.aincrad.mob.impl;

import com.aincrad.mob.MobEntity;
import com.aincrad.mob.MobType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class VoidWraithMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "void_wraith_mob"));

    public static final EntityType<VoidWraithMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(VoidWraithMob::new, MobCategory.MONSTER)
            .sized(0.8f, 2.2f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 30.0f;
    private static final float BASE_DAMAGE = 8.0f;
    private static final float BASE_SPEED = 0.32f;
    private static final int TELEPORT_COOLDOWN = 80;
    private static final int TELEPORT_RANGE = 16;
    private static final int BURST_COOLDOWN = 120;
    private static final double BURST_DAMAGE_MULTIPLIER = 1.5;
    private static final double BURST_RADIUS = 3.5;

    private int teleportTimer;
    private int burstTimer;

    public VoidWraithMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.AMBUSHER;
        this.teleportTimer = TELEPORT_COOLDOWN;
        this.burstTimer = BURST_COOLDOWN;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.teleportTimer > 0) this.teleportTimer--;
            if (this.burstTimer > 0) this.burstTimer--;

            if (this.getTarget() != null && this.burstTimer <= 0) {
                if (this.getTarget().distanceTo(this) < 3.0) {
                    this.performMeleeBurst();
                    this.burstTimer = BURST_COOLDOWN;
                }
            }

            if (this.teleportTimer <= 0 && this.getTarget() != null) {
                if (this.getTarget().distanceTo(this) > TELEPORT_RANGE * 0.5) {
                    this.teleportNearTarget();
                    this.teleportTimer = TELEPORT_COOLDOWN;
                }
            }
        }
    }

    private void teleportNearTarget() {
        if (this.getTarget() == null) return;
        double angle = this.random.nextDouble() * Math.PI * 2;
        double distance = 1.5 + this.random.nextDouble() * 2.5;
        double tx = this.getTarget().getX() + Math.cos(angle) * distance;
        double ty = this.getTarget().getY() + this.random.nextDouble() * 2.0;
        double tz = this.getTarget().getZ() + Math.sin(angle) * distance;
        this.teleportTo(tx, ty, tz);
    }

    private void performMeleeBurst() {
        for (var entity : this.level().getEntitiesOfClass(
                net.minecraft.world.entity.LivingEntity.class,
                this.getBoundingBox().inflate(BURST_RADIUS),
                e -> e != this && e instanceof Player)) {
            double damage = this.getAttributeValue(Attributes.ATTACK_DAMAGE) * BURST_DAMAGE_MULTIPLIER;
            entity.hurt(this.level().damageSources().mobAttack(this), (float) damage);
        }
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.16f;
        float dmgScale = 1.0f + (floor - 1) * 0.11f;
        float spdScale = 1.0f + (floor - 1) * 0.03f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * spdScale);
        this.getAttribute(Attributes.ARMOR).setBaseValue(floor * 1.5f);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide() && this.isAlive()) {
            this.explodeOnDeath();
        }
        super.die(damageSource);
    }

    private void explodeOnDeath() {
        for (var entity : this.level().getEntitiesOfClass(
                net.minecraft.world.entity.LivingEntity.class,
                this.getBoundingBox().inflate(2.0),
                e -> e != this && e instanceof Player)) {
            entity.hurt(this.level().damageSources().mobAttack(this),
                (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8));
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource == this.level().damageSources().fellOutOfWorld()) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }
}
