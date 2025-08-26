package io.github.mrlucky974.ironbark;

import io.github.mrlucky974.ironbark.client.renderer.IronbarkEffectRenderer;
import io.github.mrlucky974.ironbark.config.ChunkBlockConfig;
import io.github.mrlucky974.ironbark.config.IronbarkConfig;
import io.github.mrlucky974.ironbark.mixin.server.ServerChunkLoadingManagerAccessor;
import io.github.mrlucky974.ironbark.network.OreChunksPayload;
import io.github.mrlucky974.ironbark.world.chunk.ChunkOres;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IronbarkEffectManager {
    public static ChunkOres findOresInChunk(World world, Vec3i sectionPos) {
        Chunk chunk = null;
        if (world.getChunkManager().isChunkLoaded(sectionPos.getX(), sectionPos.getZ())) {
            if (world instanceof ServerWorld sw) {
                ServerChunkLoadingManager manager = sw.getChunkManager().chunkLoadingManager;
                ChunkHolder chunkHolder = ((ServerChunkLoadingManagerAccessor) manager)
                        .ironbark$getChunkHolder(ChunkPos.toLong(sectionPos.getX(), sectionPos.getZ()));

                if (chunkHolder != null) {
                    chunk = chunkHolder.getWorldChunk();
                }
            } else {
                chunk = world.getChunk(sectionPos.getX(), sectionPos.getZ(), ChunkStatus.FULL, false);
            }
        }

        if (chunk == null)
            return ChunkOres.EMPTY;

        ChunkSection section = chunk.getSection(sectionPos.getY());
        ChunkOres ores = new ChunkOres(sectionPos);
        var blockStates = section.getBlockStateContainer();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = blockStates.get(x, y, z).getBlock();
                    if (IronbarkConfig.isOreBlock(block)) {
                        Vec3i blockPos = new Vec3i(x, y, z);
                        ores.put(blockPos, IronbarkConfig.blockConfigs.get(block));
                    }
                }
            }
        }

        Ironbark.LOGGER.info("Found {} ores in chunk at {}", ores.size(), sectionPos);
        return ores;
    }

    public static HashMap<Vec3i, ChunkSection> getSurroundingChunkSections(World world, Vec3d playerPos) {
        int cx = ChunkSectionPos.getSectionCoord(playerPos.x);
        int cy = world.sectionCoordToIndex(ChunkSectionPos.getSectionCoord(playerPos.y));
        int cz = ChunkSectionPos.getSectionCoord(playerPos.z);

        HashMap<Vec3i, ChunkSection> sections = new HashMap<>();
        for (int x = cx - IronbarkConfig.chunkRadius; x < cx + IronbarkConfig.chunkRadius + 1; x++) {
            for (int z = cz - IronbarkConfig.chunkRadius; z < cz + IronbarkConfig.chunkRadius + 1; z++) {
                if (!world.getChunkManager().isChunkLoaded(x, z)) {
                    continue; // Skip unloaded chunks
                }

                WorldChunk chunk = world.getChunk(x, z);
                ChunkSection[] sectionArray = chunk.getSectionArray();

                for (int y = cy - IronbarkConfig.chunkRadius; y < cy + IronbarkConfig.chunkRadius + 1; y++) {
                    if (y < 0 || y >= sectionArray.length)
                        continue;

                    ChunkSection section = sectionArray[y];
                    if (section == null || section.isEmpty())
                        continue;

                    // Use section array indices as the key, not world coordinates
                    sections.put(new Vec3i(x, y, z), section);
                }
            }
        }

        return sections;
    }

    @Environment(EnvType.CLIENT)
    public static void readPayload(IronbarkEffectRenderer renderer, OreChunksPayload payload) {
        boolean overwrite = payload.overwrite();

        for (Vec3i chunkPos : payload.remove()) {
            renderer.removeChunk(chunkPos);
        }

        ArrayList<ChunkOres> chunks = new ArrayList<>();
        for (ChunkOres addOres : payload.add()) {
            Vec3i pos = addOres.getPos();

            ChunkOres ores = overwrite ? new ChunkOres(pos) : renderer.get(pos);
            for (Map.Entry<Vec3i, ChunkBlockConfig> ore : addOres.entrySet()) {
                Vec3i orePos = ore.getKey();
                ChunkBlockConfig config = ore.getValue();

                if (ores != null)
                    ores.processConfig(orePos, config == ChunkBlockConfig.NONE_BLOCK_CONFIG ? null : config, true);
            }

            if (overwrite)
                chunks.add(ores);
        }

        if (overwrite) {
            int bottomSectionCord = payload.bottomSectionCoord();
            renderer.addChunks(bottomSectionCord, chunks);
        }
    }
}
