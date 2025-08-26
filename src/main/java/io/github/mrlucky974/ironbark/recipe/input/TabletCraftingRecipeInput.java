package io.github.mrlucky974.ironbark.recipe.input;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public class TabletCraftingRecipeInput implements RecipeInput {
    public static final TabletCraftingRecipeInput EMPTY = new TabletCraftingRecipeInput(0, 0, List.of());
    private final int width;
    private final int height;
    private final List<ItemStack> stacks;
    private final RecipeMatcher matcher = new RecipeMatcher();
    private final int stackCount;

    private TabletCraftingRecipeInput(int width, int height, List<ItemStack> stacks) {
        this.width = width;
        this.height = height;
        this.stacks = stacks;
        int i = 0;

        for(ItemStack itemStack : stacks) {
            ++i;
            this.matcher.addInput(itemStack, 1);
        }

        this.stackCount = i;
    }

    public static TabletCraftingRecipeInput create(int width, int height, List<ItemStack> stacks) {
        return createPositioned(width, height, stacks).input();
    }

    public static TabletCraftingRecipeInput.Positioned createPositioned(int width, int height, List<ItemStack> stacks) {
        if (width != 0 && height != 0) {
            // Don't trim - preserve the full grid for exact pattern matching
            return new TabletCraftingRecipeInput.Positioned(new TabletCraftingRecipeInput(width, height, stacks), 0, 0);
        } else {
            return TabletCraftingRecipeInput.Positioned.EMPTY;
        }
    }

    public ItemStack getStackInSlot(int slot) {
        return this.stacks.get(slot);
    }

    public ItemStack getStackInSlot(int x, int y) {
        return this.stacks.get(x + y * this.width);
    }

    public int getSize() {
        return this.stacks.size();
    }

    public boolean isEmpty() {
        return this.stackCount == 0;
    }

    public RecipeMatcher getRecipeMatcher() {
        return this.matcher;
    }

    public List<ItemStack> getStacks() {
        return this.stacks;
    }

    public int getStackCount() {
        return this.stackCount;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TabletCraftingRecipeInput craftingRecipeInput)) {
            return false;
        } else {
            return this.width == craftingRecipeInput.width && this.height == craftingRecipeInput.height && this.stackCount == craftingRecipeInput.stackCount && ItemStack.stacksEqual(this.stacks, craftingRecipeInput.stacks);
        }
    }

    public int hashCode() {
        int i = ItemStack.listHashCode(this.stacks);
        i = 31 * i + this.width;
        i = 31 * i + this.height;
        return i;
    }

    public record Positioned(TabletCraftingRecipeInput input, int left, int top) {
        public static final TabletCraftingRecipeInput.Positioned EMPTY;

        static {
            EMPTY = new TabletCraftingRecipeInput.Positioned(TabletCraftingRecipeInput.EMPTY, 0, 0);
        }
    }
}
