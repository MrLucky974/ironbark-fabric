package io.github.mrlucky974.ironbark.item;

import io.github.mrlucky974.ironbark.init.RecipeInit;
import io.github.mrlucky974.ironbark.list.SoundList;
import io.github.mrlucky974.ironbark.recipe.MortarRecipe;
import io.github.mrlucky974.ironbark.recipe.input.MortarRecipeInput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ClickType;
import net.minecraft.world.World;

import java.util.Optional;

public class MortarItem extends Item {
    private final RecipeManager.MatchGetter<MortarRecipeInput, ? extends MortarRecipe> matchGetter;

    public MortarItem(Settings settings) {
        super(settings);

        this.matchGetter = RecipeManager.createCachedMatchGetter(RecipeInit.TypeInit.MORTAR);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        World world = player.getWorld();
        if (world == null || world.isClient) return false;
        if (clickType != ClickType.RIGHT) return false;

        Optional<? extends RecipeEntry<? extends MortarRecipe>> recipeMatch = this.matchGetter.getFirstMatch(createRecipeInput(otherStack), world);
        if (recipeMatch.isEmpty()) {
            return false;
        }

        MortarRecipe recipe = recipeMatch.get().value();
        craftRecipe(recipe, player, otherStack, world);

        return true;
    }

    private MortarRecipeInput createRecipeInput(ItemStack input) {
        return new MortarRecipeInput(input);
    }

    private void craftRecipe(MortarRecipe recipe, PlayerEntity player, ItemStack input, World world) {
        ItemStack output = recipe.output().copy();
        if (!player.getInventory().insertStack(output)) {
            player.dropStack(output); // Drop the remains
        }

        input.decrement(1);
        world.playSound(null, player.getBlockPos(), SoundList.MORTAR_ITEM_CRAFTED, SoundCategory.PLAYERS);
    }
}
