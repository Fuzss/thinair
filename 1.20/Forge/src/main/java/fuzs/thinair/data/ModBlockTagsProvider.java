package fuzs.thinair.data;

import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModBlockTagsProvider extends AbstractTagProvider.Blocks {

    public ModBlockTagsProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModRegistry.SAFETY_LANTERN_BLOCK.get());
        this.tag(BlockTags.WALL_POST_OVERRIDE).add(ModRegistry.SIGNAL_TORCH_BLOCK.get());
    }
}
