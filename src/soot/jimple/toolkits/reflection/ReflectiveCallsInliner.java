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
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.ClassConstant;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.NopStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.toolkits.reflection.ReflectionTraceInfo.Kind;
import soot.jimple.toolkits.scalar.CopyPropagator;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.jimple.toolkits.scalar.NopEliminator;
import soot.options.CGOptions;
import soot.options.Options;
import soot.rtlib.DefaultHandler;
import soot.rtlib.IUnexpectedReflectiveCallHandler;
import soot.rtlib.OpaquePredicate;
import soot.rtlib.SootSig;
import soot.rtlib.UnexpectedReflectiveCall;
import soot.util.Chain;
import soot.util.HashChain;

public class ReflectiveCallsInliner extends SceneTransformer {
	private ReflectionTraceInfo RTI;
	private SootMethodRef EQUALS;
	private SootMethodRef CLASS_GET_NAME;
	private SootMethodRef SOOTSIG_CONSTR;
	private SootMethodRef SOOTSIG_METHOD;
	private SootMethodRef UNINTERPRETED_METHOD;
	
	private boolean initialized = false;
	private final boolean useOpaqueFalsePredicate;
	
	/**
	 * @param useOpaqueFalsePredicate If true then the Soot inserts a predicate that always evaluates to
	 * false but which Soot itself will not be able to determine as <i>always false</i> (such predicates are
	 * called opaque). The consequence is that the inlined code will never actually be called.
	 * 
	 * Note that if useOpaqueFalsePredicate is false then the generated code may not work in cases
	 * where the program uses multiple class loaders.
	 */
	public ReflectiveCallsInliner(boolean useOpaqueFalsePredicate) {
		this.useOpaqueFalsePredicate = useOpaqueFalsePredicate;
	}
	
