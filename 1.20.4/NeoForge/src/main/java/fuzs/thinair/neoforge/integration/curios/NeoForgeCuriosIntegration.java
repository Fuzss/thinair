package fuzs.thinair.neoforge.integration.curios;

import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class NeoForgeCuriosIntegration {

    public static void registerHandlers() {
        NeoForgeModContainerHelper.getActiveModEventBus().addListener((final RegisterCapabilitiesEvent evt) -> {
            evt.registerItem(CuriosCapability.ITEM, (ItemStack itemStack, Void context) -> {
                return new ICurio() {
                    @Override
                    public ItemStack getStack() {
                        return itemStack;
                    }

                    @Override
                    public boolean canEquipFromUse(SlotContext slotContext) {
                        return true;
                    }
                };
            }, ModRegistry.RESPIRATOR_ITEM.value());
        });
    }
}
