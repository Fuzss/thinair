package fuzs.thinair.data;

import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModItemTagsProvider extends TagsProvider<Item> {

    public ModItemTagsProvider(GatherDataEvent evt, String modId) {
        super(evt.getGenerator(), Registry.ITEM, modId, evt.getExistingFileHelper());
    }

    @Override
    protected void addTags() {
        this.tag(ModRegistry.CURIOS_HEAD_TAG).add(ModRegistry.RESPIRATOR_ITEM.get());
        this.tag(ModRegistry.TRINKETS_HEAD_FACE_TAG).add(ModRegistry.RESPIRATOR_ITEM.get());
    }
}
