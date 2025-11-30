package me.kall.camelextend;

import me.kall.camelextend.data.CamelEdibleBlocks;
import me.kall.duplicationless.event.BlockChangeEvent;
import me.kall.duplicationless.util.Executor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import org.jetbrains.annotations.NotNull;

@Mod(CamelExtend.MOD_ID)
public final class CamelExtend {
    public static final String MOD_ID = "camelextend";
    public static final TagKey<Block> CAMEL_EDIBLE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "camel_edible"));

    public CamelExtend() {
        IEventBus forgeBus = NeoForge.EVENT_BUS;

        forgeBus.addListener(this::dataRebuild);
        forgeBus.addListener(this::blockChange);
    }

    public void dataRebuild(ChunkEvent.@NotNull Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ChunkPos pos = event.getChunk().getPos();
            Executor.run(() -> CamelEdibleBlocks.get(level).rebuildChunk(level, pos));
        }
    }

    public void blockChange(@NotNull BlockChangeEvent event) {
        boolean was = event.oldState().is(CAMEL_EDIBLE);
        boolean is =  event.newState().is(CAMEL_EDIBLE);

        ServerLevel level = event.level();
        long chunk = event.chunkPos();
        long block = event.blockPos();

        if (was) Executor.run(() -> CamelEdibleBlocks.get(level).remove(level, chunk, block));
        if (is) Executor.run(() -> CamelEdibleBlocks.get(level).add(level, chunk, block));
    }
}
