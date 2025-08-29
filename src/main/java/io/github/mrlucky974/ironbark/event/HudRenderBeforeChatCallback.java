package io.github.mrlucky974.ironbark.event;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface HudRenderBeforeChatCallback extends HudRenderCallback {
    Event<HudRenderBeforeChatCallback> EVENT = EventFactory.createArrayBacked(HudRenderBeforeChatCallback.class, (listeners) -> (matrixStack, delta) -> {
        for (HudRenderBeforeChatCallback event : listeners) {
            event.onHudRender(matrixStack, delta);
        }
    });
}
