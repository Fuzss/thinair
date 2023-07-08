package fuzs.thinair.world.level.block;

import fuzs.thinair.helper.AirHelper;
import fuzs.thinair.helper.AirQualityLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class SafetyLanternBlock extends LanternBlock {
    public static final EnumProperty<AirQualityLevel> AIR_QUALITY =
        EnumProperty.create("air_quality", AirQualityLevel.class);
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;

    public SafetyLanternBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState()
            .setValue(AIR_QUALITY, AirQualityLevel.GREEN)
            .setValue(LOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> definition) {
        super.createBlockStateDefinition(definition);
        definition.add(AIR_QUALITY, LOCKED);
    }

    private static BlockState setAirQuality(Level world, BlockPos pos, BlockState template) {
        return template.setValue(AIR_QUALITY, AirHelper.getO2LevelFromLocation(Vec3.atCenterOf(pos), world).getFirst());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var fluidstate = ctx.getLevel().getFluidState(ctx.getClickedPos());

        var bs = this.defaultBlockState()
            .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        bs = setAirQuality(ctx.getLevel(), ctx.getClickedPos(), bs);

        for (Direction direction : ctx.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                var out = bs.setValue(HANGING, direction == Direction.UP);
                if (out.canSurvive(ctx.getLevel(), ctx.getClickedPos())) {
                    return out;
                }
            }
        }

        return null;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer,
        ItemStack pStack) {
        pLevel.scheduleTick(pPos, this, 20, TickPriority.NORMAL);
    }

    @Override
    public InteractionResult use(BlockState bs, Level world, BlockPos pos, Player player, InteractionHand hand,
        BlockHitResult pHit) {
        var itemUsed = player.getItemInHand(hand);

        AirQualityLevel lockedAirQuality = null;
        AirQualityLevel presentLockedAirQuality = null;
        var strippedDye = false;
        if (bs.getValue(LOCKED)) {
            presentLockedAirQuality = bs.getValue(AIR_QUALITY);
        }

        if (itemUsed.is(Items.GREEN_DYE) && presentLockedAirQuality != AirQualityLevel.GREEN) {
            lockedAirQuality = AirQualityLevel.GREEN;
        } else if (itemUsed.is(Items.BLUE_DYE) && presentLockedAirQuality != AirQualityLevel.BLUE) {
            lockedAirQuality = AirQualityLevel.BLUE;
        } else if (itemUsed.is(Items.YELLOW_DYE) && presentLockedAirQuality != AirQualityLevel.YELLOW) {
            lockedAirQuality = AirQualityLevel.YELLOW;
        } else if (itemUsed.is(Items.RED_DYE) && presentLockedAirQuality != AirQualityLevel.RED) {
            lockedAirQuality = AirQualityLevel.RED;
        } else if (itemUsed.getItem() instanceof AxeItem && bs.getValue(LOCKED)) {
            strippedDye = true;
        }

        var didAnything = false;
        var newBs = bs;
        if (lockedAirQuality != null) {
            newBs = newBs.setValue(AIR_QUALITY, lockedAirQuality).setValue(LOCKED, true);
            world.levelEvent(player, 3003, pos, 0);
            if (!player.getAbilities().instabuild) {
                itemUsed.shrink(1);
            }

            didAnything = true;
        } else if (strippedDye) {
            world.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.levelEvent(player, 3005, pos, 0);
            itemUsed.hurtAndBreak(1, player, (player1) -> player1.broadcastBreakEvent(hand));
            player.swing(hand);
            newBs = newBs.setValue(LOCKED, false);
            newBs = setAirQuality(world, pos, newBs);

            didAnything = true;
        }

        world.setBlockAndUpdate(pos, newBs);

        if (didAnything) {
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        pLevel.scheduleTick(pPos, this, 20, TickPriority.NORMAL);
        if (!pState.getValue(LOCKED)) {
            pLevel.setBlockAndUpdate(pPos, setAirQuality(pLevel, pPos, pState));
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        return pState.getValue(AIR_QUALITY).getOutputSignal();
    }
}
