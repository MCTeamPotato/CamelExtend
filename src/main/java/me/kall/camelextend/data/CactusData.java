package me.kall.camelextend.data;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CactusData extends SavedData {
    public static final String DATA_NAME = "camelextend_cactus_data";

    private final Map<ResourceLocation, Map<Long, Set<Long>>> cactusPositions = new ConcurrentHashMap<>();

    public static CactusData load(CompoundTag nbt) {
        CactusData data = new CactusData();

        for (String dimKey : nbt.getAllKeys()) {
            CompoundTag dimTag = nbt.getCompound(dimKey);
            Map<Long, Set<Long>> chunkMap = new ConcurrentHashMap<>();

            for (String chunkKeyStr : dimTag.getAllKeys()) {
                long chunkKey = Long.parseLong(chunkKeyStr);
                ListTag posList = dimTag.getList(chunkKeyStr, Tag.TAG_LONG);

                Set<Long> posSet = ConcurrentHashMap.newKeySet();
                for (Tag tag : posList) {
                    posSet.add(((LongTag) tag).getAsLong());
                }

                chunkMap.put(chunkKey, posSet);
            }
            ResourceLocation dimRes = ResourceLocation.tryParse(dimKey);
            if (dimRes != null) {
                data.cactusPositions.put(dimRes, chunkMap);
            }
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        for (Map.Entry<ResourceLocation, Map<Long, Set<Long>>> dimEntry : cactusPositions.entrySet()) {
            CompoundTag dimTag = new CompoundTag();
            for (Map.Entry<Long, Set<Long>> chunkEntry : dimEntry.getValue().entrySet()) {
                ListTag posList = new ListTag();
                for (Long posLong : chunkEntry.getValue()) {
                    posList.add(LongTag.valueOf(posLong));
                }
                dimTag.put(Long.toString(chunkEntry.getKey()), posList);
            }
            nbt.put(dimEntry.getKey().toString(), dimTag);
        }
        return nbt;
    }

    public static CactusData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(CactusData::load, CactusData::new, DATA_NAME);
    }

    public void addCactus(ServerLevel level, BlockPos pos) {
        ResourceLocation dim = level.dimension().location();
        Map<Long, Set<Long>> chunkMap = cactusPositions.computeIfAbsent(dim, k -> new ConcurrentHashMap<>());
        long chunkKey = ChunkPos.asLong(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
        Set<Long> posSet = chunkMap.computeIfAbsent(chunkKey, k -> ConcurrentHashMap.newKeySet());
        if (posSet.add(pos.asLong())) {
            setDirty();
        }
    }

    public void removeCactus(ServerLevel level, BlockPos pos) {
        ResourceLocation dim = level.dimension().location();
        Map<Long, Set<Long>> chunkMap = cactusPositions.get(dim);
        if (chunkMap == null) return;
        long chunkKey = ChunkPos.asLong(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
        Set<Long> posSet = chunkMap.get(chunkKey);
        if (posSet == null) return;
        if (posSet.remove(pos.asLong())) {
            setDirty();
        }
    }

    public Map<ResourceLocation, Map<Long, Set<Long>>> getAllCactusPositions() {
        return cactusPositions;
    }
}
