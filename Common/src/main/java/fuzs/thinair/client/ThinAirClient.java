package fuzs.thinair.client;

import fuzs.puzzleslib.client.core.ClientModConstructor;
import fuzs.thinair.ThinAir;
import fuzs.thinair.helper.AirHelper;
import fuzs.thinair.init.ModRegistry;

public class ThinAirClient implements ClientModConstructor {

    @Override
    public void onRegisterItemModelProperties(ItemModelPropertiesContext context) {
        context.registerItem(ModRegistry.SAFETY_LANTERN_BLOCK.get().asItem(), ThinAir.id("air_quality"), (stack, level, maybeEntity, seed) -> {
            var entity = maybeEntity != null ? maybeEntity : stack.getEntityRepresentation();
            if (entity != null) {
                return switch (AirHelper.getO2LevelFromLocation(entity.getEyePosition(), entity.getLevel()).getFirst()) {
                    case RED -> 0;
                    case YELLOW -> 1;
                    case BLUE -> 2;
                    case GREEN -> 3;
                };
            } else {
                return 3; // just do green?
            }
        });
    }
}
