package io.github.mrlucky974.ironbark.config;

import com.mojang.serialization.DataResult;
import de.siphalor.tweed4.data.hjson.HjsonList;
import de.siphalor.tweed4.data.hjson.HjsonObject;
import de.siphalor.tweed4.data.hjson.HjsonSerializer;
import de.siphalor.tweed4.data.hjson.HjsonValue;
import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.network.ConfigPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class IronbarkConfig {
    public static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "spelunker.hjson");

    private static final Map<String[], ChunkBlockConfig> DEFAULT_BLOCK_CONFIGS = new HashMap<>();

    public static boolean globalTransition = true;
    public static boolean serverValidating = true;
    public static int chunkRadius = 1;
    public static Object2ObjectMap<Block, ChunkBlockConfig> blockConfigs = new Object2ObjectOpenHashMap<>();

    private static Consumer<Void> blockConfigInitializer = v -> {};
    private static boolean blockHighlightInitialized = false;

    public static void createDefaultConfig() throws IOException {
        HjsonObject obj;
        boolean rewrite = false;
        if(CONFIG_FILE.exists()) {
            InputStream in = new FileInputStream(CONFIG_FILE);
            obj = HjsonSerializer.INSTANCE.readValue(in).asObject();
            in.close();
        } else {
            obj = HjsonSerializer.INSTANCE.newObject();
            rewrite = true;
        }

        if(!obj.has("server-validating")) {
            obj.set("server-validating", serverValidating).setComment("""
                    Checks serverside for blocks to be highlighted and sends them to the client
                    recommended if the server has an anti-xray mod
                    default: true
                    """);
            rewrite = true;
        }

        if(!obj.has("block-transition")) {
            obj.set("block-transition", globalTransition).setComment("""
                    Determines whether an ease-out animation should be played when approaching a block
                    If this option is false it will overwrite all block-specific transitions
                    default: true
                    """);
            rewrite = true;
        }

        if (!obj.has("block-configs")) {
            HjsonList list = obj.addList("block-configs");
            list.setComment("""
                    The configuration for the given blocks
                   \s
                    highlightColor:
                        Specifies the color with which the block will be outlined
                        You can also use values like "red, dark_red, blue, aqua"
                   \s
                    transition:
                        Determines whether an ease-out animation should be played when approaching a block
                       \s
                    effectRadius:
                        How many blocks the effect should range, a higher value than 32 is not recommended
                        Must be greater or equal to 1
                   \s""");

            if(obj.has("block-highlight-colors")) {
                HjsonList oldList = obj.get("block-highlight-colors").asList();
                Iterator<HjsonValue> iterator = oldList.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    HjsonObject vo = iterator.next().asObject();
                    vo.set("transition", true);
                    vo.set("effectRadius", Optional.of(obj.get("effect-radius").asInt()).orElse(16));
                    list.set(i, vo);
                    i++;
                }
                obj.remove("block-highlight-colors");
                obj.remove("effect-radius");
            } else {
                for (Map.Entry<String[], ChunkBlockConfig> entry : DEFAULT_BLOCK_CONFIGS.entrySet()) {
                    HjsonObject eObj = list.addObject(list.size());
                    HjsonList idList = eObj.addList("blockIds");
                    for (String id : entry.getKey())
                        idList.set(idList.size(), id);

                    String color = TextColor.fromRgb(entry.getValue().getColor()).getName();
                    eObj.set("highlightColor", color).setComment("default: " + color);
                    eObj.set("transition", entry.getValue().isTransition()).setComment("default: " + entry.getValue().isTransition());
                    eObj.set("effectRadius", entry.getValue().getEffectRadius()).setComment("default: " + entry.getValue().getEffectRadius());
                }
            }
            rewrite = true;
        }

        DEFAULT_BLOCK_CONFIGS.clear();

        if(rewrite) {
            FileOutputStream out = new FileOutputStream(CONFIG_FILE);
            HjsonSerializer.INSTANCE.writeValue(out, obj);
            out.close();
        }
    }

    public static void loadConfig() throws IOException {
        InputStream in = new FileInputStream(CONFIG_FILE);
        HjsonObject obj = HjsonSerializer.INSTANCE.readValue(in).asObject();
        in.close();

        serverValidating = Optional.of(obj.get("server-validating").asBoolean()).orElse(serverValidating);

        HjsonList blockConfigsList = obj.get("block-configs").asList();
        Iterator<HjsonValue> blockConfigsIterator = blockConfigsList.iterator();
        while (blockConfigsIterator.hasNext()) {
            HjsonObject blockObj = blockConfigsIterator.next().asObject();
            List<String> blockIds = new ArrayList<>();

            HjsonList blockIdsList = blockObj.get("blockIds").asList();
            Iterator<HjsonValue> blockIdsIterator = blockIdsList.iterator();
            while (blockIdsIterator.hasNext())
                blockIds.add(blockIdsIterator.next().asString());

            String hexColor = Optional.of(blockObj.get("highlightColor").asString()).orElse("#ffffff");
            DataResult<TextColor> textColor = TextColor.parse(hexColor);
            AtomicInteger color = new AtomicInteger(0xffffff);
            textColor.ifSuccess(v -> color.set(v.getRgb())).ifError(error -> {
                Ironbark.LOGGER.error("Invalid color '{}' specified for the block(s) '{}'.", hexColor, StringUtils.join(blockIds, ", "));
            });
            boolean transition = Optional.of(blockObj.get("transition").asBoolean()).orElse(true);
            int effectRadius = Optional.of(blockObj.get("effectRadius").asInt()).orElse(1);
            if(effectRadius < 1) {
                Ironbark.LOGGER.warn("Effect radius '{}' for the block(s) '{}' is smaller than 1.", effectRadius, StringUtils.join(blockIds, ", "));
                Ironbark.LOGGER.warn("Setting it to 1.");
                effectRadius = 1;
            }

            ChunkBlockConfig config = new ChunkBlockConfig(color.get(), transition, effectRadius);
            blockConfigInitializer = blockConfigInitializer.andThen(v -> {
                for (String blockId : blockIds) {
                    Optional<Block> optBlock = Registries.BLOCK.getOrEmpty(Identifier.of(blockId));
                    if (optBlock.isEmpty()) {
                        Ironbark.LOGGER.error("Unknown block id in config: '{}'.", blockId);
                    } else {
                        Block block = optBlock.get();
                        blockConfigs.put(block, config.setBlock(block));
                    }
                }
            });
        }
    }

    public static ConfigPayload writePayload() {
        return new ConfigPayload(serverValidating, blockConfigs);
    }

    public static void readPayload(ConfigPayload payload) {
        serverValidating = payload.serverValidating();
        blockConfigs.clear();
        blockConfigs = new Object2ObjectOpenHashMap<>(payload.blockConfigs());
    }

    public static void initBlockHighlightConfig() {
        if(blockHighlightInitialized)
            return;
        blockHighlightInitialized = true;
        blockConfigInitializer.accept(null);
    }

    public static boolean isOreBlock(Block block) {
        return blockConfigs.containsKey(block);
    }

    static {
        // Coal
        DEFAULT_BLOCK_CONFIGS.put(new String[] {"minecraft:coal_ore", "minecraft:deepslate_coal_ore", "ironbark:deepslate_anthracite_coal_ore"}, new ChunkBlockConfig(0x505050, true, 16));

        // Iron
        DEFAULT_BLOCK_CONFIGS.put(new String[] {"minecraft:iron_ore", "minecraft:deepslate_iron_ore"}, new ChunkBlockConfig(0xffd1bd, true, 8));

        // Copper
        DEFAULT_BLOCK_CONFIGS.put(new String[] {"minecraft:copper_ore", "minecraft:deepslate_copper_ore"}, new ChunkBlockConfig(0xeb5e34, true, 12));

        // Gold
        DEFAULT_BLOCK_CONFIGS.put(new String[] {"minecraft:gold_ore", "minecraft:deepslate_gold_ore", "minecraft:nether_gold_ore"}, new ChunkBlockConfig(0xfff52e, true, 8));

        // Diamond
        DEFAULT_BLOCK_CONFIGS.put(new String[] {"minecraft:diamond_ore", "minecraft:deepslate_diamond_ore"}, new ChunkBlockConfig(0x2ee0ff, true, 5));

        // Emerald
        DEFAULT_BLOCK_CONFIGS.put(new String[] {"minecraft:emerald_ore", "minecraft:deepslate_emerald_ore"}, new ChunkBlockConfig(0x2eff35, true, 7));

        // Lapis
        DEFAULT_BLOCK_CONFIGS.put(new String[] {"minecraft:lapis_ore", "minecraft:deepslate_lapis_ore"}, new ChunkBlockConfig(0x312eff, true, 8));

        // Redstone
        DEFAULT_BLOCK_CONFIGS.put(new String[] {"minecraft:redstone_ore", "minecraft:deepslate_redstone_ore"}, new ChunkBlockConfig(0xff2e2e, true, 8));

        // Quartz
        DEFAULT_BLOCK_CONFIGS.put(new String[] {"minecraft:nether_quartz_ore"}, new ChunkBlockConfig(0xffffff, true, 14));
    }
}