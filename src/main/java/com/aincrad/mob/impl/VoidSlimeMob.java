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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class VoidSlimeMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "void_slime_mob"));

    public static final EntityType<VoidSlimeMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(VoidSlimeMob::new, MobCategory.MONSTER)
            .sized(1.0f, 1.0f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 26.0f;
    private static final float BASE_DAMAGE = 5.0f;
    private static final float BASE_SPEED = 0.28f;
    private static final int MAX_SPLIT = 3;
    private static final int TELEPORT_COOLDOWN = 100;
    private static final int TELEPORT_RANGE = 12;
    private static final double VOID_DAMAGE_THRESHOLD = 0.5;

    private int teleportTimer;

    public VoidSlimeMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.HOSTILE;
        this.teleportTimer = TELEPORT_COOLDOWN;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SlimeBounceGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.teleportTimer > 0) {
            this.teleportTimer--;
        }
        if (!this.level().isClientSide() && this.teleportTimer <= 0 && this.getTarget() != null) {
            if (this.getTarget().distanceTo(this) > TELEPORT_RANGE * 0.6 && this.getHealth() < this.getMaxHealth() * VOID_DAMAGE_THRESHOLD) {
                this.teleportNearTarget();
                this.teleportTimer = TELEPORT_COOLDOWN;
            }
        }
    }

    private void teleportNearTarget() {
        if (this.getTarget() == null) return;
        double angle = this.random.nextDouble() * Math.PI * 2;
        double distance = 2.0 + this.random.nextDouble() * 3.0;
        double tx = this.getTarget().getX() + Math.cos(angle) * distance;
        double ty = this.getTarget().getY() + this.random.nextDouble() * 2.0;
        double tz = this.getTarget().getZ() + Math.sin(angle) * distance;
        this.teleportTo(tx, ty, tz);
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.18f;
        float dmgScale = 1.0f + (floor - 1) * 0.10f;
        float spdScale = 1.0f + (floor - 1) * 0.02f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * spdScale);
        this.getAttribute(Attributes.ARMOR).setBaseValue(floor * 2.0f);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide() && this.getFloorLevel() > 1 && this.isAlive()) {
            int splitCount = Math.min(MAX_SPLIT, 1 + this.getFloorLevel() / 4);
            for (int i = 0; i < splitCount; i++) {
                VoidSlimeMob child = new VoidSlimeMob(TYPE, this.level());
                child.setFloorLevel(Math.max(1, this.getFloorLevel() - 2));
                child.applyFloorScaling(child.getFloorLevel());
                child.setPos(
                    this.getX() + (this.random.nextDouble() - 0.5) * 1.5,
                    this.getY(),
                    this.getZ() + (this.random.nextDouble() - 0.5) * 1.5);
                this.level().addFreshEntity(child);
            }
        }
        super.die(damageSource);
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
