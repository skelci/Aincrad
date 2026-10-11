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

/**
 * Sand Spider — fast hostile mob that burrows (short-range teleport).
 * Floors 31-40.
 */
public class SandSpiderMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "sand_spider_mob"));

    public static final EntityType<SandSpiderMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(SandSpiderMob::new, MobCategory.MONSTER)
            .sized(1.2f, 0.8f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 16.0f;
    private static final float BASE_DAMAGE = 5.0f;
    private static final float BASE_SPEED = 0.45f;
    private static final int BURROW_COOLDOWN = 100;
    private static final double BURROW_DISTANCE = 4.0;

    private int burrowTimer;

    public SandSpiderMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.HOSTILE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
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
        if (this.burrowTimer > 0) {
            this.burrowTimer--;
        }
        // Burrow: short-range teleport to target
        if (!this.level().isClientSide() && this.burrowTimer <= 0
                && this.getTarget() != null && this.getTarget().isAlive()) {
            double dist = this.distanceTo(this.getTarget());
            if (dist > 2.0 && dist < 8.0) {
                this.burrow(this.getTarget());
                this.burrowTimer = BURROW_COOLDOWN;
            }
        }
    }

    private void burrow(net.minecraft.world.entity.LivingEntity target) {
        double angle = this.random.nextDouble() * Math.PI * 2;
        double x = target.getX() + Math.cos(angle) * 1.5;
        double z = target.getZ() + Math.sin(angle) * 1.5;
        this.teleportTo(x, this.getY(), z);
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }
}
