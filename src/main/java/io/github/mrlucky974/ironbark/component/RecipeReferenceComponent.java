package io.github.mrlucky974.ironbark.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mrlucky974.ironbark.init.RecipeInit;
import io.github.mrlucky974.ironbark.recipe.TabletCraftingRecipe;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record RecipeReferenceComponent(Identifier recipeId) {
    public static final Codec<RecipeReferenceComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("recipe_id").forGetter(RecipeReferenceComponent::recipeId)
            ).apply(instance, RecipeReferenceComponent::new)
    );

    public static final PacketCodec<RegistryByteBuf, RecipeReferenceComponent> PACKET_CODEC =
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC, RecipeReferenceComponent::recipeId,
                    RecipeReferenceComponent::new
            );

    public RecipeEntry<TabletCraftingRecipe> getRecipeEntry(World world) {
        return world.getRecipeManager()
                .listAllOfType(RecipeInit.TypeInit.TABLET_CRAFTING)
                .stream()
                .filter(entry -> entry.id().equals(this.recipeId))
                .findFirst()
                .orElseThrow();
    }
}
