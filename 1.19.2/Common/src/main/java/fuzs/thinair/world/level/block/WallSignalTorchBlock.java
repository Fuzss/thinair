package fuzs.thinair.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WallSignalTorchBlock extends WallTorchBlock {

    public WallSignalTorchBlock(Properties pProperties) {
        super(pProperties, ParticleTypes.FLAME);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource random) {
        var facing = pState.getValue(FACING);
        var opposite = facing.getOpposite();
        double x = pPos.getX() + 0.5D + 0.27D * opposite.getStepX();
        double y = pPos.getY() + 0.92D;
        double z = pPos.getZ() + 0.5D + 0.27D * opposite.getStepZ();
        double dx = (random.nextDouble() - 0.5) * 0.05 + facing.getStepX() * 0.01;
        double dy = random.nextDouble() * 0.1;
        double dz = (random.nextDouble() - 0.5) * 0.05 + facing.getStepZ() * 0.01;
        pLevel.addParticle(ParticleTypes.FIREWORK, x, y, z, dx, dy, dz);
        pLevel.addParticle(this.flameParticle, x, y, z, 0.0D, 0.0D, 0.0D);

    }
}
