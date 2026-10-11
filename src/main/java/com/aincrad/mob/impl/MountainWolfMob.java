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
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MountainWolfMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "mountain_wolf_mob"));

    public static final EntityType<MountainWolfMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(MountainWolfMob::new, MobCategory.MONSTER)
            .sized(0.8f, 0.9f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 24.0f;
    private static final float BASE_DAMAGE = 5.0f;
    private static final float BASE_SPEED = 0.42f;
    private static final int PACK_RADIUS = 10;

    public MountainWolfMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.PACK;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4f));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.5, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.18f;
        float dmgScale = 1.0f + (floor - 1) * 0.12f;
        float spdScale = 1.0f + (floor - 1) * 0.03f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * spdScale);
        this.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(1.0f + (floor - 1) * 0.05f);
        this.setHealth(BASE_HP * hpScale);
    }

    /**
     * Count nearby allied MountainWolfMobs for pack bonus calculations.
     */
    public int getPackSize() {
        return this.level().getEntitiesOfClass(MountainWolfMob.class,
            this.getBoundingBox().inflate(PACK_RADIUS),
            wolf -> wolf != this && wolf.isAlive()).size() + 1;
    }

    @Override
    public void tick() {
        super.tick();
        // Pack bonus: apply speed boost when nearby allies are present
        if (!this.level().isClientSide() && this.getTarget() != null) {
            int packSize = this.getPackSize();
            if (packSize >= 3) {
                this.getAttribute(Attributes.MOVEMENT_SPEED)
                    .addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                        "pack_speed_bonus", 0.15,
                        net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result) {
            int packSize = getPackSize();
            if (packSize >= 3 && target instanceof Player player) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, false));
            }
        }
        return result;
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }
}
