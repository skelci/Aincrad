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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Invisible ambusher that strikes from stealth with a poison AOE burst.
 * Theme 8 (Swamp/Poison) — AMBUSHER type.
 */
public class VenomWraithMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "venom_wraith_mob"));

    public static final EntityType<VenomWraithMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(VenomWraithMob::new, MobCategory.MONSTER)
            .sized(0.8f, 1.9f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 22.0f;
    private static final float BASE_DAMAGE = 5.0f;
    private static final float BASE_SPEED = 0.32f;
    private static final double POISON_AOE_RADIUS = 3.0;
    private static final int POISON_DURATION = 150;

    public VenomWraithMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.AMBUSHER;
        this.setInvisible(true);
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
        float hpScale = 1.0f + (floor - 1) * 0.16f;
        float dmgScale = 1.0f + (floor - 1) * 0.14f;
        float spdScale = 1.0f + (floor - 1) * 0.03f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * spdScale);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void setTarget(LivingEntity target) {
        super.setTarget(target);
        this.setInvisible(target == null);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && !this.level().isClientSide()) {
            AABB aabb = new AABB(
                this.getX() - POISON_AOE_RADIUS,
                this.getY() - POISON_AOE_RADIUS,
                this.getZ() - POISON_AOE_RADIUS,
                this.getX() + POISON_AOE_RADIUS,
                this.getY() + POISON_AOE_RADIUS,
                this.getZ() + POISON_AOE_RADIUS
            );
            for (Entity entity : this.level().getEntities(this, aabb)) {
                if (entity instanceof LivingEntity livingEntity && entity != this) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION, 0));
                }
            }
        }
        return result;
    }
}
