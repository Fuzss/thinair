package fuzs.thinair.world.level.block;

import fuzs.thinair.ThinAir;
import fuzs.thinair.api.v1.AirQualityHelper;
import fuzs.thinair.api.v1.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.Nullable;

public class SafetyLanternBlock extends LanternBlock {
    public static final EnumProperty<AirQualityLevel> AIR_QUALITY = EnumProperty.create("air_quality", AirQualityLevel.class);
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final String TAG_AIR_QUALITY_LEVEL = ThinAir.id("air_quality_level").toLanguageKey();

    public SafetyLanternBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(AIR_QUALITY, AirQualityLevel.GREEN).setValue(LOCKED, false));
    }

    private static BlockState setAirQuality(Level level, BlockPos pos, BlockState blockState) {
        return blockState.setValue(AIR_QUALITY, AirQualityHelper.INSTANCE.getAirQualityAtLocation(level, Vec3.atCenterOf(pos)));
    }

    public static ItemStack getDisplayItemStack(AirQualityLevel airQualityLevel) {
        ItemStack itemStack = new ItemStack(ModRegistry.SAFETY_LANTERN_BLOCK.value());
        itemStack.getOrCreateTag().putInt(TAG_AIR_QUALITY_LEVEL, airQualityLevel.ordinal());
        return itemStack;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AIR_QUALITY, LOCKED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());

        BlockState blockState = this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        blockState = setAirQuality(context.getLevel(), context.getClickedPos(), blockState);

        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockState out = blockState.setValue(HANGING, direction == Direction.UP);
                if (out.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return out;
                }
            }
        }

        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity placer, ItemStack itemStack) {
        level.scheduleTick(blockPos, this, 20, TickPriority.NORMAL);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        ItemStack itemUsed = player.getItemInHand(interactionHand);

        AirQualityLevel lockedAirQuality = null;
        AirQualityLevel presentLockedAirQuality = null;
        boolean strippedDye = false;
        if (blockState.getValue(LOCKED)) {
            presentLockedAirQuality = blockState.getValue(AIR_QUALITY);
        }

        if (itemUsed.is(Items.GREEN_DYE) && presentLockedAirQuality != AirQualityLevel.GREEN) {
            lockedAirQuality = AirQualityLevel.GREEN;
        } else if (itemUsed.is(Items.BLUE_DYE) && presentLockedAirQuality != AirQualityLevel.BLUE) {
            lockedAirQuality = AirQualityLevel.BLUE;
        } else if (itemUsed.is(Items.YELLOW_DYE) && presentLockedAirQuality != AirQualityLevel.YELLOW) {
            lockedAirQuality = AirQualityLevel.YELLOW;
        } else if (itemUsed.is(Items.RED_DYE) && presentLockedAirQuality != AirQualityLevel.RED) {
            lockedAirQuality = AirQualityLevel.RED;
        } else if (itemUsed.getItem() instanceof AxeItem && blockState.getValue(LOCKED)) {
            strippedDye = true;
        }

        boolean didAnything = false;
        BlockState newBs = blockState;
        if (lockedAirQuality != null) {
            newBs = newBs.setValue(AIR_QUALITY, lockedAirQuality).setValue(LOCKED, true);
            level.levelEvent(player, 3003, pos, 0);
            if (!player.getAbilities().instabuild) {
                itemUsed.shrink(1);
            }

            didAnything = true;
        } else if (strippedDye) {
            level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3005, pos, 0);
            itemUsed.hurtAndBreak(1, player, (player1) -> player1.broadcastBreakEvent(interactionHand));
            player.swing(interactionHand);
            newBs = newBs.setValue(LOCKED, false);
            newBs = setAirQuality(level, pos, newBs);

            didAnything = true;
        }

        level.setBlockAndUpdate(pos, newBs);

        if (didAnything) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        serverLevel.scheduleTick(blockPos, this, 20, TickPriority.NORMAL);
        if (!blockState.getValue(LOCKED)) {
            serverLevel.setBlockAndUpdate(blockPos, setAirQuality(serverLevel, blockPos, blockState));
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return blockState.getValue(AIR_QUALITY).getOutputSignal();
    }
}
