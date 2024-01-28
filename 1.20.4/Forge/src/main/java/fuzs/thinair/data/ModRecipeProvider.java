package fuzs.thinair.data;

import fuzs.puzzleslib.api.data.v1.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v1.recipes.CopyTagShapelessRecipeBuilder;
import fuzs.thinair.ThinAir;
import fuzs.thinair.advancements.criterion.BreatheAirTrigger;
import fuzs.thinair.api.v1.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.EnumSet;
import java.util.function.Consumer;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> exporter) {
        BreatheAirTrigger.Instance yellowTrigger = new BreatheAirTrigger.Instance(ContextAwarePredicate.ANY,
            EnumSet.of(AirQualityLevel.YELLOW, AirQualityLevel.RED));
        ChangeDimensionTrigger.TriggerInstance netherTrigger = ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER);


        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModRegistry.RESPIRATOR_ITEM.get())
            .define('P', Items.PAPER)
            .define('S', Items.STRING)
            .define('C', Items.CHARCOAL)
            .pattern(" S ")
            .pattern("P P")
            .pattern("PCP")
            .unlockedBy("in_dangerous_air", yellowTrigger)
            .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModRegistry.AIR_BLADDER_ITEM.get())
            .define('L', Items.LEATHER)
            .define('S', Items.STRING)
            .pattern(" LS")
            .pattern("L L")
            .pattern(" L ")
            .unlockedBy("in_dangerous_air", yellowTrigger)
            .save(exporter);

        CopyTagShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModRegistry.REINFORCED_AIR_BLADDER_ITEM.get())
                .requires(Items.NETHERITE_INGOT)
                .requires(ModRegistry.AIR_BLADDER_ITEM.get())
                .copyFrom(ModRegistry.AIR_BLADDER_ITEM.get())
                .unlockedBy(getHasName(ModRegistry.AIR_BLADDER_ITEM.get()), has(ModRegistry.AIR_BLADDER_ITEM.get()))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModRegistry.SAFETY_LANTERN_BLOCK.get())
                .define('X', Items.REDSTONE_TORCH)
                .define('#', Items.COPPER_INGOT)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .unlockedBy("in_dangerous_air", yellowTrigger)
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModRegistry.SOULFIRE_BOTTLE_ITEM.get(), 3)
            .define('G', Ingredient.of(Tags.Items.GLASS))
            .define('S', Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
            .define('C', Ingredient.of(Items.CHARCOAL, Items.COAL))
            .pattern(" C ")
            .pattern("GSG")
            .pattern(" G ")
            .unlockedBy("in_nether", netherTrigger)
            .save(exporter, ThinAir.id("soulfire_bottle_from_glass"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModRegistry.SOULFIRE_BOTTLE_ITEM.get(), 3)
            .requires(Items.GLASS_BOTTLE, 3)
            .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
            .requires(Ingredient.of(Items.CHARCOAL, Items.COAL))
            .unlockedBy("in_nether", netherTrigger)
            .save(exporter);

    }
}
