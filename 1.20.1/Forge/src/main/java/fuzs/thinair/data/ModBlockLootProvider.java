package fuzs.thinair.data;

import fuzs.puzzleslib.api.data.v1.AbstractLootProvider;
import fuzs.thinair.init.ModRegistry;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModBlockLootProvider extends AbstractLootProvider.Blocks {

    public ModBlockLootProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    public void generate() {
        this.dropSelf(ModRegistry.SAFETY_LANTERN_BLOCK.get());
        this.dropSelf(ModRegistry.SIGNAL_TORCH_BLOCK.get());
    }
}
