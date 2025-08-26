package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.recipe.MortarRecipe;
import io.github.mrlucky974.ironbark.recipe.SpiceMixRecipe;
import io.github.mrlucky974.ironbark.recipe.SpicyFoodRecipe;
import io.github.mrlucky974.ironbark.recipe.TabletCraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class RecipeInit {
    public static class SerializerInit {
        public static final RecipeSerializer<SpicyFoodRecipe> CRAFTING_SPECIAL_SPICYFOOD = register("crafting_special_spicyfood",
                SpicyFoodRecipe.Serializer.INSTANCE);

        public static final RecipeSerializer<SpiceMixRecipe> CRAFTING_SPECIAL_SPICEMIX = register("crafting_special_spicemix",
                SpiceMixRecipe.Serializer.INSTANCE);

        public static final RecipeSerializer<TabletCraftingRecipe> TABLET_CRAFTING = register(TabletCraftingRecipe.Type.ID,
                TabletCraftingRecipe.Serializer.INSTANCE);

        public static final RecipeSerializer<MortarRecipe> MORTAR = register(MortarRecipe.Type.ID,
                MortarRecipe.Serializer.INSTANCE);

        public static <T extends RecipeSerializer<?>> T register(String name, T serializer) {
            return Registry.register(Registries.RECIPE_SERIALIZER, Ironbark.id(name), serializer);
        }

        public static void init() {
            Ironbark.LOGGER.info("Registering recipe serializers...");
        }
    }

    public static class TypeInit {
        public static final RecipeType<TabletCraftingRecipe> TABLET_CRAFTING = register(TabletCraftingRecipe.Type.ID, TabletCraftingRecipe.Type.INSTANCE);
        public static final RecipeType<MortarRecipe> MORTAR = register(MortarRecipe.Type.ID, MortarRecipe.Type.INSTANCE);

        public static <T extends RecipeType<?>> T register(String name, T type) {
            return Registry.register(Registries.RECIPE_TYPE, Ironbark.id(name), type);
        }

        public static void init() {
            Ironbark.LOGGER.info("Registering recipe types...");
        }
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering recipes...");
        TypeInit.init();
        SerializerInit.init();
    }
}
