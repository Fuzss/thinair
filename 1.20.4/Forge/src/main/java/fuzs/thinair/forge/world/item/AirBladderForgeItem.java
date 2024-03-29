package fuzs.thinair.forge.world.item;

import fuzs.thinair.world.item.AirBladderItem;
import net.minecraft.world.item.ItemStack;

public class AirBladderForgeItem extends AirBladderItem {

    public AirBladderForgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
    }
}
