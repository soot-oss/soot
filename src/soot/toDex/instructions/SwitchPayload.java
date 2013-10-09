package soot.toDex.instructions;

import java.util.List;

import org.jf.dexlib.Code.Opcode;

import soot.Unit;
import soot.toDex.SootToDexUtils;

/**
 * The payload for switch instructions, usually placed at the end of a method.
 * This is where the jump targets are stored.<br>
 * <br>
 * Note that this is an {@link InsnWithOffset} with multiple offsets.
 */
public abstract class SwitchPayload extends InsnWithOffset {
	
	protected Insn31t switchInsn;
	
	protected List<Unit> targets;
	
	protected int[] targetAddresses;
	
	public SwitchPayload(List<Unit> targets) {
		super(Opcode.NOP);
		this.targets = targets;
		targetAddresses = new int[targets.size()];
	}
	
	public void setSwitchInsn(Insn31t switchInsn) {
		this.switchInsn = switchInsn;
	}
	
	@Override // we have multiple offsets
	public void setOffsetAddress(List<Insn> insns) {
		for (int i = 0; i < targetAddresses.length; i++) {
			targetAddresses[i] = SootToDexUtils.getOffset(targets.get(i), insns);
		}
	}
	
	@Override // we have multiple offsets
	public boolean offsetFit() {
		int[] relativeOffsets = getRelativeOffsets();
		for (int relativeOffset : relativeOffsets) {
			if (!SootToDexUtils.fitsSigned32(relativeOffset)) {
				return false;
			}
		}
		return true;
	}
	
	// the offset is relative to the switch, not the payload
	public int[] getRelativeOffsets() {
		int switchOffset = switchInsn.getInsnOffset();
		int[] relativeOffsets = new int[targetAddresses.length];
		for (int i = 0; i < relativeOffsets.length; i++) {
			relativeOffsets[i] = targetAddresses[i] - switchOffset;
		}
		return relativeOffsets;
	}
}