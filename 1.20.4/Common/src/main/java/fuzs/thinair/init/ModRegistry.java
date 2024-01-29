package fuzs.thinair.init;

import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.capability.v3.data.LevelChunkCapabilityKey;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.BoundTagFactory;
import fuzs.puzzleslib.api.item.v2.ItemEquipmentFactories;
import fuzs.thinair.ThinAir;
import fuzs.thinair.advancements.criterion.BreatheAirTrigger;
import fuzs.thinair.advancements.criterion.SignalifyTorchTrigger;
import fuzs.thinair.advancements.criterion.UsedSoulfireTrigger;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.world.item.SoulfireBottleItem;
import fuzs.thinair.world.level.block.SafetyLanternBlock;
import fuzs.thinair.world.level.block.SignalTorchBlock;
import fuzs.thinair.world.level.block.WallSignalTorchBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    public static final ArmorMaterial RESPIRATOR_ARMOR_MATERIAL = ItemEquipmentFactories.registerArmorMaterial(ThinAir.id("respirator"), () -> Ingredient.of(Items.CHARCOAL));
    
    static final RegistryManager REGISTRY = RegistryManager.from(ThinAir.MOD_ID);
    public static final Holder.Reference<Block> SIGNAL_TORCH_BLOCK = REGISTRY.registerBlock("signal_torch", () -> new SignalTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH)));
    public static final Holder.Reference<Block> WALL_SIGNAL_TORCH_BLOCK = REGISTRY.registerBlock("wall_signal_torch", () -> new WallSignalTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WALL_TORCH).dropsLike(SIGNAL_TORCH_BLOCK.value())));
    public static final Holder.Reference<Block> SAFETY_LANTERN_BLOCK = REGISTRY.registerBlock("safety_lantern", () -> new SafetyLanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> state.getValue(SafetyLanternBlock.AIR_QUALITY).getLightLevel())));
    public static final Holder.Reference<Item> RESPIRATOR_ITEM;
    public static final Holder.Reference<Item> AIR_BLADDER_ITEM = REGISTRY.registerLazily(Registries.ITEM, "air_bladder");
    public static final Holder.Reference<Item> REINFORCED_AIR_BLADDER_ITEM = REGISTRY.registerLazily(Registries.ITEM, "reinforced_air_bladder");
    public static final Holder.Reference<Item> SOULFIRE_BOTTLE_ITEM = REGISTRY.registerItem("soulfire_bottle", () -> new SoulfireBottleItem(new Item.Properties()));
    public static final Holder.Reference<Item> SAFETY_LANTERN_ITEM = REGISTRY.registerBlockItem(SAFETY_LANTERN_BLOCK);
    public static final Holder.Reference<BreatheAirTrigger> BREATHE_AIR_TRIGGER = REGISTRY.register(Registries.TRIGGER_TYPE, "breathe_air", () -> new BreatheAirTrigger());
    public static final Holder.Reference<SignalifyTorchTrigger> SIGNALIFY_TORCH_TRIGGER = REGISTRY.register(Registries.TRIGGER_TYPE, "signalify_torch", () -> new SignalifyTorchTrigger());
    public static final Holder.Reference<UsedSoulfireTrigger> USED_SOULFIRE_TRIGGER = REGISTRY.register(Registries.TRIGGER_TYPE, "used_soulfire", () -> new UsedSoulfireTrigger());
    
    static final BoundTagFactory TAGS = BoundTagFactory.make(ThinAir.MOD_ID);
    public static final TagKey<Item> AIR_REFILLER_ITEM_TAG = TAGS.registerItemTag("air_refiller");
    public static final TagKey<EntityType<?>> AIR_QUALITY_SENSITIVE_ENTITY_TYPE_TAG = TAGS.registerEntityTypeTag("air_quality_sensitive");
    
    public static final ResourceLocation SOULFIRE_BOTTLE_BURIED_LOOT_TABLE = ThinAir.id("chest/inject/soulfire_bottle_buried");
    public static final ResourceLocation SOULFIRE_BOTTLE_SHIPWRECK_LOOT_TABLE = ThinAir.id("chest/inject/soulfire_bottle_shipwreck");
    public static final ResourceLocation SOULFIRE_BOTTLE_BIG_RUIN_LOOT_TABLE = ThinAir.id("chest/inject/soulfire_bottle_big_ruin");
    public static final ResourceLocation SOULFIRE_BOTTLE_SMALL_RUIN_LOOT_TABLE = ThinAir.id("chest/inject/soulfire_bottle_small_ruin");
    public static final ResourceLocation SAFETY_LANTERN_DUNGEON_LOOT_TABLE = ThinAir.id("chest/inject/safety_lantern_dungeon");
    public static final ResourceLocation SAFETY_LANTERN_MINESHAFT_LOOT_TABLE = ThinAir.id("chest/inject/safety_lantern_mineshaft");
    public static final ResourceLocation SAFETY_LANTERN_STRONGHOLD_LOOT_TABLE = ThinAir.id("chest/inject/safety_lantern_stronghold");
    
    static final CapabilityController CAPABILITIES = CapabilityController.from(ThinAir.MOD_ID);
    public static final LevelChunkCapabilityKey<AirBubblePositionsCapability> AIR_BUBBLE_POSITIONS_CAPABILITY = CAPABILITIES.registerLevelChunkCapability("air_bubble_positions", AirBubblePositionsCapability.class, AirBubblePositionsCapability::new);

    static {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("curios") || ModLoaderEnvironment.INSTANCE.isModLoaded("trinkets")) {
            RESPIRATOR_ITEM = REGISTRY.registerItem("respirator", () -> new Item(new Item.Properties().durability(77)));
        } else {
            RESPIRATOR_ITEM = REGISTRY.registerItem("respirator", () -> new ArmorItem(RESPIRATOR_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().durability(77)));
        }
    }

    public static void touch() {

    }
}
