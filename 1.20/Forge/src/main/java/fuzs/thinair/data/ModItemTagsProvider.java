package fuzs.thinair.data;

import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModItemTagsProvider extends AbstractTagProvider.Items {

    public ModItemTagsProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.BREATHING_UTILITY_ITEM_TAG).add(ModRegistry.RESPIRATOR_ITEM.get());
    }
}