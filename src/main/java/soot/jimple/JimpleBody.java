package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.jimple.validation.FieldRefValidator;
import soot.jimple.validation.IdentityStatementsValidator;
import soot.jimple.validation.IdentityValidator;
import soot.jimple.validation.InvokeArgumentValidator;
import soot.jimple.validation.JimpleTrapValidator;
import soot.jimple.validation.MethodValidator;
import soot.jimple.validation.NewValidator;
import soot.jimple.validation.ReturnStatementsValidator;
import soot.jimple.validation.TypesValidator;
import soot.options.Options;
import soot.util.Chain;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/** Implementation of the Body class for the Jimple IR. */
public class JimpleBody extends StmtBody {
  private static BodyValidator[] validators;

  /**
   * Returns an array containing some validators in order to validate the JimpleBody
   *
   * @return the array containing validators
   */
  private synchronized static BodyValidator[] getValidators() {
    if (validators == null) {
      validators = new BodyValidator[] { IdentityStatementsValidator.v(), TypesValidator.v(), ReturnStatementsValidator.v(),
          InvokeArgumentValidator.v(), FieldRefValidator.v(), NewValidator.v(), JimpleTrapValidator.v(),
          IdentityValidator.v(), MethodValidator.v()
          // InvokeValidator.v()
      };
    }
    return validators;
  };

  /**
   * Construct an empty JimpleBody
   */
  public JimpleBody(SootMethod m) {
    super(m);
  }

  /**
   * Construct an extremely empty JimpleBody, for parsing into.
   */
  public JimpleBody() {
  }

  /** Clones the current body, making deep copies of the contents. */
  @Override
  public Object clone() {
    Body b = new JimpleBody(getMethod());
    b.importBodyContentsFrom(this);
    return b;
  }

  /**
   * Make sure that the JimpleBody is well formed. If not, throw an exception. Right now, performs only a handful of checks.
   */
  @Override
  public void validate() {
    final List<ValidationException> exceptionList = new ArrayList<ValidationException>();
    validate(exceptionList);
    if (!exceptionList.isEmpty()) {
      throw exceptionList.get(0);
    }
  }

  /**
   * Validates the jimple body and saves a list of all validation errors
   *
   * @param exceptionList
   *          the list of validation errors
   */
  @Override
  public void validate(List<ValidationException> exceptionList) {
    super.validate(exceptionList);
    final boolean runAllValidators = Options.v().debug() || Options.v().validate();
    for (BodyValidator validator : getValidators()) {
      if (!validator.isBasicValidator() && !runAllValidators) {
        continue;
      }
      validator.validate(this, exceptionList);
    }
  }

  public void validateIdentityStatements() {
    runValidation(IdentityStatementsValidator.v());
  }

  /** Inserts usual statements for handling this & parameters into body. */
  public void insertIdentityStmts() {
    insertIdentityStmts(getMethod().getDeclaringClass());
  }

  /**
   * Inserts usual statements for handling this & parameters into body.
   *
   * @param declaringClass
   *          the class, which should be used for this references. Can be null for static methods
   */
  public void insertIdentityStmts(SootClass declaringClass) {
    final Jimple jimple = Jimple.v();
    final PatchingChain<Unit> unitChain = getUnits();
    final Chain<Local> localChain = getLocals();
    Unit lastUnit = null;

    // add this-ref before everything else
    if (!getMethod().isStatic()) {
      if (declaringClass == null) {
        throw new IllegalArgumentException(
            String.format("No declaring class given for method %s", method.getSubSignature()));
      }
      Local l = jimple.newLocal("this", RefType.v(declaringClass));
      Stmt s = jimple.newIdentityStmt(l, jimple.newThisRef((RefType) l.getType()));

      localChain.add(l);
      unitChain.addFirst(s);
      lastUnit = s;
    }

    int i = 0;
    for (Type t : getMethod().getParameterTypes()) {
      Local l = jimple.newLocal("parameter" + i, t);
      Stmt s = jimple.newIdentityStmt(l, jimple.newParameterRef(l.getType(), i));

      localChain.add(l);
      if (lastUnit == null) {
        unitChain.addFirst(s);
      } else {
        unitChain.insertAfter(s, lastUnit);
      }

      lastUnit = s;
      i++;
    }
  }

  /** Returns the first non-identity stmt in this body. */
  public Stmt getFirstNonIdentityStmt() {
    Iterator<Unit> it = getUnits().iterator();
    Object o = null;
    while (it.hasNext()) {
      if (!((o = it.next()) instanceof IdentityStmt)) {
        break;
      }
    }
    if (o == null) {
      throw new RuntimeException("no non-id statements!");
    }
    return (Stmt) o;
  }
}
