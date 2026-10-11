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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Poison-immune slime that splits into smaller slimes on death.
 * Theme 8 (Swamp/Poison) — HOSTILE type.
 */
public class SwampSlimeMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "swamp_slime_mob"));

    public static final EntityType<SwampSlimeMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(SwampSlimeMob::new, MobCategory.MONSTER)
            .sized(1.0f, 1.0f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 24.0f;
    private static final float BASE_DAMAGE = 4.0f;
    private static final float BASE_SPEED = 0.28f;
    private static final int MAX_SPLIT = 3;

    public SwampSlimeMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.HOSTILE;
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
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.18f;
        float dmgScale = 1.0f + (floor - 1) * 0.10f;
        float spdScale = 1.0f + (floor - 1) * 0.02f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * spdScale);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.addEffect(effectInstance);
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide() && this.getFloorLevel() > 1 && this.isAlive()) {
            int splitCount = Math.min(MAX_SPLIT, this.getFloorLevel() / 2);
            for (int i = 0; i < splitCount; i++) {
                SwampSlimeMob child = new SwampSlimeMob(TYPE, this.level());
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
}
