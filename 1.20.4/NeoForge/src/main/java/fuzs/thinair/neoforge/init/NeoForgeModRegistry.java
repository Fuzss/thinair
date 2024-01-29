package fuzs.thinair.neoforge.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.thinair.ThinAir;
import fuzs.thinair.neoforge.world.item.AirBladderNeoForgeItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

public class NeoForgeModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.from(ThinAir.MOD_ID);
    public static final Holder.Reference<Item> AIR_BLADDER_ITEM = REGISTRY.registerItem("air_bladder", () -> new AirBladderNeoForgeItem(new Item.Properties().durability(327)));
    public static final Holder.Reference<Item> REINFORCED_AIR_BLADDER_ITEM = REGISTRY.registerItem("reinforced_air_bladder", () -> new AirBladderNeoForgeItem(new Item.Properties().durability(1962)));

    public static void touch() {

    }
}
