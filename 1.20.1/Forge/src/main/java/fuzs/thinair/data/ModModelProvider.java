package fuzs.thinair.data;

import fuzs.puzzleslib.api.data.v1.AbstractModelProvider;
import fuzs.thinair.ThinAir;
import fuzs.thinair.api.v1.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.world.level.block.SafetyLanternBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModModelProvider extends AbstractModelProvider {

    public ModModelProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void registerStatesAndModels() {

        ResourceLocation torchTex = this.modLoc("block/signal_torch");
        this.simpleBlock(ModRegistry.SIGNAL_TORCH_BLOCK.get(), this.models().torch("signal_torch", torchTex));
        this.horizontalBlock(ModRegistry.WALL_SIGNAL_TORCH_BLOCK.get(), this.models().torchWall("wall_signal_torch", torchTex), 90);


        this.getVariantBuilder(ModRegistry.SAFETY_LANTERN_BLOCK.get()).forAllStates(blockState -> {
            String templatePath = blockState.getValue(LanternBlock.HANGING) ? "template_hanging_lantern" : "template_lantern";
            AirQualityLevel quality = blockState.getValue(SafetyLanternBlock.AIR_QUALITY);
            String name = "lantern_" + quality.getSerializedName();
            ResourceLocation texPath = this.modLoc("block/" + name);
            BlockModelBuilder model = this.models().withExistingParent(
                    name + (blockState.getValue(LanternBlock.HANGING) ? "_hanging" : ""),
                    "minecraft:" + templatePath)
                .texture("lantern", texPath);
            return ConfiguredModel.builder().modelFile(model).build();
        });

        this.basicItem(ModRegistry.RESPIRATOR_ITEM.get());
        this.basicItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.get());
        this.airBladder(ModRegistry.AIR_BLADDER_ITEM.get());
        this.airBladder(ModRegistry.REINFORCED_AIR_BLADDER_ITEM.get());

        for (AirQualityLevel airQualityLevel : AirQualityLevel.values()) {
            String name = "lantern_" + airQualityLevel.getSerializedName();
            this.basicItem(this.modLoc(name));
            ResourceLocation texPath = this.modLoc("item/" + name);
            this.itemModels().getBuilder(this.itemName(ModRegistry.SAFETY_LANTERN_ITEM.get())).override().predicate(ThinAir.id("air_quality_level"), airQualityLevel.getItemModelProperty()).model(new ModelFile.UncheckedModelFile(texPath)).end();
        }
    }

    private void airBladder(Item item) {
        for (int i = 0; i <= 2; i++) {
            String itemName = this.itemName(item);
            String name = itemName + "_" + i;
            this.basicItem(this.modLoc(name));
            float property = i / 3.0F;
            this.itemModels().getBuilder(itemName)
                    .override()
                    .predicate(new ResourceLocation("damage"), property)
                    .model(new ModelFile.UncheckedModelFile(this.modLoc("item/" + name)))
                    .end();
        }
    }
}
