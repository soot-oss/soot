package soot.dexpler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidDalvikBytecodeException extends RuntimeException {

	private static final Logger logger =LoggerFactory.getLogger(InvalidDalvikBytecodeException.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1932386032493767303L;

	public InvalidDalvikBytecodeException(String msg) {
		super(msg);
	}

}
