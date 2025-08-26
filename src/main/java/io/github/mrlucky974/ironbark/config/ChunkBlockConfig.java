package io.github.mrlucky974.ironbark.config;

import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;

public class ChunkBlockConfig {
    public static final ChunkBlockConfig NONE_BLOCK_CONFIG = new ChunkBlockConfig(0, false, 0);

    private Block block;

    private final int color;
    private final boolean transition;
    private final int effectRadius;

    private int blockRadiusMax;
    private int blockRadiusMin;

    public ChunkBlockConfig(int color, boolean transition, int effectRadius) {
        this.color = color;
        this.transition = transition;
        this.effectRadius = effectRadius;
        parseEffectRadius();
    }

    public ChunkBlockConfig(PacketByteBuf buf) {
        this(buf.readInt(), buf.readBoolean(), buf.readVarInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(color);
        buf.writeBoolean(transition);
        buf.writeVarInt(effectRadius);
    }

    private void parseEffectRadius() {
        int chunkRadius = (int) Math.ceil(effectRadius / 16f);
        if(chunkRadius > IronbarkConfig.chunkRadius)
            IronbarkConfig.chunkRadius = chunkRadius;
        blockRadiusMax = (int) Math.pow(effectRadius, 2);
        blockRadiusMin = (int) Math.pow(effectRadius - 1, 2);
    }

    public ChunkBlockConfig setBlock(Block block) {
        this.block = block;
        return this;
    }

    public Block getBlock() {
        return block;
    }

    public int getColor() {
        return color;
    }

    public boolean isTransition() {
        return transition;
    }

    public int getEffectRadius() {
        return effectRadius;
    }

    public int getBlockRadiusMax() {
        return blockRadiusMax;
    }

    public int getBlockRadiusMin() {
        return blockRadiusMin;
    }
}