package io.github.mrlucky974.ironbark.recipe;

import io.github.mrlucky974.ironbark.component.SpiceContainerComponent;
import io.github.mrlucky974.ironbark.init.ComponentInit;
import io.github.mrlucky974.ironbark.init.ItemInit;
import io.github.mrlucky974.ironbark.item.SpiceIngredient;
import io.github.mrlucky974.ironbark.spice.Spice;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SpiceMixRecipe extends SpecialCraftingRecipe {
    public SpiceMixRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        int spiceCount = 0;
        boolean hasBundle = false;

        for (int i = 0; i < input.getSize(); ++i) {
            ItemStack itemStack = input.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() == Items.BUNDLE) {
                    if (hasBundle) return false;
                    hasBundle = true;
                } else if (SpiceIngredient.of(itemStack.getItem()) != null) {
                    spiceCount++;
                } else {
                    return false;
                }
            }
        }

        return hasBundle && spiceCount >= 2;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        List<Spice> totalSpices = new ArrayList<>();

        for (int i = 0; i < input.getSize(); ++i) {
            ItemStack itemStack = input.getStackInSlot(i);
            if (!itemStack.isEmpty() && itemStack.getItem() != Items.BUNDLE) {
                SpiceIngredient ingredient = SpiceIngredient.of(itemStack.getItem());
                if (ingredient != null) {
                    List<Spice> spices = ingredient.getSpices(itemStack);
                    totalSpices.addAll(spices);
                }
            }
        }

        SpiceContainerComponent combinedEffects = new SpiceContainerComponent(totalSpices);
        ItemStack result = new ItemStack(ItemInit.SPICE_MIX, 1);
        result.set(ComponentInit.SPICES, combinedEffects);

        return result;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SpiceMixRecipe.Serializer.INSTANCE;
    }

    public static class Serializer extends SpecialRecipeSerializer<SpiceMixRecipe> {
        public static final SpiceMixRecipe.Serializer INSTANCE = new SpiceMixRecipe.Serializer(SpiceMixRecipe::new);

        public Serializer(Factory<SpiceMixRecipe> factory) {
            super(factory);
        }
    }
}
