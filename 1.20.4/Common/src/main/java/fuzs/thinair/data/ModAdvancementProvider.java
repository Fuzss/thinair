package fuzs.thinair.data;

import fuzs.puzzleslib.api.data.v2.AbstractAdvancementProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.thinair.ThinAir;
import fuzs.thinair.advancements.criterion.BreatheAirTrigger;
import fuzs.thinair.advancements.criterion.SignalifyTorchTrigger;
import fuzs.thinair.advancements.criterion.UsedSoulfireTrigger;
import fuzs.thinair.api.v1.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.world.level.block.SafetyLanternBlock;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AbstractAdvancementProvider {
    public static final AdvancementToken ROOT_ADVANCEMENT = new AdvancementToken(ThinAir.id("root"));
    public static final AdvancementToken AIR_BLADDER_ADVANCEMENT = new AdvancementToken(ThinAir.id("air_bladder"));
    public static final AdvancementToken BLUE_AIR_ADVANCEMENT = new AdvancementToken(ThinAir.id("blue_air"));
    public static final AdvancementToken SOULFIRE_BOTTLE_ADVANCEMENT = new AdvancementToken(ThinAir.id("soulfire_bottle"));
    public static final AdvancementToken RESPIRATOR_ADVANCEMENT = new AdvancementToken(ThinAir.id("respirator"));
    public static final AdvancementToken WATER_BREATHING_ADVANCEMENT = new AdvancementToken(ThinAir.id("water_breathing"));
    public static final AdvancementToken SAFETY_LANTERN_ADVANCEMENT = new AdvancementToken(ThinAir.id("safety_lantern"));
    public static final AdvancementToken DISCO_LANTERN_ADVANCEMENT = new AdvancementToken(ThinAir.id("disco_lantern"));
    public static final AdvancementToken SIGNAL_TORCH_ADVANCEMENT = new AdvancementToken(ThinAir.id("signal_torch"));

    public ModAdvancementProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addAdvancements(HolderLookup.Provider registries, Consumer<AdvancementHolder> writer) {
        Advancement.Builder.advancement()
                .display(display(SafetyLanternBlock.getDisplayItemStack(AirQualityLevel.RED), ROOT_ADVANCEMENT.id(), new ResourceLocation("textures/block/deepslate.png"), AdvancementType.TASK, false))
                .addCriterion("breathe_bad_air", BreatheAirTrigger.TriggerInstance.breatheAir(AirQualityLevel.YELLOW, AirQualityLevel.RED))
                .save(writer, ROOT_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(ModRegistry.AIR_BLADDER_ITEM.value().getDefaultInstance(), AIR_BLADDER_ADVANCEMENT.id()))
                .parent(ROOT_ADVANCEMENT.asParent())
                .addCriterion("breathe_bad_air", BreatheAirTrigger.TriggerInstance.breatheAir(AirQualityLevel.YELLOW, AirQualityLevel.RED))
                .addCriterion("using_air_bladder", CriteriaTriggers.USING_ITEM.createCriterion(new UsingItemTrigger.TriggerInstance(Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(ModRegistry.AIR_REFILLER_ITEM_TAG).build()))))
                .save(writer, AIR_BLADDER_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(Items.SOUL_CAMPFIRE.getDefaultInstance(), BLUE_AIR_ADVANCEMENT.id()))
                .parent(AIR_BLADDER_ADVANCEMENT.asParent())
                .addCriterion("breathe_blue_air", BreatheAirTrigger.TriggerInstance.breatheAir(AirQualityLevel.BLUE))
                .save(writer, BLUE_AIR_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(ModRegistry.SOULFIRE_BOTTLE_ITEM.value().getDefaultInstance(), SOULFIRE_BOTTLE_ADVANCEMENT.id(), AdvancementType.GOAL))
                .parent(BLUE_AIR_ADVANCEMENT.asParent())
                .addCriterion("used_soulfire", UsedSoulfireTrigger.TriggerInstance.usedSoulfire(ModRegistry.SOULFIRE_BOTTLE_ITEM.value()))
                .save(writer, SOULFIRE_BOTTLE_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(ModRegistry.RESPIRATOR_ITEM.value().getDefaultInstance(), RESPIRATOR_ADVANCEMENT.id()))
                .parent(ROOT_ADVANCEMENT.asParent())
                .addCriterion("breathe_yellow_air", BreatheAirTrigger.TriggerInstance.breatheAir(AirQualityLevel.YELLOW))
                .addCriterion("has_breathing_equipment", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(AirQualityLevel.YELLOW.getBreathingEquipment())))
                .save(writer, RESPIRATOR_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER_BREATHING), WATER_BREATHING_ADVANCEMENT.id(), AdvancementType.GOAL))
                .parent(RESPIRATOR_ADVANCEMENT.asParent())
                .addCriterion("breathe_red_air", BreatheAirTrigger.TriggerInstance.breatheAir(AirQualityLevel.RED))
                .addCriterion("has_water_breathing", EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects().and(MobEffects.WATER_BREATHING)))
                .save(writer, WATER_BREATHING_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(SafetyLanternBlock.getDisplayItemStack(AirQualityLevel.GREEN), SAFETY_LANTERN_ADVANCEMENT.id(), AdvancementType.TASK))
                .parent(ROOT_ADVANCEMENT.asParent())
                .addCriterion("breathe_bad_air", BreatheAirTrigger.TriggerInstance.breatheAir(AirQualityLevel.YELLOW, AirQualityLevel.RED))
                .addCriterion("has_safety_lantern", InventoryChangeTrigger.TriggerInstance.hasItems(ModRegistry.SAFETY_LANTERN_BLOCK.value()))
                .save(writer, SAFETY_LANTERN_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(SafetyLanternBlock.getDisplayItemStack(AirQualityLevel.YELLOW), DISCO_LANTERN_ADVANCEMENT.id(), AdvancementType.TASK))
                .parent(SAFETY_LANTERN_ADVANCEMENT.asParent())
                .addCriterion("red", dyeUsedOnSafetyLantern(Items.RED_DYE))
                .addCriterion("yellow", dyeUsedOnSafetyLantern(Items.YELLOW_DYE))
                .addCriterion("blue", dyeUsedOnSafetyLantern(Items.BLUE_DYE))
                .addCriterion("green", dyeUsedOnSafetyLantern(Items.GREEN_DYE))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(writer, DISCO_LANTERN_ADVANCEMENT.name());
        Advancement.Builder.advancement()
                .display(display(new ItemStack(Items.TORCH), SIGNAL_TORCH_ADVANCEMENT.id(), AdvancementType.TASK))
                .parent(ROOT_ADVANCEMENT.asParent())
                .addCriterion("signalify_torch", SignalifyTorchTrigger.TriggerInstance.signalifyTorch(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.TORCH, Blocks.WALL_TORCH))))
                .save(writer, SIGNAL_TORCH_ADVANCEMENT.name());
    }

    private static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> dyeUsedOnSafetyLantern(Item item) {
        return ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(ModRegistry.SAFETY_LANTERN_BLOCK.value())),
                ItemPredicate.Builder.item().of(item)
        );
    }
}
