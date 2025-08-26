package io.github.mrlucky974.ironbark.screen.slot;

import io.github.mrlucky974.ironbark.init.RecipeInit;
import io.github.mrlucky974.ironbark.inventory.TabletRecipeInputInventory;
import io.github.mrlucky974.ironbark.recipe.input.TabletCraftingRecipeInput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class TabletCraftingResultSlot extends Slot {
    private final TabletRecipeInputInventory input;
    private final PlayerEntity player;
    private int amount;

    public TabletCraftingResultSlot(PlayerEntity player, TabletRecipeInputInventory input, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
        this.input = input;
    }

    public boolean canInsert(ItemStack stack) {
        return false;
    }

    public ItemStack takeStack(int amount) {
        if (this.hasStack()) {
            this.amount += Math.min(amount, this.getStack().getCount());
        }

        return super.takeStack(amount);
    }

    protected void onCrafted(ItemStack stack, int amount) {
        this.amount += amount;
        this.onCrafted(stack);
    }

    protected void onTake(int amount) {
        this.amount += amount;
    }

    protected void onCrafted(ItemStack stack) {
        if (this.amount > 0) {
            stack.onCraftByPlayer(this.player.getWorld(), this.player, this.amount);
        }

        if (this.inventory instanceof RecipeUnlocker recipeUnlocker) {
            recipeUnlocker.unlockLastRecipe(this.player, this.input.getHeldStacks());
        }

        this.amount = 0;
    }

    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);
        TabletCraftingRecipeInput.Positioned positioned = this.input.createPositionedRecipeInput();
        TabletCraftingRecipeInput craftingRecipeInput = positioned.input();
        int i = positioned.left();
        int j = positioned.top();
        DefaultedList<ItemStack> defaultedList = player.getWorld().getRecipeManager().getRemainingStacks(RecipeInit.TypeInit.TABLET_CRAFTING, craftingRecipeInput, player.getWorld());

        for(int k = 0; k < craftingRecipeInput.getHeight(); ++k) {
            for(int l = 0; l < craftingRecipeInput.getWidth(); ++l) {
                int m = l + i + (k + j) * this.input.getWidth();
                ItemStack itemStack = this.input.getStack(m);
                ItemStack itemStack2 = defaultedList.get(l + k * craftingRecipeInput.getWidth());
                if (!itemStack.isEmpty()) {
                    this.input.removeStack(m, 1);
                    itemStack = this.input.getStack(m);
                }

                if (!itemStack2.isEmpty()) {
                    if (itemStack.isEmpty()) {
                        this.input.setStack(m, itemStack2);
                    } else if (ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2)) {
                        itemStack2.increment(itemStack.getCount());
                        this.input.setStack(m, itemStack2);
                    } else if (!this.player.getInventory().insertStack(itemStack2)) {
                        this.player.dropItem(itemStack2, false);
                    }
                }
            }
        }

    }

    public boolean disablesDynamicDisplay() {
        return true;
    }
}