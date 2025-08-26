package io.github.mrlucky974.ironbark.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.recipe.MortarRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class MortarEmiRecipe extends BasicEmiRecipe {

    public MortarEmiRecipe(MortarRecipe recipe) {
        super(IronbarkEmiPlugin.MORTAR_RECIPE_CATEGORY, getId(recipe), 70, 18);
        this.inputs.add(EmiIngredient.of(recipe.input()));
        this.outputs.add(EmiStack.of(recipe.output()));
    }

    private static @NotNull Identifier getId(MortarRecipe recipe) {
        return Ironbark.id("/%s/%s".formatted(MortarRecipe.Type.ID, recipe.output().getItem().toString().replace(":", "/")));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // Add an arrow texture to indicate processing
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 22, 0);

        // Adds an input slot on the left
        widgets.addSlot(inputs.getFirst(), 0, 0);

        // Adds an output slot on the right
        // Note that output slots need to call `recipeContext` to inform EMI about their recipe context
        // This includes being able to resolve recipe trees, favorite stacks with recipe context, and more
        widgets.addSlot(outputs.getFirst(), 50, 0).recipeContext(this);
    }
}
