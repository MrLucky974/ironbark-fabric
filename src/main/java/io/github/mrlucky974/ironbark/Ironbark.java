package io.github.mrlucky974.ironbark;

import io.github.mrlucky974.ironbark.command.CoinsCommand;
import io.github.mrlucky974.ironbark.component.SpiceContainerComponent;
import io.github.mrlucky974.ironbark.config.IronbarkConfig;
import io.github.mrlucky974.ironbark.event.FoodEatenCallback;
import io.github.mrlucky974.ironbark.init.*;
import io.github.mrlucky974.ironbark.item.SpiceIngredient;
import io.github.mrlucky974.ironbark.list.RegistryList;
import io.github.mrlucky974.ironbark.network.BankUpdatePayload;
import io.github.mrlucky974.ironbark.network.ConfigPayload;
import io.github.mrlucky974.ironbark.network.InitialSyncPayload;
import io.github.mrlucky974.ironbark.network.OreChunksPayload;
import io.github.mrlucky974.ironbark.recipe.SpicyFoodRecipe;
import io.github.mrlucky974.ironbark.spice.Spice;
import io.github.mrlucky974.ironbark.spice.SpiceEffect;
import io.github.mrlucky974.ironbark.util.StatusEffectMerger;
import io.github.mrlucky974.ironbark.world.IronbarkPersistentState;
import io.github.mrlucky974.ironbark.world.PlayerData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ironbark implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Ironbark");
    public static final String MOD_ID = "ironbark";

    public static final Text SPICY_TOOLTIP = Text.translatable("tooltip." + MOD_ID + ".spicy")
            .formatted(Formatting.DARK_PURPLE, Formatting.BOLD);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing mod...");

        try {
            IronbarkConfig.createDefaultConfig();
            IronbarkConfig.loadConfig();
        } catch (IOException e) {
            Ironbark.LOGGER.error("Failed to load config file", e);
        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> IronbarkConfig.initBlockHighlightConfig());

        registerPayloads();
        SpiceInit.init();
        ComponentInit.init();
        BlockInit.init();
        BlockEntityInit.init();
        ScreenHandlerTypeInit.init();
        ItemInit.init();
        ItemGroupInit.init();
        RecipeInit.init();
        StatusEffectInit.init();
        PotionInit.init();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerData playerState = IronbarkPersistentState.getPlayerState(handler.getPlayer());
            server.execute(() -> {
                ServerPlayNetworking.send(handler.getPlayer(), new InitialSyncPayload(playerState.coins));
            });
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CoinsCommand.register(dispatcher);
        });

        FoodEatenCallback.EVENT.register(((stack, world, user) -> {
            if (SpicyFoodRecipe.isEdible(stack) && SpiceIngredient.of(stack.getItem()) == null) {
                SpiceContainerComponent spiceContainerComponent = stack.get(ComponentInit.SPICES);
                if (spiceContainerComponent != null) {
                    spiceContainerComponent.apply(user);
                }
            }
        }));

        ItemTooltipCallback.EVENT.register(((stack, tooltipContext, tooltipType, tooltip) ->  {
            if (SpicyFoodRecipe.isEdible(stack) && SpiceIngredient.of(stack.getItem()) == null) {
                SpiceContainerComponent spiceContainerComponent = stack.get(ComponentInit.SPICES);
                if (spiceContainerComponent != null) {
                    tooltip.add(SPICY_TOOLTIP);

                    if (tooltipType.isAdvanced()) {
                        List<Spice> spices = spiceContainerComponent.spices();

                        Map<Spice, Integer> spiceCounts = new HashMap<>();
                        for (Spice spice : spices) {
                            spiceCounts.merge(spice, 1, Integer::sum);
                        }

                        for (Map.Entry<Spice, Integer> entry : spiceCounts.entrySet()) {
                            Spice spice = entry.getKey();
                            int count = entry.getValue();

                            tooltip.add(Text.literal("")
                                    .append(spice.getName())
                                    .append(" x" + count)
                                    .formatted(Formatting.BLUE)
                            );
                        }
                    }
                }
            }
        }));
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(ConfigPayload.ID, ConfigPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(OreChunksPayload.ID, OreChunksPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(InitialSyncPayload.ID, InitialSyncPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(BankUpdatePayload.ID, BankUpdatePayload.PACKET_CODEC);
    }

    public static Identifier id(String name) {
        return Identifier.of(Ironbark.MOD_ID, name);
    }
}
