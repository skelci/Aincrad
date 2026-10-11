package com.aincrad.mob.impl;

import com.aincrad.mob.MobEntity;
import com.aincrad.mob.MobType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CliffGolemMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "cliff_golem_mob"));

    public static final EntityType<CliffGolemMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(CliffGolemMob::new, MobCategory.MONSTER)
            .sized(1.8f, 2.5f)
            .fireImmune()
            .build(TYPE_KEY));

    private static final float BASE_HP = 120.0f;
    private static final float BASE_DAMAGE = 12.0f;
    private static final float BASE_SPEED = 0.18f;
    private static final float AOE_RADIUS = 4.0f;
    private static final int SLAM_COOLDOWN = 100;

    private int slamCooldown;

    public CliffGolemMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.HOSTILE;
        this.slamCooldown = SLAM_COOLDOWN;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.30f;
        float dmgScale = 1.0f + (floor - 1) * 0.15f;
        float spdScale = 1.0f + (floor - 1) * 0.01f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * spdScale);
        this.getAttribute(Attributes.ARMOR).setBaseValue(floor * 3.0f);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0f);
        this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(2.0f);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.slamCooldown > 0) {
            this.slamCooldown--;
        }
        // Auto-trigger ground slam when enemies are in range and cooldown is ready
        if (!this.level().isClientSide() && this.slamCooldown <= 0) {
            boolean hasNearbyEnemies = this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(AOE_RADIUS),
                e -> e != this && e.closerThan(this, AOE_RADIUS * 0.8f))
                .stream().findAny().isPresent();
            if (hasNearbyEnemies) {
                this.performGroundSlam();
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && target instanceof LivingEntity living) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 0.01) {
                living.knockback(1.5, -dx / dist, -dz / dist);
            }
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 80, 1, false, false));
        }
        return result;
    }

    /**
     * Ground slam AOE: deals heavy damage to all nearby enemies and applies knockback.
     */
    public void performGroundSlam() {
        if (this.level().isClientSide() || this.slamCooldown > 0) {
            return;
        }
        this.slamCooldown = SLAM_COOLDOWN + this.random.nextInt(40);

        AABB slamArea = new AABB(
            this.getX() - AOE_RADIUS, this.getY() - 2.0, this.getZ() - AOE_RADIUS,
            this.getX() + AOE_RADIUS, this.getY() + 2.0, this.getZ() + AOE_RADIUS);

        this.level().getEntitiesOfClass(
            LivingEntity.class,
            slamArea,
            e -> e != this && e.closerThan(this, AOE_RADIUS + 1.0))
            .forEach(e -> {
                float slamDamage = BASE_DAMAGE * 0.8f + this.getFloorLevel() * 2.0f;
                e.hurt(this.level().damageSources().mobAttack(this), slamDamage);
                double dx = e.getX() - this.getX();
                double dz = e.getZ() - this.getZ();
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > 0.01) {
                    e.knockback(2.5, dx / dist, dz / dist);
                    e.push(dx / dist * 0.5, 0.8, dz / dist * 0.5);
                }
                if (e instanceof Player player) {
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
                }
            });

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
            SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE,
            0.5f, 0.5f + this.random.nextFloat() * 0.3f);
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON
            || effect.getEffect() == MobEffects.WEAKNESS
            || effect.getEffect() == MobEffects.MOVEMENT_SLOWDOWN) {
            return false;
        }
        return super.canBeAffected(effect);
    }
}
