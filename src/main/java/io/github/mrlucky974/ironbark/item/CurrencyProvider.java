package io.github.mrlucky974.ironbark.item;

import net.minecraft.item.ItemStack;

public interface CurrencyProvider {
    public int getAmount(ItemStack stack);
}
