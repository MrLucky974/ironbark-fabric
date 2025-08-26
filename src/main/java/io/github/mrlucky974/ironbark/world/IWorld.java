package io.github.mrlucky974.ironbark.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface IWorld {
    void spelunker$UpdateBlock(BlockPos pos, BlockState oldBlock, BlockState newBlock);
    void spelunker$UpdateChunks();
}
