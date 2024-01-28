package fuzs.thinair.core;

import fuzs.puzzleslib.util.PuzzlesUtil;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = PuzzlesUtil.loadServiceProvider(CommonAbstractions.class);

    default Optional<ItemStack> findEquippedItem(LivingEntity entity, Item item) {
        for (ItemStack stack : entity.getArmorSlots()) {
            if (stack.is(item)) {
                return Optional.of(stack);
            }
        }
        return Optional.empty();
    }
}
