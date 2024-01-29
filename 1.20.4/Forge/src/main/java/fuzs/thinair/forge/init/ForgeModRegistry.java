package fuzs.thinair.forge.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.thinair.ThinAir;
import fuzs.thinair.forge.world.item.AirBladderForgeItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

public class ForgeModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.from(ThinAir.MOD_ID);
    public static final Holder.Reference<Item> AIR_BLADDER_ITEM = REGISTRY.registerItem("air_bladder", () -> new AirBladderForgeItem(new Item.Properties().durability(327)));
    public static final Holder.Reference<Item> REINFORCED_AIR_BLADDER_ITEM = REGISTRY.registerItem("reinforced_air_bladder", () -> new AirBladderForgeItem(new Item.Properties().durability(1962)));

    public static void touch() {

    }
}
