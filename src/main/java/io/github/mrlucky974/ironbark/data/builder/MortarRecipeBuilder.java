package io.github.mrlucky974.ironbark.data.builder;

import io.github.mrlucky974.ironbark.recipe.MortarRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public record MortarRecipeBuilder(Ingredient input, ItemStack output) {
    public static MortarRecipeBuilder create(Ingredient input, ItemStack output) {
        return new MortarRecipeBuilder(input, output);
    }

    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        exporter.accept(recipeId, new MortarRecipe(this.input, this.output), null);
    }
}
