package fuzs.thinair.data.client;

import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.thinair.ThinAir;
import fuzs.thinair.data.ModAdvancementProvider;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potions;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.addCreativeModeTab(ThinAir.MOD_ID, ThinAir.MOD_NAME);
        builder.add(ModRegistry.RESPIRATOR_ITEM.value(), "Respirator");
        builder.add(ModRegistry.AIR_BLADDER_ITEM.value(), "Air Bladder");
        builder.add(ModRegistry.REINFORCED_AIR_BLADDER_ITEM.value(), "Reinforced Air Bladder");
        builder.add(ModRegistry.SOULFIRE_BOTTLE_ITEM.value(), "Bottle of Soulfire");
        builder.add(ModRegistry.SAFETY_LANTERN_BLOCK.value(), "Safety Lantern");
        builder.add(MobEffects.WATER_BREATHING, "Free Breathing");
        builder.add(Potions.WATER_BREATHING, "Free Breathing");
        builder.addGenericDamageType(DamageTypes.DROWN, "%1$s asphyxiated");
        builder.addPlayerDamageType(DamageTypes.DROWN, "%1$s asphyxiated whilst trying to escape %2$s");
        builder.add(SoundEvents.PLAYER_HURT_DROWN, "Player asphyxiating");
        builder.add(ModAdvancementProvider.ROOT_ADVANCEMENT.title(), ThinAir.MOD_NAME);
        builder.add(ModAdvancementProvider.ROOT_ADVANCEMENT.description(),
                "The air is not always breathable deep in the world and in other dimensions"
        );
        builder.add(ModAdvancementProvider.AIR_BLADDER_ADVANCEMENT.title(), "In and Out");
        builder.add(ModAdvancementProvider.AIR_BLADDER_ADVANCEMENT.description(),
                "Use an Air Bladder to refill your air supply on the go"
        );
        builder.add(ModAdvancementProvider.BLUE_AIR_ADVANCEMENT.title(), "Deep Blues");
        builder.add(ModAdvancementProvider.BLUE_AIR_ADVANCEMENT.description(),
                "Breathe the life force given off by Soul Fire to maintain your air (but not increase it!)"
        );
        builder.add(ModAdvancementProvider.SOULFIRE_BOTTLE_ADVANCEMENT.title(), "In Case Of Emergency");
        builder.add(ModAdvancementProvider.SOULFIRE_BOTTLE_ADVANCEMENT.description(),
                "Restore your lungs with the souls trapped in a Bottle of Soulfire"
        );
        builder.add(ModAdvancementProvider.RESPIRATOR_ADVANCEMENT.title(), "I'd Take a Deep Breath...");
        builder.add(ModAdvancementProvider.RESPIRATOR_ADVANCEMENT.description(),
                "Protect yourself from choking air with something like a Respirator"
        );
        builder.add(ModAdvancementProvider.WATER_BREATHING_ADVANCEMENT.title(), "... And Hold It");
        builder.add(ModAdvancementProvider.WATER_BREATHING_ADVANCEMENT.description(),
                "Breathe freely where there is no air at all"
        );
        builder.add(ModAdvancementProvider.SAFETY_LANTERN_ADVANCEMENT.title(), "Our Number One Priority");
        builder.add(ModAdvancementProvider.SAFETY_LANTERN_ADVANCEMENT.description(),
                "Use a Safety Lantern to check the air quality around you"
        );
        builder.add(ModAdvancementProvider.DISCO_LANTERN_ADVANCEMENT.title(), "Red Light! Green Light!");
        builder.add(ModAdvancementProvider.DISCO_LANTERN_ADVANCEMENT.description(),
                "Use a piece of Dye to manually change the color of a Safety Lamp (you can scrape the color off with an axe)"
        );
        builder.add(ModAdvancementProvider.SIGNAL_TORCH_ADVANCEMENT.title(), "Ooh, ES-CAPE");
        builder.add(ModAdvancementProvider.SIGNAL_TORCH_ADVANCEMENT.description(),
                "Right-click a torch to make it spew particles, perhaps to signal the exit of a cave"
        );
    }
}
