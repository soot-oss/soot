package soot.util;

import soot.Body;
import soot.Local;

/**
 * Class for packing local numbers such that bit sets can easily be used to
 * reference locals in bodies
 * 
 * @author Steven Arzt
 *
 */
public class LocalBitSetPacker {

	private final Body body;

	private Local[] locals;
	private int[] oldNumbers;

	public LocalBitSetPacker(Body body) {
		this.body = body;
	}

	/**
	 * Reassigns the local numbers such that a dense bit set can be created over
	 * them
	 */
	public void pack() {
		int n = body.getLocalCount();
		locals = new Local[n];
		oldNumbers = new int[n];
		n = 0;
		for (Local local : body.getLocals()) {
			locals[n] = local;
			oldNumbers[n] = local.getNumber();
			local.setNumber(n++);
		}
	}

	/**
	 * Restores the original local numbering
	 */
	public void unpack() {
		for (int i = 0; i < locals.length; i++)
			locals[i].setNumber(oldNumbers[i]);
		locals = null;
		oldNumbers = null;
	}

	public int getLocalCount() {
		return locals == null ? 0 : locals.length;
	}

}
