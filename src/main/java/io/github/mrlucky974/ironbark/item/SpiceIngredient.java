package io.github.mrlucky974.ironbark.item;

import io.github.mrlucky974.ironbark.component.SpiceContainerComponent;
import io.github.mrlucky974.ironbark.init.ComponentInit;
import io.github.mrlucky974.ironbark.spice.Spice;
import io.github.mrlucky974.ironbark.spice.SpiceEffect;
import io.github.mrlucky974.ironbark.util.StatusEffectMerger;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface SpiceIngredient {
    default List<Spice> getSpices(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return List.of();

        SpiceContainerComponent component = stack.get(ComponentInit.SPICES);
        assert component != null;
        return component.spices();
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

    static void apply(ItemStack stack, LivingEntity user) {
        SpiceIngredient ingredient = SpiceIngredient.of(stack.getItem());
        if (ingredient != null) {
            List<StatusEffectInstance> statusEffectInstances = new ArrayList<>();
            for (Spice spice : ingredient.getSpices(stack)) {
                for (SpiceEffect effect : spice.getEffects()) {
                    statusEffectInstances.add(effect.createStatusEffectInstance());
                }
            }

            List<StatusEffectInstance> mergedStatusEffectInstances = StatusEffectMerger.mergeEffects(statusEffectInstances, StatusEffectMerger.DurationMergeStrategy.COMBINE_DURATION);
            mergedStatusEffectInstances.forEach(user::addStatusEffect);
        }
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
