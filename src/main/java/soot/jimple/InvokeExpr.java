package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
 * Copyright (C) 2004 Ondrej Lhotak
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

import soot.SootMethod;
import soot.SootMethodRef;
import soot.Value;
import soot.ValueBox;

/**
 * Represents method invocation expression.
 *
 * @see VirtualInvokeExpr invokevirtual
 * @see InterfaceInvokeExpr invokeinterface
 * @see SpecialInvokeExpr invokespecial
 * @see StaticInvokeExpr invokestatic
 * @see DynamicInvokeExpr invokedynamic
 */
public interface InvokeExpr extends Expr {

  void setMethodRef(SootMethodRef smr);

  SootMethodRef getMethodRef();

  /**
   * Resolves {@link SootMethodRef} to {@link SootMethod}.
   *
   * @return {@link SootMethod} instance, or {@code null} when reference cannot be resolved and
   *         {@link soot.options.Options#ignore_resolution_errors} is {@code true}
   * @throws soot.SootMethodRefImpl.ClassResolutionFailedException
   *           when reference cannot be resolved and {@link soot.options.Options#ignore_resolution_errors} is {@code false}
   */
  SootMethod getMethod();

  List<Value> getArgs();

  Value getArg(int index);

  int getArgCount();

  void setArg(int index, Value arg);

  ValueBox getArgBox(int index);

}
