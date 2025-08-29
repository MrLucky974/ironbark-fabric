package io.github.mrlucky974.ironbark.mixin.client;

import io.github.mrlucky974.ironbark.event.HudRenderBeforeChatCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At(value = "TAIL"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        HudRenderBeforeChatCallback.EVENT.invoker().onHudRender(context, tickCounter);
    }
}