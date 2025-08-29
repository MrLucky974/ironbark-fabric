package io.github.mrlucky974.ironbark.client.renderer.hud;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.event.HudRenderBeforeCallback;
import io.github.mrlucky974.ironbark.init.StatusEffectInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class IronGutsHudRenderer implements HudRenderBeforeCallback {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final Identifier IRON_GUTS = Ironbark.id("textures/gui/sprites/iron_guts.png");

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        int width = drawContext.getScaledWindowWidth();
        int height = drawContext.getScaledWindowHeight();
        int x = width / 2;
        int yOffset = 39;
        int y = height - yOffset;

        ClientPlayerEntity player = CLIENT.player;
        assert player != null;

        if (player.isCreative()) {
            return;
        }

        if (!player.hasStatusEffect(StatusEffectInit.IRON_GUTS)) {
            return;
        }

        int hunger = player.getHungerManager().getFoodLevel();
        for (int i = 0; i < 10; i++) {
            if (hunger - 1 >= i * 2 + 1) {
                drawContext.drawTexture(IRON_GUTS, x + 82 - (i * 8), y, 0, 0, 9, 9, 18, 9);
            } else if (hunger - 1 >= i * 2) {
                drawContext.drawTexture(IRON_GUTS, x + 82 - (i * 8), y, 9, 0, 9, 9, 18, 9);
            } else {
                break;
            }
        }
    }
}
