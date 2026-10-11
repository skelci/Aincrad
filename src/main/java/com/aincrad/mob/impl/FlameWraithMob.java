package com.aincrad.mob.impl;

import com.aincrad.mob.MobEntity;
import com.aincrad.mob.MobType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
 * Flame Wraith — ambusher with fire AOE and invisibility.
 * Floors 41-50.
 */
public class FlameWraithMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "flame_wraith_mob"));

    public static final EntityType<FlameWraithMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(FlameWraithMob::new, MobCategory.MONSTER)
            .sized(0.8f, 1.8f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 26.0f;
    private static final float BASE_DAMAGE = 7.0f;
    private static final float BASE_SPEED = 0.35f;
    private static final int INVIS_DURATION = 60;
    private static final int FIRE_AOE_COOLDOWN = 100;
    private static final double FIRE_AOE_RADIUS = 3.0;
    private static final int FIRE_DURATION = 5;

    private int fireAoeTimer;

    public FlameWraithMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.AMBUSHER;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5, false));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.15f;
        float dmgScale = 1.0f + (floor - 1) * 0.10f;
        float spdScale = 1.0f + (floor - 1) * 0.02f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * spdScale);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.fireAoeTimer > 0) this.fireAoeTimer--;

        if (!this.level().isClientSide() && this.getTarget() != null && this.getTarget().isAlive()) {
            double dist = this.distanceTo(this.getTarget());
            // Invisibility when far
            if (dist > 5.0) {
                this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, INVIS_DURATION, 0, false, false));
            }
            // Fire AOE burst
            if (this.fireAoeTimer <= 0 && dist < FIRE_AOE_RADIUS) {
                this.fireAoe();
                this.fireAoeTimer = FIRE_AOE_COOLDOWN;
            }
        }
    }

    private void fireAoe() {
        for (net.minecraft.world.entity.LivingEntity entity : this.level().getEntitiesOfClass(
                net.minecraft.world.entity.LivingEntity.class,
                this.getBoundingBox().inflate(FIRE_AOE_RADIUS))) {
            if (entity != this && entity.isAlive()) {
                entity.setRemainingFireTicks(FIRE_DURATION * 20);
                entity.hurt(this.level().damageSources().inFire(),
                    (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5));
            }
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }
}
