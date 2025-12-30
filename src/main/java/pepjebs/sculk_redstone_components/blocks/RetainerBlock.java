package pepjebs.sculk_redstone_components.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RetainerBlock extends AbstractRedstoneGateBlock implements BlockEntityProvider {

    public RetainerBlock(Settings settings) {
        super(settings);
        this.setDefaultState((((this.stateManager.getDefaultState())
                .with(FACING, Direction.NORTH)).with(POWERED, false)));
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    @Override
    protected int getOutputLevel(BlockView view, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = view.getBlockEntity(pos);
        if (!(blockEntity instanceof ComparatorBlockEntity comparatorBlockEntity)) {
            return 0;
        }
        return comparatorBlockEntity.getOutputSignal();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ComparatorBlockEntity(pos, state);
    }

    @Nullable
    public MapCodec<ShriekerBlock> getCodec() {
        return null;
    }

    private void update(World world, BlockPos pos, BlockState state) {
        int computedSignal = this.calculateOutputSignal(world, pos, state);
        boolean statePowered = state.get(POWERED);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ComparatorBlockEntity comparatorBlockEntity) {
            comparatorBlockEntity.setOutputSignal(computedSignal);
        }
        if (statePowered && computedSignal == 0) {
            world.setBlockState(pos, state.with(POWERED, false), 2);
        } else if (!statePowered && computedSignal > 0) {
            world.setBlockState(pos, state.with(POWERED, true), 2);
        }
        this.updateTarget(world, pos, state);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.update(world, pos ,state);
    }

    private int calculateOutputSignal(World world, BlockPos pos, BlockState state) {
        int inputSidePower = super.getPower(world, pos, state);
        Direction direction = state.get(FACING);
        Direction dirRotated = direction.rotateClockwise(Direction.Axis.Y);
        var rotatedPos = pos.offset(dirRotated);
        int dirRotatedPower = world.getEmittedRedstonePower(rotatedPos, dirRotated);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ComparatorBlockEntity comparatorBlockEntity)) {
            return 0;
        }
        if (dirRotatedPower > 0) {
            return 0;
        } else if (comparatorBlockEntity.getOutputSignal() > 0) {
            return comparatorBlockEntity.getOutputSignal();
        } else {
            return Math.max(inputSidePower, 0);
        }
    }

    /**
     * Returns the input redstone power level provided to this gate block
     */
    @Override
    protected int getPower(World world, BlockPos pos, BlockState state) {
        int i = super.getPower(world, pos, state);
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.hasComparatorOutput()) {
            i = blockState.getComparatorOutput(world, blockPos, direction);
        } else if (i < 15 && blockState.isSolidBlock(world, blockPos)) {
            blockPos = blockPos.offset(direction);
            blockState = world.getBlockState(blockPos);
            ItemFrameEntity itemFrameEntity = this.getAttachedItemFrame(world, direction, blockPos);
            int j = Math.max(itemFrameEntity == null ? -2147483648 : itemFrameEntity.getComparatorPower(), blockState.hasComparatorOutput() ? blockState.getComparatorOutput(world, blockPos, direction) : -2147483648);
            if (j != -2147483648) {
                i = j;
            }
        }

        return i;
    }

    /**
     * Adds FACING, which keeps track of which direction the gate is accepting input from
     * & POWERED which is used by the blockstate to display powered-ness
     */
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Nullable
    private ItemFrameEntity getAttachedItemFrame(World world, Direction facing, BlockPos pos) {
        List<ItemFrameEntity> list = world.getEntitiesByClass(ItemFrameEntity.class, new Box(pos.getX(), pos.getY(), pos.getZ(), (pos.getX() + 1), (pos.getY() + 1), (pos.getZ() + 1)), (itemFrame) -> itemFrame != null && itemFrame.getHorizontalFacing() == facing);
        return list.size() == 1 ? list.get(0) : null;
    }
}
