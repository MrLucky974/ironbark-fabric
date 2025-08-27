package io.github.mrlucky974.ironbark.item;

import io.github.mrlucky974.ironbark.list.TagList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RepairGemItem extends Item {
    private final int cooldown;

    public RepairGemItem(int cooldown, Settings settings) {
        super(settings);
        this.cooldown = cooldown;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (world.isClient) return;
        if (!(entity instanceof PlayerEntity playerEntity)) return;

        float cooldown = playerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), 0.0F);
        if (cooldown <= 0) {
            if (repair(playerEntity, false, false, false)) {
                if (stack.isDamageable()) stack.setDamage(1); // Damage the gem.
            }
            playerEntity.getItemCooldownManager().set(stack.getItem(), this.cooldown);
        }
    }

    private static boolean repair(PlayerEntity player, boolean isUnbreakable, boolean repairOtherGems, boolean repairSingleItem) {
        PlayerInventory inventory = player.getInventory();
        boolean flag = false;

        // Scan the player's inventory.
        for (int slotId = 0; slotId < inventory.size(); slotId++) {
            ItemStack stack = inventory.getStack(slotId);

            boolean canRepairGems = (!isUnbreakable && repairOtherGems);
            if ((stack.getItem() instanceof RepairGemItem && !canRepairGems) || stack.isIn(TagList.Items.REPAIR_GEMS_BLACKLIST)) continue;

            if (!stack.isEmpty() && stack.isDamageable()) {
                // Do not repair if holding & using.
                if (player.handSwinging && stack == player.getStackInHand(Hand.MAIN_HAND)) continue;

                // Repair.
                if (stack.isDamaged()) {
                    stack.setDamage(stack.getDamage() - 1);
                    flag = true;
                    if (repairSingleItem) return true; // Only repair one item at a time.
                }
            }
        }

        return flag;
    }
}
