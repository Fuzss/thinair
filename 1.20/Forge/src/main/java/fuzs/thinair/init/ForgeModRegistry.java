package fuzs.thinair.init;

import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.thinair.world.item.AirBladderForgeItem;
import net.minecraft.world.item.Item;

import static fuzs.thinair.init.ModRegistry.REGISTRY;

public class ForgeModRegistry {
    public static final RegistryReference<Item> AIR_BLADDER_ITEM = REGISTRY.registerItem("air_bladder", () -> new AirBladderForgeItem(new Item.Properties().durability(327)));
    public static final RegistryReference<Item> REINFORCED_AIR_BLADDER_ITEM = REGISTRY.registerItem("reinforced_air_bladder", () -> new AirBladderForgeItem(new Item.Properties().durability(1962)));

    public static void touch() {

    }
}
