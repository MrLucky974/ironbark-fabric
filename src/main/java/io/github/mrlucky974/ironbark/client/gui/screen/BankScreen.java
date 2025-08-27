package io.github.mrlucky974.ironbark.client.gui.screen;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.screen.BankScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BankScreen extends HandledScreen<BankScreenHandler> {
    private static final Identifier TEXTURE = Ironbark.id("textures/gui/container/bank.png");
    private static final Identifier COIN_OUTLINE = Ironbark.id("textures/gui/sprites/coin_outline.png");

    public BankScreen(BankScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        this.titleY = 7;
        this.backgroundHeight = 148;
        this.playerInventoryTitleY = this.backgroundHeight - 95;

        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        context.drawTexture(COIN_OUTLINE, i + 12, j + 20, 0, 0, 16, 16, 16, 16);
    }
}
