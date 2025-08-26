package io.github.mrlucky974.ironbark.mixin.client;

import com.mojang.authlib.GameProfile;
import io.github.mrlucky974.ironbark.client.IronbarkClient;
import io.github.mrlucky974.ironbark.client.renderer.IronbarkEffectRenderer;
import io.github.mrlucky974.ironbark.init.StatusEffectInit;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickInject(CallbackInfo ci) {
        IronbarkEffectRenderer renderer = IronbarkClient.effectRenderer;
        if (renderer.setActive(hasStatusEffect(StatusEffectInit.SPELUNKER)))
            renderer.clear();
    }
}
