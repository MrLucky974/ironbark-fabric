package io.github.mrlucky974.ironbark.inventory;

import io.github.mrlucky974.ironbark.recipe.input.TabletCraftingRecipeInput;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeInputProvider;

import java.util.List;

public interface TabletRecipeInputInventory extends Inventory, RecipeInputProvider {
    int getWidth();

    int getHeight();

    List<ItemStack> getHeldStacks();

    default TabletCraftingRecipeInput createRecipeInput() {
        return this.createPositionedRecipeInput().input();
    }

    default TabletCraftingRecipeInput.Positioned createPositionedRecipeInput() {
        return TabletCraftingRecipeInput.createPositioned(this.getWidth(), this.getHeight(), this.getHeldStacks());
    }
}