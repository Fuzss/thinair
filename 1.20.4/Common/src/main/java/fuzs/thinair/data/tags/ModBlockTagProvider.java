package fuzs.thinair.data.tags;

import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.thinair.api.v1.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

public class ModBlockTagProvider extends AbstractTagProvider.Blocks {


    public ModBlockTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModRegistry.SAFETY_LANTERN_BLOCK.value());
        this.tag(BlockTags.WALL_POST_OVERRIDE).add(ModRegistry.SIGNAL_TORCH_BLOCK.value());
        this.tag(AirQualityLevel.BLUE.getAirProvidersTag())
                .add(Blocks.SOUL_CAMPFIRE,
                        Blocks.SOUL_FIRE,
                        Blocks.SOUL_TORCH,
                        Blocks.SOUL_WALL_TORCH,
                        Blocks.SOUL_LANTERN
                );
        this.tag(AirQualityLevel.RED.getAirProvidersTag()).add(Blocks.LAVA, Blocks.FIRE);
        this.tag(AirQualityLevel.GREEN.getAirProvidersTag())
                .add(Blocks.END_PORTAL, Blocks.NETHER_PORTAL, Blocks.END_GATEWAY);
        this.tag(AirQualityLevel.YELLOW.getAirProvidersTag());
    }
}
