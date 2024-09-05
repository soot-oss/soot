package soot.dotnet.members.method;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Steven Arzt
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import soot.FastHierarchy;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.UnitPatchingChain;
import soot.Value;
import soot.VoidType;
import soot.dotnet.types.DotnetType;
import soot.dotnet.values.FunctionPointerConstant;
import soot.jimple.AssignStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.internal.JTableSwitchStmt;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class DelegateHandler {

  public static final String DELEGATE_HOLDER_CLASSNAME = "DelegateHolder";
  private static final String DELEGATE_INTERFACE_CLASSNAME = "IDelegate";
  private static final String FUNCTION_ID_FIELDNAME = "functionID";
  private static final String INSTANCE_FIELDNAME = "instance";
  private static final String GET_LIST = "getList";

  public static final String REMOVE_METHOD_NAME = "removeDelegate";
  public static final String COMBINE_WITH_METHOD_NAME = "combineWith";
  public static final String COMBINE = "combine";
  public static final String INVOKE_METHOD_NAME = "doInvoke";
  public static final String DELEGATE_LIST_NAME = "DelegateList";

  public static class DelegateInfo implements Tag {

    public static String DELEGATE_NAME = "DelegateInfo";
    private RefType listType = RefType.v("System.Collections.Generic.List`1");

    public List<Type> delegateParameters;
    public Type delegateReturn;
    private SootMethod callSingle;
    private JTableSwitchStmt tableSwitch;
    public final AtomicInteger counter = new AtomicInteger();
    private SootField listField;
    private SootField instanceField;
    private SootField functionIDField;
    private Local retLocal;
    private Local instanceParameter;
    private JimpleBody callSingleBody;
    private SootMethod mCtorSingle;
    private SootMethod mCtorList;

    @Override
    public String getName() {
      return DELEGATE_NAME;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
      return null;
    }

    public SootMethodRef getListMethod(Type retType, String methodName, Type... paramTypes) {
      return Scene.v().makeMethodRef(Scene.v().getSootClass("System.Collections.Generic.List`1"), methodName,
          Arrays.asList(paramTypes), retType, false);
    }

    private DelegateInfo(SootClass actualDelegateClass) {
      SootMethod m = actualDelegateClass.getMethodByName("Invoke");
      delegateParameters = m.getParameterTypes();
      delegateReturn = m.getReturnType();
      Scene sc = Scene.v();
      SootClass dh = createDelegateHolder(sc);
      instanceField = dh.getFieldByName(INSTANCE_FIELDNAME);
      functionIDField = dh.getFieldByName(FUNCTION_ID_FIELDNAME);

      actualDelegateClass.addInterface(dh);
      List<Type> callSinglePTypes = new ArrayList<>(delegateParameters.size() + 1);
      callSinglePTypes.add(sc.getObjectType()); // this is the instance
      callSinglePTypes.add(IntType.v()); // this is the id
      callSinglePTypes.addAll(delegateParameters);

      Jimple j = Jimple.v();
      listField = sc.makeSootField(DELEGATE_LIST_NAME, listType);
      actualDelegateClass.addField(listField);
      createDelegateInterface(sc);
      createListConstructor(sc, actualDelegateClass);

      callSingle = sc.makeSootMethod("callSingle", callSinglePTypes, delegateReturn, Modifier.PUBLIC | Modifier.STATIC);
      actualDelegateClass.addMethod(callSingle);
      callSingleBody = j.newBody(callSingle);
      callSingle.setActiveBody(callSingleBody);
      callSingleBody.insertIdentityStmts();
      instanceParameter = callSingleBody.getParameterLocal(0);
      Stmt uret;
      retLocal = null;
      if (delegateReturn instanceof VoidType) {
        uret = j.newReturnVoidStmt();
      } else {
        retLocal = j.newLocal("retVal", delegateReturn);
        callSingleBody.getLocals().add(retLocal);
        uret = j.newReturnStmt(NullConstant.v());
      }
      tableSwitch
          = (JTableSwitchStmt) j.newTableSwitchStmt(callSingleBody.getParameterLocal(1), 0, 0, new ArrayList<>(), uret);
      callSingleBody.getUnits().add(tableSwitch);
      callSingleBody.getUnits().add(uret);

      createDelegateMethods(sc, actualDelegateClass, callSinglePTypes);

      createSingleConstructor(sc, actualDelegateClass);

    }

    private void createDelegateMethods(Scene sc, SootClass actualDelegateClass, List<Type> callSinglePTypes) {
      SootClass delegateInterface = sc.getSootClass(DELEGATE_INTERFACE_CLASSNAME);
      Jimple j = Jimple.v();
      SootMethod combineWith = sc.makeSootMethod(COMBINE_WITH_METHOD_NAME, Arrays.asList(delegateInterface.getType()),
          delegateInterface.getType(), Modifier.PUBLIC);
      RefType enumerable = RefType.v("System.Collections.Generic.IEnumerable`1");
      RefType delegateHolderType = RefType.v(DELEGATE_HOLDER_CLASSNAME);
      RefType at = actualDelegateClass.getType();
      {
        JimpleBody bodycombi = j.newBody(combineWith);
        combineWith.setActiveBody(bodycombi);
        actualDelegateClass.addMethod(combineWith);
        bodycombi.insertIdentityStmts();
        Local lclList = j.newLocal("List", listType);
        Local lclMyList = j.newLocal("MyList", listType);
        Local lclOtherList = j.newLocal("OtherList", listType);
        bodycombi.getLocals().add(lclList);
        bodycombi.getLocals().add(lclMyList);
        bodycombi.getLocals().add(lclOtherList);
        bodycombi.getUnits()
            .add(j.newAssignStmt(lclMyList, j.newInstanceFieldRef(bodycombi.getThisLocal(), listField.makeRef())));
        bodycombi.getUnits().add(j.newAssignStmt(lclOtherList, j.newInterfaceInvokeExpr(bodycombi.getParameterLocal(0),
            delegateInterface.getMethodByName(GET_LIST).makeRef())));
        bodycombi.getUnits().add(j.newAssignStmt(lclList, j.newNewExpr(listType)));
        bodycombi.getUnits().add(
            j.newInvokeStmt(j.newSpecialInvokeExpr(lclList, getListMethod(VoidType.v(), "<init>", enumerable), lclMyList)));
        bodycombi.getUnits().add(j.newInvokeStmt(
            j.newVirtualInvokeExpr(lclList, getListMethod(VoidType.v(), "AddRange", enumerable), lclOtherList)));
        Local del = j.newLocal("resDelegate", at);
        bodycombi.getLocals().add(del);
        bodycombi.getUnits().add(j.newAssignStmt(del, j.newNewExpr(at)));
        bodycombi.getUnits().add(j.newInvokeStmt(j.newSpecialInvokeExpr(del, mCtorList.makeRef(), lclList)));
        bodycombi.getUnits().add(j.newReturnStmt(del));

      }
      {
        //public static List<Delegate> remove(List<Delegate> my, List<Delegate> other)
        //{
        //    List<Delegate> list = new List<Delegate>(my);
        //
        //    int idx = list.Count;
        //    idx -= other.Count;
        //    if (other.Count == 0)
        //        return list;
        //    Delegate firstOtherElem = other[0];
        //next:
        //    while (idx >= 0)
        //    {
        //        idx = list.LastIndexOf(firstOtherElem, idx);
        //        if (idx == -1)
        //            break;
        //        int i = idx;
        //        int x = 0;
        //        while (i < idx + other.Count)
        //        {
        //            if (list[i] != other[x])
        //            {
        //                idx--;
        //                goto next;
        //            }
        //            x++;
        //            i++;
        //        }
        //        list.RemoveRange(idx, other.Count);
        //        break;
        //    }
        //    return list;
        //}

        SootMethod removeFrom = sc.makeSootMethod(REMOVE_METHOD_NAME, Arrays.asList(delegateInterface.getType()),
            delegateInterface.getType(), Modifier.PUBLIC);
        // Remove is pretty hard, unfortunately.
        // Basically, what we need to do, when Delegate.Remove(a, b) is called:
        // Find the last occurrence of the complete list of b in a and remove it from a. Return the resulting list.

        Local lclList = j.newLocal("List", listType);
        Local lclMyList = j.newLocal("MyList", listType);
        Local lclOtherList = j.newLocal("OtherList", listType);
        actualDelegateClass.addMethod(removeFrom);
        JimpleBody bodyrem = j.newBody(removeFrom);
        removeFrom.setActiveBody(bodyrem);
        bodyrem.insertIdentityStmts();
        bodyrem.getLocals().add(lclList);
        bodyrem.getLocals().add(lclMyList);
        bodyrem.getLocals().add(lclOtherList);
        bodyrem.getUnits()
            .add(j.newAssignStmt(lclMyList, j.newInstanceFieldRef(bodyrem.getThisLocal(), listField.makeRef())));
        bodyrem.getUnits().add(j.newAssignStmt(lclOtherList,
            j.newInterfaceInvokeExpr(bodyrem.getParameterLocal(0), delegateInterface.getMethodByName(GET_LIST).makeRef())));
        bodyrem.getUnits().add(j.newAssignStmt(lclList, j.newNewExpr(listType)));
        bodyrem.getUnits().add(
            j.newInvokeStmt(j.newSpecialInvokeExpr(lclList, getListMethod(VoidType.v(), "<init>", enumerable), lclMyList)));

        // Now we have a copy of our list and we have the other list, we can start...
        Local idx = j.newLocal("idx", IntType.v());
        Local otherCount = j.newLocal("otherCount", IntType.v());
        SootMethodRef getCount = getListMethod(IntType.v(), "get_Count");
        Local del = j.newLocal("resDelegate", at);
        bodyrem.getLocals().add(idx);
        bodyrem.getLocals().add(del);
        bodyrem.getLocals().add(otherCount);
        Stmt beforeEnd = j.newAssignStmt(del, j.newNewExpr(at));

        bodyrem.getUnits().add(j.newAssignStmt(idx, j.newVirtualInvokeExpr(lclList, getCount)));
        bodyrem.getUnits().add(j.newAssignStmt(otherCount, j.newVirtualInvokeExpr(lclOtherList, getCount)));
        bodyrem.getUnits().add(j.newAssignStmt(idx, j.newSubExpr(idx, otherCount)));
        bodyrem.getUnits().add(j.newIfStmt(j.newEqExpr(otherCount, IntConstant.v(0)), beforeEnd));

        Local firstOtherElem = j.newLocal("firstElem", sc.getObjectType());
        bodyrem.getLocals().add(firstOtherElem);
        bodyrem.getUnits().add(j.newAssignStmt(firstOtherElem, j.newVirtualInvokeExpr(lclOtherList,
            getListMethod(sc.getObjectType(), "get_Item", IntType.v()), IntConstant.v(0))));

        IfStmt outerLoop = j.newIfStmt(j.newLtExpr(idx, IntConstant.v(0)), beforeEnd);
        bodyrem.getUnits().add(outerLoop);
        bodyrem.getUnits().add(j.newAssignStmt(idx, j.newVirtualInvokeExpr(lclList,
            getListMethod(IntType.v(), "LastIndexOf", sc.getObjectType(), IntType.v()), firstOtherElem, idx)));
        bodyrem.getUnits().add(j.newIfStmt(j.newEqExpr(idx, IntConstant.v(-1)), beforeEnd));

        Local i = j.newLocal("i", IntType.v());
        Local x = j.newLocal("x", IntType.v());
        bodyrem.getLocals().add(i);
        bodyrem.getLocals().add(x);
        bodyrem.getUnits().add(j.newAssignStmt(i, idx));
        bodyrem.getUnits().add(j.newAssignStmt(x, IntConstant.v(0)));
        Local to = j.newLocal("toTarget", IntType.v());
        bodyrem.getLocals().add(to);
        bodyrem.getUnits().add(j.newAssignStmt(to, j.newAddExpr(idx, otherCount)));
        InvokeStmt remove = j.newInvokeStmt(j.newVirtualInvokeExpr(lclList,
            getListMethod(VoidType.v(), "RemoveRange", IntType.v(), IntType.v()), idx, otherCount));
        Unit loopHeader = j.newIfStmt(j.newGeExpr(i, to), remove);
        bodyrem.getUnits().add(loopHeader);

        Local delegate1 = j.newLocal("delegate1", delegateHolderType);
        Local delegate2 = j.newLocal("delegate2", delegateHolderType);
        bodyrem.getLocals().add(delegate1);
        bodyrem.getLocals().add(delegate2);
        bodyrem.getUnits().add(j.newAssignStmt(delegate1,
            j.newVirtualInvokeExpr(lclList, getListMethod(sc.getObjectType(), "get_Item", IntType.v()), i)));
        bodyrem.getUnits().add(j.newAssignStmt(delegate2,
            j.newVirtualInvokeExpr(lclOtherList, getListMethod(sc.getObjectType(), "get_Item", IntType.v()), x)));

        Unit incX = j.newAssignStmt(x, j.newAddExpr(x, IntConstant.v(1)));

        bodyrem.getUnits().add(j.newIfStmt(j.newEqExpr(delegate1, delegate2), incX));
        bodyrem.getUnits().add(j.newAssignStmt(idx, j.newSubExpr(idx, IntConstant.v(1))));
        bodyrem.getUnits().add(j.newGotoStmt(outerLoop));

        bodyrem.getUnits().add(incX);
        bodyrem.getUnits().add(j.newAssignStmt(i, j.newAddExpr(i, IntConstant.v(1))));
        bodyrem.getUnits().add(j.newGotoStmt(loopHeader));

        bodyrem.getUnits().add(remove);

        bodyrem.getUnits().add(beforeEnd);
        bodyrem.getUnits().add(j.newInvokeStmt(j.newSpecialInvokeExpr(del, mCtorList.makeRef(), lclList)));
        ReturnStmt ret = j.newReturnStmt(del);
        bodyrem.getUnits().add(ret);
      }

      {
        SootMethod getList = sc.makeSootMethod(GET_LIST, Collections.emptyList(),
            RefType.v("System.Collections.Generic.List`1"), Modifier.PUBLIC);
        actualDelegateClass.addMethod(getList);
        JimpleBody bodylist = j.newBody(getList);
        getList.setActiveBody(bodylist);
        bodylist.insertIdentityStmts();
        Local lclList = j.newLocal("List", listType);
        bodylist.getLocals().add(lclList);
        bodylist.getUnits()
            .add(j.newAssignStmt(lclList, j.newInstanceFieldRef(bodylist.getThisLocal(), listField.makeRef())));
        bodylist.getUnits().add(j.newReturnStmt(lclList));
      }
      {
        Type retType;
        if (delegateReturn instanceof VoidType) {
          retType = VoidType.v();
        } else {
          retType = sc.getObjectType();
        }
        SootMethod doInvoke = sc.makeSootMethod(INVOKE_METHOD_NAME, delegateParameters, retType, Modifier.PUBLIC);
        SootMethod m = actualDelegateClass.getMethodByName("callSingle");
        actualDelegateClass.addMethod(doInvoke);
        JimpleBody bodyInvoke = j.newBody(doInvoke);
        doInvoke.setActiveBody(bodyInvoke);
        bodyInvoke.insertIdentityStmts();
        Local lclList = j.newLocal("List", listType);
        bodyInvoke.getLocals().add(lclList);
        Local lclRet = null;
        if (!(delegateReturn instanceof VoidType)) {
          lclRet = j.newLocal("RetVal", delegateReturn);
          bodyInvoke.getLocals().add(lclRet);
        }

        bodyInvoke.getUnits()
            .add(j.newAssignStmt(lclList, j.newInstanceFieldRef(bodyInvoke.getThisLocal(), listField.makeRef())));

        Local lclCurrentDelegateHolder = j.newLocal("currentDH", delegateHolderType);
        bodyInvoke.getLocals().add(lclCurrentDelegateHolder);
        Local lclCount = j.newLocal("count", IntType.v());
        bodyInvoke.getLocals().add(lclCount);
        SootMethodRef getCount = getListMethod(IntType.v(), "get_Count");
        bodyInvoke.getUnits().add(j.newAssignStmt(lclCount, j.newVirtualInvokeExpr(lclList, getCount)));
        Local lclIndex = j.newLocal("i", IntType.v());
        bodyInvoke.getLocals().add(lclIndex);
        bodyInvoke.getUnits().add(j.newAssignStmt(lclIndex, IntConstant.v(0)));

        Stmt retS;
        if (lclRet != null) {
          retS = j.newReturnStmt(lclRet);
        } else {
          retS = j.newReturnVoidStmt();
        }

        Stmt backedge = j.newIfStmt(j.newEqExpr(lclIndex, lclCount), retS);
        bodyInvoke.getUnits().add(backedge);
        //loop
        List<Value> parameters = new ArrayList<>();
        Local objFromDelegate = j.newLocal("objFromDelegate", sc.getObjectType());
        bodyInvoke.getLocals().add(objFromDelegate);
        Local instanceFromDelegate = j.newLocal("instance", IntType.v());
        bodyInvoke.getLocals().add(instanceFromDelegate);

        bodyInvoke.getUnits().add(j.newAssignStmt(lclCurrentDelegateHolder,
            j.newVirtualInvokeExpr(lclList, getListMethod(sc.getObjectType(), "get_Item", IntType.v()), lclIndex)));
        bodyInvoke.getUnits()
            .add(j.newAssignStmt(objFromDelegate, j.newInstanceFieldRef(lclCurrentDelegateHolder, instanceField.makeRef())));
        bodyInvoke.getUnits().add(j.newAssignStmt(instanceFromDelegate,
            j.newInstanceFieldRef(lclCurrentDelegateHolder, functionIDField.makeRef())));

        parameters.add(objFromDelegate);
        parameters.add(instanceFromDelegate);
        parameters.addAll(bodyInvoke.getParameterLocals());

        StaticInvokeExpr inv = j.newStaticInvokeExpr(m.makeRef(), parameters);
        if (lclRet == null) {
          bodyInvoke.getUnits().add(j.newInvokeStmt(inv));
        } else {
          bodyInvoke.getUnits().add(j.newAssignStmt(lclRet, inv));
        }
        bodyInvoke.getUnits().add(j.newAssignStmt(lclIndex, j.newAddExpr(lclIndex, IntConstant.v(1))));
        bodyInvoke.getUnits().add(j.newGotoStmt(backedge));

        bodyInvoke.getUnits().add(retS);
      }

      SootMethod toString
          = sc.makeSootMethod("ToString", Collections.emptyList(), RefType.v("System.String"), Modifier.PUBLIC);
      JimpleBody jb = j.newBody(toString);
      toString.setActiveBody(jb);
      actualDelegateClass.addMethod(toString);
      jb.insertIdentityStmts();
      Local list = j.newLocal("list", listType);
      Local res = j.newLocal("res", RefType.v("System.String"));
      jb.getLocals().add(list);
      jb.getLocals().add(res);

      jb.getUnits().add(j.newAssignStmt(list, j.newInstanceFieldRef(jb.getThisLocal(), listField.makeRef())));
      SootMethodRef mr = getListMethod(RefType.v("System.String"), "ToString");
      jb.getUnits().add(j.newAssignStmt(res, j.newVirtualInvokeExpr(list, mr)));
      jb.getUnits().add(j.newReturnStmt(res));
    }

    protected void createSingleConstructor(Scene sc, SootClass actualDelegateClass) {
      Jimple j = Jimple.v();
      //we use a long type as a parameter to distinguish it from the one generate by the .NET compiler
      mCtorSingle
          = sc.makeSootMethod("<init>", Arrays.asList(sc.getObjectType(), LongType.v()), VoidType.v(), Modifier.PUBLIC);

      JimpleBody bodyctorsingle = j.newBody(mCtorSingle);
      mCtorSingle.setActiveBody(bodyctorsingle);
      actualDelegateClass.addMethod(mCtorSingle);
      bodyctorsingle.insertIdentityStmts();
      Local lclList = j.newLocal("List", listType);
      bodyctorsingle.getLocals().add(lclList);
      Local intConverter = j.newLocal("intConverter", IntType.v());
      bodyctorsingle.getLocals().add(intConverter);
      bodyctorsingle.getUnits()
          .add(j.newAssignStmt(intConverter, j.newCastExpr(bodyctorsingle.getParameterLocal(1), IntType.v())));
      bodyctorsingle.getUnits().add(j.newAssignStmt(lclList, j.newNewExpr(listType)));
      bodyctorsingle.getUnits().add(j.newInvokeStmt(j.newSpecialInvokeExpr(lclList, getListMethod(VoidType.v(), "<init>"))));

      Local val = createHolderInstance(bodyctorsingle, bodyctorsingle.getParameterLocal(0), intConverter);

      bodyctorsingle.getUnits().add(
          j.newInvokeStmt(j.newVirtualInvokeExpr(lclList, getListMethod(VoidType.v(), "Add", sc.getObjectType()), val)));
      bodyctorsingle.getUnits()
          .add(j.newAssignStmt(j.newInstanceFieldRef(bodyctorsingle.getThisLocal(), listField.makeRef()), lclList));
      bodyctorsingle.getUnits().add(j.newReturnVoidStmt());
    }

    protected void createListConstructor(Scene sc, SootClass actualDelegateClass) {
      Jimple j = Jimple.v();
      mCtorList = sc.makeSootMethod("<init>", Arrays.asList(listType), VoidType.v(), Modifier.PUBLIC);

      JimpleBody bodyctorlist = j.newBody(mCtorList);
      mCtorList.setActiveBody(bodyctorlist);
      actualDelegateClass.addMethod(mCtorList);
      bodyctorlist.insertIdentityStmts();
      bodyctorlist.getUnits().add(j.newAssignStmt(j.newInstanceFieldRef(bodyctorlist.getThisLocal(), listField.makeRef()),
          bodyctorlist.getParameterLocal(0)));
      bodyctorlist.getUnits().add(j.newReturnVoidStmt());
    }

    private static Local createHolderInstance(JimpleBody jb, Value instance, Value functionid) {
      Jimple j = Jimple.v();
      RefType holderType = RefType.v(DELEGATE_HOLDER_CLASSNAME);
      Local l = j.newLocal("holder", holderType);
      jb.getLocals().add(l);
      jb.getUnits().add(j.newAssignStmt(l, j.newNewExpr(holderType)));
      jb.getUnits().add(j.newInvokeStmt(
          j.newSpecialInvokeExpr(l, holderType.getSootClass().getMethodByName("<init>").makeRef(), instance, functionid)));
      return l;
    }

    public static synchronized SootClass createDelegateInterface(Scene sc) {
      SootClass delegateInterface = sc.getSootClassUnsafe(DELEGATE_INTERFACE_CLASSNAME);
      if (delegateInterface != null) {
        return delegateInterface;
      }
      delegateInterface
          = sc.makeSootClass(DELEGATE_INTERFACE_CLASSNAME, Modifier.PUBLIC | Modifier.INTERFACE | Modifier.ABSTRACT);
      delegateInterface.setApplicationClass();

      SootMethod combineWith = sc.makeSootMethod(COMBINE_WITH_METHOD_NAME, Arrays.asList(delegateInterface.getType()),
          delegateInterface.getType(), Modifier.PUBLIC | Modifier.ABSTRACT);
      delegateInterface.addMethod(combineWith);
      SootMethod removeFrom = sc.makeSootMethod(REMOVE_METHOD_NAME, Arrays.asList(delegateInterface.getType()),
          delegateInterface.getType(), Modifier.PUBLIC | Modifier.ABSTRACT);
      delegateInterface.addMethod(removeFrom);
      SootMethod getList = sc.makeSootMethod(GET_LIST, Collections.emptyList(),
          RefType.v("System.Collections.Generic.List`1"), Modifier.PUBLIC | Modifier.ABSTRACT);
      delegateInterface.addMethod(getList);

      SootMethod combine
          = sc.makeSootMethod(COMBINE, Arrays.asList(delegateInterface.getType(), delegateInterface.getType()),
              delegateInterface.getType(), Modifier.PUBLIC | Modifier.STATIC);
      delegateInterface.addMethod(combine);
      Jimple j = Jimple.v();
      JimpleBody bd = j.newBody(combine);
      combine.setActiveBody(bd);
      bd.insertIdentityStmts();
      UnitPatchingChain uc = bd.getUnits();

      Local l = j.newLocal("retHandler", delegateInterface.getType());
      bd.getLocals().add(l);
      Unit retOne = j.newReturnStmt(bd.getParameterLocal(1));
      Unit retTwo = j.newReturnStmt(bd.getParameterLocal(0));
      uc.add(j.newIfStmt(j.newEqExpr(bd.getParameterLocal(0), NullConstant.v()), retOne));
      uc.add(j.newIfStmt(j.newEqExpr(bd.getParameterLocal(1), NullConstant.v()), retTwo));
      //            assign.setRightOp(j.newInterfaceInvokeExpr((Local) inv.getArg(0), combineMRef, inv.getArg(1)));
      uc.add(j.newAssignStmt(l,
          j.newInterfaceInvokeExpr(bd.getParameterLocal(0), combineWith.makeRef(), bd.getParameterLocal(1))));
      uc.add(j.newReturnStmt(l));
      uc.add(retOne);
      uc.add(retTwo);

      return delegateInterface;
    }

    protected static synchronized SootClass createDelegateHolder(Scene sc) {
      SootClass dh = sc.getSootClassUnsafe(DELEGATE_HOLDER_CLASSNAME);
      if (dh != null) {
        return dh;
      }
      SootClass delegateHolder = sc.makeSootClass(DELEGATE_HOLDER_CLASSNAME, Modifier.PUBLIC);
      delegateHolder.setApplicationClass();

      SootField instanceField = sc.makeSootField(INSTANCE_FIELDNAME, sc.getObjectType(), Modifier.PUBLIC);
      delegateHolder.addField(instanceField);
      SootField functionIDField = sc.makeSootField(FUNCTION_ID_FIELDNAME, IntType.v(), Modifier.PUBLIC);
      delegateHolder.addField(functionIDField);

      SootMethod ctor
          = sc.makeSootMethod("<init>", Arrays.asList(sc.getObjectType(), IntType.v()), VoidType.v(), Modifier.PUBLIC);
      Jimple j = Jimple.v();
      JimpleBody jb = j.newBody(ctor);
      ctor.setActiveBody(jb);
      delegateHolder.addMethod(ctor);
      jb.insertIdentityStmts();
      jb.getUnits()
          .add(j.newAssignStmt(j.newInstanceFieldRef(jb.getThisLocal(), instanceField.makeRef()), jb.getParameterLocal(0)));
      jb.getUnits().add(
          j.newAssignStmt(j.newInstanceFieldRef(jb.getThisLocal(), functionIDField.makeRef()), jb.getParameterLocal(1)));
      jb.getUnits().add(j.newReturnVoidStmt());
      DotnetType.createStructDefaultHashCodeEquals(delegateHolder);

      SootMethod toString
          = sc.makeSootMethod("ToString", Collections.emptyList(), RefType.v("System.String"), Modifier.PUBLIC);
      jb = j.newBody(toString);
      toString.setActiveBody(jb);
      delegateHolder.addMethod(toString);
      jb.insertIdentityStmts();
      Local l1 = j.newLocal("l1", RefType.v("System.Object"));
      Local l2 = j.newLocal("l2", IntType.v());
      Local res = j.newLocal("res", RefType.v("System.String"));
      jb.getLocals().add(l1);
      jb.getLocals().add(l2);
      jb.getLocals().add(res);

      jb.getUnits().add(j.newAssignStmt(l1, j.newInstanceFieldRef(jb.getThisLocal(), instanceField.makeRef())));
      jb.getUnits().add(j.newAssignStmt(l2, j.newInstanceFieldRef(jb.getThisLocal(), functionIDField.makeRef())));
      SootMethodRef mr = sc.makeMethodRef(Scene.v().getSootClass("System.String"), "Concat",
          Arrays.asList(sc.getObjectType(), sc.getObjectType(), sc.getObjectType()), RefType.v("System.String"), true);
      jb.getUnits().add(j.newAssignStmt(res, j.newStaticInvokeExpr(mr, l1, StringConstant.v(" - "), l2)));
      jb.getUnits().add(j.newReturnStmt(res));
      return delegateHolder;
    }

    public synchronized static DelegateInfo getTag(SootClass actualDelegateClass) {
      DelegateInfo i = (DelegateInfo) actualDelegateClass.getTag(DelegateInfo.DELEGATE_NAME);
      if (i == null) {
        i = new DelegateInfo(actualDelegateClass);
        actualDelegateClass.addTag(i);
      }
      return i;
    }

    private Map<FunctionPointerConstant, Integer> signatureToFunctionID = new HashMap<>();

    public synchronized int addDelegateMethod(FunctionPointerConstant fpc) {
      Integer id = signatureToFunctionID.get(fpc);
      if (id != null) {
        return id;
      }
      id = counter.getAndIncrement();
      Jimple j = Jimple.v();
      // parameters to pass on:
      List<Value> args = new ArrayList<>();
      List<Local> locals = callSingleBody.getParameterLocals();
      for (int i = 2; i < locals.size(); i++) {
        args.add(locals.get(i));
      }
      InvokeExpr expr;
      if (fpc.isVirtual()) {
        expr = j.newVirtualInvokeExpr(instanceParameter, fpc.getSootMethodRef(), args);
      } else {
        SootMethod resolved = fpc.getSootMethodRef().resolve();
        if (resolved.isStatic()) {
          expr = j.newStaticInvokeExpr(fpc.getSootMethodRef(), args);
        } else {
          expr = j.newSpecialInvokeExpr(instanceParameter, fpc.getSootMethodRef(), args);
        }
      }

      Stmt call;
      Stmt ret;
      if (delegateReturn instanceof VoidType) {
        call = j.newInvokeStmt(expr);
        ret = j.newReturnVoidStmt();
      } else {
        call = j.newAssignStmt(retLocal, expr);
        ret = j.newReturnStmt(retLocal);
      }

      List<Unit> toAdd = Arrays.asList(call, ret);
      callSingleBody.getUnits().insertBeforeNoRedirect(toAdd, callSingleBody.getUnits().getLast());
      List<UnitBox> boxes = tableSwitch.getUnitBoxes();
      UnitBox box = j.newStmtBox(call);
      boxes.add(boxes.size() - 1, box);
      tableSwitch.getTargetBoxes().add(box);
      tableSwitch.setHighIndex(id);
      signatureToFunctionID.put(fpc, id);
      return id;
    }

    public InvokeStmt getCallToDelegateCreator(Local delegateObj, Value base, int num) {
      Jimple j = Jimple.v();
      return j.newInvokeStmt(j.newSpecialInvokeExpr(delegateObj, mCtorSingle.makeRef(), base, LongConstant.v(num)));
    }

  }

  public static void replaceDelegates(JimpleBody jb) {
    UnitPatchingChain u = jb.getUnits();
    Stmt c = (Stmt) u.getFirst();
    RefType obj = Scene.v().getObjectType();
    FastHierarchy fh = null;
    SootClass delegateClass = Scene.v().getSootClassUnsafe("System.Delegate");
    SootClass multidelegateClass = Scene.v().getSootClassUnsafe("System.MulticastDelegate");
    if (delegateClass == null) {
      return;
    }
    Scene sc = Scene.v();
    Jimple j = Jimple.v();
    while (c != null) {
      Stmt next = (Stmt) u.getSuccOf(c);
      if (c.containsInvokeExpr()) {
        InvokeExpr inv = c.getInvokeExpr();
        if (inv instanceof InstanceInvokeExpr && inv.getMethodRef().getName().equals("Invoke")) {
          SootClass decl = inv.getMethodRef().getDeclaringClass();
          if (fh == null) {
            fh = Scene.v().getOrMakeFastHierarchy();
          }
          if (fh.canStoreClass(decl, delegateClass) || fh.canStoreClass(decl, multidelegateClass)) {
            InstanceInvokeExpr istinv = (InstanceInvokeExpr) inv;
            SootMethodRef invoke = sc.makeMethodRef(decl, INVOKE_METHOD_NAME, inv.getMethodRef().getParameterTypes(),
                inv.getMethodRef().getReturnType(), false);
            c.getInvokeExprBox().setValue(j.newSpecialInvokeExpr((Local) istinv.getBase(), invoke, inv.getArgs()));
          }
        }
        if (inv instanceof SpecialInvokeExpr && inv.getArgCount() == 2) {
          SootMethod m = inv.getMethod();
          if (m.getParameterType(0) == obj && m.isConstructor()) {
            if (fh == null) {
              fh = Scene.v().getOrMakeFastHierarchy();
            }
            if (fh.canStoreClass(m.getDeclaringClass(), delegateClass)
                || fh.canStoreClass(m.getDeclaringClass(), multidelegateClass)) {
              // this is a delegate registration method
              SootClass actualDelegateClass = m.getDeclaringClass();
              DelegateInfo d = DelegateInfo.getTag(actualDelegateClass);

              FunctionPointerConstant fpc = (FunctionPointerConstant) inv.getArg(1);
              int num = d.addDelegateMethod(fpc);
              InstanceInvokeExpr instInvoke = (InstanceInvokeExpr) inv;
              InvokeStmt newInvStatement = d.getCallToDelegateCreator((Local) instInvoke.getBase(), inv.getArg(0), num);
              u.swapWith(c, newInvStatement);
            }
          }
        } else if (inv instanceof StaticInvokeExpr && c instanceof AssignStmt) {
          AssignStmt assign = (AssignStmt) c;
          switch (inv.getMethodRef().getSignature()) {
            case "<System.Delegate: System.Delegate Combine(System.Delegate,System.Delegate)>":
              SootClass delegateInterface = getOrCreateCommonDelegateInterface(sc);
              SootMethodRef combineMRef = delegateInterface.getMethodByName(COMBINE).makeRef();
              assign.setRightOp(j.newStaticInvokeExpr(combineMRef, inv.getArg(0), inv.getArg(1)));
              break;
            case "<System.Delegate: System.Delegate Remove(System.Delegate,System.Delegate)>":
              delegateInterface = getOrCreateCommonDelegateInterface(sc);
              SootMethodRef removeMRef = delegateInterface.getMethodByName(REMOVE_METHOD_NAME).makeRef();
              assign.setRightOp(j.newInterfaceInvokeExpr((Local) inv.getArg(0), removeMRef, inv.getArg(1)));
              break;
          }
        }
      }
      c = next;
    }
  }

  public static SootClass getOrCreateCommonDelegateInterface(Scene scene) {
    SootClass delegateInterface = scene.getSootClassUnsafe(DELEGATE_INTERFACE_CLASSNAME);
    if (delegateInterface == null) {
      delegateInterface = DelegateInfo.createDelegateInterface(scene);
    }
    return delegateInterface;
  }

}
