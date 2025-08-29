package io.github.mrlucky974.ironbark.item;

import io.github.mrlucky974.ironbark.spice.Spice;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpiceMixItem extends Item implements SpiceIngredient {
    public SpiceMixItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        List<Spice> spices = getSpices(stack);

        Map<Spice, Integer> spiceCounts = new HashMap<>();
        for (Spice spice : spices) {
            spiceCounts.merge(spice, 1, Integer::sum);
        }

        for (Map.Entry<Spice, Integer> entry : spiceCounts.entrySet()) {
            Spice spice = entry.getKey();
            int count = entry.getValue();

            tooltip.add(Text.literal("")
                    .append(spice.getName())
                    .append(" x" + count)
            );
        }
    }
}
