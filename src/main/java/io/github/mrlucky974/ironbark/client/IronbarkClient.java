package io.github.mrlucky974.ironbark.client;

import io.github.mrlucky974.ironbark.client.gui.screen.CraftingTabletScreen;
import io.github.mrlucky974.ironbark.init.ScreenHandlerTypeInit;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class IronbarkClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ScreenHandlerTypeInit.CRAFTING_TABLET, CraftingTabletScreen::new);
    }
}
