package fuzs.thinair.world.item;

import fuzs.thinair.capability.AirProtectionCapability;
import fuzs.thinair.helper.AirHelper;
import fuzs.thinair.helper.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class AirBladderItem extends Item {
    // We have to cache the air quality on the item in order to get the animations
    // because getUseAnimation doesn't provide the player
    private static final String TAG_OXYGEN_LEVEL = "oxygenLevel";

    public AirBladderItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        Optional<AirProtectionCapability> maybeCap = ModRegistry.AIR_PROTECTION_CAPABILITY.maybeGet(pPlayer);
        maybeCap.ifPresent(cap -> cap.setProtected(true));
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        AirQualityLevel o2Level = AirHelper.getO2LevelFromLocation(livingEntity.getEyePosition(), livingEntity.level()).getFirst();
        stack.getOrCreateTag().putString(TAG_OXYGEN_LEVEL, o2Level.toString());

        ServerPlayer maybeSplayer = null;
        if (livingEntity instanceof ServerPlayer splayer) {
            maybeSplayer = splayer;
        }
        if (o2Level == AirQualityLevel.GREEN) {
            // refill
            if (stack.getDamageValue() > 0) {
                stack.hurt(-4, livingEntity.getRandom(), maybeSplayer);
            }
        } else if (stack.getDamageValue() < stack.getMaxDamage()) {
            // damage and replenish air
            for (int i = 0; i < 4; i++) {
                boolean failed = stack.hurt(1, livingEntity.getRandom(), maybeSplayer);
                if (failed) {
                    break;
                }
                if (livingEntity.getAirSupply() < livingEntity.getMaxAirSupply()) {
                    livingEntity.setAirSupply(livingEntity.getAirSupply() + 1);
                } else {
                    break;
                }
            }

        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        stopUsing(pStack, pLivingEntity);
        return pStack;
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        stopUsing(pStack, pLivingEntity);
    }

    public static void stopUsing(ItemStack stack, LivingEntity user) {
        stack.removeTagKey(TAG_OXYGEN_LEVEL);
        Optional<AirProtectionCapability> maybeCap = ModRegistry.AIR_PROTECTION_CAPABILITY.maybeGet(user);
        maybeCap.ifPresent(cap -> cap.setProtected(false));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        UseAnim out = UseAnim.NONE;
        if (pStack.getDamageValue() < pStack.getMaxDamage() && pStack.hasTag()) {
            CompoundTag tag = pStack.getTag();
            if (tag.contains(TAG_OXYGEN_LEVEL, Tag.TAG_STRING)) {
                String o2LevelStr = tag.getString(TAG_OXYGEN_LEVEL);
                try {
                    AirQualityLevel o2Level = AirQualityLevel.valueOf(o2LevelStr);
                    out = (o2Level == AirQualityLevel.GREEN) ? UseAnim.BOW : UseAnim.DRINK;
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return out;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 9001;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}
