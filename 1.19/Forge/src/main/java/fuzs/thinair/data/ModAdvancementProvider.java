package fuzs.thinair.data;

import com.mojang.datafixers.util.Either;
import fuzs.thinair.ThinAir;
import fuzs.thinair.advancements.*;
import fuzs.thinair.advancements.criterion.BreatheAirTrigger;
import fuzs.thinair.advancements.criterion.SignalificateTorchTrigger;
import fuzs.thinair.advancements.criterion.UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger;
import fuzs.thinair.helper.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.EnumSet;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    protected static DisplayInfo simple(ItemLike icon, String name, FrameType frameType) {
        return simpleWithBackground(icon, name, frameType, null);
    }

    protected static DisplayInfo simpleWithBackground(ItemLike icon, String name, FrameType frameType, ResourceLocation background) {
        String expandedName = "advancement." + ThinAir.MOD_ID + ":" + name;
        return new DisplayInfo(new ItemStack(icon.asItem()), Component.translatable(expandedName), Component.translatable(expandedName + ".desc"), background, frameType, true, true, false);
    }

    private static String prefix(String name) {
        return ThinAir.MOD_ID + ":" + name;
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> advancements, ExistingFileHelper fileHelper) {
        var root = Advancement.Builder.advancement().display(simpleWithBackground(ModRegistry.FAKE_ALWAYS_RED_LANTERN_ITEM.get(), "root", FrameType.TASK, new ResourceLocation("textures/block/deepslate.png"))).addCriterion("bad_air", new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY, EnumSet.of(AirQualityLevel.YELLOW, AirQualityLevel.RED), null, null)).save(advancements, prefix("root"));

        var bladder = Advancement.Builder.advancement().display(simple(ModRegistry.AIR_BLADDER_ITEM.get(), "air_bladder", FrameType.TASK)).parent(root).addCriterion("bad_air", new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY, EnumSet.of(AirQualityLevel.YELLOW, AirQualityLevel.RED), null, Either.left(AirProtectionSource.BLADDER))).save(advancements, prefix("air_bladder"));

        var soul = Advancement.Builder.advancement().display(simple(Items.SOUL_CAMPFIRE, "soul", FrameType.TASK)).parent(bladder).addCriterion("bad_air", new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY, EnumSet.allOf(AirQualityLevel.class), AirSource.SOUL, null)).save(advancements, prefix("soul"));

        Advancement.Builder.advancement().display(simple(ModRegistry.SOULFIRE_BOTTLE_ITEM.get(), "soulfire_bottle", FrameType.GOAL)).parent(soul).addCriterion("on_use", new UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger.Instance(EntityPredicate.Composite.ANY)).save(advancements, prefix("soulfire_bottle"));

        var protectYellow = Advancement.Builder.advancement().display(simple(ModRegistry.RESPIRATOR_ITEM.get(), "protection_from_yellow", FrameType.TASK)).parent(root).addCriterion("protecc", new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY, EnumSet.of(AirQualityLevel.YELLOW, AirQualityLevel.RED), null, null)).save(advancements, prefix("protection_from_yellow"));

        Advancement.Builder.advancement().display(new DisplayInfo(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER_BREATHING), Component.translatable("advancement." + ThinAir.MOD_ID + ":protection_from_red"), Component.translatable("advancement." + ThinAir.MOD_ID + ":protection_from_red.desc"), null, FrameType.GOAL, true, true, false)).parent(protectYellow).addCriterion("protecc", new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY, EnumSet.of(AirQualityLevel.RED), null, Either.right(Unit.INSTANCE))).save(advancements, prefix("protection_from_red"));

        var lantern = Advancement.Builder.advancement().display(simple(ModRegistry.FAKE_ALWAYS_GREEN_LANTERN_ITEM.get(), "lantern", FrameType.TASK)).parent(root).addCriterion("place", new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new ItemPredicate[]{ItemPredicate.Builder.item().of(ModRegistry.SAFETY_LANTERN_BLOCK.get()).build()})).save(advancements, prefix("lantern"));

        Advancement.Builder.advancement().display(simple(ModRegistry.FAKE_RAINBOW_LANTERN_ITEM.get(), "disco_lantern", FrameType.TASK)).parent(lantern).addCriterion("red", new ItemInteractWithBlockTrigger.TriggerInstance(CriteriaTriggers.ITEM_USED_ON_BLOCK.getId(), EntityPredicate.Composite.ANY, LocationPredicate.ANY, ItemPredicate.Builder.item().of(Tags.Items.DYES_RED).build())).addCriterion("yellow", new ItemInteractWithBlockTrigger.TriggerInstance(CriteriaTriggers.ITEM_USED_ON_BLOCK.getId(), EntityPredicate.Composite.ANY, LocationPredicate.ANY, ItemPredicate.Builder.item().of(Tags.Items.DYES_YELLOW).build())).addCriterion("blue", new ItemInteractWithBlockTrigger.TriggerInstance(CriteriaTriggers.ITEM_USED_ON_BLOCK.getId(), EntityPredicate.Composite.ANY, LocationPredicate.ANY, ItemPredicate.Builder.item().of(Tags.Items.DYES_BLUE).build())).addCriterion("green", new ItemInteractWithBlockTrigger.TriggerInstance(CriteriaTriggers.ITEM_USED_ON_BLOCK.getId(), EntityPredicate.Composite.ANY, LocationPredicate.ANY, ItemPredicate.Builder.item().of(Tags.Items.DYES_GREEN).build())).requirements(new String[][]{{"red", "yellow", "blue", "green"}}).save(advancements, prefix("disco_lantern"));

        Advancement.Builder.advancement().display(simple(Items.TORCH, "signal_torch", FrameType.TASK)).parent(root).addCriterion("use", new SignalificateTorchTrigger.Instance(EntityPredicate.Composite.ANY, LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.TORCH, Blocks.WALL_TORCH).build()).build())).save(advancements, prefix("signal_torch"));
    }
}
