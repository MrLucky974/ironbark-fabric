package io.github.mrlucky974.ironbark.data.builder;

import io.github.mrlucky974.ironbark.recipe.TabletCraftingRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TabletCraftingRecipeBuilder {
    private final ItemStack result;
    private final List<String> pattern = new ArrayList<>();
    private final Map<Character, Ingredient> key = new LinkedHashMap<>();

    public TabletCraftingRecipeBuilder(ItemConvertible output, int count) {
        this(new ItemStack(output, count));
    }

    public TabletCraftingRecipeBuilder(ItemStack result) {
        this.result = result;
    }

    public static TabletCraftingRecipeBuilder create(ItemConvertible output) {
        return create(output, 1);
    }

    public static TabletCraftingRecipeBuilder create(ItemConvertible output, int count) {
        return new TabletCraftingRecipeBuilder(output, count);
    }

    public static TabletCraftingRecipeBuilder create(ItemStack result) {
        return new TabletCraftingRecipeBuilder(result);
    }

    public TabletCraftingRecipeBuilder input(Character symbol, ItemConvertible item) {
        return this.input(symbol, Ingredient.ofItems(item));
    }

    public TabletCraftingRecipeBuilder input(Character symbol, Ingredient ingredient) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        }
        if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }
        this.key.put(symbol, ingredient);
        return this;
    }

    public TabletCraftingRecipeBuilder pattern(String patternStr) {
        if (!this.pattern.isEmpty() && patternStr.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        this.pattern.add(patternStr);
        return this;
    }

    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        this.validate(recipeId);
        
        TabletCraftingRecipe recipe = new TabletCraftingRecipe(
                this.createRawShapedRecipe(),
                this.result
        );
        
        exporter.accept(recipeId, recipe, null);
    }

    public void offerTo(RecipeExporter exporter, String recipeId) {
        Identifier identifier = Identifier.of(recipeId);
        this.offerTo(exporter, identifier);
    }

    public void offerTo(RecipeExporter exporter) {
        this.offerTo(exporter, getItemId(this.result.getItem()));
    }

    private void validate(Identifier recipeId) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for tablet crafting recipe " + recipeId + "!");
        }
        
        Set<Character> patternSymbols = new HashSet<>();
        for (String row : this.pattern) {
            for (char c : row.toCharArray()) {
                if (c != ' ') {
                    patternSymbols.add(c);
                }
            }
        }

        Set<Character> keySymbols = new HashSet<>(this.key.keySet());

        if (!keySymbols.equals(patternSymbols)) {
            Set<Character> missingFromKey = new HashSet<>(patternSymbols);
            missingFromKey.removeAll(keySymbols);
            
            Set<Character> missingFromPattern = new HashSet<>(keySymbols);
            missingFromPattern.removeAll(patternSymbols);
            
            throw new IllegalStateException("Recipe " + recipeId + " has mismatched keys and pattern. " +
                    "Missing from key: " + missingFromKey + ", Missing from pattern: " + missingFromPattern);
        }
    }

    private RawShapedRecipe createRawShapedRecipe() {
        int width = this.pattern.getFirst().length();
        int height = this.pattern.size();
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(width * height, Ingredient.EMPTY);

        for (int y = 0; y < height; y++) {
            String row = this.pattern.get(y);
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);
                if (c != ' ') {
                    ingredients.set(y * width + x, this.key.get(c));
                }
            }
        }

        return new RawShapedRecipe(width, height, ingredients, Optional.of(new RawShapedRecipe.Data(this.key, this.pattern)));
    }

    private static Identifier getItemId(Item item) {
        return Identifier.of(item.toString());
    }
}
