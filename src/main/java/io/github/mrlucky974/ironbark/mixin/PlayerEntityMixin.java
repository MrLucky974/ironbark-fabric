package io.github.mrlucky974.ironbark.mixin;

import io.github.mrlucky974.ironbark.IronbarkEffectManager;
import io.github.mrlucky974.ironbark.client.IronbarkClient;
import io.github.mrlucky974.ironbark.config.IronbarkConfig;
import io.github.mrlucky974.ironbark.init.StatusEffectInit;
import io.github.mrlucky974.ironbark.network.OreChunksPayload;
import io.github.mrlucky974.ironbark.world.chunk.ChunkOres;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Unique
    private int lastCx, lastCy, lastCz;
    @Unique
    private boolean forceOreChunkUpdate = true;
    @Unique
    private final HashSet<Vec3i> spelunkerEffectChunks = new HashSet<>();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void moveEndInject(CallbackInfo ci) {
        World world = getWorld();

        if (!hasStatusEffect(StatusEffectInit.SPELUNKER)) {
            if (!spelunkerEffectChunks.isEmpty())
                spelunkerEffectChunks.clear();
            forceOreChunkUpdate = true;
            return;
        }

        if (IronbarkConfig.serverValidating && world.isClient())
            return;

        int cx = ChunkSectionPos.getSectionCoord(getX());
        int cy = ChunkSectionPos.getSectionCoord(getY());
        int cz = ChunkSectionPos.getSectionCoord(getZ());

        // update if player crosses chunk border
        if (cx != lastCx || cy != lastCy || cz != lastCz || forceOreChunkUpdate) {
            forceOreChunkUpdate = false;
            HashMap<Vec3i, ChunkSection> newChunks = IronbarkEffectManager.getSurroundingChunkSections(world, getPos());

            // calc difference and find ores
            HashSet<Vec3i> remove = new HashSet<>();
            spelunkerEffectChunks.removeIf(p -> {
                if (!newChunks.containsKey(p)) {
                    remove.add(p);
                    return true;
                }
                return false;
            });

            ArrayList<ChunkOres> add = new ArrayList<>();
            for (Map.Entry<Vec3i, ChunkSection> section : newChunks.entrySet()) {
                Vec3i pos = section.getKey();
                if (!spelunkerEffectChunks.contains(pos)) {
                    add.add(IronbarkEffectManager.findOresInChunk(world, pos));
                    spelunkerEffectChunks.add(pos);
                }
            }

            // handle new and removed chunk sections
            if (world.isClient()) {
                IronbarkClient.effectRenderer.updateChunks(world, remove, add);
            } else if (IronbarkConfig.serverValidating) {
                OreChunksPayload payload = new OreChunksPayload(true, remove, add, world.getBottomSectionCoord());
                ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, payload);
            }
        }

        lastCx = cx;
        lastCy = cy;
        lastCz = cz;
    }
}
