package soot;

/**
 * Exception that is thrown when a field is accessed through an ambiguous name
 * 
 * @author Steven Arzt
 *
 */
public class AmbiguousFieldException extends RuntimeException {

  private static final long serialVersionUID = -1713255335762612121L;

  public AmbiguousFieldException(String fieldName, String className) {
    super(String.format("Ambiguous field name %s in class %s", fieldName, className));
  }

}
