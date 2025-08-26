package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlockInit {
    public static final Block DEEPSLATE_ANTHRACITE_COAL_ORE = registerWithItem("deepslate_anthracite_coal_ore", new Block(Block.Settings.copy(Blocks.DEEPSLATE_COAL_ORE).strength(5.5F, 3.0F)));
    public static final Block ANTHRACITE_COAL_BLOCK = registerWithItem("anthracite_coal_block", new Block(Block.Settings.copy(net.minecraft.block.Blocks.COAL_BLOCK)));

    public static <T extends Block> T register(String name, T block) {
        return Registry.register(Registries.BLOCK, Ironbark.id(name), block);
    }

    public static <T extends Block> T registerWithItem(String name, T block, Item.Settings settings) {
        T registeredBlock = register(name, block);
        ItemInit.register(name, new BlockItem(registeredBlock, settings));
        return registeredBlock;
    }

    public static <T extends Block> T registerWithItem(String name, T block) {
        return registerWithItem(name, block, new Item.Settings());
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering blocks...");
    }
}
