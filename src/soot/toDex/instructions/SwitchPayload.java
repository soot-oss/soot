package soot.toDex.instructions;

import java.util.List;

import soot.Unit;

/**
 * The payload for switch instructions, usually placed at the end of a method.
 * This is where the jump targets are stored.<br>
 * <br>
 * Note that this is an {@link InsnWithOffset} with multiple offsets.
 */
public abstract class SwitchPayload extends AbstractPayload {
	
	protected Insn31t switchInsn;
	
	protected List<Unit> targets;
		
	public SwitchPayload(List<Unit> targets) {
		super();
		this.targets = targets;
	}
	
	public void setSwitchInsn(Insn31t switchInsn) {
		this.switchInsn = switchInsn;
	}
	
	@Override
	public int getMaxJumpOffset() {
		return Short.MAX_VALUE;
	}

}