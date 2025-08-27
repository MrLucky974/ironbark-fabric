package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.item.*;
import io.github.mrlucky974.ironbark.list.FoodList;
import io.github.mrlucky974.ironbark.network.CoinSackComponent;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;


public class ItemInit {
    public static final List<ItemConvertible> BLACKLIST = new ArrayList<>();
    public static final float DEFAULT_SPICE_DURATION = 3f;

    public static final Item STEEL_INGOT = register("steel_ingot", new Item(new Item.Settings()));
    public static final Item FLOUR = register("flour", new Item(new Item.Settings()));
    public static final Item DOUGH = register("dough", new Item(new Item.Settings()));
    public static final Item BARK = register("bark", new Item(new Item.Settings()));
    public static final Item JUNGLE_SAP = register("jungle_sap", new Item(new Item.Settings()));
    public static final Item SPRUCE_SAP = register("spruce_sap", new Item(new Item.Settings()));
    public static final Item ANTHRACITE_COAL = register("anthracite_coal", new Item(new Item.Settings()));
    public static final Item MULCH = register("mulch", new Item(new Item.Settings()));
    public static final Item NETHERIUM_INGOT = register("netherium_ingot", new Item(new Item.Settings()));
    public static final Item NETHERIUM_PLATE = register("netherium_plate", new Item(new Item.Settings()));
    public static final Item ROSE_GOLD_INGOT = register("rose_gold_ingot", new Item(new Item.Settings()));
    public static final Item CHARRED_BONE = register("charred_bone", new Item(new Item.Settings()));

    public static final Item SPICE_ASHTHORN = register("spice_ashthorn",
            new SpiceItem(StatusEffects.WITHER, DEFAULT_SPICE_DURATION,
                    new Item.Settings().food(FoodList.SPICE_FOOD_COMPONENT)));

    public static final Item SPICE_SMOLDERROOT = register("spice_smolderroot",
            new SpiceItem(StatusEffects.FIRE_RESISTANCE, DEFAULT_SPICE_DURATION,
                    new Item.Settings().food(FoodList.SPICE_FOOD_COMPONENT)));

    public static final Item SPICE_BRIGHTBURST = register("spice_brightburst",
            new SpiceItem(StatusEffects.HASTE, DEFAULT_SPICE_DURATION,
                    new Item.Settings().food(FoodList.SPICE_FOOD_COMPONENT)));

    public static final Item SPICE_GOLDENBLOOM = register("spice_goldenbloom",
            new SpiceItem(StatusEffects.REGENERATION, DEFAULT_SPICE_DURATION,
                    new Item.Settings().food(FoodList.SPICE_FOOD_COMPONENT)));

    public static final Item SPICE_SKYPEPPER = register("spice_skypepper",
            new SpiceItem(StatusEffects.SPEED, DEFAULT_SPICE_DURATION,
                    new Item.Settings().food(FoodList.SPICE_FOOD_COMPONENT)));

    public static final Item SPICE_MIX = register("spice_mix",
            new SpiceMixItem(new Item.Settings().maxCount(1).recipeRemainder(Items.BUNDLE)));

    public static final Item ANCIENT_CLAY_TABLET = register("ancient_clay_tablet",
            new AncientClayTabletItem(new Item.Settings().maxCount(1).rarity(ExtendedRarity.ANCIENT)));

    public static final Item MORTAR = register("mortar", new MortarItem(new Item.Settings().maxCount(1)));

    public static final Item END_STAR = register("end_star",
            new Item(new Item.Settings()
                    .rarity(ExtendedRarity.LEGENDARY)
                    .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            )
    );

    public static final CoinItem COPPER_COIN = register("copper_coin",
            new CoinItem(1, new Item.Settings()));

    public static final CoinItem IRON_COIN = register("iron_coin",
            new CoinItem(64, new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final CoinItem GOLD_COIN = register("gold_coin",
            new CoinItem(64 * 8, new Item.Settings().rarity(Rarity.RARE)));

    public static final CoinItem ROSE_GOLD_COIN = register("rose_gold_coin",
            new CoinItem(64 * 64, new Item.Settings().rarity(Rarity.RARE)));

    public static final Item COIN_SACK = register("coin_sack",
            new CoinSackItem(new Item.Settings().maxCount(1)));

    public static final Item WEAK_REPAIR_GEM = register("weak_repair_gem",
            new RepairGemItem(200, new Item.Settings().maxDamage(100).rarity(Rarity.COMMON)));

    public static final Item BASIC_REPAIR_GEM = register("basic_repair_gem",
            new RepairGemItem(150, new Item.Settings().maxDamage(240).rarity(Rarity.COMMON)));

    public static final Item STRONG_REPAIR_GEM = register("strong_repair_gem",
            new RepairGemItem(100, new Item.Settings().maxDamage(460).rarity(Rarity.UNCOMMON)));

    public static final Item BETTER_REPAIR_GEM = register("better_repair_gem",
            new RepairGemItem(25, new Item.Settings().maxDamage(750).rarity(Rarity.RARE)));

    public static final Item ULTIMATE_REPAIR_GEM = register("ultimate_repair_gem",
            new RepairGemItem(2, new Item.Settings().maxDamage(975).rarity(Rarity.EPIC)));

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, Ironbark.id(name), item);
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering items...");
        ItemInit.BLACKLIST.add(ItemInit.SPICE_MIX);
        ItemInit.BLACKLIST.add(ItemInit.ANCIENT_CLAY_TABLET);

        FuelRegistry.INSTANCE.add(ItemInit.ANTHRACITE_COAL, 3200);
        FuelRegistry.INSTANCE.add(ItemInit.MULCH, 800);
        FuelRegistry.INSTANCE.add(BlockInit.ANTHRACITE_COAL_BLOCK.asItem(), 32000);
        FuelRegistry.INSTANCE.add(BlockInit.CHARCOAL_BLOCK.asItem(), 16000);
    }
}
