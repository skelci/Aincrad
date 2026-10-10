package com.aincrad.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.level.Level;

public abstract class MobEntity extends Mob {
    protected MobType mobType;
    protected int floorLevel;
    protected int phase;

    protected MobEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.HOSTILE;
        this.floorLevel = 1;
        this.phase = 1;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector = new GoalSelector();
        this.targetSelector = new GoalSelector();
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
}
