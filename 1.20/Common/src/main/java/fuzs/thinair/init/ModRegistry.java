package fuzs.thinair.init;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.capability.v2.data.CapabilityKey;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.thinair.ThinAir;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.capability.AirBubblePositionsCapabilityImpl;
import fuzs.thinair.world.item.RespiratorItem;
import fuzs.thinair.world.item.SoulfireBottleItem;
import fuzs.thinair.world.level.block.SafetyLanternBlock;
import fuzs.thinair.world.level.block.SignalTorchBlock;
import fuzs.thinair.world.level.block.WallSignalTorchBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.instant(ThinAir.MOD_ID);
    public static final RegistryReference<Block> SIGNAL_TORCH_BLOCK = REGISTRY.registerBlock("signal_torch", () -> new SignalTorchBlock(BlockBehaviour.Properties.copy(Blocks.TORCH)));
    public static final RegistryReference<Block> WALL_SIGNAL_TORCH_BLOCK = REGISTRY.registerBlock("wall_signal_torch", () -> new WallSignalTorchBlock(BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).dropsLike(SIGNAL_TORCH_BLOCK.get())));
    public static final RegistryReference<Block> SAFETY_LANTERN_BLOCK = REGISTRY.registerBlock("safety_lantern", () -> new SafetyLanternBlock(BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> state.getValue(SafetyLanternBlock.AIR_QUALITY).getLightLevel())));
    public static final RegistryReference<Item> RESPIRATOR_ITEM = REGISTRY.registerItem("respirator", () -> new RespiratorItem(new Item.Properties().durability(77)));
    public static final RegistryReference<Item> AIR_BLADDER_ITEM = REGISTRY.placeholder(Registries.ITEM, "air_bladder");
    public static final RegistryReference<Item> SOULFIRE_BOTTLE_ITEM = REGISTRY.registerItem("soulfire_bottle", () -> new SoulfireBottleItem(new Item.Properties()));
    public static final RegistryReference<Item> SAFETY_LANTERN_ITEM = REGISTRY.registerItem("safety_lantern", () -> new BlockItem(SAFETY_LANTERN_BLOCK.get(), new Item.Properties()));

    public static final TagKey<Item> BREATHING_UTILITY_ITEM_TAG = REGISTRY.registerItemTag("breathing_utility");
    public static final TagKey<EntityType<?>> AIR_QUALITY_SENSITIVE_ENTITY_TYPE_TAG = REGISTRY.registerEntityTypeTag("air_quality_sensitive");
    
    static final CapabilityController CAPABILITIES = CapabilityController.from(ThinAir.MOD_ID);
    public static final CapabilityKey<AirBubblePositionsCapability> AIR_BUBBLE_POSITIONS_CAPABILITY = CAPABILITIES.registerLevelChunkCapability("air_bubble_positions", AirBubblePositionsCapability.class, o -> new AirBubblePositionsCapabilityImpl());

    public static final ResourceLocation SOULFIRE_BOTTLE_BURIED_LOOT_TABLE = REGISTRY.makeKey("soulfire_bottle_buried");
    public static final ResourceLocation SOULFIRE_BOTTLE_SHIPWRECK_LOOT_TABLE = REGISTRY.makeKey("soulfire_bottle_shipwreck");
    public static final ResourceLocation SOULFIRE_BOTTLE_BIG_RUIN_LOOT_TABLE = REGISTRY.makeKey("soulfire_bottle_big_ruin");
    public static final ResourceLocation SOULFIRE_BOTTLE_SMALL_RUIN_LOOT_TABLE = REGISTRY.makeKey("soulfire_bottle_small_ruin");
    public static final ResourceLocation SAFETY_LANTERN_DUNGEON_LOOT_TABLE = REGISTRY.makeKey("safety_lantern_dungeon");
    public static final ResourceLocation SAFETY_LANTERN_MINESHAFT_LOOT_TABLE = REGISTRY.makeKey("safety_lantern_mineshaft");
    public static final ResourceLocation SAFETY_LANTERN_STRONGHOLD_LOOT_TABLE = REGISTRY.makeKey("safety_lantern_stronghold");

    public static void touch() {

    }
}
