package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrice Pominville
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

@SuppressWarnings("serial")
public class CompilationDeathException extends RuntimeException {
  private final int mStatus;

  public static final int COMPILATION_ABORTED = 0;
  public static final int COMPILATION_SUCCEEDED = 1;

  public CompilationDeathException(String msg, Throwable t) {
    super(msg, t);
    mStatus = COMPILATION_ABORTED;
  }

  public CompilationDeathException(String msg) {
    super(msg);
    mStatus = COMPILATION_ABORTED;
  }

  public CompilationDeathException(int status, String msg) {
    super(msg);
    mStatus = status;
  }

  public CompilationDeathException(int status) {
    mStatus = status;
  }

  public int getStatus() {
    return mStatus;
  }
}
