package com.aincrad.mob.impl;

import com.aincrad.mob.MobEntity;
import com.aincrad.mob.MobType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class StormSkeletonMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "storm_skeleton_mob"));

    public static final EntityType<StormSkeletonMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(StormSkeletonMob::new, MobCategory.MONSTER)
            .sized(0.6f, 1.99f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 22.0f;
    private static final float BASE_DAMAGE = 4.0f;
    private static final float BASE_SPEED = 0.28f;
    private static final int RANGED_ATTACK_RANGE = 20;
    private static final double LIGHTNING_CHAIN_CHANCE = 0.25;
    private static final int LIGHTNING_CHAIN_RADIUS = 4;
    private static final int LIGHTNING_CHAIN_COUNT = 3;

    private final RangedBowAttackGoal<StormSkeletonMob> bowAttackGoal =
        new RangedBowAttackGoal<>(this, 1.0, 20, RANGED_ATTACK_RANGE);

    public StormSkeletonMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.RANGED;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(4, this.bowAttackGoal);
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.15f;
        float dmgScale = 1.0f + (floor - 1) * 0.12f;
        float spdScale = 1.0f + (floor - 1) * 0.02f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * spdScale);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(RANGED_ATTACK_RANGE * 1.5);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        Arrow arrow = new Arrow(this.level(), this);
        double dx = target.getX() - this.getX();
        double dy = target.getBoundingBox().minY + target.getBbHeight() / 3.0 - arrow.getY();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        double dyAdjusted = dy + dist * 0.2;
        arrow.shoot(dx, dyAdjusted, dz, 1.6f, (float) (14 - this.level().getDifficulty().getId() * 4));

        double damage = this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        arrow.setBaseDamage(damage);

        this.level().addFreshEntity(arrow);

        // Storm effect: chance to chain lightning to nearby entities after hit
        if (this.random.nextDouble() < LIGHTNING_CHAIN_CHANCE) {
            this.level().getEntitiesOfClass(
                LivingEntity.class,
                target.getBoundingBox().inflate(LIGHTNING_CHAIN_RADIUS),
                e -> e != target && e != this && e.closerThan(target, LIGHTNING_CHAIN_RADIUS))
                .limit(LIGHTNING_CHAIN_COUNT)
                .forEach(e -> {
                    e.hurt(this.level().damageSources().mobAttack(this),
                        BASE_DAMAGE * 0.5 + this.getFloorLevel());
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, false));
                });
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }
}
