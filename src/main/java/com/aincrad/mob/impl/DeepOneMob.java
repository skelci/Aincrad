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
import net.minecraft.world.phys.Vec3;

/**
 * Deep One — ambusher with water movement and melee burst.
 * Floors 51-60.
 */
public class DeepOneMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "deep_one_mob"));

    public static final EntityType<DeepOneMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(DeepOneMob::new, MobCategory.MONSTER)
            .sized(0.8f, 1.8f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 28.0f;
    private static final float BASE_DAMAGE = 8.0f;
    private static final float BASE_SPEED = 0.4f;
    private static final int INVIS_DURATION = 60;
    private static final int BURST_COOLDOWN = 80;

    private int burstTimer;

    public DeepOneMob(EntityType<? extends MobEntity> entityType, Level level) {
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
        if (this.burstTimer > 0) this.burstTimer--;

        if (!this.level().isClientSide() && this.getTarget() != null && this.getTarget().isAlive()) {
            double dist = this.distanceTo(this.getTarget());
            // Invisibility when far
            if (dist > 5.0) {
                this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, INVIS_DURATION, 0, false, false));
            }
            // Melee burst dash
            if (this.burstTimer <= 0 && dist < 4.0 && dist > 1.5) {
                Vec3 dir = this.getTarget().position().subtract(this.position()).normalize();
                this.setDeltaMovement(dir.x * 1.2, 0.2, dir.z * 1.2);
                this.burstTimer = BURST_COOLDOWN;
            }
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
