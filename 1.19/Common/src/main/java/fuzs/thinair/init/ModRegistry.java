package fuzs.thinair.init;

import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.capability.data.CapabilityKey;
import fuzs.puzzleslib.core.CommonAbstractions;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.core.ModLoaderEnvironment;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.init.RegistryReference;
import fuzs.thinair.ThinAir;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.capability.AirBubblePositionsCapabilityImpl;
import fuzs.thinair.capability.AirProtectionCapability;
import fuzs.thinair.capability.AirProtectionCapabilityImpl;
import fuzs.thinair.world.item.AirBladderItem;
import fuzs.thinair.world.item.RespiratorArmorMaterial;
import fuzs.thinair.world.item.SoulfireBottleItem;
import fuzs.thinair.world.level.block.SafetyLanternBlock;
import fuzs.thinair.world.level.block.SignalTorchBlock;
import fuzs.thinair.world.level.block.WallSignalTorchBlock;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    public static final CreativeModeTab TAB = CommonAbstractions.INSTANCE.creativeModeTab(ThinAir.MOD_ID, () -> new ItemStack(ModRegistry.AIR_BLADDER_ITEM.get()));
    
    static final RegistryManager REGISTRY = CommonFactories.INSTANCE.registration(ThinAir.MOD_ID);
    public static final RegistryReference<Block> SIGNAL_TORCH_BLOCK = REGISTRY.registerBlock("signal_torch", () -> new SignalTorchBlock(BlockBehaviour.Properties.copy(Blocks.TORCH)));
    public static final RegistryReference<Block> WALL_SIGNAL_TORCH_BLOCK = REGISTRY.registerBlock("wall_signal_torch", () -> new WallSignalTorchBlock(BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).dropsLike(SIGNAL_TORCH_BLOCK.get())));
    public static final RegistryReference<Block> SAFETY_LANTERN_BLOCK = REGISTRY.registerBlock("safety_lantern", () -> new SafetyLanternBlock(BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> state.getValue(SafetyLanternBlock.AIR_QUALITY).getLightLevel())));
    public static final RegistryReference<Item> RESPIRATOR_ITEM = REGISTRY.registerItem("respirator", () -> getRespiratorItem(new Item.Properties().stacksTo(1).defaultDurability(64).tab(TAB)));
    public static final RegistryReference<Item> AIR_BLADDER_ITEM = REGISTRY.registerItem("air_bladder", () -> new AirBladderItem(new Item.Properties().stacksTo(1).durability(327).tab(TAB)));
    public static final RegistryReference<Item> SOULFIRE_BOTTLE_ITEM = REGISTRY.registerItem("soulfire_bottle", () -> new SoulfireBottleItem(new Item.Properties().tab(TAB)));
    public static final RegistryReference<Item> SAFETY_LANTERN_ITEM = REGISTRY.registerItem("safety_lantern", () -> new BlockItem(SAFETY_LANTERN_BLOCK.get(), new Item.Properties().tab(TAB)));
    public static final RegistryReference<Item> FAKE_ALWAYS_RED_LANTERN_ITEM = REGISTRY.registerItem("fake_always_red_lantern", () -> new Item(new Item.Properties()));
    public static final RegistryReference<Item> FAKE_ALWAYS_YELLOW_LANTERN_ITEM = REGISTRY.registerItem("fake_always_yellow_lantern", () -> new Item(new Item.Properties()));
    public static final RegistryReference<Item> FAKE_ALWAYS_GREEN_LANTERN_ITEM = REGISTRY.registerItem("fake_always_green_lantern", () -> new Item(new Item.Properties()));
    public static final RegistryReference<Item> FAKE_RAINBOW_LANTERN_ITEM = REGISTRY.registerItem("fake_rainbow_lantern", () -> new Item(new Item.Properties()));
    
    static final CapabilityController CAPABILITIES = CommonFactories.INSTANCE.capabilities(ThinAir.MOD_ID);
    public static final CapabilityKey<AirProtectionCapability> AIR_PROTECTION_CAPABILITY = CAPABILITIES.registerEntityCapability("air_protection", AirProtectionCapability.class, o -> new AirProtectionCapabilityImpl(), LivingEntity.class);
    public static final CapabilityKey<AirBubblePositionsCapability> AIR_BUBBLE_POSITIONS_CAPABILITY = CAPABILITIES.registerLevelChunkCapability("air_bubble_positions", AirBubblePositionsCapability.class, o -> new AirBubblePositionsCapabilityImpl());

    public static final ResourceLocation SOULFIRE_BOTTLE_BURIED_LOOT_TABLE = REGISTRY.makeKey("soulfire_bottle_buried");
    public static final ResourceLocation SOULFIRE_BOTTLE_SHIPWRECK_LOOT_TABLE = REGISTRY.makeKey("soulfire_bottle_shipwreck");
    public static final ResourceLocation SOULFIRE_BOTTLE_BIG_RUIN_LOOT_TABLE = REGISTRY.makeKey("soulfire_bottle_big_ruin");
    public static final ResourceLocation SOULFIRE_BOTTLE_SMALL_RUIN_LOOT_TABLE = REGISTRY.makeKey("soulfire_bottle_small_ruin");
    public static final ResourceLocation SAFETY_LANTERN_DUNGEON_LOOT_TABLE = REGISTRY.makeKey("safety_lantern_dungeon");
    public static final ResourceLocation SAFETY_LANTERN_MINESHAFT_LOOT_TABLE = REGISTRY.makeKey("safety_lantern_mineshaft");
    public static final ResourceLocation SAFETY_LANTERN_STRONGHOLD_LOOT_TABLE = REGISTRY.makeKey("safety_lantern_stronghold");

    public static final TagKey<Item> CURIOS_HEAD_TAG = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("curios", "head"));
    public static final TagKey<Item> TRINKETS_HEAD_FACE_TAG = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("trinkets", "head/face"));

    public static void touch() {

    }

    private static Item getRespiratorItem(Item.Properties properties) {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("curios") || ModLoaderEnvironment.INSTANCE.isModLoaded("trinkets")) {
            return new Item(properties);
        } else {
            return new ArmorItem(RespiratorArmorMaterial.INSTANCE, EquipmentSlot.HEAD, properties);
        }
    }
}
