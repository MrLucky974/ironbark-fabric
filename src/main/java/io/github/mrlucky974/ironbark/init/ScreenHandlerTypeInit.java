package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.network.BlockPosPayload;
import io.github.mrlucky974.ironbark.network.TabletCraftingRecipeEntryPayload;
import io.github.mrlucky974.ironbark.screen.BankScreenHandler;
import io.github.mrlucky974.ironbark.screen.CraftingTabletScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ScreenHandlerTypeInit {
    public static final ScreenHandlerType<CraftingTabletScreenHandler> CRAFTING_TABLET =
            register("crafting_tablet", CraftingTabletScreenHandler::new, TabletCraftingRecipeEntryPayload.PACKET_CODEC);

    public static final ScreenHandlerType<BankScreenHandler> BANK =
            register("bank", BankScreenHandler::new, BlockPosPayload.PACKET_CODEC);

    public static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, Ironbark.id(name), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static <T extends ScreenHandler, D extends CustomPayload> ExtendedScreenHandlerType<T, D>
    register(String name,
             ExtendedScreenHandlerType.ExtendedFactory<T, D> factory,
             PacketCodec<? super RegistryByteBuf, D> codec) {
        return Registry.register(Registries.SCREEN_HANDLER, Ironbark.id(name), new ExtendedScreenHandlerType<>(factory, codec));
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering screen handler types...");
    }
}
