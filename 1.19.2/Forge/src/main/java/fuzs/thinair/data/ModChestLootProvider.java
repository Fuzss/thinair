package fuzs.thinair.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.ChestLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModChestLootProvider extends LootTableProvider {
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> subProviders = ImmutableList.of(Pair.of(ModChestLoot::new, LootContextParamSets.CHEST));
    private final String modId;

    public ModChestLootProvider(DataGenerator dataGenerator, String modId) {
        super(dataGenerator);
        this.modId = modId;
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return this.subProviders;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {

    }

    private static class ModChestLoot extends ChestLoot {

        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> builder) {
            builder.accept(ModRegistry.SOULFIRE_BOTTLE_BURIED_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 10.0F))))));
            builder.accept(ModRegistry.SOULFIRE_BOTTLE_SHIPWRECK_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.get()).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 5.0F)))).add(EmptyLootItem.emptyItem().setWeight(5))));
            builder.accept(ModRegistry.SOULFIRE_BOTTLE_BIG_RUIN_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.get()).setWeight(8).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 8.0F)))).add(EmptyLootItem.emptyItem().setWeight(2))));
            builder.accept(ModRegistry.SOULFIRE_BOTTLE_SMALL_RUIN_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.get()).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))).add(EmptyLootItem.emptyItem().setWeight(7))));
            builder.accept(ModRegistry.SAFETY_LANTERN_DUNGEON_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SAFETY_LANTERN_BLOCK.get()).setWeight(3).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))).add(EmptyLootItem.emptyItem().setWeight(7))));
            builder.accept(ModRegistry.SAFETY_LANTERN_MINESHAFT_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SAFETY_LANTERN_BLOCK.get()).setWeight(7).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))).add(EmptyLootItem.emptyItem().setWeight(3))));
            builder.accept(ModRegistry.SAFETY_LANTERN_STRONGHOLD_LOOT_TABLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModRegistry.SAFETY_LANTERN_BLOCK.get()).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))).add(EmptyLootItem.emptyItem().setWeight(5))));
        }
    }
}
