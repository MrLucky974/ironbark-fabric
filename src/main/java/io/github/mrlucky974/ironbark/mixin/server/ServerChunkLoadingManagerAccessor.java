package io.github.mrlucky974.ironbark.mixin.server;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkLoadingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerChunkLoadingManager.class)
public interface ServerChunkLoadingManagerAccessor {
    @Invoker("getChunkHolder")
    ChunkHolder ironbark$getChunkHolder(long pos);
}
