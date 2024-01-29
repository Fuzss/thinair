package fuzs.thinair.world.item;

import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoulfireBottleItem extends Item {

    public SoulfireBottleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        player.setAirSupply(player.getMaxAirSupply());
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        ItemStack itemInHandCopy = itemInHand.copy();
        if (!player.getAbilities().instabuild) {
            itemInHand.shrink(1);
        }
        level.playSound(player, player, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0f, 1.3f);
        level.playSound(player, player, SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 2.0f, 1.1f);

        Vec3 look = player.getLookAngle().scale(0.5);
        for (int i = 0; i < 10; i++) {
            level.addParticle(ParticleTypes.SOUL,
                player.getX() + (Math.random() - 0.5) * 0.5 + look.x,
                player.getEyeY() + (Math.random() - 0.5) * 0.5,
                player.getZ() + (Math.random() - 0.5) * 0.5 + look.z,
                (Math.random() - 0.5) * 0.1, Math.random() * 0.1, (Math.random() - 0.5) * 0.1);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (player instanceof ServerPlayer) {
            ModRegistry.USED_SOULFIRE_TRIGGER.value().trigger((ServerPlayer) player, itemInHandCopy);
        }

        return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide);
    }
}
