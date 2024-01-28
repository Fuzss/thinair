package fuzs.thinair.data;

import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModEntityTypeTagsProvider extends AbstractTagProvider.EntityTypes {

    public ModEntityTypeTagsProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.AIR_QUALITY_SENSITIVE_ENTITY_TYPE_TAG).add(EntityType.PLAYER, EntityType.VILLAGER, EntityType.WANDERING_TRADER, EntityType.PILLAGER, EntityType.EVOKER, EntityType.VINDICATOR);
    }
}
