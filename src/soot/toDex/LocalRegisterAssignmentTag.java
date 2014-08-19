package soot.toDex;

import soot.Local;
import soot.Value;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class LocalRegisterAssignmentTag implements Tag {


	public static final String TAGNAME = "LocalRegisterAssignmentTag";
	private Local local;
	private Register register;

	private LocalRegisterAssignmentTag(Register register, Local local) {
		this.register = register;
		this.local = local;
	}

	@Override
	public String getName() {
		return TAGNAME;
	}

	@Override
	public byte[] getValue() throws AttributeValueException {
		throw new AttributeValueException();
	}

	public Register getRegister() {
		return register;
	}
	
	public Local getLocal() {
		return local;
	}

	public static LocalRegisterAssignmentTag v(Register localReg, Value lhs) {
		return new LocalRegisterAssignmentTag(localReg, (Local) lhs);
	}

}
