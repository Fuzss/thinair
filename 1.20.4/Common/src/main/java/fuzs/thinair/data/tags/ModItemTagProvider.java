package fuzs.thinair.data.tags;

import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.tags.TypedTagFactory;
import fuzs.thinair.api.v1.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.HolderLookup;

public class ModItemTagProvider extends AbstractTagProvider.Items {

    public ModItemTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(AirQualityLevel.YELLOW.getBreathingEquipment()).add(ModRegistry.RESPIRATOR_ITEM.value());
        this.tag(AirQualityLevel.RED.getBreathingEquipment());
        this.tag(ModRegistry.AIR_REFILLER_ITEM_TAG).add(ModRegistry.AIR_BLADDER_ITEM.value(), ModRegistry.REINFORCED_AIR_BLADDER_ITEM.value());
        this.tag(TypedTagFactory.ITEM.curios("head")).add(ModRegistry.RESPIRATOR_ITEM.value());
        this.tag(TypedTagFactory.ITEM.trinkets("head/face")).add(ModRegistry.RESPIRATOR_ITEM.value());
    }
}
