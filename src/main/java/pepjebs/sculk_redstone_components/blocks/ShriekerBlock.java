package pepjebs.sculk_redstone_components.blocks;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import pepjebs.sculk_redstone_components.SculkRedstoneComponentsMod;

public class ShriekerBlock extends AbstractRedstoneGateBlock implements BlockEntityProvider {

    public ShriekerBlock(Settings settings) {
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
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ComparatorBlockEntity comparatorBlockEntity)) {
            return 0;
        }
        Direction direction = state.get(FACING);
        Direction dirRotated = direction.rotateClockwise(Direction.Axis.Y);
        var rotatedPos = pos.offset(dirRotated);
        int dirRotatedPower = world.getEmittedRedstonePower(rotatedPos, dirRotated);
        if (comparatorBlockEntity.getOutputSignal() == 0 && inputSidePower > 0) {
            if (dirRotatedPower > 0) {
                world.syncWorldEvent(3007, pos, 0);
                world.emitGameEvent(GameEvent.SHRIEK, pos, GameEvent.Emitter.of(state));
            } else {
                world.emitGameEvent(
                        Registries.GAME_EVENT.get(new Identifier("minecraft", "resonate_"+inputSidePower)),
                        pos,
                        GameEvent.Emitter.of(state)
                );
            }
        }
        comparatorBlockEntity.setOutputSignal(inputSidePower);
        return inputSidePower;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }
}
