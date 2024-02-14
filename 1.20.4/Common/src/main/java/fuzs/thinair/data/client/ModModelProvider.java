package fuzs.thinair.data.client;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.client.data.v2.ItemModelProperties;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.thinair.api.v1.AirQualityLevel;
import fuzs.thinair.client.ThinAirClient;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.world.level.block.SafetyLanternBlock;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LanternBlock;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModModelProvider extends AbstractModelProvider {

    public ModModelProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addBlockModels(BlockModelGenerators builder) {
        builder.createNormalTorch(ModRegistry.SIGNAL_TORCH_BLOCK.value(), ModRegistry.WALL_SIGNAL_TORCH_BLOCK.value());
        createSafetyLanternBlock(builder);
    }

    private static void createSafetyLanternBlock(BlockModelGenerators builder) {
        builder.skipAutoItemBlock(ModRegistry.SAFETY_LANTERN_BLOCK.value());
        builder.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModRegistry.SAFETY_LANTERN_BLOCK.value())
                .with(PropertyDispatch.properties(SafetyLanternBlock.AIR_QUALITY, LanternBlock.HANGING)
                        .select(AirQualityLevel.GREEN,
                                false,
                                createSafetyLanternVariant(builder, ModelTemplates.LANTERN, AirQualityLevel.GREEN)
                        )
                        .select(AirQualityLevel.BLUE,
                                false,
                                createSafetyLanternVariant(builder, ModelTemplates.LANTERN, AirQualityLevel.BLUE)
                        )
                        .select(AirQualityLevel.YELLOW,
                                false,
                                createSafetyLanternVariant(builder, ModelTemplates.LANTERN, AirQualityLevel.YELLOW)
                        )
                        .select(AirQualityLevel.RED,
                                false,
                                createSafetyLanternVariant(builder, ModelTemplates.LANTERN, AirQualityLevel.RED)
                        )
                        .select(AirQualityLevel.GREEN,
                                true,
                                createSafetyLanternVariant(builder,
                                        ModelTemplates.HANGING_LANTERN,
                                        AirQualityLevel.GREEN
                                )
                        )
                        .select(AirQualityLevel.BLUE,
                                true,
                                createSafetyLanternVariant(builder,
                                        ModelTemplates.HANGING_LANTERN,
                                        AirQualityLevel.BLUE
                                )
                        )
                        .select(AirQualityLevel.YELLOW,
                                true,
                                createSafetyLanternVariant(builder,
                                        ModelTemplates.HANGING_LANTERN,
                                        AirQualityLevel.YELLOW
                                )
                        )
                        .select(AirQualityLevel.RED,
                                true,
                                createSafetyLanternVariant(builder, ModelTemplates.HANGING_LANTERN, AirQualityLevel.RED)
                        )));
    }

    private static Variant createSafetyLanternVariant(BlockModelGenerators builder, ModelTemplate modelTemplate, AirQualityLevel airQualityLevel) {
        TexturedModel.Provider provider = TexturedModel.createDefault(block -> new TextureMapping().put(TextureSlot.LANTERN,
                TextureMapping.getBlockTexture(block, "_" + airQualityLevel.getSerializedName())
        ), modelTemplate);
        return Variant.variant()
                .with(VariantProperties.MODEL,
                        provider.createWithSuffix(ModRegistry.SAFETY_LANTERN_BLOCK.value(),
                                "_" + airQualityLevel.getSerializedName(),
                                builder.modelOutput
                        )
                );
    }

    @Override
    public void addItemModels(ItemModelGenerators builder) {
        builder.generateFlatItem(ModRegistry.RESPIRATOR_ITEM.value(), ModelTemplates.FLAT_ITEM);
        builder.generateFlatItem(ModRegistry.SOULFIRE_BOTTLE_ITEM.value(), ModelTemplates.FLAT_ITEM);
        createSafetyLanternItem(builder, ModRegistry.SAFETY_LANTERN_ITEM.value());
        createAirBladderItem(builder, ModRegistry.AIR_BLADDER_ITEM.value());
        createAirBladderItem(builder, ModRegistry.REINFORCED_AIR_BLADDER_ITEM.value());
    }

    private static void createSafetyLanternItem(ItemModelGenerators builder, Item item) {
        // make sure this already runs so per-quality models are added to builder#output here, otherwise raises ConcurrentModificationException
        Map<AirQualityLevel, ResourceLocation> airQualityLocations = Stream.of(AirQualityLevel.values())
                .collect(Collectors.toMap(Function.identity(), airQualityLevel -> {
                    ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(item,
                            "_" + airQualityLevel.getSerializedName()
                    );
                    return ModelTemplates.FLAT_ITEM.create(modelLocation,
                            TextureMapping.layer0(modelLocation),
                            builder.output
                    );
                }, (o1, o2) -> o2, Maps::newLinkedHashMap));

        ItemModelProperties[] itemModelProperties = airQualityLocations.entrySet()
                .stream()
                .map(entry -> {
                    return ItemModelProperties.singleOverride(entry.getValue(),
                            ThinAirClient.AIR_QUALITY_LEVEL_MODEL_PROPRTY,
                            entry.getKey().getItemModelProperty()
                    );
                })
                .toArray(ItemModelProperties[]::new);

        ModelTemplate modelTemplate = new ModelTemplate(Optional.empty(), Optional.empty());
        modelTemplate.create(ModelLocationUtils.getModelLocation(item),
                new TextureMapping(),
                builder.output,
                ItemModelProperties.overridesFactory(modelTemplate, itemModelProperties)
        );
    }

    private static void createAirBladderItem(ItemModelGenerators builder, Item item) {
        String[] suffixes = {"_full", "_used", "_almost_empty"};
        ItemModelProperties[] itemModelProperties = new ItemModelProperties[suffixes.length];
        for (int i = 0; i < itemModelProperties.length; i++) {
            ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(item, suffixes[i]);
            ResourceLocation resourceLocation = ModelTemplates.FLAT_ITEM.create(modelLocation,
                    TextureMapping.layer0(modelLocation),
                    builder.output
            );
            float propertyValue = i / (float) itemModelProperties.length;
            itemModelProperties[i] = ItemModelProperties.singleOverride(resourceLocation, new ResourceLocation("damage"), propertyValue);
        }
        ModelTemplate modelTemplate = new ModelTemplate(Optional.empty(), Optional.empty());
        modelTemplate.create(ModelLocationUtils.getModelLocation(item),
                new TextureMapping(),
                builder.output,
                ItemModelProperties.overridesFactory(modelTemplate, itemModelProperties)
        );
    }
}
