package io.github.mrlucky974.ironbark.mixin;

import io.github.mrlucky974.ironbark.event.BlockStrippedCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {
    @Inject(method = "tryStrip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", ordinal = 0, shift = At.Shift.AFTER))
    private static void ironbark$tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player,
                                          BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
        BlockStrippedCallback.EVENT.invoker().onWoodStripped(world, pos, player, state);
    }
}
