/* Soot - a J*va Optimization Framework
 * Copyright (C) 2010 Eric Bodden
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

package soot.jimple.toolkits.reflection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BooleanType;
import soot.Local;
import soot.PatchingChain;
import soot.PhaseOptions;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.javaToJimple.LocalGenerator;
import soot.javaToJimple.toolkits.GotoEliminator;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.ClassConstant;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;
import soot.jimple.NopStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.toolkits.reflection.ReflectionTraceInfo.Kind;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.options.CGOptions;
import soot.rtlib.SootSig;
import soot.util.Chain;
import soot.util.HashChain;

public class ReflectiveCallsInliner extends SceneTransformer {
	private SootClass SOOT_SIG_CLASS;
	private SootMethodRef EQUALS;
	private SootMethodRef ERROR_CONSTRUCTOR;
	private SootMethodRef CLASS_GET_NAME;
	private SootMethodRef SOOTSIG_CONSTR;
	private SootMethodRef SOOTSIG_METHOD;
	
	boolean initialized = false;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void internalTransform(String phaseName, Map options) {
		if(!initialized) {
			SOOT_SIG_CLASS = Scene.v().getSootClass(SootSig.class.getName());
			SOOT_SIG_CLASS.setApplicationClass();
			EQUALS = Scene.v().makeMethodRef(Scene.v().getSootClass("java.lang.Object"), "equals", Collections.<Type>singletonList(RefType.v("java.lang.Object")), BooleanType.v(), false);
			ERROR_CONSTRUCTOR = Scene.v().makeMethodRef(Scene.v().getSootClass("java.lang.Error"), SootMethod.constructorName, Collections.<Type>singletonList(RefType.v("java.lang.String")), VoidType.v(), false);
			CLASS_GET_NAME = Scene.v().makeMethodRef(Scene.v().getSootClass("java.lang.Class"), "getName", Collections.<Type>emptyList(), RefType.v("java.lang.String"), false);
			
			RefType constrType = RefType.v("java.lang.reflect.Constructor");
			SOOTSIG_CONSTR = Scene.v().makeMethodRef(SOOT_SIG_CLASS, "sootSignature", Collections.<Type>singletonList(constrType), RefType.v("java.lang.String"), true);
			RefType objectType = RefType.v("java.lang.Object");
			RefType methodType = RefType.v("java.lang.reflect.Method");
			List<Type> paramTypes = Arrays.asList(new Type[] {objectType, methodType});
			SOOTSIG_METHOD = Scene.v().makeMethodRef(SOOT_SIG_CLASS, "sootSignature", paramTypes, RefType.v("java.lang.String"), true);

			initialized = true;
		}
		CGOptions cgOptions = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
		String logFilePath = cgOptions.reflection_log();
		ReflectionTraceInfo rti = new ReflectionTraceInfo(logFilePath);
		for(SootMethod m: rti.methodsContainingReflectiveCalls()) {
			{
				Set<String> classForNameClassNames = rti.classForNameClassNames(m);
				if(!classForNameClassNames.isEmpty()) {
					m.retrieveActiveBody();
					inlineRelectiveCalls(m.getActiveBody(),classForNameClassNames, ReflectionTraceInfo.Kind.ClassForName);
					m.getActiveBody().validate();
				}
			}{
				Set<String> classNewInstanceClassNames = rti.classNewInstanceClassNames(m);
				if(!classNewInstanceClassNames.isEmpty()) {
					m.retrieveActiveBody();
					inlineRelectiveCalls(m.getActiveBody(),classNewInstanceClassNames, ReflectionTraceInfo.Kind.ClassNewInstance);
					m.getActiveBody().validate();
				}
			}{
				Set<String> constructorNewInstanceSignatures = rti.constructorNewInstanceSignatures(m);
				if(!constructorNewInstanceSignatures.isEmpty()) {
					m.retrieveActiveBody();
					inlineRelectiveCalls(m.getActiveBody(), constructorNewInstanceSignatures, ReflectionTraceInfo.Kind.ConstructorNewInstance);
					m.getActiveBody().validate();
				}
			}{
				Set<String> methodInvokeSignatures = rti.methodInvokeSignatures(m);
				if(!methodInvokeSignatures.isEmpty()) {
					m.retrieveActiveBody();
					inlineRelectiveCalls(m.getActiveBody(), methodInvokeSignatures, ReflectionTraceInfo.Kind.MethodInvoke);
					m.getActiveBody().validate();
				}
			}
			DeadAssignmentEliminator.v().transform(m.getActiveBody());
			System.err.println(m.getActiveBody());
		}
	}

	/*	Replaces "c = Class.forName(arg)" by:
	 * 
	 *  if(arg.equals("C1")) goto l1
	 *  if(arg.equals("C2")) goto l2
	 *  throw new Error (arg)
	 *  l1: c = C1.class;
	 *      goto E;
	 *  l2: c = C2.class;
	 *  	goto E;
	 *  E;
	 */
	private void inlineRelectiveCalls(Body b, Set<String> targets, Kind callKind) {
		PatchingChain<Unit> units = b.getUnits();
		Iterator<Unit> iter = units.snapshotIterator();
		LocalGenerator localGen = new LocalGenerator(b);
		while(iter.hasNext()) {
			Chain<Unit> newUnits = new HashChain<Unit>();
			Stmt s = (Stmt) iter.next();
			if(s.containsInvokeExpr()) {
				InvokeExpr ie = s.getInvokeExpr();
				Local targetNameLocal = null;
				if(callKind==Kind.ClassForName && ie.getMethodRef().getSignature().equals("<java.lang.Class: java.lang.Class forName(java.lang.String)>")) {
					Value classNameValue = ie.getArg(0);
					if(classNameValue instanceof StringConstant) {
						StringConstant stringConstant = (StringConstant) classNameValue;
						ValueBox argBox = s.getInvokeExprBox();
						argBox.setValue(ClassConstant.v(stringConstant.value));						
						continue; //we are done already in that case
					} 
					
					targetNameLocal = (Local) classNameValue;
				} else if(callKind==Kind.ClassNewInstance && ie.getMethodRef().getSignature().equals("<java.lang.Class: java.lang.Object newInstance()>")) {
					Local classLocal = (Local) ((InstanceInvokeExpr)ie).getBase();
					VirtualInvokeExpr getNameExpr = Jimple.v().newVirtualInvokeExpr(classLocal, CLASS_GET_NAME);					
					targetNameLocal = localGen.generateLocal(RefType.v("java.lang.String"));					
					AssignStmt assignStmt = Jimple.v().newAssignStmt(targetNameLocal, getNameExpr);
					newUnits.add(assignStmt);
				} else if(callKind==Kind.ConstructorNewInstance && ie.getMethodRef().getSignature().equals("<java.lang.reflect.Constructor: java.lang.Object newInstance(java.lang.Object[])>")) {
					Local constrLocal = (Local) ((InstanceInvokeExpr)ie).getBase();
					StaticInvokeExpr getNameExpr = Jimple.v().newStaticInvokeExpr(SOOTSIG_CONSTR, constrLocal);
					targetNameLocal = localGen.generateLocal(RefType.v("java.lang.String"));
					AssignStmt assignStmt = Jimple.v().newAssignStmt(targetNameLocal, getNameExpr);
					newUnits.add(assignStmt);
				} else if(callKind==Kind.MethodInvoke && ie.getMethodRef().getSignature().equals("<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>")) {
					Local constrLocal = (Local) ((InstanceInvokeExpr)ie).getBase();
					Local recvLocal = (Local) ie.getArg(0);
					StaticInvokeExpr getNameExpr = Jimple.v().newStaticInvokeExpr(SOOTSIG_METHOD, recvLocal, constrLocal);
					targetNameLocal = localGen.generateLocal(RefType.v("java.lang.String"));
					AssignStmt assignStmt = Jimple.v().newAssignStmt(targetNameLocal, getNameExpr);
					newUnits.add(assignStmt);
				}
				
				if(targetNameLocal==null) continue; //if the invoke expression is no reflective call, continue
				
				NopStmt endLabel = Jimple.v().newNopStmt();
				for(String target : targets) {
					VirtualInvokeExpr equalsCall = Jimple.v().newVirtualInvokeExpr(targetNameLocal,EQUALS,StringConstant.v(target));
					Local resultLocal = localGen.generateLocal(BooleanType.v());
					AssignStmt equalsAssignStmt = Jimple.v().newAssignStmt(resultLocal, equalsCall);						
					newUnits.add(equalsAssignStmt);
					
					NopStmt jumpTarget = Jimple.v().newNopStmt();
					
					IfStmt ifStmt = Jimple.v().newIfStmt(Jimple.v().newEqExpr(IntConstant.v(0), resultLocal), jumpTarget);
					newUnits.add(ifStmt);
					
					Local freshLocal;
					Value replacement=null;
					Local[] paramLocals=null;
					Local recvLocal = null;
					switch(callKind) {
					case ClassForName: 
					{
						freshLocal = localGen.generateLocal(RefType.v("java.lang.Class"));
						replacement = ClassConstant.v(target.replace('.','/'));
						break;
					}
					case ClassNewInstance:
					{
						RefType targetType = RefType.v(target);
						freshLocal = localGen.generateLocal(targetType);
						replacement = Jimple.v().newNewExpr(targetType);
						break;
					}
					case ConstructorNewInstance:
					{
						SootMethod constructor = Scene.v().getMethod(target);
						Local argsArrayLocal = (Local) s.getInvokeExpr().getArg(0);
						int i=0;
						paramLocals = new Local[constructor.getParameterCount()];
						for(Type paramType: ((Collection<Type>)constructor.getParameterTypes())) {
							paramLocals[i] = localGen.generateLocal(paramType);
							ArrayRef arrayRef = Jimple.v().newArrayRef(argsArrayLocal, IntConstant.v(i));
							AssignStmt assignStmt;
							if(paramType instanceof PrimType) {
							    PrimType primType = (PrimType) paramType;
								// Unbox the value if needed
							    RefType boxedType = primType.boxedType();
								SootMethodRef ref = Scene.v().makeMethodRef(
							      boxedType.getSootClass(),
							      paramType + "Value",
							      Collections.<Type>emptyList(),
							      paramType,
							      false
							    );
							    Local boxedLocal = localGen.generateLocal(RefType.v("java.lang.Object"));
							    AssignStmt arrayLoad = Jimple.v().newAssignStmt(boxedLocal, arrayRef);
							    newUnits.add(arrayLoad);
							    Local castedLocal = localGen.generateLocal(boxedType);
							    AssignStmt cast = Jimple.v().newAssignStmt(castedLocal, Jimple.v().newCastExpr(boxedLocal, boxedType));
							    newUnits.add(cast);
							    VirtualInvokeExpr unboxInvokeExpr = Jimple.v().newVirtualInvokeExpr(castedLocal,ref);
								assignStmt = Jimple.v().newAssignStmt(paramLocals[i], unboxInvokeExpr);
							} else {
								assignStmt = Jimple.v().newAssignStmt(paramLocals[i], arrayRef);
							}
							newUnits.add(assignStmt);
							
							i++;
						}
						RefType targetType = constructor.getDeclaringClass().getType();
						freshLocal = localGen.generateLocal(targetType);
						replacement = Jimple.v().newNewExpr(targetType);
						
						break;
					}
					case MethodInvoke: 
					{
						SootMethod method = Scene.v().getMethod(target);
						Local recvObjectLocal = (Local) ie.getArg(0);
						Local argsArrayLocal = (Local) s.getInvokeExpr().getArg(1);
						int i=0;
						paramLocals = new Local[method.getParameterCount()];
						for(Type paramType: ((Collection<Type>)method.getParameterTypes())) {
							paramLocals[i] = localGen.generateLocal(paramType);
							ArrayRef arrayRef = Jimple.v().newArrayRef(argsArrayLocal, IntConstant.v(i));
							AssignStmt assignStmt;
							if(paramType instanceof PrimType) {
							    PrimType primType = (PrimType) paramType;
								// Unbox the value if needed
							    RefType boxedType = primType.boxedType();
								SootMethodRef ref = Scene.v().makeMethodRef(
							      boxedType.getSootClass(),
							      paramType + "Value",
							      Collections.<Type>emptyList(),
							      paramType,
							      false
							    );
							    Local boxedLocal = localGen.generateLocal(RefType.v("java.lang.Object"));
							    AssignStmt arrayLoad = Jimple.v().newAssignStmt(boxedLocal, arrayRef);
							    newUnits.add(arrayLoad);
							    Local castedLocal = localGen.generateLocal(boxedType);
							    AssignStmt cast = Jimple.v().newAssignStmt(castedLocal, Jimple.v().newCastExpr(boxedLocal, boxedType));
							    newUnits.add(cast);
							    VirtualInvokeExpr unboxInvokeExpr = Jimple.v().newVirtualInvokeExpr(castedLocal,ref);
								assignStmt = Jimple.v().newAssignStmt(paramLocals[i], unboxInvokeExpr);
							} else {
								assignStmt = Jimple.v().newAssignStmt(paramLocals[i], arrayRef);
							}
							newUnits.add(assignStmt);
							
							i++;
						}
						RefType targetType = method.getDeclaringClass().getType();
						freshLocal = localGen.generateLocal(targetType);
//						replacement = Jimple.v().newNewExpr(targetType);
						
						replacement = Jimple.v().newCastExpr(recvObjectLocal, method.getDeclaringClass().getType());
//						recvLocal = localGen.generateLocal(method.getDeclaringClass().getType());
//						AssignStmt recvCastStmt = Jimple.v().newAssignStmt(recvLocal, castRecv);
//						newUnits.add(recvCastStmt);						
						
						break;
					}
					default:
						throw new InternalError();
					}
					
					AssignStmt replStmt = Jimple.v().newAssignStmt(freshLocal, replacement);
					newUnits.add(replStmt);
					
					switch(callKind) {
					case ClassNewInstance:
					{
						SootClass targetClass = Scene.v().getSootClass(target);
						SpecialInvokeExpr constrCallExpr = Jimple.v().newSpecialInvokeExpr(freshLocal, Scene.v().makeMethodRef(targetClass, SootMethod.constructorName, Collections.<Type>emptyList(), VoidType.v(), false));
						InvokeStmt constrCallStmt2 = Jimple.v().newInvokeStmt(constrCallExpr);
						newUnits.add(constrCallStmt2);
						break;
					}
					case ConstructorNewInstance:
					{
						SootMethod constructor = Scene.v().getMethod(target);
						SpecialInvokeExpr constrCallExpr = Jimple.v().newSpecialInvokeExpr(freshLocal, constructor.makeRef(), Arrays.asList(paramLocals));
						InvokeStmt constrCallStmt2 = Jimple.v().newInvokeStmt(constrCallExpr);
						newUnits.add(constrCallStmt2);
						break;
					}
					case MethodInvoke:
						SootMethod method = Scene.v().getMethod(target);
						VirtualInvokeExpr invokeExpr = Jimple.v().newVirtualInvokeExpr(freshLocal, method.makeRef(), Arrays.asList(paramLocals));
						InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(invokeExpr);
						newUnits.add(invokeStmt);
						break;
					} 
					
					if(s instanceof AssignStmt) {
						AssignStmt assignStmt = (AssignStmt) s;
						Value leftOp = assignStmt.getLeftOp();
						AssignStmt newAssignStmt = Jimple.v().newAssignStmt(leftOp, freshLocal);
						newUnits.add(newAssignStmt);
					}
						
					GotoStmt gotoStmt = Jimple.v().newGotoStmt(endLabel);
					newUnits.add(gotoStmt);
					
					newUnits.add(jumpTarget);
				}
				
				Local exceptionLocal = localGen.generateLocal(RefType.v("java.lang.Error"));
				NewExpr newExpr = Jimple.v().newNewExpr(RefType.v("java.lang.Error"));
				AssignStmt newExceptionStmt = Jimple.v().newAssignStmt(exceptionLocal, newExpr);
				newUnits.add(newExceptionStmt);
				
				SpecialInvokeExpr constrCall = Jimple.v().newSpecialInvokeExpr(exceptionLocal, ERROR_CONSTRUCTOR, targetNameLocal);
				InvokeStmt constrCallStmt = Jimple.v().newInvokeStmt(constrCall);
				newUnits.add(constrCallStmt);
				
				ThrowStmt throwStmt = Jimple.v().newThrowStmt(exceptionLocal);
				newUnits.add(throwStmt);

				newUnits.add(endLabel);
				
				units.insertAfter(newUnits, s);

				units.remove(s);
			}
		}
	}

}
