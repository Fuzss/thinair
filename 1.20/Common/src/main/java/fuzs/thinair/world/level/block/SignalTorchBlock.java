package fuzs.thinair.world.level.block;

import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.thinair.advancements.ModAdvancementTriggers;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SignalTorchBlock extends TorchBlock {

    public SignalTorchBlock(Properties pProperties) {
        super(pProperties, ParticleTypes.FLAME);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource random) {
        double x = pPos.getX() + 0.5D;
        double y = pPos.getY() + 0.7D;
        double z = pPos.getZ() + 0.5D;
        double dx = (random.nextDouble() - 0.5) * 0.05;
        double dy = random.nextDouble() * 0.1;
        double dz = (random.nextDouble() - 0.5) * 0.05;
        pLevel.addParticle(ParticleTypes.FIREWORK, x, y, z, dx, dy, dz);
        pLevel.addParticle(this.flameParticle, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public static EventResultHolder<InteractionResult> onUseBlock(Player player, Level level, InteractionHand interactionHand, BlockHitResult hitResult) {
        if (!CommonConfig.enableSignalTorches.get() || interactionHand != InteractionHand.MAIN_HAND || player.isDiscrete()) {
            return EventResultHolder.pass();
        }
        BlockPos pos = hitResult.getBlockPos();
        BlockState bs = level.getBlockState(pos);

        Block nextBlock = null;
        float pitch = 1f;
        if (bs.is(Blocks.TORCH)) {
            nextBlock = ModRegistry.SIGNAL_TORCH_BLOCK.get();
        } else if (bs.is(Blocks.WALL_TORCH)) {
            nextBlock = ModRegistry.WALL_SIGNAL_TORCH_BLOCK.get();
        } else if (bs.is(ModRegistry.SIGNAL_TORCH_BLOCK.get())) {
            nextBlock = Blocks.TORCH;
            pitch = 0.8f;
        } else if (bs.is(ModRegistry.WALL_SIGNAL_TORCH_BLOCK.get())) {
            nextBlock = Blocks.WALL_TORCH;
            pitch = 0.8f;
        }
        if (nextBlock != null) {
            if (player instanceof ServerPlayer splayer) {
                ModAdvancementTriggers.SIGNALIFICATE_TORCH.trigger(splayer, pos);
            }

            BlockState nextBs = nextBlock.defaultBlockState();
            if (bs.hasProperty(WallTorchBlock.FACING)) {
                nextBs = nextBs.setValue(WallTorchBlock.FACING, bs.getValue(WallTorchBlock.FACING));
            }
            level.setBlockAndUpdate(pos, nextBs);
            level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1f, pitch);
            player.swing(interactionHand);

            return EventResultHolder.interrupt(InteractionResult.sidedSuccess(level.isClientSide));
        }
        return EventResultHolder.pass();
    }
}
