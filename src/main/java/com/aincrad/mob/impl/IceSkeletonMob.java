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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.level.Level;

public class IceSkeletonMob extends MobEntity implements RangedAttackMob {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "ice_skeleton_mob"));

    public static final EntityType<IceSkeletonMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(IceSkeletonMob::new, MobCategory.MONSTER)
            .sized(0.6f, 1.99f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 20.0f;
    private static final float BASE_DAMAGE = 4.5f;
    private static final float BASE_SPEED = 0.30f;
    private static final int SLOW_DURATION = 100;
    private static final int SLOW_AMPLIFIER = 0;

    public IceSkeletonMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.RANGED;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedArrowAttackGoal(this, 1.0, 20, 18.0f));
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
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        var arrowRef = BuiltInRegistries.ENTITY_TYPE.get(Identifier.fromNamespaceAndPath("minecraft", "arrow")).orElse(null);
        if (arrowRef == null) return;
        Arrow arrow = new Arrow((EntityType<Arrow>) arrowRef.value(), this.level());
        arrow.setPos(this.getX(), this.getEyeY() - 0.1, this.getZ());
        double dx = target.getX() - this.getX();
        double dy = target.getBoundingBox().minY + target.getBbHeight() / 3.0 - arrow.getY();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        double vy = dy + dist * 0.2;
        arrow.shoot(dx, vy, dz, 1.7f, 0.8f);
        arrow.setBaseDamage(BASE_DAMAGE * (1.0f + (this.getFloorLevel() - 1) * 0.10f));
        this.level().addFreshEntity(arrow);
        this.swingForAttack(net.minecraft.world.InteractionHand.MAIN_HAND);
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, SLOW_DURATION, SLOW_AMPLIFIER));
    }
}
