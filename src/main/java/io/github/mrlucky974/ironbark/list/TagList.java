package io.github.mrlucky974.ironbark.list;

import io.github.mrlucky974.ironbark.Ironbark;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class TagList {
    public static class Items {
        public static final TagKey<Item> SPICY_ITEM_BLACKLIST = getTagKey("spicy_item_blacklist");
        public static final TagKey<Item> REPAIR_GEMS_BLACKLIST = getTagKey("repair_gems_blacklist");

        public static TagKey<Item> getTagKey(String name) {
            return TagKey.of(RegistryKeys.ITEM, Ironbark.id(name));
        };
    }
}
