package io.github.mrlucky974.ironbark.data.provider;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.data.builder.MortarRecipeBuilder;
import io.github.mrlucky974.ironbark.data.builder.TabletCraftingRecipeBuilder;
import io.github.mrlucky974.ironbark.init.ItemInit;
import io.github.mrlucky974.ironbark.recipe.SpiceMixRecipe;
import io.github.mrlucky974.ironbark.recipe.SpicyFoodRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class IronbarkRecipeProvider extends FabricRecipeProvider {
    public IronbarkRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        ComplexRecipeJsonBuilder.create(SpicyFoodRecipe::new).offerTo(recipeExporter, "spicy_food");
        ComplexRecipeJsonBuilder.create(SpiceMixRecipe::new).offerTo(recipeExporter, "spice_mix");

        offerSmelting(recipeExporter, List.of(ItemInit.DOUGH), RecipeCategory.FOOD, Items.BREAD, 0.1f, 200, "dough_to_bread");

        MortarRecipeBuilder.create(
                Ingredient.ofItems(Items.BONE),
                new ItemStack(Items.BONE_MEAL, 9)
        ).offerTo(recipeExporter, Ironbark.id("bone_to_bone_meal"));

        TabletCraftingRecipeBuilder.create(ItemInit.NETHERIUM_PLATE)
                .pattern("   ")
                .pattern("nnn")
                .pattern("   ")
                .input('n', ItemInit.NETHERIUM_INGOT)
                .offerTo(recipeExporter, Ironbark.id("netherium_plate_tablet"));

        TabletCraftingRecipeBuilder.create(ItemInit.END_STAR)
                .pattern("   ")
                .pattern(" s ")
                .pattern("   ")
                .input('s', Items.NETHER_STAR)
                .offerTo(recipeExporter, Ironbark.id("end_star_tablet"));
    }
}
