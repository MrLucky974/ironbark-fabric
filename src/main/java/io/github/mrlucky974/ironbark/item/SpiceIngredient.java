package io.github.mrlucky974.ironbark.item;

import io.github.mrlucky974.ironbark.component.SpiceEffectsComponent;
import io.github.mrlucky974.ironbark.init.ComponentInit;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public interface SpiceIngredient {
    default SpiceEffectsComponent getSpiceEffect(@Nullable ItemStack stack) {
        if (stack == null)
            return null;

        return stack.get(ComponentInit.SPICE_EFFECTS_COMPONENT);
    }

    static List<SpiceIngredient> getAll() {
        return Registries.ITEM.stream()
                .map(SpiceIngredient::of)
                .filter(Objects::nonNull)
                .toList();
    }

    @Nullable
    static SpiceIngredient of(ItemConvertible item) {
        Item actualItem = item.asItem();

        SpiceIngredient fromBlock = extractSpiceIngredientFromBlockItem(actualItem);
        if (fromBlock != null) {
            return fromBlock;
        }

        return actualItem instanceof SpiceIngredient spiceIngredient ? spiceIngredient : null;
    }

    @Nullable
    private static SpiceIngredient extractSpiceIngredientFromBlockItem(Item item) {
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            return block instanceof SpiceIngredient spiceIngredient ? spiceIngredient : null;
        }
        return null;
    }
}
