package soot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;

import soot.util.NumberedString;

/**
 * Allows one-time parsing of method subsignatures.
 * Note that these method sub signatures are resolved, i.e. 
 * we resolve the complete types upon construction.
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
    if (!m.matches())
      throw new IllegalArgumentException("Not a valid subsignature: " + subsig);

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
    this.numberedSubSig
        = new NumberedString(returnType + " " + methodName + "(" + Joiner.on(',').join(parameterTypes) + ")");
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
   * Tries to find the exact method in a class. Returns null
   * if cannot be found
   * @param c the class
   * @return the method (or null)
   */
  public SootMethod getInClassUnsafe(SootClass c) {
    return c.getMethodUnsafe(numberedSubSig);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
    result = prime * result + ((parameterTypes == null) ? 0 : parameterTypes.hashCode());
    result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MethodSubSignature other = (MethodSubSignature) obj;
    if (methodName == null) {
      if (other.methodName != null)
        return false;
    } else if (!methodName.equals(other.methodName))
      return false;
    if (!parameterTypes.equals(other.parameterTypes))
      return false;
    if (returnType == null) {
      if (other.returnType != null)
        return false;
    } else if (!returnType.equals(other.returnType))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return numberedSubSig.toString();
  }
}
