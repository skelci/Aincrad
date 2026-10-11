package com.aincrad.mob.impl;

import com.aincrad.crystal.CrystalItem;
import com.aincrad.crystal.CrystalType;
import com.aincrad.crystal.CrystalTier;
import com.aincrad.crystal.drop.CrystalDropHandler;
import com.aincrad.mob.MobEntity;
import com.aincrad.mob.MobType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CrystalBatMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "crystal_bat_mob"));

    public static final EntityType<CrystalBatMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(CrystalBatMob::new, MobCategory.MONSTER)
            .sized(0.6f, 0.6f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 14.0f;
    private static final float BASE_DAMAGE = 3.0f;
    private static final float BASE_SPEED = 0.35f;
    private static final float BONUS_CRYSTAL_CHANCE = 0.15f;

    public CrystalBatMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.HOSTILE;
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.14f;
        float dmgScale = 1.0f + (floor - 1) * 0.09f;
        float spdScale = 1.0f + (floor - 1) * 0.03f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * dmgScale);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * spdScale);
        this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(BASE_SPEED * 2.5f * spdScale);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (!this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            if (this.random.nextFloat() < BONUS_CRYSTAL_CHANCE) {
                CrystalType type = CrystalType.values()[this.random.nextInt(CrystalType.values().length)];
                CrystalTier tier = this.random.nextFloat() < 0.2f ? CrystalTier.FLAWLESS : CrystalTier.ROUGH;
                ItemStack bonusCrystal = new ItemStack(new CrystalItem(type, tier));
                this.spawnAtLocation(serverLevel, bonusCrystal);
            }
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }
}
