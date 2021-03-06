package com.github.se7_kn8.gates.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class RotarySwitch extends HorizontalFaceBlock {

	public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;

	protected static final VoxelShape LEVER_NORTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
	protected static final VoxelShape LEVER_SOUTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
	protected static final VoxelShape LEVER_WEST_AABB = Block.makeCuboidShape(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
	protected static final VoxelShape LEVER_EAST_AABB = Block.makeCuboidShape(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
	protected static final VoxelShape FLOOR_Z_SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
	protected static final VoxelShape FLOOR_X_SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
	protected static final VoxelShape CEILING_Z_SHAPE = Block.makeCuboidShape(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
	protected static final VoxelShape CEILING_X_SHAPE = Block.makeCuboidShape(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);


	public RotarySwitch() {
		super(Properties.from(Blocks.LEVER));
		this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(POWER, 0).with(FACE, AttachFace.WALL));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch(state.get(FACE)) {
			case FLOOR:
				switch(state.get(HORIZONTAL_FACING).getAxis()) {
					case X:
						return FLOOR_X_SHAPE;
					case Z:
					default:
						return FLOOR_Z_SHAPE;
				}
			case WALL:
				switch(state.get(HORIZONTAL_FACING)) {
					case EAST:
						return LEVER_EAST_AABB;
					case WEST:
						return LEVER_WEST_AABB;
					case SOUTH:
						return LEVER_SOUTH_AABB;
					case NORTH:
					default:
						return LEVER_NORTH_AABB;
				}
			case CEILING:
			default:
				switch(state.get(HORIZONTAL_FACING).getAxis()) {
					case X:
						return CEILING_X_SHAPE;
					case Z:
					default:
						return CEILING_Z_SHAPE;
				}
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!worldIn.isRemote) {
			BlockState newState;
			if (player.isSneaking()) {
				int power = state.get(POWER);
				if (power == 0) {
					power = 16;
				}
				power -= 1;
				newState = state.with(POWER, power);
			} else {
				newState = state.func_235896_a_(POWER);
			}
			worldIn.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, 0.6f);
			worldIn.setBlockState(pos, newState);
			worldIn.notifyNeighborsOfStateChange(pos, this);
			worldIn.notifyNeighborsOfStateChange(pos.offset(getFacing(newState).getOpposite()), this);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockState.get(POWER);
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (getFacing(blockState) == side) {
			return blockState.get(POWER);
		}
		return 0;
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACE, HORIZONTAL_FACING, POWER);
	}

}
