package fuzs.thinair.data;

import fuzs.thinair.ThinAir;
import fuzs.thinair.helper.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItemModels extends ItemModelProvider {

    public ModItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ThinAir.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(ModRegistry.RESPIRATOR_ITEM.get());
        this.basicItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.get());

        for (int i = 0; i <= 2; i++) {
            String name = "air_bladder_" + i;
            this.basicItem(this.modLoc(name));
            float prop = (float) i / 3f;
            this.getBuilder(this.name(ModRegistry.AIR_BLADDER_ITEM.get()))
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
            this.getBuilder(this.name(ModRegistry.SAFETY_LANTERN_ITEM.get()))
                .override()
                .predicate(ThinAir.id("air_quality"), i)
                .model(new ModelFile.UncheckedModelFile(texPath))
                .end();

            this.singleTexture("fake_always_" + aq.getSerializedName() + "_lantern", new ResourceLocation("item/generated"),
                "layer0", texPath);
        }
        this.singleTexture("fake_rainbow_lantern", new ResourceLocation("item/generated"),
            "layer0", this.modLoc("item/lantern_rainbow"));
    }

    public String name(Item item) {
        return ForgeRegistries.ITEMS.getKey(item).getPath();
    }
}
