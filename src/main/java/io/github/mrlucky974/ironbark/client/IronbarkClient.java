package io.github.mrlucky974.ironbark.client;

import io.github.mrlucky974.ironbark.IronbarkEffectManager;
import io.github.mrlucky974.ironbark.client.gui.screen.CraftingTabletScreen;
import io.github.mrlucky974.ironbark.client.renderer.IronbarkEffectRenderer;
import io.github.mrlucky974.ironbark.config.IronbarkConfig;
import io.github.mrlucky974.ironbark.init.ScreenHandlerTypeInit;
import io.github.mrlucky974.ironbark.mixin.client.WorldRendererAccessor;
import io.github.mrlucky974.ironbark.network.ConfigPayload;
import io.github.mrlucky974.ironbark.network.OreChunksPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class IronbarkClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Ironbark/Client");

    public static final IronbarkEffectRenderer effectRenderer = new IronbarkEffectRenderer();

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ScreenHandlerTypeInit.CRAFTING_TABLET, CraftingTabletScreen::new);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            LOGGER.info("Client started, initializing block highlight config...");
            IronbarkConfig.initBlockHighlightConfig();
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
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
        });

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
    }
}
