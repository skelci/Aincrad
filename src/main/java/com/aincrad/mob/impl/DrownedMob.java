package com.aincrad.mob.impl;

import com.aincrad.mob.MobEntity;
import com.aincrad.mob.MobType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Drowned — hostile melee mob that throws tridents.
 * Floors 51-60.
 */
public class DrownedMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "drowned_mob"));

    public static final EntityType<DrownedMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(DrownedMob::new, MobCategory.MONSTER)
            .sized(0.6f, 1.8f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 24.0f;
    private static final float BASE_DAMAGE = 6.0f;
    private static final float BASE_SPEED = 0.3f;
    private static final int TRIDENT_COOLDOWN = 80;
    private int tridentTimer;

    public DrownedMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.HOSTILE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));

        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
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
        if (this.tridentTimer > 0) this.tridentTimer--;

        if (!this.level().isClientSide() && this.tridentTimer <= 0
                && this.getTarget() != null && this.getTarget().isAlive()) {
            double dist = this.distanceTo(this.getTarget());
            if (dist > 4.0 && dist < 12.0) {
                this.throwTrident(this.getTarget());
                this.tridentTimer = TRIDENT_COOLDOWN;
            }
        }
    }

    private void throwTrident(net.minecraft.world.entity.LivingEntity target) {
        Projectile trident = new ThrownTrident(this.level(), this,
            new ItemStack(Items.TRIDENT));
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.3333) - trident.getY();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        trident.shoot(dx, dy + dist * 0.2, dz, 1.5f, (float)(14 - this.level().getDifficulty().getId() * 4));
        this.level().addFreshEntity(trident);
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }
}