package fuzs.thinair.world.item;

import fuzs.thinair.advancements.ModAdvancementTriggers;
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

public class SoulfireBottleItem extends Item {

    public SoulfireBottleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        player.setAirSupply(player.getMaxAirSupply());
        var stack = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        world.playSound(player, player, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0f, 1.3f);
        world.playSound(player, player, SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 2.0f, 1.1f);

        var look = player.getLookAngle().scale(0.5);
        for (int i = 0; i < 10; i++) {
            world.addParticle(ParticleTypes.SOUL,
                player.getX() + (Math.random() - 0.5) * 0.5 + look.x,
                player.getEyeY() + (Math.random() - 0.5) * 0.5,
                player.getZ() + (Math.random() - 0.5) * 0.5 + look.z,
                (Math.random() - 0.5) * 0.1, Math.random() * 0.1, (Math.random() - 0.5) * 0.1);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        // augh
        if (player instanceof ServerPlayer splayer) {
            ModAdvancementTriggers.USE_SOULFIRE_BOTTLE.trigger(splayer);
        }

        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
    }
}
