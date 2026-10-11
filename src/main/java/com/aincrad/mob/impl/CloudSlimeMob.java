package com.aincrad.mob.impl;

import com.aincrad.mob.MobEntity;
import com.aincrad.mob.MobType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CloudSlimeMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "cloud_slime_mob"));

    public static final EntityType<CloudSlimeMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(CloudSlimeMob::new, MobCategory.MONSTER)
            .sized(1.0f, 1.0f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 28.0f;
    private static final float BASE_DAMAGE = 6.0f;
    private static final float BASE_SPEED = 0.4f;
    private static final int MAX_SPLIT = 2;

    public CloudSlimeMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.HOSTILE;
        this.moveControl = new FlyingMoveControl(this, 15, true);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.20f;
        float dmgScale = 1.0f + (floor - 1) * 0.12f;
        float spdScale = 1.0f + (floor - 1) * 0.03f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * spdScale);
        this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(BASE_SPEED * 1.8f * spdScale);
        this.getAttribute(Attributes.ARMOR).setBaseValue(floor * 1.5f);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void tick() {
        super.tick();
        // Bouncy movement: bounce up and down while pursuing target
        if (!this.level().isClientSide() && this.getTarget() != null) {
            double bounce = Math.sin(this.level().getGameTime() * 0.4) * 0.15;
            this.setDeltaMovement(this.getDeltaMovement().x, bounce, this.getDeltaMovement().z);
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && this.random.nextFloat() < 0.15f && target instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30, 1, false, false));
        }
        return result;
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide() && this.getFloorLevel() > 1 && this.isAlive()) {
            int splitCount = Math.min(MAX_SPLIT, this.getFloorLevel() / 4);
            for (int i = 0; i < splitCount; i++) {
                CloudSlimeMob child = new CloudSlimeMob(TYPE, this.level());
                child.setFloorLevel(Math.max(1, this.getFloorLevel() - 2));
                child.applyFloorScaling(child.getFloorLevel());
                child.setPos(
                    this.getX() + (this.random.nextDouble() - 0.5) * 2.0,
                    this.getY() + 2.0,
                    this.getZ() + (this.random.nextDouble() - 0.5) * 2.0);
                this.level().addFreshEntity(child);
            }
        }
        super.die(damageSource);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }
}
