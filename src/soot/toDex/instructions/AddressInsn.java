package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;

import soot.toDex.Register;

/* 
 * inspired by com.android.dx.dex.code.CodeAddress: pseudo insn for usage
 * as branch target or start/end of an exception handler range. has size 0,
 * so its offset is the same as the following real insn.
 */
public class AddressInsn extends AbstractInsn {
	
	private Object originalSource;

	public AddressInsn(Object originalSource) {
		super(Opcode.NOP);
		this.originalSource = originalSource;
	}
	
	public Object getOriginalSource() {
		return originalSource;
	}
	
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		return new BitSet(0);
	}

	@Override
	protected Instruction getRealInsn0() {
		return null;
	}
	
	@Override
	public int getSize(int codeAddress) {
		return 0;
	}
	
	@Override
	public String toString() {
		return "address instruction for " + originalSource;
	}
	
	public AddressInsn shallowCloneWithRegs(List<Register> newRegs) {
		AddressInsn shallowClone = new AddressInsn(getOriginalSource());
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}