package fuzs.thinair.handler;

import com.mojang.datafixers.util.Pair;
import fuzs.thinair.advancements.AirProtectionSource;
import fuzs.thinair.advancements.AirSource;
import fuzs.thinair.advancements.ModAdvancementTriggers;
import fuzs.thinair.helper.AirHelper;
import fuzs.thinair.helper.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public class TickAirChecker {

    public static void onLivingTick(LivingEntity entity) {

        Pair<AirQualityLevel, AirSource> o2Pair = AirHelper.getO2LevelFromLocation(entity.getEyePosition(), entity.level);
        int deltaO2 = 0;
        AirProtectionSource protection = switch (o2Pair.getFirst()) {
            case GREEN -> {
                deltaO2 = 4;
                yield AirProtectionSource.NONE;
            }
            case BLUE -> {
                deltaO2 = 0;
                yield AirProtectionSource.NONE;
            }
            case YELLOW -> {
                AirProtectionSource prot = AirHelper.getProtectionFromYellow(entity);
                if (prot == AirProtectionSource.NONE) {
                    if (entity.level.getGameTime() % 4 == 0) {
                        deltaO2 = -1;
                    }
                } else if (prot == AirProtectionSource.RESPIRATOR && entity.level.getGameTime() % (20 * 15) == 0) {
                    ServerPlayer maybeSplayer = null;
                    if (entity instanceof ServerPlayer splayer) {
                        maybeSplayer = splayer;
                    }
                    Iterable<ItemStack> armorItems = entity.getArmorSlots();
                    for (ItemStack item : armorItems) {
                        if (item.is(ModRegistry.RESPIRATOR_ITEM.get())) {
                            item.hurt(1, entity.getRandom(), maybeSplayer);
                            break;
                        }
                    }
                }
                yield prot;
            }
            case RED -> {
                AirProtectionSource prot = AirHelper.getProtectionFromRed(entity);
                if (prot == AirProtectionSource.NONE) {
                    deltaO2 = -1;
                }
                yield prot;
            }
        };

        if (entity instanceof ServerPlayer splayer) {
            ModAdvancementTriggers.BREATHE_AIR.trigger(splayer, o2Pair.getFirst(), o2Pair.getSecond(), protection);
        }

        if (deltaO2 != 0) {
            boolean skipAirLossDueToRespiration = false;
            if (deltaO2 < 0) {
                int respirationLevel = EnchantmentHelper.getRespiration(entity);
                if (respirationLevel != 0 && entity.getRandom().nextInt(respirationLevel + 1) > 0) {
                    skipAirLossDueToRespiration = true;
                }
            }
            if (!skipAirLossDueToRespiration) {
                int newO2 = Math.min(entity.getAirSupply() + deltaO2, entity.getMaxAirSupply());
                entity.setAirSupply(newO2);

                if (newO2 <= -20) {
                    entity.setAirSupply(newO2 + 20);
                    // copy code from pristine
                    Vec3 vel = entity.getDeltaMovement();

                    for (int i = 0; i < 8; i++) {
                        RandomSource random = entity.level.getRandom();
                        double dx = random.nextDouble() - random.nextDouble();
                        double dy = random.nextDouble() - random.nextDouble();
                        double dz = random.nextDouble() - random.nextDouble();
                        entity.level.addParticle(ParticleTypes.BUBBLE, entity.getX() + dx, entity.getY() + dy, entity.getZ() + dz, vel.x,
                            vel.y, vel.z);
                    }

                    entity.hurt(DamageSource.DROWN, 2.0F);
                }
            }
        }
    }
}
