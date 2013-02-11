package soot.toDex.instructions;

import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.PackedSwitchDataPseudoInstruction;

import soot.jimple.Stmt;

/**
 * The payload for a packed-switch instruction.
 * 
 * @see SwitchPayload
 */
public class PackedSwitchPayload extends SwitchPayload {
	
	private int firstKey;
	
	public PackedSwitchPayload(int firstKey, List<Stmt> targets) {
		super(targets);
		this.firstKey = firstKey;
	}
	
	@Override
	public int getSize() {
		// size = (identFieldSize+sizeFieldSize+firstKeyFieldSize) + (numTargets * targetFieldSize)
		return 4 + targets.size() * 2;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new PackedSwitchDataPseudoInstruction(firstKey, getRelativeOffsets());
	}
}