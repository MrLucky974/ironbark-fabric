package io.github.mrlucky974.ironbark.client;

import io.github.mrlucky974.ironbark.client.gui.screen.CraftingTabletScreen;
import io.github.mrlucky974.ironbark.init.ScreenHandlerTypeInit;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IronbarkClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Ironbark (Client)");

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ScreenHandlerTypeInit.CRAFTING_TABLET, CraftingTabletScreen::new);
    }
}
