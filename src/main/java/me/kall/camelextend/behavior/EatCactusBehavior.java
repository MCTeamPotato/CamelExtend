package me.kall.camelextend.behavior;

import com.google.common.collect.ImmutableMap;
import me.kall.camelextend.data.CactusData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EatCactusBehavior extends Behavior<Camel> {
    private BlockPos targetCactusPos;
    private WalkTarget walkTarget;

    public EatCactusBehavior() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), 400, 600);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Camel mob) {
        if (mob.getHealth() >= mob.getMaxHealth()) return false;
        CactusData data = CactusData.get(level);
        ResourceLocation dim = level.dimension().location();

        Map<Long, Set<Long>> chunkMap = data.getAllCactusPositions().get(dim);
        if (chunkMap == null) return false;

        BlockPos mobPos = mob.blockPosition();

        BlockPos nearest = null;
        double nearestDistSqr = Double.MAX_VALUE;

        int sectionX = SectionPos.blockToSectionCoord(mobPos.getX());
        int sectionZ = SectionPos.blockToSectionCoord(mobPos.getZ());

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                long chunkKey = ChunkPos.asLong(sectionX + dx, sectionZ + dz);
                Set<Long> positions = chunkMap.get(chunkKey);
                if (positions == null) continue;

                for (long posLong : positions) {
                    BlockPos cactusPos = BlockPos.of(posLong);
                    double distSqr = cactusPos.distToCenterSqr(mobPos.getX(), mobPos.getY(), mobPos.getZ());
                    if (distSqr < nearestDistSqr) {
                        nearest = cactusPos;
                        nearestDistSqr = distSqr;
                    }
                }
            }
        }

        if (nearest != null) {
            this.targetCactusPos = nearest;
            return true;
        }

        return false;
    }

    @Override
    protected void start(ServerLevel level, Camel mob, long gameTime) {
        if (this.targetCactusPos == null) return;
        this.walkTarget = new WalkTarget(this.targetCactusPos, 1.5F, 1);
        mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, this.walkTarget);
    }

    @Override
    protected void tick(ServerLevel level, Camel mob, long gameTime) {
        if (this.targetCactusPos == null || this.walkTarget == null) return;

        Brain<?> brain = mob.getBrain();
        brain.setMemory(MemoryModuleType.WALK_TARGET, this.walkTarget);
        double distSqr = this.targetCactusPos.distToCenterSqr(mob.getX(), mob.getY(), mob.getZ());

        if (distSqr < 8.0F) {
            eatCactus(level, mob, targetCactusPos);
            CactusData.get(level).removeCactus(level, targetCactusPos);

            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            this.targetCactusPos = null;
            this.walkTarget = null;
        }
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Camel mob, long gameTime) {
        return this.targetCactusPos != null;
    }

    private void eatCactus(ServerLevel level, Camel mob, BlockPos pos) {
        mob.heal(2.0F);
        level.destroyBlock(pos, false, mob);
    }

    @Override
    protected void stop(ServerLevel level, Camel mob, long gameTime) {
        this.targetCactusPos = null;
        this.walkTarget = null;
        mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }
}
