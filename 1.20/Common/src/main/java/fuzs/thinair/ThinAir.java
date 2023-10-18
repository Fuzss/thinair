package fuzs.thinair;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingHurtCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.level.ServerChunkEvents;
import fuzs.puzzleslib.api.event.v1.level.ServerLevelEvents;
import fuzs.puzzleslib.api.event.v1.level.ServerLevelTickEvents;
import fuzs.puzzleslib.api.event.v1.server.LootTableLoadEvents;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.thinair.handler.DrownedAttackHandler;
import fuzs.thinair.handler.ServerAirBubbleTracker;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.world.level.block.SignalTorchBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.IntPredicate;

public class ThinAir implements ModConstructor {
    public static final String MOD_ID = "thinair";
    public static final String MOD_NAME = "Thin Air";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        registerHandlers();
    }

    private static void registerHandlers() {
        LootTableLoadEvents.MODIFY.register((LootDataManager lootManager, ResourceLocation identifier, Consumer<LootPool> addPool, IntPredicate removePool) -> {
            injectLootPool(identifier, addPool, BuiltInLootTables.BURIED_TREASURE, ModRegistry.SOULFIRE_BOTTLE_BURIED_LOOT_TABLE);
            injectLootPool(identifier, addPool, BuiltInLootTables.SHIPWRECK_TREASURE, ModRegistry.SOULFIRE_BOTTLE_SHIPWRECK_LOOT_TABLE);
            injectLootPool(identifier, addPool, BuiltInLootTables.UNDERWATER_RUIN_BIG, ModRegistry.SOULFIRE_BOTTLE_BIG_RUIN_LOOT_TABLE);
            injectLootPool(identifier, addPool, BuiltInLootTables.UNDERWATER_RUIN_SMALL, ModRegistry.SOULFIRE_BOTTLE_SMALL_RUIN_LOOT_TABLE);
            injectLootPool(identifier, addPool, BuiltInLootTables.SIMPLE_DUNGEON, ModRegistry.SAFETY_LANTERN_DUNGEON_LOOT_TABLE);
            injectLootPool(identifier, addPool, BuiltInLootTables.ABANDONED_MINESHAFT, ModRegistry.SAFETY_LANTERN_MINESHAFT_LOOT_TABLE);
            injectLootPool(identifier, addPool, BuiltInLootTables.STRONGHOLD_CORRIDOR, ModRegistry.SAFETY_LANTERN_STRONGHOLD_LOOT_TABLE);
        });
        PlayerInteractEvents.USE_BLOCK.register(SignalTorchBlock::onUseBlock);
        ServerChunkEvents.LOAD.register(ServerAirBubbleTracker::onChunkUnload);
        ServerLevelEvents.UNLOAD.register(ServerAirBubbleTracker::onLevelUnload);
        ServerLevelTickEvents.END.register(ServerAirBubbleTracker::consumeReqdChunksServer);
        LivingHurtCallback.EVENT.register(DrownedAttackHandler::onLivingHurt);
    }

    private static void injectLootPool(ResourceLocation identifier, Consumer<LootPool> addPool, ResourceLocation builtInLootTable, ResourceLocation injectedLootTable) {
        if (identifier.equals(builtInLootTable)) {
            addPool.accept(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootTableReference.lootTableReference(injectedLootTable)).build());
        }
    }

    @Override
    public void onRegisterCreativeModeTabs(CreativeModeTabContext context) {
        context.registerCreativeModeTab(CreativeModeTabConfigurator.from(MOD_ID).icon(() -> new ItemStack(ModRegistry.AIR_BLADDER_ITEM.get())).displayItems((itemDisplayParameters, output) -> {
            output.accept(ModRegistry.RESPIRATOR_ITEM.get());
            output.accept(ModRegistry.AIR_BLADDER_ITEM.get());
            output.accept(ModRegistry.SOULFIRE_BOTTLE_ITEM.get());
            output.accept(ModRegistry.SAFETY_LANTERN_ITEM.get());
        }));
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
