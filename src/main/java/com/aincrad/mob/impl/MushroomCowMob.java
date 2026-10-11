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
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MushroomCowMob extends MobEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "mushroom_cow_mob"));

    public static final EntityType<MushroomCowMob> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(MushroomCowMob::new, MobCategory.CREATURE)
            .sized(0.9f, 1.4f)
            .build(TYPE_KEY));

    private static final float BASE_HP = 14.0f;
    private static final float BASE_DAMAGE = 0.0f;
    private static final float BASE_SPEED = 0.25f;

    public MushroomCowMob(EntityType<? extends MobEntity> entityType, Level level) {
        super(entityType, level);
        this.mobType = MobType.PASSIVE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.25, net.minecraft.world.item.crafting.Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    protected void applyFloorScaling(int floor) {
        float hpScale = 1.0f + (floor - 1) * 0.15f;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP * hpScale);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
        this.setHealth(BASE_HP * hpScale);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (!this.level().isClientSide()) {
            int count = 1 + this.random.nextInt(3);
            this.spawnAtLocation((net.minecraft.server.level.ServerLevel) this.level(), new net.minecraft.world.item.ItemStack(Items.RED_MUSHROOM, count));
            if (this.random.nextFloat() < 0.5f) {
                this.spawnAtLocation((net.minecraft.server.level.ServerLevel) this.level(), new net.minecraft.world.item.ItemStack(Items.BROWN_MUSHROOM, 1));
            }
            if (this.random.nextFloat() < 0.3f) {
                this.spawnAtLocation((net.minecraft.server.level.ServerLevel) this.level(), new net.minecraft.world.item.ItemStack(Items.LEATHER, 1));
            }
        }
    }
}
