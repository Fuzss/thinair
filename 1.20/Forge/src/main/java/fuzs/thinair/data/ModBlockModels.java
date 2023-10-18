package fuzs.thinair.data;

import fuzs.puzzleslib.api.data.v1.AbstractModelProvider;
import fuzs.thinair.ThinAir;
import fuzs.thinair.api.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.world.level.block.SafetyLanternBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Locale;

public class ModBlockModels extends AbstractModelProvider {

    public ModBlockModels(GatherDataEvent evt, String modId) {
        super(evt, modId);
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
        this.basicItem(ModRegistry.RESPIRATOR_ITEM.get());
        this.basicItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.get());

        for (int i = 0; i <= 2; i++) {
            String name = "air_bladder_" + i;
            this.basicItem(this.modLoc(name));
            float prop = (float) i / 3f;
            this.itemModels().getBuilder(this.itemName(ModRegistry.AIR_BLADDER_ITEM.get()))
                    .override()
                    .predicate(new ResourceLocation("damage"), prop)
                    .model(new ModelFile.UncheckedModelFile(this.modLoc("item/" + name)))
                    .end();
        }

        AirQualityLevel[] aqs = AirQualityLevel.values();
        for (int i = 0; i < aqs.length; i++) {
            AirQualityLevel aq = aqs[3 - i];
            String name = "lantern_" + aq.getSerializedName();
            this.basicItem(this.modLoc(name));
            ResourceLocation texPath = this.modLoc("item/" + name);
            this.itemModels().getBuilder(this.itemName(ModRegistry.SAFETY_LANTERN_ITEM.get()))
                    .override()
                    .predicate(ThinAir.id("air_quality"), i)
                    .model(new ModelFile.UncheckedModelFile(texPath))
                    .end();

            this.itemModels().singleTexture("fake_always_" + aq.getSerializedName() + "_lantern", new ResourceLocation("item/generated"),
                    "layer0", texPath);
        }
        this.itemModels().singleTexture("fake_rainbow_lantern", new ResourceLocation("item/generated"),
                "layer0", this.modLoc("item/lantern_rainbow"));
    }
}
