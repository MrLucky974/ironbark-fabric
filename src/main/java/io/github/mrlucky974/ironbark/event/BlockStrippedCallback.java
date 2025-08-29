package io.github.mrlucky974.ironbark.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface BlockStrippedCallback {
    Event<BlockStrippedCallback> EVENT = EventFactory.createArrayBacked(BlockStrippedCallback.class,
            (listeners) -> (world, pos, player, state) -> {
                for (BlockStrippedCallback listener : listeners) {
                    listener.onWoodStripped(world, pos, player, state);
                }
            });


    void onWoodStripped(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state);
}
