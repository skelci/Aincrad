package com.aincrad.mob.impl;

import com.aincrad.mob.MobEntity;
import com.aincrad.mob.MobType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Jellyfish — passive mob that delivers electric damage on contact.
 * Floors 51-60.
 */
public class JellyfishMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "jellyfish_mob"));

    public static final EntityType<JellyfishMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(JellyfishMob::new, MobCategory.MONSTER)
            .sized(0.8f, 1.0f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 18.0f;
    private static final float BASE_DAMAGE = 4.0f;
    private static final float BASE_SPEED = 0.15f;
    private static final float ELECTRIC_DAMAGE = 3.0f;
    private static final double ELECTRIC_RANGE = 1.5;
    private static final int ELECTRIC_COOLDOWN = 40;

    private int electricTimer;

    public JellyfishMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.PASSIVE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.15f;
        float dmgScale = 1.0f + (floor - 1) * 0.10f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.electricTimer > 0) this.electricTimer--;

        if (!this.level().isClientSide() && this.electricTimer <= 0) {
            for (Entity entity : this.level().getEntities(this,
                    this.getBoundingBox().inflate(ELECTRIC_RANGE),
                    e -> e instanceof net.minecraft.world.entity.LivingEntity && e != this)) {
                if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
                    living.hurt(this.level().damageSources().lightningBolt(), ELECTRIC_DAMAGE);
                    this.electricTimer = ELECTRIC_COOLDOWN;
                    break;
                }
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
