package fuzs.thinair.data.tags;

import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;

public class ModEntityTypeTagProvider extends AbstractTagProvider.EntityTypes {

    public ModEntityTypeTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.AIR_QUALITY_SENSITIVE_ENTITY_TYPE_TAG).add(EntityType.PLAYER, EntityType.VILLAGER, EntityType.WANDERING_TRADER, EntityType.PILLAGER, EntityType.EVOKER, EntityType.VINDICATOR, EntityType.ILLUSIONER);
    }
}
