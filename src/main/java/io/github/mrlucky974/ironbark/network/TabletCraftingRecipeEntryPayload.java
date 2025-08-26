package io.github.mrlucky974.ironbark.network;

import io.github.mrlucky974.ironbark.recipe.TabletCraftingRecipe;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeEntry;

public class TabletCraftingRecipeEntryPayload extends RecipeEntryPayload<TabletCraftingRecipe> {
    public static final PacketCodec<RegistryByteBuf, TabletCraftingRecipeEntryPayload> PACKET_CODEC = PacketCodec.tuple(
            getTypedRecipeCodec(), TabletCraftingRecipeEntryPayload::getRecipeEntry,
            TabletCraftingRecipeEntryPayload::new
    );

    public TabletCraftingRecipeEntryPayload(RecipeEntry<TabletCraftingRecipe> recipeEntry) {
        super(recipeEntry);
    }
}
