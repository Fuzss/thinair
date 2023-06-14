package fuzs.thinair.data;

import com.mojang.datafixers.util.Either;
import fuzs.thinair.advancements.AirProtectionSource;
import fuzs.thinair.advancements.criterion.BreatheAirTrigger;
import fuzs.thinair.helper.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import java.util.EnumSet;
import java.util.function.Consumer;

public class ModCraftingRecipes extends RecipeProvider {

    public ModCraftingRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipes) {
        BreatheAirTrigger.Instance yellowTrigger = new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY,
            EnumSet.of(AirQualityLevel.YELLOW, AirQualityLevel.RED), null,
            Either.left(AirProtectionSource.NONE));
        ChangeDimensionTrigger.TriggerInstance netherTrigger = ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER);


        ShapedRecipeBuilder.shaped(ModRegistry.RESPIRATOR_ITEM.get())
            .define('P', Items.PAPER)
            .define('S', Items.STRING)
            .define('C', Items.CHARCOAL)
            .pattern(" S ")
            .pattern("P P")
            .pattern("PCP")
            .unlockedBy("in_dangerous_air", yellowTrigger)
            .save(recipes);

        ShapedRecipeBuilder.shaped(ModRegistry.AIR_BLADDER_ITEM.get())
            .define('L', Items.LEATHER)
            .define('S', Items.STRING)
            .pattern(" LS")
            .pattern("L L")
            .pattern(" L ")
            .unlockedBy("in_dangerous_air", yellowTrigger)
            .save(recipes);

        ShapedRecipeBuilder.shaped(ModRegistry.SAFETY_LANTERN_BLOCK.get())
                .define('X', Items.REDSTONE_TORCH)
                .define('#', Items.COPPER_INGOT)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .unlockedBy("in_dangerous_air", yellowTrigger)
                .save(recipes);

        ShapedRecipeBuilder.shaped(ModRegistry.SOULFIRE_BOTTLE_ITEM.get(), 3)
            .define('G', Ingredient.of(Tags.Items.GLASS))
            .define('S', Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
            .define('C', Ingredient.of(Items.CHARCOAL, Items.COAL))
            .pattern(" C ")
            .pattern("GSG")
            .pattern(" G ")
            .unlockedBy("in_nether", netherTrigger)
            .save(recipes, "soulfire_bottle_from_glass");

        ShapelessRecipeBuilder.shapeless(ModRegistry.SOULFIRE_BOTTLE_ITEM.get(), 3)
            .requires(Items.GLASS_BOTTLE, 3)
            .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
            .requires(Ingredient.of(Items.CHARCOAL, Items.COAL))
            .unlockedBy("in_nether", netherTrigger)
            .save(recipes);

    }
}
