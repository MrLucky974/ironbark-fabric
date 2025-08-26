package io.github.mrlucky974.ironbark.recipe;

import io.github.mrlucky974.ironbark.component.SpiceEffectsComponent;
import io.github.mrlucky974.ironbark.init.ComponentInit;
import io.github.mrlucky974.ironbark.item.SpiceIngredient;
import io.github.mrlucky974.ironbark.list.TagList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpicyFoodRecipe extends SpecialCraftingRecipe {
    public SpicyFoodRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        int spiceCount = 0;
        boolean hasMainFood = false;

        for(int i = 0; i < input.getSize(); ++i) {
            ItemStack itemStack = input.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                if (SpiceIngredient.of(itemStack.getItem()) != null) {
                    spiceCount++;
                } else if (isValidFood(itemStack) && !hasMainFood) {
                    hasMainFood = true;
                } else {
                    return false;
                }
            }
        }

        return hasMainFood && spiceCount > 0;
    }

    public static boolean isValidFood(ItemStack itemStack) {
        boolean isEdible = isEdible(itemStack);
        boolean isSpice = SpiceIngredient.of(itemStack.getItem()) != null;
        boolean hasSpice = itemStack.contains(ComponentInit.SPICE_EFFECTS_COMPONENT);
        boolean isBlacklisted = itemStack.isIn(TagList.Items.SPICY_ITEM_BLACKLIST);

        return isEdible && !hasSpice && !isSpice && !isBlacklisted;
    }

    public static boolean isEdible(ItemStack itemStack) {
        return itemStack.contains(DataComponentTypes.FOOD);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack itemStack = null;

        List<SpiceEffectsComponent.SpiceEffect> effects = new ArrayList<>();
        for (int i = 0; i < input.getSize(); ++i) {
            ItemStack itemStack2 = input.getStackInSlot(i);
            if (!itemStack2.isEmpty()) {
                if (isValidFood(itemStack2) && itemStack == null) {
                    itemStack = new ItemStack(itemStack2.getItem(), 1);
                }

                SpiceIngredient ingredient = SpiceIngredient.of(itemStack2.getItem());
                if (ingredient != null) {
                    SpiceEffectsComponent spiceEffects = ingredient.getSpiceEffect(itemStack2);
                    effects.addAll(spiceEffects.effects());
                }
            }
        }

        Objects.requireNonNull(itemStack).set(ComponentInit.SPICE_EFFECTS_COMPONENT, new SpiceEffectsComponent(effects));
        return itemStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer extends SpecialRecipeSerializer<SpicyFoodRecipe> {
        public static final Serializer INSTANCE = new Serializer(SpicyFoodRecipe::new);

        public Serializer(Factory<SpicyFoodRecipe> factory) {
            super(factory);
        }
    }
}
