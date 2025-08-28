package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import oshi.util.tuples.Pair;
import ziyue.filters.Filter;
import ziyue.filters.FilterBuilder;

import java.util.ArrayList;

public class FilterInit {
    public static final ArrayList<Pair<Filter, Item>> FILTER_ITEMS = new ArrayList<>();

    private static final Filter UNCATEGORIZED_ITEMS = FilterBuilder
            .registerUncategorizedItemsFilter(ItemGroupInit.MAIN_GROUP);

    public static final Filter MATERIALS = register("materials",
            ItemGroupInit.MAIN_GROUP, ItemInit.NETHERIUM_PLATE);

    public static final Filter REPAIR_GEMS = register("repair_gems",
            ItemGroupInit.MAIN_GROUP, ItemInit.ULTIMATE_REPAIR_GEM);

    public static final Filter UTILITIES = register("utilities",
            ItemGroupInit.MAIN_GROUP, ItemInit.MORTAR);

    public static final Filter CURRENCY = register("currency",
            ItemGroupInit.MAIN_GROUP, new ItemStack(ItemInit.GOLD_COIN, 64));

    public static final Filter BLOCKS = register("blocks",
            ItemGroupInit.MAIN_GROUP, BlockInit.INDUSTRIAL_NETHERIUM_BLOCK);

    public static final Filter FOOD = register("food",
            ItemGroupInit.MAIN_GROUP, ItemInit.SPICE_MIX);

    public static Filter register(String name, ItemGroup group, ItemConvertible itemConvertible) {
        return FilterBuilder.registerFilter(group,
                Text.translatable("filter.%s.%s".formatted(Ironbark.MOD_ID, name)),
                () -> new ItemStack(itemConvertible)
        );
    }

    public static Filter register(String name, ItemGroup group, ItemStack stack) {
        return FilterBuilder.registerFilter(group,
                Text.translatable("filter.%s.%s".formatted(Ironbark.MOD_ID, name)),
                () -> stack
        );
    }

    public static void addItem(Item item, Filter filter) {
        FILTER_ITEMS.add(new Pair<>(filter, item));
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering filters...");
        FILTER_ITEMS.forEach(pair -> pair.getA().addItems(pair.getB()));
    }
}
