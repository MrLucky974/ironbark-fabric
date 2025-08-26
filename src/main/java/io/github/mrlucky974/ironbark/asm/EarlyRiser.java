package io.github.mrlucky974.ironbark.asm;

import com.chocohead.mm.api.ClassTinkerers;
import com.chocohead.mm.api.EnumAdder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.util.Formatting;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        EnumAdder rarityEnumAdder = ClassTinkerers.enumBuilder(remapper.mapClassName("intermediary", "net.minecraft.class_1814"), "I", "Ljava/lang/String;", "Lnet/minecraft/util/Formatting;");
        rarityEnumAdder
                .addEnum("LEGENDARY", 4, "legendary", Formatting.GOLD)
                .addEnum("ANCIENT", 5, "ancient", Formatting.DARK_RED)
                .build();
    }
}
