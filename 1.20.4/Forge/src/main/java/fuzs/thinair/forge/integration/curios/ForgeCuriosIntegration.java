package fuzs.thinair.forge.integration.curios;

import fuzs.thinair.init.ModRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class ForgeCuriosIntegration {

    public static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, (AttachCapabilitiesEvent<ItemStack> evt) -> {
            ItemStack itemStack = evt.getObject();
            if (itemStack.is(ModRegistry.RESPIRATOR_ITEM.get())) {
                ICapabilityProvider provider = CuriosApi.createCurioProvider(new ICurio() {

                    @Override
                    public ItemStack getStack() {
                        return itemStack;
                    }

                    @Override
                    public boolean canEquipFromUse(SlotContext slotContext) {
                        return true;
                    }
                });
                evt.addCapability(CuriosCapability.ID_ITEM, provider);
            }
        });
    }
}
