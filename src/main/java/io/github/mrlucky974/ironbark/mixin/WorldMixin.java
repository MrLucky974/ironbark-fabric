package io.github.mrlucky974.ironbark.mixin;

import io.github.mrlucky974.ironbark.client.IronbarkClient;
import io.github.mrlucky974.ironbark.config.ChunkBlockConfig;
import io.github.mrlucky974.ironbark.config.IronbarkConfig;
import io.github.mrlucky974.ironbark.init.StatusEffectInit;
import io.github.mrlucky974.ironbark.network.OreChunksPayload;
import io.github.mrlucky974.ironbark.world.IWorld;
import io.github.mrlucky974.ironbark.world.chunk.ChunkOres;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(World.class)
public abstract class WorldMixin implements IWorld {
    @Shadow
    public abstract boolean isClient();

    @Shadow @Nullable
    public abstract MinecraftServer getServer();

    @Unique
    private final Map<Vec3i, ChunkOres> dirtySpelunkerChunks = new ConcurrentHashMap<>();

    @Inject(method = "onBlockChanged", at = @At("HEAD"))
    private void ironbark$onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci) {
        spelunker$UpdateBlock(pos, oldBlock, newBlock);
    }

    @Override
    public void spelunker$UpdateBlock(BlockPos pos, BlockState oldBlock, BlockState newBlock) {
        if(!(IronbarkConfig.isOreBlock(oldBlock.getBlock()) || IronbarkConfig.isOreBlock(newBlock.getBlock())))
            return;

        Vec3i chunkPos = new Vec3i(
                ChunkSectionPos.getSectionCoord(pos.getX()),
                ((World) (Object) this).sectionCoordToIndex(ChunkSectionPos.getSectionCoord(pos.getY())),
                ChunkSectionPos.getSectionCoord(pos.getZ())
        );
        if (isClient()) {
            spelunker$UpdateBlockClient(chunkPos, pos, newBlock);
            return;
        }

        dirtySpelunkerChunks.compute(chunkPos, (p, chunk) -> {
            if (chunk == null)
                chunk = new ChunkOres(chunkPos);
            chunk.put(ChunkOres.toLocalCoord(pos), IronbarkConfig.blockConfigs.getOrDefault(newBlock.getBlock(), ChunkBlockConfig.NONE_BLOCK_CONFIG));
            return chunk;
        });
    }

    @Unique
    @Environment(EnvType.CLIENT)
    private void spelunker$UpdateBlockClient(Vec3i chunkPos, BlockPos pos, BlockState newBlock) {
        MinecraftClient client = MinecraftClient.getInstance();
        if ((!IronbarkConfig.serverValidating || client.isInSingleplayer()) && client.player != null && client.player.hasStatusEffect(StatusEffectInit.SPELUNKER)) {
            ChunkOres chunk = IronbarkClient.effectRenderer.get(chunkPos);
            if (chunk != null)
                chunk.processConfig(pos, IronbarkConfig.blockConfigs.get(newBlock.getBlock()), false);
        }
    }

    @Override
    public void spelunker$UpdateChunks() {
        if(dirtySpelunkerChunks.isEmpty())
            return;

        if (!IronbarkConfig.serverValidating)
            return;

        Collection<ServerPlayerEntity> players = PlayerLookup.all(Objects.requireNonNull(getServer())).stream()
                .filter(p -> p.hasStatusEffect(StatusEffectInit.SPELUNKER))
                .toList();

        if (players.isEmpty())
            return;

        OreChunksPayload payload = new OreChunksPayload(false, Collections.emptyList(), dirtySpelunkerChunks.values(), ((World) (Object) this).getBottomSectionCoord());
        dirtySpelunkerChunks.clear();
        for (ServerPlayerEntity p : players)
            if (p.hasStatusEffect(StatusEffectInit.SPELUNKER))
                ServerPlayNetworking.send(p, payload);
    }
}
