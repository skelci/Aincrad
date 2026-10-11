package com.aincrad.mob.impl;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumSet;

public class RangedArrowAttackGoal extends Goal {

    private final Mob mob;
    private final RangedAttackMob rangedMob;
    private final double speedModifier;
    private final int attackInterval;
    private final float attackRadius;
    private int attackTime = -1;
    private int seeTime;

    public RangedArrowAttackGoal(Mob mob, double speedModifier, int attackInterval, float attackRadius) {
        this.mob = mob;
        this.rangedMob = (RangedAttackMob) mob;
        this.speedModifier = speedModifier;
        this.attackInterval = attackInterval;
        this.attackRadius = attackRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || !this.mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            double distSqr = this.mob.distanceToSqr(target);
            boolean canSee = this.mob.getSensing().hasLineOfSight(target);
            if (canSee) {
                ++this.seeTime;
            } else {
                this.seeTime = 0;
            }

            if (distSqr <= this.attackRadius * this.attackRadius && this.seeTime >= 5) {
                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().moveTo(target, this.speedModifier);
            }

            this.mob.getLookControl().setLookAt(target, 30.0f, 30.0f);

            if (--this.attackTime <= 0) {
                if (canSee) {
                    this.rangedMob.performRangedAttack(target, 1.0f);
                    this.attackTime = this.attackInterval;
                }
            }
        }
    }
}
