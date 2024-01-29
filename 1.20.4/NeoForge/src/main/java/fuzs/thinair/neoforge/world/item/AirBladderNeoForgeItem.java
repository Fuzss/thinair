package fuzs.thinair.neoforge.world.item;

import fuzs.thinair.world.item.AirBladderItem;
import net.minecraft.world.item.ItemStack;

public class AirBladderNeoForgeItem extends AirBladderItem {

    public AirBladderNeoForgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
    }
}
