package io.github.mrlucky974.ironbark.client.renderer;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.client.IronbarkClient;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BankRenderer implements HudRenderCallback {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static final Identifier COPPER_COIN = Ironbark.id("textures/item/copper_coin.png");

    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MatrixStack matrices = drawContext.getMatrices();

        int x = 8;
        int y = 8;

        matrices.push();
        {
            float scale = 1.2f;
            matrices.scale(scale, scale, 1f);
            drawContext.drawTexture(COPPER_COIN, x, y, 3, 5, 10, 7, 16, 16);
        }
        matrices.pop();

        drawContext.drawText(CLIENT.textRenderer, formatCoins(IronbarkClient.playerData.coins), x + 18, y + 2, 0xFFFFFFFF, true);
    }

    private static String formatCoins(long coins) {
        if (coins >= 1_000_000_000_000L) {
            return trimDecimal(coins / 1_000_000_000_000.0) + "T";
        } else if (coins >= 1_000_000_000L) {
            return trimDecimal(coins / 1_000_000_000.0) + "B";
        } else if (coins >= 1_000_000L) {
            return trimDecimal(coins / 1_000_000.0) + "M";
        } else if (coins >= 1_000L) {
            return trimDecimal(coins / 1_000.0) + "K";
        } else {
            return String.valueOf(coins);
        }
    }

    private static String trimDecimal(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        } else {
            return String.format("%.1f", value);
        }
    }
}
