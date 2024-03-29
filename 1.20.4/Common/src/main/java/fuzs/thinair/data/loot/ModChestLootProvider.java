package fuzs.thinair.data.loot;

import fuzs.puzzleslib.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class ModChestLootProvider extends AbstractLootProvider.Simple {

    public ModChestLootProvider(DataProviderContext context) {
        super(LootContextParamSets.CHEST, context);
    }

    @Override
    public void addLootTables() {
        this.add(ModRegistry.SOULFIRE_BOTTLE_BURIED_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.value()).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 10.0F))))));
        this.add(ModRegistry.SOULFIRE_BOTTLE_SHIPWRECK_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.value()).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 5.0F)))).add(EmptyLootItem.emptyItem().setWeight(5))));
        this.add(ModRegistry.SOULFIRE_BOTTLE_BIG_RUIN_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.value()).setWeight(8).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 8.0F)))).add(EmptyLootItem.emptyItem().setWeight(2))));
        this.add(ModRegistry.SOULFIRE_BOTTLE_SMALL_RUIN_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.value()).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))).add(EmptyLootItem.emptyItem().setWeight(7))));
        this.add(ModRegistry.SAFETY_LANTERN_DUNGEON_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SAFETY_LANTERN_BLOCK.value()).setWeight(3).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))).add(EmptyLootItem.emptyItem().setWeight(7))));
        this.add(ModRegistry.SAFETY_LANTERN_MINESHAFT_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SAFETY_LANTERN_BLOCK.value()).setWeight(7).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))).add(EmptyLootItem.emptyItem().setWeight(3))));
        this.add(ModRegistry.SAFETY_LANTERN_STRONGHOLD_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SAFETY_LANTERN_BLOCK.value()).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))).add(EmptyLootItem.emptyItem().setWeight(5))));
    }
}
