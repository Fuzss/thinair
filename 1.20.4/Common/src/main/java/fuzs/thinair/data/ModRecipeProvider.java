package fuzs.thinair.data;

import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.recipes.CopyTagShapelessRecipeBuilder;
import fuzs.thinair.advancements.criterion.BreatheAirTrigger;
import fuzs.thinair.api.v1.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addRecipes(RecipeOutput recipeOutput) {
        Criterion<BreatheAirTrigger.TriggerInstance> breatheAirTrigger = BreatheAirTrigger.TriggerInstance.breatheAir(AirQualityLevel.YELLOW, AirQualityLevel.RED);
        Criterion<ChangeDimensionTrigger.TriggerInstance> changedDimensionToNetherTrigger = ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModRegistry.RESPIRATOR_ITEM.value())
            .define('P', Items.PAPER)
            .define('S', Items.STRING)
            .define('C', Items.CHARCOAL)
            .pattern(" S ")
            .pattern("P P")
            .pattern("PCP")
            .unlockedBy("in_dangerous_air", breatheAirTrigger)
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModRegistry.AIR_BLADDER_ITEM.value())
            .define('L', Items.LEATHER)
            .define('S', Items.STRING)
            .pattern(" LS")
            .pattern("L L")
            .pattern(" L ")
            .unlockedBy("in_dangerous_air", breatheAirTrigger)
            .save(recipeOutput);
        CopyTagShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModRegistry.REINFORCED_AIR_BLADDER_ITEM.value())
            .requires(Items.NETHERITE_INGOT)
            .requires(ModRegistry.AIR_BLADDER_ITEM.value())
            .copyFrom(ModRegistry.AIR_BLADDER_ITEM.value())
            .unlockedBy(getHasName(ModRegistry.AIR_BLADDER_ITEM.value()), has(ModRegistry.AIR_BLADDER_ITEM.value()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModRegistry.SAFETY_LANTERN_BLOCK.value())
            .define('X', Items.REDSTONE_TORCH)
            .define('#', Items.COPPER_INGOT)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .unlockedBy("in_dangerous_air", breatheAirTrigger)
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModRegistry.SOULFIRE_BOTTLE_ITEM.value(), 3)
            .define('G', Ingredient.of(Blocks.GLASS))
            .define('S', Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
            .define('C', Ingredient.of(Items.CHARCOAL, Items.COAL))
            .pattern(" C ")
            .pattern("GSG")
            .pattern(" G ")
            .unlockedBy("in_nether", changedDimensionToNetherTrigger)
            .save(recipeOutput, "soulfire_bottle_from_glass");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModRegistry.SOULFIRE_BOTTLE_ITEM.value(), 3)
            .requires(Items.GLASS_BOTTLE, 3)
            .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
            .requires(Ingredient.of(Items.CHARCOAL, Items.COAL))
            .unlockedBy("in_nether", changedDimensionToNetherTrigger)
            .save(recipeOutput);
    }
}
