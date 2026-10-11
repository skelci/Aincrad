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
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class CrystalSkeletonMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "crystal_skeleton_mob"));

    public static final EntityType<CrystalSkeletonMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(CrystalSkeletonMob::new, MobCategory.MONSTER)
            .sized(0.6f, 1.99f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 22.0f;
    private static final float BASE_DAMAGE = 6.0f;
    private static final float BASE_SPEED = 0.25f;
    private static final int RANGED_ATTACK_RANGE = 20;
    private static final float CRITICAL_ARROW_CHANCE = 0.25f;
    private static final double CRITICAL_DAMAGE_MULTIPLIER = 1.8;

    private final RangedBowAttackGoal<CrystalSkeletonMob> bowAttackGoal =
        new RangedBowAttackGoal<>(this, 1.0, 20, RANGED_ATTACK_RANGE);

    public CrystalSkeletonMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.RANGED;
    }

    @Override
    protected void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource random, net.minecraft.world.DifficultyInstance difficulty) {
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
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
        float dmgScale = 1.0f + (floor - 1) * 0.10f;
        float spdScale = 1.0f + (floor - 1) * 0.02f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * spdScale);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(RANGED_ATTACK_RANGE * 1.5);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void performRangedAttack(net.minecraft.world.entity.LivingEntity target, float velocity) {
        Arrow arrow = new Arrow(this.level(), this);
        double dx = target.getX() - this.getX();
        double dy = target.getBoundingBox().minY + target.getBbHeight() / 3.0 - arrow.getY();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        double dyAdjusted = dy + dist * 0.2;
        arrow.shoot(dx, dyAdjusted, dz, 1.6f, (float) (14 - this.level().getDifficulty().getId() * 4));

        double damage = this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (this.random.nextFloat() < CRITICAL_ARROW_CHANCE) {
            damage *= CRITICAL_DAMAGE_MULTIPLIER;
        }
        arrow.setBaseDamage(damage);

        this.level().addFreshEntity(arrow);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }
}
