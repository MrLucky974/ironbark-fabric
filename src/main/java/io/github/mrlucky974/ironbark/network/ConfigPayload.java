package io.github.mrlucky974.ironbark.network;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.config.ChunkBlockConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;

public record ConfigPayload(boolean serverValidating, Object2ObjectMap<Block, ChunkBlockConfig> blockConfigs) implements CustomPayload {
    public static final Id<ConfigPayload> ID = new Id<>(Ironbark.id("config"));

    public static final PacketCodec<PacketByteBuf, ConfigPayload> PACKET_CODEC =
            PacketCodec.of(ConfigPayload::write, ConfigPayload::read);

    private void write(PacketByteBuf buf) {
        buf.writeBoolean(this.serverValidating);
        buf.writeVarInt(this.blockConfigs.size());

        for (Map.Entry<Block, ChunkBlockConfig> entry : this.blockConfigs().entrySet()) {
            buf.writeIdentifier(Registries.BLOCK.getId(entry.getKey())); // Block ID
            entry.getValue().write(buf); // use your existing write method
        }
    }

    private static ConfigPayload read(PacketByteBuf buf) {
        boolean serverValidating = buf.readBoolean();
        int size = buf.readVarInt();

        Object2ObjectMap<Block, ChunkBlockConfig> map = new Object2ObjectOpenHashMap<>();
        for (int i = 0; i < size; i++) {
            Identifier blockId = buf.readIdentifier();
            Block block = Registries.BLOCK.get(blockId);
            ChunkBlockConfig config = new ChunkBlockConfig(buf).setBlock(block);
            map.put(block, config);
        }

        return new ConfigPayload(serverValidating, map);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
