package me.kall.camelextend.behavior;

import com.google.common.collect.ImmutableMap;
import me.kall.camelextend.data.CamelEdibleBlocks;
import me.kall.duplicationless.data.ChunkData;
import me.kall.duplicationless.util.Positions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class EatEdibleBlockBehavior extends Behavior<Camel> {
    private @Nullable BlockPos target;
    private @Nullable WalkTarget walkTarget;

    public EatEdibleBlockBehavior() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), 400, 600);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Camel mob) {
        if (mob.isPanicking()) return false;
        if (mob.getHealth() >= mob.getMaxHealth()) return false;
        if (level.getServer().getTickCount() % 200 != 0) return false;

        ChunkData<Long, BlockState> data = CamelEdibleBlocks.get(level);
        BlockPos mobPos = mob.blockPosition();
        long chunk = Positions.toChunk(mobPos);
        long target = data.pick(level, chunk).orElse(Long.MIN_VALUE);

        if (target != Long.MIN_VALUE) {
            this.target = BlockPos.of(target);
            return true;
        }

        int chunkX = ChunkPos.getX(chunk);
        int chunkZ = ChunkPos.getZ(chunk);
        for (int radius = 1; radius < 3; radius++) {
            for(int x = -radius; x <= radius; ++x) {
                for(int z = -radius; z <= radius; ++z) {
                    if (x == 0 && z == 0) continue;
                    target = data.pick(level, ChunkPos.asLong(x + chunkX, z + chunkZ)).orElse(Long.MIN_VALUE);
                    if (target != Long.MIN_VALUE) {
                        this.target = BlockPos.of(target);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected void start(ServerLevel level, Camel mob, long gameTime) {
        if (this.target == null) return;
        this.walkTarget = new WalkTarget(this.target, 4.0F, 1);
        mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, this.walkTarget);
    }

    @Override
    protected void tick(ServerLevel level, Camel mob, long gameTime) {
        if (this.target == null || this.walkTarget == null) return;

        Brain<?> brain = mob.getBrain();
        brain.setMemory(MemoryModuleType.WALK_TARGET, this.walkTarget);
        double distSqr = this.target.distToCenterSqr(mob.getX(), mob.getY(), mob.getZ());

        if (distSqr < 6.4F) {
            this.eat(level, mob, this.target);
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            this.walkTarget = null;
            this.target = null;
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
