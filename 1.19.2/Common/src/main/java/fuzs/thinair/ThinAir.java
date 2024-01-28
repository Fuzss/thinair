package fuzs.thinair;

import fuzs.puzzleslib.core.ModConstructor;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThinAir implements ModConstructor {
    public static final String MOD_ID = "thinair";
    public static final String MOD_NAME = "Thin Air";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
    }

    @Override
    public void onLootTableModification(LootTablesModifyContext context) {
        injectLootPool(context, BuiltInLootTables.BURIED_TREASURE, ModRegistry.SOULFIRE_BOTTLE_BURIED_LOOT_TABLE);
        injectLootPool(context, BuiltInLootTables.SHIPWRECK_TREASURE, ModRegistry.SOULFIRE_BOTTLE_SHIPWRECK_LOOT_TABLE);
        injectLootPool(context, BuiltInLootTables.UNDERWATER_RUIN_BIG, ModRegistry.SOULFIRE_BOTTLE_BIG_RUIN_LOOT_TABLE);
        injectLootPool(context, BuiltInLootTables.UNDERWATER_RUIN_SMALL, ModRegistry.SOULFIRE_BOTTLE_SMALL_RUIN_LOOT_TABLE);
        injectLootPool(context, BuiltInLootTables.SIMPLE_DUNGEON, ModRegistry.SAFETY_LANTERN_DUNGEON_LOOT_TABLE);
        injectLootPool(context, BuiltInLootTables.ABANDONED_MINESHAFT, ModRegistry.SAFETY_LANTERN_MINESHAFT_LOOT_TABLE);
        injectLootPool(context, BuiltInLootTables.STRONGHOLD_CORRIDOR, ModRegistry.SAFETY_LANTERN_STRONGHOLD_LOOT_TABLE);
    }

    private static void injectLootPool(LootTablesModifyContext context, ResourceLocation builtInLootTable, ResourceLocation injectedLootTable) {
        if (context.getId().equals(builtInLootTable)) {
            context.addLootPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootTableReference.lootTableReference(injectedLootTable)).build());
        }
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
