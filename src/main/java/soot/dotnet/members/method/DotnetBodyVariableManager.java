package soot.dotnet.members.method;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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
import soot.ArrayType;
import soot.Body;
import soot.Local;
import soot.LocalGenerator;
import soot.NullType;
import soot.PrimType;
import soot.RefType;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetBasicTypes;
import soot.dotnet.types.DotnetTypeFactory;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.Jimple;
import soot.jimple.NullConstant;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JimpleLocal;

/**
 * Part of the DotnetBody Manager for variables in a .NET Body (Store, initialization, etc.)
 */
public class DotnetBodyVariableManager {

  private final DotnetBody dotnetBody;
  private final Body mainJb;
  public final LocalGenerator localGenerator;
  private final HashSet<String> localsToCast = new HashSet<>();

  public DotnetBodyVariableManager(DotnetBody dotnetBody, Body mainJb) {
    this.dotnetBody = dotnetBody;
    this.mainJb = mainJb;
    localGenerator = new DefaultLocalGenerator(mainJb);
  }

  /**
   * Add parameters of the .NET method to the Jimple Body
   */
  public void fillMethodParameter() {
    DotnetMethod dotnetMethodSig = dotnetBody.getDotnetMethodSig();
    fillMethodParameter(mainJb, dotnetMethodSig.getParameterDefinitions());
  }

  /**
   * Add parameters of the .NET method to the Jimple Body
   *
   * @param jb
   * @param parameters
   */
  public void fillMethodParameter(Body jb, List<ProtoAssemblyAllTypes.ParameterDefinition> parameters) {
    // parameters
    for (int i = 0; i < parameters.size(); i++) {
      ProtoAssemblyAllTypes.ParameterDefinition parameter = parameters.get(i);
      Local paramLocal
          = Jimple.v().newLocal(parameter.getParameterName(), DotnetTypeFactory.toSootType(parameter.getType()));
      jb.getLocals().add(paramLocal);
      jb.getUnits().add(Jimple.v().newIdentityStmt(paramLocal,
          Jimple.v().newParameterRef(DotnetTypeFactory.toSootType(parameter.getType()), i)));
    }
  }

  /**
   * Add and Initialize all known variables/locals at the beginning
   *
   * @param variableMsgList
   */
  public void addInitLocalVariables(List<ProtoIlInstructions.IlVariableMsg> variableMsgList) {
    List<ProtoIlInstructions.IlVariableMsg> initLocalValueTypes = new ArrayList<>();
    for (ProtoIlInstructions.IlVariableMsg v : variableMsgList) {
      if (v.getVariableKind() != ProtoIlInstructions.IlVariableMsg.IlVariableKind.LOCAL) {
        continue;
      }

      addOrGetVariable(v, this.mainJb);
      if (v.getHasInitialValue()) {
        initLocalValueTypes.add(v);
      }

      // for unsafe methods, where no definition is used
      if (!(v.getType().getFullname().equals(DotnetBasicTypes.SYSTEM_OBJECT))) {
        initLocalValueTypes.add(v);
      }
    }
    initLocalVariables(initLocalValueTypes);
  }

  private void initLocalVariables(List<ProtoIlInstructions.IlVariableMsg> locals) {
    for (ProtoIlInstructions.IlVariableMsg v : locals) {
      if (v.getVariableKind() != ProtoIlInstructions.IlVariableMsg.IlVariableKind.LOCAL) {
        continue;
      }

      Local variable = addOrGetVariable(v, this.mainJb);

      if (variable.getType() instanceof PrimType) {
        AssignStmt assignStmt = Jimple.v().newAssignStmt(variable, DotnetTypeFactory.initType(variable));
        mainJb.getUnits().add(assignStmt);
        continue;
      }

      if (variable.getType() instanceof ArrayType) {
        AssignStmt assignStmt = Jimple.v().newAssignStmt(variable, NullConstant.v());
        mainJb.getUnits().add(assignStmt);
        continue;
      }

      // In general, structs are classes inherited by System.ValueType
      // Normally, structs are not instantiated with new obj, but while visiting default_value instruction a pseudo
      // constructor is called
      // create new valuetype "object"
      AssignStmt assignStmt
          = Jimple.v().newAssignStmt(variable, Jimple.v().newNewExpr(RefType.v(v.getType().getFullname())));
      mainJb.getUnits().add(assignStmt);
    }
  }

