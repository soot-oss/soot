package soot.validation;

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

import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;

/**
 * Validates classes to make sure that all method signatures are valid
 * 
 * @author Steven Arzt
 */
public enum MethodDeclarationValidator implements ClassValidator {
  INSTANCE;

  public static MethodDeclarationValidator v() {
    return INSTANCE;
  }

  @Override
  public void validate(SootClass sc, List<ValidationException> exceptions) {
    if (sc.isConcrete()) {
      for (SootMethod sm : sc.getMethods()) {
        for (Type tp : sm.getParameterTypes()) {
          if (tp == null) {
            exceptions.add(new ValidationException(sm, "Null parameter types are invalid"));
          }
          if (tp instanceof VoidType) {
            exceptions.add(new ValidationException(sm, "Void parameter types are invalid"));
          }
          if (!tp.isAllowedInFinalCode()) {
            exceptions.add(new ValidationException(sm, "Parameter type not allowed in final code"));
          }
        }
      }
    }
  }

  @Override
  public boolean isBasicValidator() {
    return true;
  }

}
