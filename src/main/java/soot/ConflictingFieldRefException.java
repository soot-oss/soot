package soot;

/**
 * Exception that is thrown when code tries to resolve a reference for some
 * field "fld: type1", but the target class already declares a field "fld:
 * type2". In other words, this exception denotes a mismatch in expected and
 * declared type.
 * 
 * @author Steven Arzt
 *
 */
public class ConflictingFieldRefException extends RuntimeException {

	private static final long serialVersionUID = -2351763146637880592L;

	private final SootField existingField;
	private final Type requestedType;

	public ConflictingFieldRefException(SootField existingField, Type requestedType) {
		this.existingField = existingField;
		this.requestedType = requestedType;
	}

	@Override
	public String toString() {
		return String.format("Existing field %s does not match expected field type %s", existingField.toString(),
				requestedType.toString());
	}

}
