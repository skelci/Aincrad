package com.aincrad.mob.impl;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Ranged attack goal that shoots arrows at the target and applies poison.
 * Used by MarshSkeletonMob to deliver poison-tipped arrow attacks.
 */
public class RangedPoisonArrowGoal extends Goal {

    private final Mob mob;
    private final float attackRadius;
    private final int attackInterval;
    private int attackTime = 0;
    private int seeTime = 0;

    public RangedPoisonArrowGoal(Mob mob, float attackRadius, int attackInterval) {
        this.mob = mob;
        this.attackRadius = attackRadius;
        this.attackInterval = attackInterval;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive()
            && this.mob.distanceToSqr(target) <= this.attackRadius * this.attackRadius;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.attackTime = 0;
        this.seeTime = 0;
    }

    @Override
    public void stop() {
        this.seeTime = 0;
        this.attackTime = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distSq = this.mob.distanceToSqr(target);
        boolean canSee = this.mob.getSensing().hasLineOfSight(target);

        if (canSee) {
            this.seeTime++;
        } else {
            this.seeTime = 0;
        }

        if (distSq > this.attackRadius * this.attackRadius * 0.64) {
            this.mob.getNavigation().moveTo(target, 1.0);
        } else {
            this.mob.getNavigation().stop();
        }

        if (this.attackTime > 0) {
            this.attackTime--;
        }

        if (this.seeTime >= 10 && this.attackTime <= 0) {
            this.shootPoisonArrow(target);
            this.attackTime = this.attackInterval;
        }
    }

    private void shootPoisonArrow(LivingEntity target) {
        Arrow arrow = new Arrow(this.mob.level(), this.mob);
        arrow.setBaseDamage(2.0);

        Vec3 targetPos = target.getEyePosition();
        Vec3 arrowPos = arrow.position();
        Vec3 direction = targetPos.subtract(arrowPos).normalize();

        double speed = 3.0;
        arrow.setDeltaMovement(direction.x * speed, direction.y * speed, direction.z * speed);
        this.mob.level().addFreshEntity(arrow);

        target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
    }
}
