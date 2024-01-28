package fuzs.thinair.integration.curios;

import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CuriosIntegration {

    public static void onAttachCapabilities(final AttachCapabilitiesEvent<ItemStack> evt) {
        if (!evt.getObject().is(ModRegistry.RESPIRATOR_ITEM.get())) return;
        evt.addCapability(CuriosCapability.ID_ITEM, new ICapabilityProvider() {
            private final ICurio curio = () -> evt.getObject();

            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return CuriosCapability.ITEM.orEmpty(cap, LazyOptional.of(() -> this.curio));
            }
        });
    }

    public static void sendInterModComms() {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
    }

    public static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, CuriosIntegration::onAttachCapabilities);
    }
}
