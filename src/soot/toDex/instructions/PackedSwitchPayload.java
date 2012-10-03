package soot.toDex.instructions;

import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.PackedSwitchDataPseudoInstruction;

import soot.jimple.Stmt;
import soot.toDex.Register;

public class PackedSwitchPayload extends SwitchPayload {
	
	private int firstKey;
	
	public PackedSwitchPayload(int firstKey, List<Stmt> targets) {
		super(targets);
		this.firstKey = firstKey;
	}
	
	@Override
	public int getSize(int codeAddress) {
		// size = (identFieldSize+sizeFieldSize+firstKeyFieldSize) + (numTargets * targetFieldSize)
		return 4 + targets.size() * 2;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new PackedSwitchDataPseudoInstruction(firstKey, getRelativeOffsets());
	}

	public PackedSwitchPayload shallowCloneWithRegs(List<Register> newRegs) {
		PackedSwitchPayload shallowClone = new PackedSwitchPayload(firstKey, targets);
		shallowClone.setInsnOffset(getInsnOffset());
		shallowClone.offset = offset;
		shallowClone.offsetAddress = offsetAddress;
		shallowClone.setSwitchInsn(switchInsn);
		shallowClone.targets = targets;
		shallowClone.targetAddresses = targetAddresses;
		return shallowClone;
	}
}