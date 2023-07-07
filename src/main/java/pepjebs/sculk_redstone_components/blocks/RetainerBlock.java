package pepjebs.sculk_redstone_components.blocks;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pepjebs.sculk_redstone_components.SculkRedstoneComponentsMod;

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
    protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
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

    @Override
    protected int getPower(World world, BlockPos pos, BlockState state) {
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
            comparatorBlockEntity.setOutputSignal(0);
            return 0;
        } else if (comparatorBlockEntity.getOutputSignal() > 0) {
            return comparatorBlockEntity.getOutputSignal();
        } else {
            int newPower = Math.max(inputSidePower, 0);
            comparatorBlockEntity.setOutputSignal(newPower);
            return newPower;
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }
}
