package fuzs.thinair.init;

import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.thinair.world.item.AirBladderFabricItem;
import net.minecraft.world.item.Item;

import static fuzs.thinair.init.ModRegistry.REGISTRY;

public class FabricModRegistry {
    public static final RegistryReference<Item> AIR_BLADDER_ITEM = REGISTRY.registerItem("air_bladder", () -> new AirBladderFabricItem(new Item.Properties().stacksTo(1).durability(327)));

    public static void touch() {

    }
}
