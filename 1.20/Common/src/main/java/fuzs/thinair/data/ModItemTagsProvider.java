package fuzs.thinair.data;

import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.tags.TypedTagFactory;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.HolderLookup;

public class ModItemTagsProvider extends AbstractTagProvider.Items {

    public ModItemTagsProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.BREATHING_EQUIPMENT_ITEM_TAG).add(ModRegistry.RESPIRATOR_ITEM.get());
        this.tag(ModRegistry.AIR_REFILLER_ITEM_TAG).add(ModRegistry.AIR_BLADDER_ITEM.get(), ModRegistry.REINFORCED_AIR_BLADDER_ITEM.get());
        this.tag(TypedTagFactory.ITEM.curios("head")).add(ModRegistry.RESPIRATOR_ITEM.get());
        this.tag(TypedTagFactory.ITEM.trinkets("head/face")).add(ModRegistry.RESPIRATOR_ITEM.get());
    }
}
