package io.github.mrlucky974.ironbark.mixin.client;

import io.github.mrlucky974.ironbark.client.IronbarkClient;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @ModifyVariable(
            method = "render",
            at = @At(value = "CONSTANT", args = "stringValue=blockentities", ordinal = 0),
            ordinal = 3
    )
    private boolean ironbark$modify(boolean value) {
        return value || IronbarkClient.effectRenderer.isActive();
    }
}
