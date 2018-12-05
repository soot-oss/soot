package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

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
