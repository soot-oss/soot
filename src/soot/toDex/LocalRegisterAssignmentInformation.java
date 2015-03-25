package soot.toDex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;

/**
 * Contains information about which register maps to which local
 */
public class LocalRegisterAssignmentInformation {

	private static final Logger logger =LoggerFactory.getLogger(LocalRegisterAssignmentInformation.class);

	private Local local;
	private Register register;


	public LocalRegisterAssignmentInformation(Register register, Local local) {
		this.register = register;
		this.local = local;
	}


	public static LocalRegisterAssignmentInformation v(Register register, Local l) {
		return new LocalRegisterAssignmentInformation(register, l);
	}


	public Local getLocal() {
		return local;
	}


	public Register getRegister() {
		return register;
	}

}
