package soot;

/**
 * Exception that is thrown when a method is accessed through an ambiguous name
 * 
 * @author Steven Arzt
 *
 */
public class AmbiguousMethodException extends RuntimeException {

  private static final long serialVersionUID = -3200937620978653123L;

  public AmbiguousMethodException(String methodName, String className) {
    super(String.format("Ambiguous method name %s in class %s", methodName, className));
  }

}
