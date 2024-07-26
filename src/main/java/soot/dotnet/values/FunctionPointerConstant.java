package soot.dotnet.values;

import java.util.ArrayList;
import java.util.List;

import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethodRef;
import soot.Type;
import soot.dotnet.members.ByReferenceWrapperGenerator;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.Constant;
import soot.util.Switch;

public class FunctionPointerConstant extends Constant {

  private static final long serialVersionUID = 1L;
  private DotnetMethod method;
  private boolean virtual;
  private SootMethodRef methodRef;

  public FunctionPointerConstant(DotnetMethod method, boolean virtual) {
    this.method = method;
    this.virtual = virtual;
    List<Type> parameterDefs = new ArrayList<>();
    for (ProtoAssemblyAllTypes.ParameterDefinition parameterDefinition : method.getProtoMessage().getParameterList()) {
      Type t = DotnetTypeFactory.toSootType(parameterDefinition.getType());
      if (ByReferenceWrapperGenerator.needsWrapper(parameterDefinition)) {
        SootClass sc = ByReferenceWrapperGenerator.getWrapperClass(t);
        t = sc.getType();
      }
      parameterDefs.add(t);

    }
    methodRef = Scene.v().makeMethodRef(method.getDeclaringClass(), method.getName(), parameterDefs,
        DotnetTypeFactory.toSootType(method.getReturnType()), method.isStatic());
  }

  @Override
  public int hashCode() {
    SootMethodRef mr = methodRef;
    int hash = mr.getName().hashCode();
    hash = hash * 31 + mr.getParameterTypes().size();
    hash = hash * 31 + mr.getReturnType().hashCode();
    hash = hash * 31 + mr.getDeclaringClass().hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FunctionPointerConstant) {
      FunctionPointerConstant other = (FunctionPointerConstant) obj;
      SootMethodRef omr = other.methodRef;
      SootMethodRef mr = methodRef;
      if (virtual != other.virtual) {
        return false;
      }
      if (virtual) {
        return mr.resolve() == omr.resolve();
      }
      if (!mr.getName().equals(omr.getName()) || !mr.getParameterTypes().equals(omr.getParameterTypes())
          || !mr.getReturnType().equals(omr.getReturnType())) {
        return false;
      }
      if (mr.getDeclaringClass() != omr.getDeclaringClass()) {
        return false;
      }
    }
    return super.equals(obj);
  }

  public DotnetMethod getDotnetMethod() {
    return method;
  }

  public boolean isVirtual() {
    return virtual;
  }

  public SootMethodRef getSootMethodRef() {
    return methodRef;
  }

  public FunctionPointerConstant clone() {
    throw new RuntimeException("Not supported");
  }

  @Override
  public boolean equivTo(Object o) {
    throw new RuntimeException("Not supported");
  }

  @Override
  public int equivHashCode() {
    throw new RuntimeException("Not supported");
  }

  @Override
  public Type getType() {
    return RefType.v("System.Delegate");
  }

  @Override
  public void apply(Switch sw) {
    throw new RuntimeException("Not supported");
  }

  @Override
  public String toString() {
    return "Function Ptr: " + methodRef + " - " + (virtual ? "virtual" : "non-virtual");
  }

}
