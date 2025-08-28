package io.github.mrlucky974.ironbark.client;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.IronbarkEffectManager;
import io.github.mrlucky974.ironbark.client.gui.screen.BankScreen;
import io.github.mrlucky974.ironbark.client.gui.screen.CraftingTabletScreen;
import io.github.mrlucky974.ironbark.client.renderer.BankRenderer;
import io.github.mrlucky974.ironbark.client.renderer.IronbarkEffectRenderer;
import io.github.mrlucky974.ironbark.client.renderer.item.ScytheItemRenderer;
import io.github.mrlucky974.ironbark.config.IronbarkConfig;
import io.github.mrlucky974.ironbark.init.FilterInit;
import io.github.mrlucky974.ironbark.init.ItemInit;
import io.github.mrlucky974.ironbark.init.ScreenHandlerTypeInit;
import io.github.mrlucky974.ironbark.item.CoinItem;
import io.github.mrlucky974.ironbark.item.CurrencyProvider;
import io.github.mrlucky974.ironbark.mixin.client.WorldRendererAccessor;
import io.github.mrlucky974.ironbark.network.BankUpdatePayload;
import io.github.mrlucky974.ironbark.network.ConfigPayload;
import io.github.mrlucky974.ironbark.network.InitialSyncPayload;
import io.github.mrlucky974.ironbark.network.OreChunksPayload;
import io.github.mrlucky974.ironbark.world.PlayerData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class IronbarkClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Ironbark/Client");

    public static PlayerData playerData = new PlayerData();
    public static final IronbarkEffectRenderer effectRenderer = new IronbarkEffectRenderer();
    public static final BankRenderer bankRenderer = new BankRenderer();

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            LOGGER.info("Client started, initializing block highlight config...");
            IronbarkConfig.initBlockHighlightConfig();
        });

        registerScreens();
        registerPayloadGlobalReceivers();
        registerModelPredicateProviders();

        FilterInit.init();

        HudRenderCallback.EVENT.register(bankRenderer);

        WorldRenderEvents.AFTER_ENTITIES.register(IronbarkClient::renderOutlineEffect);
    }

    private static void renderOutlineEffect(WorldRenderContext context) {
        try {
            WorldRendererAccessor worldRenderer = (WorldRendererAccessor) context.worldRenderer();
            if (effectRenderer.isActive()) {
                effectRenderer.render(
                        Objects.requireNonNull(context.matrixStack()),
                        context.camera(),
                        worldRenderer.getBufferBuilders().getOutlineVertexConsumers()
                );
            }
        } catch (Exception e) {
            LOGGER.error("Error during outline rendering", e);
        }
    }

    private static void registerModelPredicateProviders() {
        Registries.ITEM.getIds()
                .stream()
                .filter(key -> key.getNamespace().equals(Ironbark.MOD_ID))
                .map(Registries.ITEM::getOrEmpty)
                .map(Optional::orElseThrow)
                .filter(item -> item instanceof CoinItem)
                .forEach(item -> {
                    ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("stack"), (itemStack, clientWorld, livingEntity, seed) -> (float) itemStack.getCount() / itemStack.getMaxCount());
                });

        ModelPredicateProviderRegistry.register(ItemInit.COIN_SACK, Identifier.ofVanilla("amount"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (itemStack.getItem() instanceof CurrencyProvider currencyProvider) {
                int amount = currencyProvider.getAmount(itemStack);
                return amount > 0 ? 1.0F : 0.0F;
            }
            return 0.0F;
        });
    }

    private static void registerPayloadGlobalReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(OreChunksPayload.ID, (payload, context) -> {
            LOGGER.info("Received OreChunksPayload: overwrite={}, remove={}, add={}, bottomSectionCoord={}",
                    payload.overwrite(), payload.remove().size(), payload.add().size(), payload.bottomSectionCoord());
            IronbarkEffectManager.readPayload(effectRenderer, payload);
        });

        ClientPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
            LOGGER.info("Received ConfigPayload: serverValidating={}, blockConfigs={}",
                    payload.serverValidating(), payload.blockConfigs().size());
            IronbarkConfig.readPayload(payload);
        });

        ClientPlayNetworking.registerGlobalReceiver(InitialSyncPayload.ID, (payload, context) -> {
            playerData.coins = payload.coins();
        });

        ClientPlayNetworking.registerGlobalReceiver(BankUpdatePayload.ID, (payload, context) -> {
            playerData.coins = payload.coins();
        });
    }

    private static void registerScreens() {
        HandledScreens.register(ScreenHandlerTypeInit.CRAFTING_TABLET, CraftingTabletScreen::new);
        HandledScreens.register(ScreenHandlerTypeInit.BANK, BankScreen::new);
    }
}
