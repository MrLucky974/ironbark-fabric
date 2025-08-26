package io.github.mrlucky974.ironbark.client.gui.screen;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.recipe.TabletCraftingRecipe;
import io.github.mrlucky974.ironbark.screen.CraftingTabletScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class CraftingTabletScreen extends HandledScreen<CraftingTabletScreenHandler> {
    private static final Identifier TEXTURE = Ironbark.id("textures/gui/container/ancient_clay_tablet.png");

    private final RecipeEntry<TabletCraftingRecipe> recipeEntry;

    public CraftingTabletScreen(CraftingTabletScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.recipeEntry = handler.getRecipeEntry();
    }

    protected void init() {
        super.init();
        this.titleX = 29;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawSlot(DrawContext context, Slot slot) {
        if (!slot.hasStack() && slot.inventory == this.handler.getInput()) {
            TabletCraftingRecipe recipe = this.recipeEntry.value();

            int i = slot.x;
            int j = slot.y;
            int k = slot.x + slot.y * this.backgroundWidth;

            DefaultedList<Ingredient> ingredients = recipe.getIngredients();
            if (slot.getIndex() < ingredients.size()) {
                Ingredient ingredient = ingredients.get(slot.getIndex());
                if (!ingredient.isEmpty()) {
                    ItemStack[] matchingStacks = ingredient.getMatchingStacks();

                    if (matchingStacks.length > 0) {
                        ItemStack ghostItem = matchingStacks[0].copyWithCount(1);
                        renderGhostItem(context, ghostItem, i, j, k);
                    }
                }
            }
        }

        super.drawSlot(context, slot);
    }

    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    private void renderGhostItem(DrawContext context, ItemStack stack, int x, int y, int seed) {
        // Save the current matrix state
        context.getMatrices().push();

        // Apply transparency for ghost effect
        context.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);

        // Render the item
        context.drawItem(stack, x, y, seed);

        // Reset shader color
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Restore matrix state
        context.getMatrices().pop();
    }

    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);
    }
}