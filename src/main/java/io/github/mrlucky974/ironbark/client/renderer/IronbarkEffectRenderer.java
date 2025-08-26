package io.github.mrlucky974.ironbark.client.renderer;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.config.ChunkBlockConfig;
import io.github.mrlucky974.ironbark.config.IronbarkConfig;
import io.github.mrlucky974.ironbark.world.chunk.ChunkOres;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IronbarkEffectRenderer {
    private static final ModelPart.Cuboid CUBE = new ModelPart.Cuboid(0, 0, 0, 0, 0, 16, 16, 16, 0, 0, 0, false, 0, 0, EnumSet.allOf(Direction.class));
    private static final RenderLayer RENDER_LAYER = RenderLayer.getOutline(Ironbark.id("textures/none.png"));

    private final ConcurrentMap<Vec3i, ChunkOres> chunkSections = new ConcurrentHashMap<>();
    private boolean active = false;

    public void render(MatrixStack matrices, Camera camera, OutlineVertexConsumerProvider vertexConsumers) {
        Vec3d pos = camera.getPos();
        matrices.push();
        matrices.translate(-pos.x, -pos.y, -pos.z);
        for (Map.Entry<Vec3i, ChunkOres> chunkSection : chunkSections.entrySet()) // render sections
            renderChunk(chunkSection.getValue(), matrices, pos, vertexConsumers);
        matrices.pop();
    }

    public boolean setActive(boolean value) {
        boolean init = value && !active;
        this.active = value;
        return init;
    }

    public boolean isActive() {
        return active;
    }

    public void clear() {
        chunkSections.clear();
    }

    public void updateChunks(World world, Collection<Vec3i> remove, Collection<ChunkOres> add) {
        for (Vec3i v : remove)
            chunkSections.remove(v);
        for (ChunkOres chunk : add) {
            chunkSections.put(chunk.getPos(), chunk
                    .remapToBlockCoordinates(world.getBottomSectionCoord())
            );
        }
    }

    public void removeChunk(Vec3i pos) {
        chunkSections.remove(pos);
    }

    public ChunkOres get(Vec3i pos) {
        return chunkSections.get(pos);
    }

    public void addChunks(int bottomSectionCord, Collection<ChunkOres> chunks) {
        for (ChunkOres chunk : chunks)
            chunkSections.put(chunk.getPos(), chunk.remapToBlockCoordinates(bottomSectionCord));
    }

    /*
     * RENDER CHUNKS
     */

    public void renderChunk(ChunkOres chunk, MatrixStack matrices, Vec3d playerPos, OutlineVertexConsumerProvider vertexConsumers) {
        for (Map.Entry<Vec3i, ChunkBlockConfig> ore : chunk.entrySet()) {
            Vec3i pos = ore.getKey();
            double squareDistance = toSquaredDistanceFromCenter(pos, playerPos.getX(), playerPos.getY(), playerPos.getZ());
            ChunkBlockConfig block = ore.getValue();
            if (squareDistance > block.getBlockRadiusMax())
                continue;
            float fade;
            if (IronbarkConfig.globalTransition && block.isTransition()) {
                fade = Math.min(1 - (float) ((squareDistance - block.getBlockRadiusMin()) / (block.getBlockRadiusMax() - block.getBlockRadiusMin())), 1);
                fade = easeOutCirc(fade);
            } else fade = 1;
            matrices.push();
            matrices.translate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            matrices.scale(fade, fade, fade);
            {
                matrices.push();
                matrices.translate(-0.5, -0.5, -0.5);
                CUBE.renderCuboid(matrices.peek(), setOutlineColor(block.getColor(), vertexConsumers), 0, OverlayTexture.DEFAULT_UV, 0);
                matrices.pop();
            }
            matrices.pop();
        }
    }

    private VertexConsumer setOutlineColor(int color, OutlineVertexConsumerProvider vertexConsumers) {
        vertexConsumers.setColor((color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff, 255);
        return vertexConsumers.getBuffer(RENDER_LAYER);
    }

    private static float easeOutCirc(float x) {
        return (float) Math.sqrt(1 - Math.pow(x - 1, 2));
    }

    private static double toSquaredDistanceFromCenter(Vec3i pos, double x, double y, double z) {
        double d = (double) pos.getX() + 0.5D - x;
        double e = (double) pos.getY() + 0.5D - y;
        double f = (double) pos.getZ() + 0.5D - z;
        return d * d + e * e + f * f;
    }
}
