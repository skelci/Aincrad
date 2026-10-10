package com.aincrad.world.portal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A portal entity that exists in the world and checks for player overlap
 * each tick. When a player enters the portal bounds, dimension
 * teleportation is triggered.
 *
 * This entity is invisible and has no collision; it purely serves as a
 * trigger zone for floor-to-floor teleportation.
 */
public class FloorPortalEntity extends Entity {

    private static final Logger LOGGER = LoggerFactory.getLogger("aincrad-portal");

    /** Floor this portal entity is currently on. */
    private int sourceFloor;

    /** Floor this portal entity leads to. */
    private int targetFloor;

    /** Spawn position on the target floor. */
    private BlockPos targetPos;

    /** Whether this portal is active. */
    private boolean isActive;

    /** The type identifier for NBT serialization. */
    public static final String ENTITY_ID = "floor_portal";

    public FloorPortalEntity(EntityType<?> type, World world) {
        super(type, world);
        this.sourceFloor = 1;
        this.targetFloor = 1;
        this.targetPos = BlockPos.ORIGIN;
        this.isActive = true;
        this.noClip = true; // Pass through blocks
    }

    public FloorPortalEntity(EntityType<?> type, World world,
                            int sourceFloor, int targetFloor, BlockPos targetPos) {
        this(type, world);
        this.sourceFloor = sourceFloor;
        this.targetFloor = targetFloor;
        this.targetPos = targetPos;
    }

    /**
     * Main tick logic: check if any player overlaps this portal's bounds
     * and trigger dimension teleportation if so.
     */
    @Override
    public void tick() {
        if (this.level().isClient() || !this.isActive) {
            return;
        }

        Box searchBox = this.getBoundingBox().inflate(0.5);
        List<PlayerEntity> players = this.level().getEntitiesOfClass(
                PlayerEntity.class, searchBox,
                player -> !player.isSpectator() && player.isAlive()
        );

        for (PlayerEntity player : players) {
            LOGGER.debug("Player {} overlapped portal entity (floor {} -> {})",
                    player.getName().getString(), sourceFloor, targetFloor);
            PortalManager.getInstance().teleportToFloor(player, targetFloor);
            break; // Only teleport one player per tick
        }
    }

    /**
     * Returns the spawn position of the target floor.
     */
    public BlockPos getTargetPos() {
        return this.targetPos;
    }

    public int getSourceFloor() {
        return sourceFloor;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    // --- NBT serialization ---

    @Override
    protected void defineSynchedData() {
        // No synchronized data for now
    }

    @Override
    protected void readAdditionalSaveData(NbtCompound nbt) {
        this.sourceFloor = nbt.getInt("SourceFloor");
        this.targetFloor = nbt.getInt("TargetFloor");
        if (nbt.contains("TargetPos")) {
            this.targetPos = BlockPos.of(nbt.getLong("TargetPos"));
        }
        this.isActive = nbt.getBoolean("IsActive");
    }

    @Override
    protected void addAdditionalSaveData(NbtCompound nbt) {
        nbt.putInt("SourceFloor", this.sourceFloor);
        nbt.putInt("TargetFloor", this.targetFloor);
        nbt.putLong("TargetPos", this.targetPos.asLong());
        nbt.putBoolean("IsActive", this.isActive);
    }

    @Override
    public String toString() {
        return "FloorPortalEntity{source=" + sourceFloor + ", target=" + targetFloor +
                ", pos=" + targetPos + ", active=" + isActive + "}";
    }
}
