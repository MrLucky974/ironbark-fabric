package io.github.mrlucky974.ironbark.data.provider;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.data.builder.MortarRecipeBuilder;
import io.github.mrlucky974.ironbark.data.builder.TabletCraftingRecipeBuilder;
import io.github.mrlucky974.ironbark.init.BlockInit;
import io.github.mrlucky974.ironbark.init.ItemInit;
import io.github.mrlucky974.ironbark.recipe.SpiceMixRecipe;
import io.github.mrlucky974.ironbark.recipe.SpicyFoodRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
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

        TabletCraftingRecipeBuilder.create(ItemInit.END_STAR)
                .pattern(" e ")
                .pattern("ese")
                .pattern(" e ")
                .input('s', Items.NETHER_STAR)
                .input('e', Items.ENDER_EYE)
                .offerTo(recipeExporter, Ironbark.id("end_star_tablet"));

        addPackingAndUnpackingRecipes(RecipeCategory.MISC, BlockInit.STEEL_BLOCK, ItemInit.STEEL_INGOT, recipeExporter);
        addPackingAndUnpackingRecipes(RecipeCategory.MISC, BlockInit.NETHERIUM_BLOCK, ItemInit.NETHERIUM_INGOT, recipeExporter);
        addPackingAndUnpackingRecipes(RecipeCategory.MISC, BlockInit.ROSE_GOLD_BLOCK, ItemInit.ROSE_GOLD_INGOT, recipeExporter);
        addPackingAndUnpackingRecipes(RecipeCategory.MISC, BlockInit.ANTHRACITE_COAL_BLOCK, ItemInit.ANTHRACITE_COAL, recipeExporter);
        addPackingAndUnpackingRecipes(RecipeCategory.MISC, BlockInit.CHARCOAL_BLOCK, Items.CHARCOAL, recipeExporter);

        addPackingRecipe(RecipeCategory.MISC, BlockInit.INDUSTRIAL_NETHERIUM_BLOCK, ItemInit.NETHERIUM_PLATE, recipeExporter);
    }

    private void addPackingAndUnpackingRecipes(RecipeCategory category, ItemConvertible packedResult, ItemConvertible ingredient,
                                               RecipeExporter exporter) {
        // Block from ingots (shaped)
        addPackingRecipe(category, packedResult, ingredient, exporter);

        // Ingots from block (shapeless)
        addUnpackingRecipe(category, packedResult, ingredient, exporter);
    }

    private static void addUnpackingRecipe(RecipeCategory category, ItemConvertible packedResult, ItemConvertible ingredient, RecipeExporter exporter) {
        ShapelessRecipeJsonBuilder.create(category, ingredient, 9)
                .input(packedResult)
                .criterion(hasItem(packedResult), conditionsFromItem(packedResult))
                .offerTo(exporter);
    }

    private static void addPackingRecipe(RecipeCategory category, ItemConvertible packedResult, ItemConvertible ingredient, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(category, packedResult, 1)
                .pattern("iii")
                .pattern("iii")
                .pattern("iii")
                .input('i', ingredient)
                .criterion(hasItem(ingredient), conditionsFromItem(ingredient))
                .offerTo(exporter);
    }
}
