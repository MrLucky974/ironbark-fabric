package io.github.mrlucky974.ironbark.client.renderer.item;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class ScytheItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

    }
}
