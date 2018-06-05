package soot.jimple.validation;

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

import java.util.List;

import soot.Body;
import soot.SootMethod;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

public enum MethodValidator implements BodyValidator {
  INSTANCE;

  public static MethodValidator v() {
    return INSTANCE;
  }

  /**
   * Checks the following invariants on this Jimple body:
   * <ol>
   * <li>static initializer should have 'static' modifier
   * </ol>
   */
  @Override
  public void validate(Body body, List<ValidationException> exceptions) {
    SootMethod method = body.getMethod();
    if (method.isAbstract()) {
      return;
    }
    if (method.isStaticInitializer() && !method.isStatic()) {
      exceptions.add(new ValidationException(method,
          SootMethod.staticInitializerName + " should be static! Static initializer without 'static'('0x8') modifier"
              + " will cause problem when running on android platform: "
              + "\"<clinit> is not flagged correctly wrt/ static\"!"));
    }
  }

  @Override
  public boolean isBasicValidator() {
    return true;
  }
}
