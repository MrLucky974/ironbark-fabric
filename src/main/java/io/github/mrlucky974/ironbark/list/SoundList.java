package io.github.mrlucky974.ironbark.list;

import io.github.mrlucky974.ironbark.Ironbark;
import net.minecraft.sound.SoundEvent;

public class SoundList {
    public static SoundEvent MORTAR_ITEM_CRAFTED = getSoundEvent("mortar_item_crafted");

    public static SoundEvent getSoundEvent(String name) {
        return SoundEvent.of(Ironbark.id(name));
    }
}
