package com.aincrad.mob;

import com.aincrad.crystal.drop.CrystalDropHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class MobEntity extends PathfinderMob {
    protected MobType mobType;
    protected int floorLevel;
    protected int phase;

    protected MobEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.HOSTILE;
        this.floorLevel = 1;
        this.phase = 1;
        this.applyFloorScaling(this.floorLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        if (isAggressive()) {
            this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.player.Player.class, true));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.phase > 1 && this.getTarget() == null) {
            this.setPhase(1);
        }
    }

    public MobType getMobType() {
        return this.mobType;
    }

    public void setMobType(MobType mobType) {
        this.mobType = mobType;
    }

    public int getFloorLevel() {
        return this.floorLevel;
    }

    public void setFloorLevel(int floorLevel) {
        this.floorLevel = floorLevel;
        this.applyFloorScaling(floorLevel);
    }

    public int getPhase() {
        return this.phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public boolean isAggressive() {
        return switch (this.mobType) {
            case HOSTILE, PACK, RANGED, AMBUSHER, ENRAGED, ELITE, BOSS, RAID_BOSS -> true;
            default -> false;
        };
    }

    /**
     * Apply floor-based stat scaling. Called after construction and whenever
     * the floor level changes. Subclasses override to set their own attributes.
     *
     * @param floor the current floor level (1+)
     */
    protected void applyFloorScaling(int floor) {
        // Default: no-op. Subclasses override.
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (!this.level().isClientSide()) {
            var crystal = CrystalDropHandler.rollCrystal(false, 0);
            if (!crystal.isEmpty()) {
                this.spawnAtLocation((net.minecraft.server.level.ServerLevel) this.level(), crystal);
            }
        }
    }
}
