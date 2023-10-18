package fuzs.thinair.data;

import fuzs.thinair.advancements.criterion.BreatheAirTrigger;
import fuzs.thinair.advancements.criterion.SignalificateTorchTrigger;
import fuzs.thinair.advancements.criterion.UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger;
import fuzs.thinair.api.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.world.level.block.SafetyLanternBlock;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.EnumSet;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AbstractAdvancementProvider {

    public ModAdvancementProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> exporter, ExistingFileHelper fileHelper) {
        Advancement root = Advancement.Builder.advancement().display(this.simpleWithBackground(SafetyLanternBlock.getDisplayItemStack(AirQualityLevel.RED), "root", FrameType.TASK, new ResourceLocation("textures/block/deepslate.png"))).addCriterion("bad_air", new BreatheAirTrigger.Instance(ContextAwarePredicate.ANY, EnumSet.of(AirQualityLevel.YELLOW, AirQualityLevel.RED))).save(exporter, this.prefix("root"));
        Advancement soul = Advancement.Builder.advancement().display(this.simple(new ItemStack(Items.SOUL_CAMPFIRE), "soul", FrameType.TASK)).parent(root).addCriterion("bad_air", new BreatheAirTrigger.Instance(ContextAwarePredicate.ANY, EnumSet.of(AirQualityLevel.BLUE))).save(exporter, this.prefix("soul"));
        Advancement.Builder.advancement().display(this.simple(new ItemStack(ModRegistry.SOULFIRE_BOTTLE_ITEM.get()), "soulfire_bottle", FrameType.GOAL)).parent(soul).addCriterion("on_use", new UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger.Instance(ContextAwarePredicate.ANY)).save(exporter, this.prefix("soulfire_bottle"));
        Advancement protectYellow = Advancement.Builder.advancement().display(this.simple(new ItemStack(ModRegistry.RESPIRATOR_ITEM.get()), "protection_from_yellow", FrameType.TASK)).parent(root).addCriterion("protecc", new BreatheAirTrigger.Instance(ContextAwarePredicate.ANY, EnumSet.of(AirQualityLevel.YELLOW, AirQualityLevel.RED))).save(exporter, this.prefix("protection_from_yellow"));
        Advancement.Builder.advancement().display(new DisplayInfo(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER_BREATHING), Component.translatable("advancement." + this.modId + ":protection_from_red"), Component.translatable("advancement." + this.modId + ":protection_from_red.desc"), null, FrameType.GOAL, true, true, false)).parent(protectYellow).addCriterion("protecc", new BreatheAirTrigger.Instance(ContextAwarePredicate.ANY, EnumSet.of(AirQualityLevel.RED))).save(exporter, this.prefix("protection_from_red"));
        Advancement lantern = Advancement.Builder.advancement().display(this.simple(SafetyLanternBlock.getDisplayItemStack(AirQualityLevel.GREEN), "lantern", FrameType.TASK)).parent(root).addCriterion("place", new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new ItemPredicate[]{ItemPredicate.Builder.item().of(ModRegistry.SAFETY_LANTERN_BLOCK.get()).build()})).save(exporter, this.prefix("lantern"));
        Advancement.Builder.advancement().display(this.simple(SafetyLanternBlock.getDisplayItemStack(AirQualityLevel.YELLOW), "disco_lantern", FrameType.TASK)).parent(lantern).addCriterion("red", dyeUsedOnSafetyLantern(Items.RED_DYE)).addCriterion("yellow", dyeUsedOnSafetyLantern(Items.YELLOW_DYE)).addCriterion("blue", dyeUsedOnSafetyLantern(Items.BLUE_DYE)).addCriterion("green", dyeUsedOnSafetyLantern(Items.GREEN_DYE)).requirements(new String[][]{{"red", "yellow", "blue", "green"}}).save(exporter, this.prefix("disco_lantern"));
        Advancement.Builder.advancement().display(this.simple(new ItemStack(Items.TORCH), "signal_torch", FrameType.TASK)).parent(root).addCriterion("use", new SignalificateTorchTrigger.Instance(ContextAwarePredicate.ANY, LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.TORCH, Blocks.WALL_TORCH).build()).build())).save(exporter, this.prefix("signal_torch"));
    }

    private static CriterionTriggerInstance dyeUsedOnSafetyLantern(Item dyeItem) {
        LootItemCondition locationCheck = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(ModRegistry.SAFETY_LANTERN_BLOCK.get()).build())).build();
        LootItemCondition matchTool = MatchTool.toolMatches(ItemPredicate.Builder.item().of(dyeItem)).build();
        return new ItemUsedOnLocationTrigger.TriggerInstance(((CriterionTrigger<?>) CriteriaTriggers.ITEM_USED_ON_BLOCK).getId(), ContextAwarePredicate.ANY, ContextAwarePredicate.create(locationCheck, matchTool));
    }
}
