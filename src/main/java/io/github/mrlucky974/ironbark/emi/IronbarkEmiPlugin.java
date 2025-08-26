package io.github.mrlucky974.ironbark.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.init.ItemInit;
import io.github.mrlucky974.ironbark.init.RecipeInit;
import io.github.mrlucky974.ironbark.recipe.MortarRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;

public class IronbarkEmiPlugin implements EmiPlugin {
    public static final EmiStack MORTAR_WORKSTATION = EmiStack.of(ItemInit.MORTAR);
    public static final EmiRecipeCategory MORTAR_RECIPE_CATEGORY
            = new EmiRecipeCategory(Ironbark.id("mortar"), MORTAR_WORKSTATION);

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MORTAR_RECIPE_CATEGORY);

        registry.addWorkstation(MORTAR_RECIPE_CATEGORY, MORTAR_WORKSTATION);

        RecipeManager manager = registry.getRecipeManager();

        for (RecipeEntry<MortarRecipe> recipe : manager.listAllOfType(RecipeInit.TypeInit.MORTAR)) {
            registry.addRecipe(new MortarEmiRecipe(recipe.value()));
        }
    }
}