  /**
   * Add or get variable/local of this method body
   *
   * @param v
   * @param jbTmp
   * @return
   */
  public Local addOrGetVariable(ProtoIlInstructions.IlVariableMsg v, Body jbTmp) {
    return addOrGetVariable(v, null, jbTmp);
  }

  /**
   * Type of local is got by the protoVariableMsg but in some cases we need to define the type of the local
   *
   * @param v
   * @param type
   * @param jbTmp
   * @return
   */
  public Local addOrGetVariable(ProtoIlInstructions.IlVariableMsg v, Type type, Body jbTmp) {
    if (v == null) {
      return null;
    }

    if (v.getName().equals("this")) {
      return this.mainJb.getThisLocal();
    }

    if (this.mainJb.getLocals().stream().anyMatch(x -> x.getName().equals(v.getName()))) {
      return this.mainJb.getLocals().stream().filter(x -> x.getName().equals(v.getName())).findFirst().orElse(null);
    }

    Type localType = (type == null || type instanceof UnknownType || type instanceof NullType)
        ? DotnetTypeFactory.toSootType(v.getType())
        : DotnetTypeFactory.toSootType(type); // deprecated JimpleToDotnetType(type)

    Local newLocal = Jimple.v().newLocal(v.getName(), localType);
    this.mainJb.getLocals().add(newLocal);
    if (jbTmp != null && jbTmp != this.mainJb) {
      jbTmp.getLocals().add(newLocal); // dummy due to clone method
    }
    return newLocal;
  }

  public void addLocalVariable(Local local) {
    if (this.mainJb.getLocals().contains(local)) {
      return;
    }
    this.mainJb.getLocals().add(local);
  }

  /**
   * Recursively get value of a locals chain
   *
   * @param v
   * @param jb
   * @return
   */
  public static Value inlineLocals(Value v, Body jb) {
    Unit unit = jb.getUnits().stream().filter(x -> x instanceof JAssignStmt && ((JAssignStmt) x).getLeftOp().equals(v))
        .findFirst().orElse(null);
    if (unit instanceof AssignStmt) {
      if (((AssignStmt) unit).getRightOp() instanceof JimpleLocal) {
        return inlineLocals(((JAssignStmt) unit).getRightOp(), jb);
      } else if (((AssignStmt) unit).getRightOp() instanceof CastExpr) {
        CastExpr ce = (CastExpr) ((AssignStmt) unit).getRightOp();
        return inlineLocals(ce.getOp(), jb);
      } else {
        return ((AssignStmt) unit).getRightOp();
      }
    }
    return null;
  }

  /**
   * Sometimes we need to cast locals to fulfill the validation. In this case we add them to this set and cast them later on
   *
   * @param local
   */
  public void addLocalsToCast(String local) {
    localsToCast.add(local);
  }

  public boolean localsToCastContains(String local) {
    return localsToCast.contains(local);
  }
  /**
   * Assign expression only to new local variable to use the value in complex operations, when val is not a local or constant
   *
   * @param val : assign to local
   */
  public Value simplifyIfNotPrimitiveWithLocal(Value val) {
	 if(!(val instanceof Local || val instanceof Constant))
		 return (Value) generateLocalAndAssign(val);
	 return val;
  }
  
  /**
   * Assign expression only to new local variable to use the value in complex operations, when val is not a local
   *
   * @param val : assign to local
   */

  public Local simplifyWithLocal(Value val) {
		 if(!(val instanceof Local)) 
			 return generateLocalAndAssign(val);
		 return (Local) val;
	  }
  /**
   * Assign expression to new local variable to use the value in complex operations
   *
   * @param val : assign to local
   */
  private Local generateLocalAndAssign(Value val) {
	  Local local = localGenerator.generateLocal(val.getType());
	  mainJb.getUnits().add(Jimple.v().newAssignStmt(local, val));
      return local;
  }
}
