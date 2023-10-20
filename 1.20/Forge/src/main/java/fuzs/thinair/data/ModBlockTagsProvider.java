package fuzs.thinair.data;

import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import fuzs.thinair.api.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModBlockTagsProvider extends AbstractTagProvider.Blocks {

    public ModBlockTagsProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModRegistry.SAFETY_LANTERN_BLOCK.get());
        this.tag(BlockTags.WALL_POST_OVERRIDE).add(ModRegistry.SIGNAL_TORCH_BLOCK.get());
        this.tag(AirQualityLevel.BLUE.getAirProvidersTag()).add(Blocks.SOUL_CAMPFIRE, Blocks.SOUL_FIRE, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.SOUL_LANTERN);
        this.tag(AirQualityLevel.RED.getAirProvidersTag()).add(Blocks.LAVA);
        this.tag(AirQualityLevel.GREEN.getAirProvidersTag()).add(Blocks.END_PORTAL, Blocks.NETHER_PORTAL, Blocks.END_GATEWAY);
        this.tag(AirQualityLevel.YELLOW.getAirProvidersTag());
    }
}
