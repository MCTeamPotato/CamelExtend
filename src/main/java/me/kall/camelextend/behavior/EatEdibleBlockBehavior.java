package me.kall.camelextend.behavior;

import com.google.common.collect.ImmutableMap;
import me.kall.camelextend.data.CamelEdibleData;
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
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EatEdibleBlockBehavior extends Behavior<Camel> {
    private BlockPos target;
    private WalkTarget walkTarget;

    private static final int[] SELF_CHUNK_X = {0};
    private static final int[] SELF_CHUNK_Z = {0};

    private static final int[] ORTHOGONAL_CHUNK_X = {0, 1, 0, -1};
    private static final int[] ORTHOGONAL_CHUNK_Z = {-1, 0, 1, 0};

    private static final int[] DIAGONAL_CHUNK_X = {1, 1, -1, -1};
    private static final int[] DIAGONAL_CHUNK_Z = {-1, 1, 1, -1};

    public EatEdibleBlockBehavior() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), 400, 600);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Camel mob) {
        if (mob.getHealth() >= mob.getMaxHealth()) return false;

        CamelEdibleData data = CamelEdibleData.get(level);
        ResourceLocation dim = level.dimension().location();
        Map<Long, Set<Long>> chunkMap = data.getAllCamelEdibleBlocks().get(dim);
        if (chunkMap == null || chunkMap.isEmpty()) return false;

        BlockPos mobPos = mob.blockPosition();
        int baseSectionX = SectionPos.blockToSectionCoord(mobPos.getX());
        int baseSectionZ = SectionPos.blockToSectionCoord(mobPos.getZ());

        BlockPos found = searchChunkLayer(chunkMap, baseSectionX, baseSectionZ, SELF_CHUNK_X, SELF_CHUNK_Z);
        if (found != null) {
            this.target = found;
            return true;
        }

        found = searchChunkLayer(chunkMap, baseSectionX, baseSectionZ, ORTHOGONAL_CHUNK_X, ORTHOGONAL_CHUNK_Z);
        if (found != null) {
            this.target = found;
            return true;
        }

        found = searchChunkLayer(chunkMap, baseSectionX, baseSectionZ, DIAGONAL_CHUNK_X, DIAGONAL_CHUNK_Z);
        if (found != null) {
            this.target = found;
            return true;
        }

        return false;
    }

    private @Nullable BlockPos searchChunkLayer(Map<Long, Set<Long>> chunkMap, int baseX, int baseZ, int[] xOffsets, int[] zOffsets) {
        for (int i = 0; i < xOffsets.length; i++) {
            int chunkX = baseX + xOffsets[i];
            int chunkZ = baseZ + zOffsets[i];
            long chunkKey = ChunkPos.asLong(chunkX, chunkZ);

            Set<Long> positions = chunkMap.get(chunkKey);
            if (positions != null && !positions.isEmpty()) {
                return BlockPos.of(positions.iterator().next());
            }
        }
        return null;
    }

    @Override
    protected void start(ServerLevel level, Camel mob, long gameTime) {
        if (this.target == null) return;
        this.walkTarget = new WalkTarget(this.target, 1.5F, 1);
        mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, this.walkTarget);
    }

    @Override
    protected void tick(ServerLevel level, Camel mob, long gameTime) {
        if (this.target == null || this.walkTarget == null) return;

        Brain<?> brain = mob.getBrain();
        brain.setMemory(MemoryModuleType.WALK_TARGET, this.walkTarget);
        double distSqr = this.target.distToCenterSqr(mob.getX(), mob.getY(), mob.getZ());

        if (distSqr < 8.0F) {
            eat(level, mob, target);
            CamelEdibleData.get(level).removeCamelEdible(level, target);

            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            this.target = null;
            this.walkTarget = null;
        }
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Camel mob, long gameTime) {
        return this.target != null;
    }

    private void eat(ServerLevel level, Camel mob, BlockPos pos) {
        mob.heal(2.0F);
        level.destroyBlock(pos, false, mob);
    }

    @Override
    protected void stop(ServerLevel level, Camel mob, long gameTime) {
        this.target = null;
        this.walkTarget = null;
        mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }
}
