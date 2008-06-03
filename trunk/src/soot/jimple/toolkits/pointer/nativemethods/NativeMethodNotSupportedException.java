/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Feng Qian
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/**
 * An exception throwed by the simulator when a native method's side-effect
 * can not be simulated safely.
 *
 * @author Feng Qian
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;

public class NativeMethodNotSupportedException
  extends RuntimeException {

  private String msg;

  public NativeMethodNotSupportedException(SootMethod method){
    String message = "The following native method is not supported: \n  "
      +method.getSignature();
    this.msg = message;
  }

  public NativeMethodNotSupportedException(String message){
    this.msg = message;
  }

  public NativeMethodNotSupportedException(){
  }

  public String toString(){
    return msg;
  }
}
