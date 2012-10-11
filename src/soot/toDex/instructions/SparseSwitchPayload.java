package soot.toDex.instructions;

import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.SparseSwitchDataPseudoInstruction;

import soot.jimple.Stmt;
import soot.toDex.Register;

/**
 * The payload for a sparse-switch instruction.
 * 
 * @see SwitchPayload
 */
public class SparseSwitchPayload extends SwitchPayload {

	private int[] keys;
	
	public SparseSwitchPayload(int[] keys, List<Stmt> targets) {
		super(targets);
		this.keys = keys;
	}
	
	@Override
	public int getSize(int codeAddress) {
		// size = (identFieldSize+sizeFieldSize) + numTargets * (keyFieldSize+targetFieldSize)
		return 2 + targets.size() * 4;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new SparseSwitchDataPseudoInstruction(keys, getRelativeOffsets());
	}
	
	public SparseSwitchPayload shallowCloneWithRegs(List<Register> newRegs) {
		SparseSwitchPayload shallowClone = new SparseSwitchPayload(keys, targets);
		shallowClone.setInsnOffset(getInsnOffset());
		shallowClone.offset = offset;
		shallowClone.offsetAddress = offsetAddress;
		shallowClone.setSwitchInsn(switchInsn);
		shallowClone.targets = targets;
		shallowClone.targetAddresses = targetAddresses;
		return shallowClone;
	}
}