	@Override
	protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
		if(!initialized) {
			CGOptions cgOptions = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
			String logFilePath = cgOptions.reflection_log();
			RTI = new ReflectionTraceInfo(logFilePath);

			SootClass SOOT_SIG_CLASS = Scene.v().getSootClass(SootSig.class.getName());
			SOOT_SIG_CLASS.setApplicationClass();
			EQUALS = Scene.v().makeMethodRef(Scene.v().getSootClass("java.lang.Object"), "equals", Collections.<Type>singletonList(RefType.v("java.lang.Object")), BooleanType.v(), false);
			CLASS_GET_NAME = Scene.v().makeMethodRef(Scene.v().getSootClass("java.lang.Class"), "getName", Collections.<Type>emptyList(), RefType.v("java.lang.String"), false);
			
			RefType constrType = RefType.v("java.lang.reflect.Constructor");
			SOOTSIG_CONSTR = Scene.v().makeMethodRef(SOOT_SIG_CLASS, "sootSignature", Collections.<Type>singletonList(constrType), RefType.v("java.lang.String"), true);
			RefType objectType = RefType.v("java.lang.Object");
			RefType methodType = RefType.v("java.lang.reflect.Method");
			List<Type> paramTypes = Arrays.asList(new Type[] {objectType, methodType});
			SOOTSIG_METHOD = Scene.v().makeMethodRef(SOOT_SIG_CLASS, "sootSignature", paramTypes, RefType.v("java.lang.String"), true);
			
			Scene.v().getSootClass(UnexpectedReflectiveCall.class.getName()).setApplicationClass();
			Scene.v().getSootClass(IUnexpectedReflectiveCallHandler.class.getName()).setApplicationClass();
			Scene.v().getSootClass(DefaultHandler.class.getName()).setApplicationClass();
			Scene.v().getSootClass(OpaquePredicate.class.getName()).setApplicationClass();

			UNINTERPRETED_METHOD = Scene.v().makeMethodRef(Scene.v().getSootClass("soot.rtlib.OpaquePredicate"), "getFalse", Collections.<Type>emptyList(), BooleanType.v(), true);	

			initialized = true;
		}
		for(SootMethod m: RTI.methodsContainingReflectiveCalls()) {
			m.retrieveActiveBody();
			Body b = m.getActiveBody();
			{
				Set<String> classForNameClassNames = RTI.classForNameClassNames(m);
				if(!classForNameClassNames.isEmpty()) {
					inlineRelectiveCalls(m,classForNameClassNames, ReflectionTraceInfo.Kind.ClassForName);
					if(Options.v().validate()) b.validate();
				}
			}{
				Set<String> classNewInstanceClassNames = RTI.classNewInstanceClassNames(m);
				if(!classNewInstanceClassNames.isEmpty()) {
					inlineRelectiveCalls(m,classNewInstanceClassNames, ReflectionTraceInfo.Kind.ClassNewInstance);
					if(Options.v().validate()) b.validate();
				}
			}{
				Set<String> constructorNewInstanceSignatures = RTI.constructorNewInstanceSignatures(m);
				if(!constructorNewInstanceSignatures.isEmpty()) {
					inlineRelectiveCalls(m, constructorNewInstanceSignatures, ReflectionTraceInfo.Kind.ConstructorNewInstance);
					if(Options.v().validate()) b.validate();
				}
			}{
				Set<String> methodInvokeSignatures = RTI.methodInvokeSignatures(m);
				if(!methodInvokeSignatures.isEmpty()) {
					inlineRelectiveCalls(m, methodInvokeSignatures, ReflectionTraceInfo.Kind.MethodInvoke);
					if(Options.v().validate()) b.validate();
				}
			}
			//clean up after us
			DeadAssignmentEliminator.v().transform(b);
			CopyPropagator.v().transform(b);
			NopEliminator.v().transform(b);			
		}
	}

	@SuppressWarnings("unchecked")
	private void inlineRelectiveCalls(SootMethod m, Set<String> targets, Kind callKind) {
		if(!m.hasActiveBody()) m.retrieveActiveBody();
		Body b = m.getActiveBody();
		PatchingChain<Unit> units = b.getUnits();
		Iterator<Unit> iter = units.snapshotIterator();
		LocalGenerator localGen = new LocalGenerator(b);
		
		//for all units
		while(iter.hasNext()) {
			Chain<Unit> newUnits = new HashChain<Unit>();
			Stmt s = (Stmt) iter.next();
			
			Local predLocal = null;
			Local seenLocal = null;
			if(useOpaqueFalsePredicate) {
				predLocal = localGen.generateLocal(BooleanType.v());
				StaticInvokeExpr staticInvokeExpr = Jimple.v().newStaticInvokeExpr(UNINTERPRETED_METHOD);
				newUnits.add(Jimple.v().newAssignStmt(predLocal, staticInvokeExpr));
				seenLocal = localGen.generateLocal(BooleanType.v());
				newUnits.add(Jimple.v().newAssignStmt(seenLocal, IntConstant.v(0)));
			}
			
			//if we have an invoke expression, test to see if it is a reflective invoke expression
			if(s.containsInvokeExpr()) {
				InvokeExpr ie = s.getInvokeExpr();
				Local targetNameLocal = null;
				if(callKind==Kind.ClassForName && ie.getMethodRef().getSignature().equals("<java.lang.Class: java.lang.Class forName(java.lang.String)>")) {
					//on Class.forName(n): targetName = n
					Value classNameValue = ie.getArg(0);
					if(classNameValue instanceof StringConstant) {
						//special case: parameter is a String constant; in that case we just replace the entire expression by a class constant
						StringConstant stringConstant = (StringConstant) classNameValue;
						ValueBox argBox = s.getInvokeExprBox();
						argBox.setValue(ClassConstant.v(stringConstant.value));						
						continue; //we are done already in that case
					} 					
					targetNameLocal = (Local) classNameValue;
				} else if(callKind==Kind.ClassNewInstance && ie.getMethodRef().getSignature().equals("<java.lang.Class: java.lang.Object newInstance()>")) {
					//on clazz.newInstance(): targetName = clazz.getName()
					Local classLocal = (Local) ((InstanceInvokeExpr)ie).getBase();
					VirtualInvokeExpr getNameExpr = Jimple.v().newVirtualInvokeExpr(classLocal, CLASS_GET_NAME);					
					targetNameLocal = localGen.generateLocal(RefType.v("java.lang.String"));					
					AssignStmt assignStmt = Jimple.v().newAssignStmt(targetNameLocal, getNameExpr);
					newUnits.add(assignStmt);
				} else if(callKind==Kind.ConstructorNewInstance && ie.getMethodRef().getSignature().equals("<java.lang.reflect.Constructor: java.lang.Object newInstance(java.lang.Object[])>")) {
					//on constr.newInstance(): targetName = SootSig.sootSignature(constr)
					Local constrLocal = (Local) ((InstanceInvokeExpr)ie).getBase();
					StaticInvokeExpr getNameExpr = Jimple.v().newStaticInvokeExpr(SOOTSIG_CONSTR, constrLocal);
					targetNameLocal = localGen.generateLocal(RefType.v("java.lang.String"));
					AssignStmt assignStmt = Jimple.v().newAssignStmt(targetNameLocal, getNameExpr);
					newUnits.add(assignStmt);
				} else if(callKind==Kind.MethodInvoke && ie.getMethodRef().getSignature().equals("<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>")) {
					//on method.invoke(obj): targetName = SootSig.sootSignature(obj,method)
					Local methodLocal = (Local) ((InstanceInvokeExpr)ie).getBase();
					Value recv = ie.getArg(0);
					StaticInvokeExpr getNameExpr = Jimple.v().newStaticInvokeExpr(SOOTSIG_METHOD, recv, methodLocal);
					targetNameLocal = localGen.generateLocal(RefType.v("java.lang.String"));
					AssignStmt assignStmt = Jimple.v().newAssignStmt(targetNameLocal, getNameExpr);
					newUnits.add(assignStmt);
				}
				
				if(targetNameLocal==null) continue; //if the invoke expression is no reflective call, continue
				
				//for all recorded targets
				NopStmt endLabel = Jimple.v().newNopStmt();
				for(String target : targets) {
					
					//if(!targetName.equals(<target>) goto jumpTarget
					VirtualInvokeExpr equalsCall = Jimple.v().newVirtualInvokeExpr(targetNameLocal,EQUALS,StringConstant.v(target));
					Local resultLocal = localGen.generateLocal(BooleanType.v());
					AssignStmt equalsAssignStmt = Jimple.v().newAssignStmt(resultLocal, equalsCall);						
					newUnits.add(equalsAssignStmt);
					
					NopStmt jumpTarget = Jimple.v().newNopStmt();
					
					IfStmt ifStmt = Jimple.v().newIfStmt(Jimple.v().newEqExpr(IntConstant.v(0), resultLocal), jumpTarget);
					newUnits.add(ifStmt);
					
					
					if(useOpaqueFalsePredicate)  {
						//if we get here then we passed the if check; record this in "seenLocal"
						newUnits.add(Jimple.v().newAssignStmt(seenLocal, IntConstant.v(1)));
						
						//if predLocal == 0 goto <original reflective call>
						newUnits.add(Jimple.v().newIfStmt(Jimple.v().newEqExpr(IntConstant.v(0), predLocal), jumpTarget));
					}
					
					Local freshLocal;
					Value replacement=null;
					Local[] paramLocals=null;
					switch(callKind) {
					case ClassForName: 
					{
						//replace by: <Class constant for <target>>
						freshLocal = localGen.generateLocal(RefType.v("java.lang.Class"));
						replacement = ClassConstant.v(target.replace('.','/'));
						break;
					}
					case ClassNewInstance:
					{
						//replace by: new <target>
						RefType targetType = RefType.v(target);
						freshLocal = localGen.generateLocal(targetType);
						replacement = Jimple.v().newNewExpr(targetType);
						break;
					}
					case ConstructorNewInstance:
					{
						/* replace r=constr.newInstance(args) by:
						 * Object p0 = args[0];
						 * ...
						 * Object pn = args[n];
						 * T0 a0 = (T0)p0;
						 * ...
						 * Tn an = (Tn)pn;
						 */
						SootMethod constructor = Scene.v().getMethod(target);
						Local argsArrayLocal = (Local) s.getInvokeExpr().getArg(0);
						int i=0;
						paramLocals = new Local[constructor.getParameterCount()];
						for(Type paramType: ((Collection<Type>)constructor.getParameterTypes())) {
							paramLocals[i] = localGen.generateLocal(paramType);
							unboxParameter(argsArrayLocal, i, paramLocals, paramType, newUnits, localGen);
							i++;
						}
						RefType targetType = constructor.getDeclaringClass().getType();
						freshLocal = localGen.generateLocal(targetType);
						replacement = Jimple.v().newNewExpr(targetType);
						
						break;
					}
					case MethodInvoke: 
					{
						/* replace r=m.invoke(obj,args) by:
						 * T recv = (T)obj;
						 * Object p0 = args[0];
						 * ...
						 * Object pn = args[n];
						 * T0 a0 = (T0)p0;
						 * ...
						 * Tn an = (Tn)pn;
						 */
						SootMethod method = Scene.v().getMethod(target);
						Value recvObject = ie.getArg(0);
						Local argsArrayLocal = (Local) s.getInvokeExpr().getArg(1);
						int i=0;
						paramLocals = new Local[method.getParameterCount()];
						for(Type paramType: ((Collection<Type>)method.getParameterTypes())) {
							paramLocals[i] = localGen.generateLocal(paramType);
							unboxParameter(argsArrayLocal, i, paramLocals, paramType, newUnits, localGen);							
							i++;
						}
						RefType targetType = method.getDeclaringClass().getType();
						freshLocal = localGen.generateLocal(targetType);						
						replacement = Jimple.v().newCastExpr(recvObject, method.getDeclaringClass().getType());
						
						break;
					}
					default:
						throw new InternalError("Unknown kind of reflective call "+callKind);
					}
					
					AssignStmt replStmt = Jimple.v().newAssignStmt(freshLocal, replacement);
					newUnits.add(replStmt);
					
					switch(callKind) {
					case ClassNewInstance:
					{
						//add: r.<init>()
						SootClass targetClass = Scene.v().getSootClass(target);
						SpecialInvokeExpr constrCallExpr = Jimple.v().newSpecialInvokeExpr(freshLocal, Scene.v().makeMethodRef(targetClass, SootMethod.constructorName, Collections.<Type>emptyList(), VoidType.v(), false));
						InvokeStmt constrCallStmt2 = Jimple.v().newInvokeStmt(constrCallExpr);
						newUnits.add(constrCallStmt2);
						break;
					}
					case ConstructorNewInstance:
					{
						//add: r=<target>(a0,...,an);
						SootMethod constructor = Scene.v().getMethod(target);
						SpecialInvokeExpr constrCallExpr = Jimple.v().newSpecialInvokeExpr(freshLocal, constructor.makeRef(), Arrays.asList(paramLocals));
						InvokeStmt constrCallStmt2 = Jimple.v().newInvokeStmt(constrCallExpr);
						newUnits.add(constrCallStmt2);
						break;
					}
					case MethodInvoke:
						//add: r=recv.<target>(a0,...,an);
						SootMethod method = Scene.v().getMethod(target);
						InvokeExpr invokeExpr;
						if(method.isStatic())
							invokeExpr = Jimple.v().newStaticInvokeExpr(method.makeRef(), Arrays.asList(paramLocals));
						else
							invokeExpr = Jimple.v().newVirtualInvokeExpr(freshLocal, method.makeRef(), Arrays.asList(paramLocals));
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
				
				//

				Unit end = newUnits.getLast();
				units.insertAfter(newUnits, s);
				units.remove(s);
				units.insertAfter(s, end);
				units.insertAfter(endLabel, s);
				
				//insert error-notification code
				switch(callKind) {
					case ClassForName:
					{
						SootMethodRef notifyMethodRef = Scene.v().makeMethodRef(Scene.v().getSootClass(UnexpectedReflectiveCall.class.getName()), "classForName", Collections.<Type>singletonList(RefType.v("java.lang.String")), VoidType.v(), true);
						InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(notifyMethodRef,s.getInvokeExpr().getArg(0)));
						units.insertAfter(invokeStmt, s);
						break;				
					}
					case ClassNewInstance:
					{
						SootMethodRef notifyMethodRef = Scene.v().makeMethodRef(Scene.v().getSootClass(UnexpectedReflectiveCall.class.getName()), "classNewInstance", Collections.<Type>singletonList(RefType.v("java.lang.Class")), VoidType.v(), true);
						InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(notifyMethodRef,((InstanceInvokeExpr)s.getInvokeExpr()).getBase()));
						units.insertAfter(invokeStmt, s);
						break;				
					}
					case ConstructorNewInstance:
					{
						SootMethodRef notifyMethodRef = Scene.v().makeMethodRef(Scene.v().getSootClass(UnexpectedReflectiveCall.class.getName()), "constructorNewInstance", Collections.<Type>singletonList(RefType.v("java.lang.reflect.Constructor")), VoidType.v(), true);
						InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(notifyMethodRef,((InstanceInvokeExpr)s.getInvokeExpr()).getBase()));
						units.insertAfter(invokeStmt, s);
						break;				
					}
					case MethodInvoke:
					{
						RefType objectType = RefType.v("java.lang.Object");
						RefType methodType = RefType.v("java.lang.reflect.Method");
						List<Type> paramTypes = Arrays.asList(new Type[] {objectType, methodType});
						SootMethodRef notifyMethodRef = Scene.v().makeMethodRef(Scene.v().getSootClass(UnexpectedReflectiveCall.class.getName()), "methodInvoke", paramTypes, VoidType.v(), true);
						InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(notifyMethodRef,s.getInvokeExpr().getArg(0),((InstanceInvokeExpr)s.getInvokeExpr()).getBase()));
						units.insertAfter(invokeStmt, s);
						break;				
					}
					default:
						throw new InternalError("Unknown kind of reflective call "+callKind);
				}
				if(useOpaqueFalsePredicate) {
					//if seenLocal skip notification code
					IfStmt ifStmt = Jimple.v().newIfStmt(Jimple.v().newEqExpr(IntConstant.v(1), seenLocal), endLabel);
					units.insertAfter(ifStmt,s);	
				}
			}
		}
	}

	
	/** Auto-unboxes an argument array.
	 * @param argsArrayLocal a local holding the argument Object[] array
	 * @param paramIndex the index of the parameter to unbox
	 * @param paramType the (target) type of the parameter
	 * @param newUnits the Unit chain to which the unboxing code will be appended
	 * @param localGen a {@link LocalGenerator} for the body holding the units
	 */
	private void unboxParameter(Local argsArrayLocal, int paramIndex, Local[] paramLocals, Type paramType, Chain<Unit> newUnits, LocalGenerator localGen) {
		ArrayRef arrayRef = Jimple.v().newArrayRef(argsArrayLocal, IntConstant.v(paramIndex));
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
			assignStmt = Jimple.v().newAssignStmt(paramLocals[paramIndex], unboxInvokeExpr);
		} else {
		    Local boxedLocal = localGen.generateLocal(RefType.v("java.lang.Object"));
		    AssignStmt arrayLoad = Jimple.v().newAssignStmt(boxedLocal, arrayRef);
		    newUnits.add(arrayLoad);
		    Local castedLocal = localGen.generateLocal(paramType);
		    AssignStmt cast = Jimple.v().newAssignStmt(castedLocal, Jimple.v().newCastExpr(boxedLocal, paramType));
		    newUnits.add(cast);
			assignStmt = Jimple.v().newAssignStmt(paramLocals[paramIndex], castedLocal);
		}
		newUnits.add(assignStmt);
	}

}
