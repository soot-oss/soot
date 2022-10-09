package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import soot.jimple.Stmt;
import soot.util.NumberedString;

/**
 * Allows one-time parsing of method subsignatures. Note that these method sub signatures are resolved, i.e. we resolve the
 * complete types upon construction.
 * 
 * @author Marc Miltenberger
 */
public class MethodSubSignature {
  public final String methodName;
  public final Type returnType;
  public final List<Type> parameterTypes;
  public final NumberedString numberedSubSig;
  private static final Pattern PATTERN_METHOD_SUBSIG
      = Pattern.compile("(?<returnType>.*?) (?<methodName>.*?)\\((?<parameters>.*?)\\)");

  public MethodSubSignature(SootMethodRef r) {
    methodName = r.getName();
    returnType = r.getReturnType();
    parameterTypes = r.getParameterTypes();
    numberedSubSig = r.getSubSignature();
  }

  public MethodSubSignature(NumberedString subsig) {
    this.numberedSubSig = subsig;
    Matcher m = PATTERN_METHOD_SUBSIG.matcher(subsig.toString());
    if (!m.matches()) {
      throw new IllegalArgumentException("Not a valid subsignature: " + subsig);
    }

    Scene sc = Scene.v();
    methodName = m.group(2);
    returnType = sc.getTypeUnsafe(m.group(1));
    String parameters = m.group(3);
    String[] spl = parameters.split(",");
    parameterTypes = new ArrayList<>(spl.length);

    if (parameters != null && !parameters.isEmpty()) {
      for (String p : spl) {
        parameterTypes.add(sc.getTypeUnsafe(p.trim()));
      }
    }
  }

  public MethodSubSignature(String methodName, Type returnType, List<Type> parameterTypes) {
    this.methodName = methodName;
    this.returnType = returnType;
    this.parameterTypes = parameterTypes;
    this.numberedSubSig = Scene.v().getSubSigNumberer()
        .findOrAdd(returnType + " " + methodName + "(" + Joiner.on(',').join(parameterTypes) + ")");
  }

  /**
   * Creates a new instance of the {@link MethodSubSignature} class based on a call site. The subsignature of the callee will
   * be taken from the method referenced at the call site.
   * 
   * @param callSite
   *          The call site
   */
  public MethodSubSignature(Stmt callSite) {
    this(callSite.getInvokeExpr().getMethodRef().getSubSignature());
  }

  public String getMethodName() {
    return methodName;
  }

  public Type getReturnType() {
    return returnType;
  }

  public List<Type> getParameterTypes() {
    return parameterTypes;
  }

  public NumberedString getNumberedSubSig() {
    return numberedSubSig;
  }

  /**
   * Tries to find the exact method in a class. Returns null if cannot be found
   * 
   * @param c
   *          the class
   * @return the method (or null)
   */
  public SootMethod getInClassUnsafe(SootClass c) {
    return c.getMethodUnsafe(numberedSubSig);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((numberedSubSig == null) ? 0 : numberedSubSig.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    MethodSubSignature other = (MethodSubSignature) obj;
    if (numberedSubSig == null) {
      if (other.numberedSubSig != null) {
        return false;
      }
    } else if (!numberedSubSig.equals(other.numberedSubSig)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return numberedSubSig.toString();
  }
}
