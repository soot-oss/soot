package soot.toDex.instructions;

import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.SparseSwitchDataPseudoInstruction;

import soot.Unit;

/**
 * The payload for a sparse-switch instruction.
 * 
 * @see SwitchPayload
 */
public class SparseSwitchPayload extends SwitchPayload {

	private int[] keys;
	
	public SparseSwitchPayload(int[] keys, List<Unit> targets) {
		super(targets);
		this.keys = keys;
	}
	
	@Override
	public int getSize() {
		// size = (identFieldSize+sizeFieldSize) + numTargets * (keyFieldSize+targetFieldSize)
		return 2 + targets.size() * 4;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new SparseSwitchDataPseudoInstruction(keys, getRelativeOffsets());
	}
}