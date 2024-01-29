package fuzs.thinair.fabric.world.item;

import fuzs.thinair.world.item.AirBladderItem;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AirBladderFabricItem extends AirBladderItem implements FabricItem {

    public AirBladderFabricItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }
}
