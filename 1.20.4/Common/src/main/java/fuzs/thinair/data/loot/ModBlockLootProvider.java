package fuzs.thinair.data.loot;

import fuzs.puzzleslib.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.thinair.init.ModRegistry;

public class ModBlockLootProvider extends AbstractLootProvider.Blocks {

    public ModBlockLootProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addLootTables() {
        this.dropSelf(ModRegistry.SAFETY_LANTERN_BLOCK.value());
        this.dropSelf(ModRegistry.SIGNAL_TORCH_BLOCK.value());
    }
}
