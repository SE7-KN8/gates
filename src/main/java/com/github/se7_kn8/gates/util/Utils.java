package com.github.se7_kn8.gates.util;

import net.minecraft.block.Block;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.function.Predicate;

public class Utils {

	public static final Predicate<String> NUMBER_STRING_9_CHARACTERS = s -> (s.matches("^[0-9]+$") || s.equals("")) && s.length() < 10;

	public static final VoxelShape GATE_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

}
