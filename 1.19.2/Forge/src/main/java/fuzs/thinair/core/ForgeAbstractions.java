package fuzs.thinair.core;

import fuzs.puzzleslib.core.ModLoaderEnvironment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class ForgeAbstractions implements CommonAbstractions {

    @Override
    public Optional<ItemStack> findEquippedItem(LivingEntity entity, Item item) {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("curios")) {
            return CuriosApi.getCuriosHelper().findFirstCurio(entity, item).map(SlotResult::stack).or(() -> CommonAbstractions.super.findEquippedItem(entity, item));
        } else {
            return CommonAbstractions.super.findEquippedItem(entity, item);
        }
    }
}
