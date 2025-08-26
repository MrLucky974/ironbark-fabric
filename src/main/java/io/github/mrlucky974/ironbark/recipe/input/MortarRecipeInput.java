package io.github.mrlucky974.ironbark.recipe.input;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record MortarRecipeInput(ItemStack input) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.input;
    }

    @Override
    public int getSize() {
        return this.input != null && !this.input.isEmpty() ? 1 : 0;
    }
}
