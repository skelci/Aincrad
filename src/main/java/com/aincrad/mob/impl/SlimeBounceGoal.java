package com.aincrad.mob.impl;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Bouncing movement goal for slime-type mobs.
 * Makes the mob hop toward its target with random intervals.
 */
public class SlimeBounceGoal extends Goal {

    private final Mob mob;
    private final JumpControl jumpControl;
    private int bounceTimer;
    private Vec3 targetPos;

    public SlimeBounceGoal(Mob mob) {
        this.mob = mob;
        this.jumpControl = mob.getJumpControl();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.mob.isPassenger()) {
            return false;
        }
        if (this.mob.onGround()) {
            if (this.bounceTimer <= 0) {
                this.bounceTimer = 20 + this.mob.getRandom().nextInt(20);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.onGround() && this.bounceTimer > 0;
    }

    @Override
    public void start() {
        this.bounceTimer = 20 + this.mob.getRandom().nextInt(20);
    }

    @Override
    public void tick() {
        --this.bounceTimer;
        if (this.bounceTimer == 0) {
            Vec3 direction;
            if (this.mob.getTarget() != null) {
                direction = this.mob.getTarget().position().subtract(this.mob.position()).normalize();
            } else {
                double angle = this.mob.getRandom().nextDouble() * Math.PI * 2;
                direction = new Vec3(Math.cos(angle), 0, Math.sin(angle));
            }
            double speed = 0.4 + this.mob.getRandom().nextDouble() * 0.2;
            this.mob.setDeltaMovement(direction.x * speed, 0.5, direction.z * speed);
            this.jumpControl.jump();
        }
    }

    @Override
    public void stop() {
        this.bounceTimer = 0;
    }
}
