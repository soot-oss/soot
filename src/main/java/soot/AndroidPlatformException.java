package soot;

/**
 * Exception that is thrown when Soot cannot find the correct platform API version for a DEX or APK file
 * 
 * @author Steven Arzt
 *
 */
public class AndroidPlatformException extends RuntimeException {

  private static final long serialVersionUID = 5582559536663042315L;

  public AndroidPlatformException() {
    super();
  }

  public AndroidPlatformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public AndroidPlatformException(String message, Throwable cause) {
    super(message, cause);
  }

  public AndroidPlatformException(String message) {
    super(message);

  }

  public AndroidPlatformException(Throwable cause) {
    super(cause);
  }

}
