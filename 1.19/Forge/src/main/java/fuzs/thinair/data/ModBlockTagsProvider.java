package fuzs.thinair.data;

import fuzs.thinair.ThinAir;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, ThinAir.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        // presumably, this method is "add tag"
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModRegistry.SAFETY_LANTERN_BLOCK.get());
        this.tag(BlockTags.WALL_POST_OVERRIDE).add(ModRegistry.SIGNAL_TORCH_BLOCK.get());
    }
}
