package com.aincrad.mob.impl;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

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
        if (this.attackTime > 0) {
            this.attackTime--;
        }
    }
}
