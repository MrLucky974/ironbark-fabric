package io.github.mrlucky974.ironbark.network;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.config.ChunkBlockConfig;
import io.github.mrlucky974.ironbark.world.chunk.ChunkOres;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public record OreChunksPayload(boolean overwrite, Collection<Vec3i> remove, Collection<ChunkOres> add, int bottomSectionCoord) implements CustomPayload {
    public static final Id<OreChunksPayload> ID = new Id<>(Ironbark.id("ore_chunks"));

    public static final PacketCodec<PacketByteBuf, OreChunksPayload> PACKET_CODEC =
            PacketCodec.of(OreChunksPayload::write, OreChunksPayload::read);

    private void write(PacketByteBuf buf) {
        buf.writeBoolean(overwrite);

        buf.writeVarInt(remove.size());
        for (Vec3i pos : remove) {
            buf.writeVarInt(pos.getX());
            buf.writeVarInt(pos.getY());
            buf.writeVarInt(pos.getZ());
        }

        buf.writeVarInt(add.size());
        for (ChunkOres ores : add) {
            Vec3i pos = ores.getPos();
            buf.writeVarInt(pos.getX());
            buf.writeVarInt(pos.getY());
            buf.writeVarInt(pos.getZ());

            buf.writeVarInt(ores.size());
            for (Map.Entry<Vec3i, ChunkBlockConfig> ore : ores.entrySet()) {
                Vec3i orePos = ore.getKey();
                buf.writeByte(orePos.getX());
                buf.writeByte(orePos.getY());
                buf.writeByte(orePos.getZ());

                ChunkBlockConfig conf = ore.getValue();
                buf.writeVarInt(ChunkBlockConfig.NONE_BLOCK_CONFIG == conf ? -1 : Registries.BLOCK.getRawId(conf.getBlock()));
            }
        }

        if (overwrite)
            buf.writeVarInt(this.bottomSectionCoord);
    }

    private static OreChunksPayload read(PacketByteBuf buf) {
        boolean overwrite = buf.readBoolean();

        // Read "remove" list
        int removeSize = buf.readVarInt();
        Collection<Vec3i> remove = new ArrayList<>(removeSize);
        for (int i = 0; i < removeSize; i++) {
            int x = buf.readVarInt();
            int y = buf.readVarInt();
            int z = buf.readVarInt();
            remove.add(new Vec3i(x, y, z));
        }

        // Read "add" list
        int addSize = buf.readVarInt();
        Collection<ChunkOres> add = new ArrayList<>(addSize);
        for (int i = 0; i < addSize; i++) {
            int cx = buf.readVarInt();
            int cy = buf.readVarInt();
            int cz = buf.readVarInt();
            Vec3i chunkPos = new Vec3i(cx, cy, cz);

            int oreCount = buf.readVarInt();
            ChunkOres ores = new ChunkOres(chunkPos);
            for (int j = 0; j < oreCount; j++) {
                int ox = buf.readByte();
                int oy = buf.readByte();
                int oz = buf.readByte();
                Vec3i orePos = new Vec3i(ox, oy, oz);

                int rawId = buf.readVarInt();
                ChunkBlockConfig conf;
                if (rawId == -1) {
                    conf = ChunkBlockConfig.NONE_BLOCK_CONFIG;
                } else {
                    conf = new ChunkBlockConfig(0, false, 0).setBlock(Registries.BLOCK.get(rawId));
                }

                ores.put(orePos, conf);
            }

            add.add(ores);
        }

        // Read bottomSectionCoord only if overwrite == true
        int bottomSectionCord = overwrite ? buf.readVarInt() : 0;
        return new OreChunksPayload(overwrite, remove, add, bottomSectionCord);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
