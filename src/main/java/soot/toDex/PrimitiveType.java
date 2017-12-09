package soot.toDex;

import java.util.Locale;

/**
 * An enumeration for the primitive types the Dalvik VM can handle.
 */
public enum PrimitiveType {
	
	// NOTE: the order is relevant for cast code generation, so do not change it
	BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE;
	
	public String getName() {
		// return lower case name that is locale-insensitive
		return this.name().toLowerCase(Locale.ENGLISH);
	}
	
	public static PrimitiveType getByName(String name) {
		for (PrimitiveType p : values()) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		throw new RuntimeException("not found: " + name);
	}
}
