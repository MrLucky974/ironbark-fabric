package io.github.mrlucky974.ironbark.mixin.server;

import io.github.mrlucky974.ironbark.world.IWorld;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "onBlockChanged", at = @At("HEAD"))
    public void onBlockChangedInject(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci) {
        ((IWorld) this).spelunker$UpdateBlock(pos, oldBlock, newBlock);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        ((IWorld) this).spelunker$UpdateChunks();
    }
}
