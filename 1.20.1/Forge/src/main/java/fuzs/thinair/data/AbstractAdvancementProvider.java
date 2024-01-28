package fuzs.thinair.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AbstractAdvancementProvider implements ForgeAdvancementProvider.AdvancementGenerator, DataProvider {
    private final DataProvider provider;
    protected final String modId;

    public AbstractAdvancementProvider(GatherDataEvent evt, String modId) {
        this.provider = new ForgeAdvancementProvider(evt.getGenerator().getPackOutput(), evt.getLookupProvider(), evt.getExistingFileHelper(), List.of(this));
        this.modId = modId;
    }

    protected DisplayInfo simple(ItemStack itemStack, String name, FrameType frameType) {
        return this.simpleWithBackground(itemStack, name, frameType, null);
    }

    protected DisplayInfo simpleWithBackground(ItemStack itemStack, String name, FrameType frameType, ResourceLocation background) {
        name = "advancement." + this.modId + ":" + name;
        return new DisplayInfo(itemStack, Component.translatable(name), Component.translatable(name + ".desc"), background, frameType, true, true, false);
    }

    protected String prefix(String name) {
        return new ResourceLocation(this.modId, name).toString();
    }

    @Override
    public abstract void generate(HolderLookup.Provider registries, Consumer<Advancement> exporter, ExistingFileHelper fileHelper);

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return this.provider.run(cachedOutput);
    }

    @Override
    public String getName() {
        // multiple data providers cannot have the same name, so handle it like this
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(this.getClass().getSimpleName()), ' ');
    }
}
