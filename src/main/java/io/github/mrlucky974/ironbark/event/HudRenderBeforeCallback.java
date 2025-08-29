package io.github.mrlucky974.ironbark.event;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface HudRenderBeforeCallback extends HudRenderCallback {
    Event<HudRenderBeforeCallback> EVENT = EventFactory.createArrayBacked(HudRenderBeforeCallback.class, (listeners) -> (matrixStack, delta) -> {
        for (HudRenderBeforeCallback event : listeners) {
            event.onHudRender(matrixStack, delta);
        }
    });
}
