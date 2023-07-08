package fuzs.thinair.data;

import fuzs.thinair.world.level.block.SafetyLanternBlock;
import fuzs.thinair.helper.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Locale;

public class ModBlockModels extends BlockStateProvider {

    public ModBlockModels(DataGenerator dataGenerator, String modId, ExistingFileHelper fileHelper) {
        super(dataGenerator, modId, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ResourceLocation torchTex = this.modLoc("block/signal_torch");
        this.simpleBlock(ModRegistry.SIGNAL_TORCH_BLOCK.get(), this.models().torch("signal_torch", torchTex).renderType("cutout"));
        this.horizontalBlock(ModRegistry.WALL_SIGNAL_TORCH_BLOCK.get(), this.models().torchWall("wall_signal_torch", torchTex).renderType("cutout"), 90);


        this.getVariantBuilder(ModRegistry.SAFETY_LANTERN_BLOCK.get()).forAllStates(bs -> {
            AirQualityLevel quality = bs.getValue(SafetyLanternBlock.AIR_QUALITY);
            String templatePath = bs.getValue(LanternBlock.HANGING) ? "template_hanging_lantern" : "template_lantern";
            String name = "lantern_" + quality.name().toLowerCase(Locale.ROOT);
            ResourceLocation texPath = this.modLoc("block/" + name);
            BlockModelBuilder model = this.models().withExistingParent(
                    name + (bs.getValue(LanternBlock.HANGING) ? "_hanging" : ""),
                    "minecraft:" + templatePath)
                .texture("lantern", texPath).renderType("cutout");
            return ConfiguredModel.builder().modelFile(model).build();
        });
    }
}
