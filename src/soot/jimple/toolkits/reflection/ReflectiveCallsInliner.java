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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BooleanType;
import soot.Local;
import soot.PatchingChain;
import soot.PhaseOptions;
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
import soot.jimple.AssignStmt;
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
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.toolkits.reflection.ReflectionTraceInfo.Kind;
import soot.options.CGOptions;
import soot.rtlib.SootSig;
import soot.util.Chain;
import soot.util.HashChain;

public class ReflectiveCallsInliner extends SceneTransformer {
	private SootClass SOOT_SIG_CLASS;
	private SootMethodRef EQUALS;
	private SootMethodRef ERROR_CONSTRUCTOR;
	private SootMethodRef CLASS_GET_NAME;
	
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
			initialized = true;
		}
		CGOptions cgOptions = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
		String logFilePath = cgOptions.reflection_log();
		ReflectionTraceInfo rti = new ReflectionTraceInfo(logFilePath);
		for(SootMethod m: rti.methodsContainingReflectiveCalls()) {
			Set<String> classForNameClassNames = rti.classForNameClassNames(m);
			if(!classForNameClassNames.isEmpty()) {
				m.retrieveActiveBody();
				inlineRelectiveCalls(m.getActiveBody(),classForNameClassNames, ReflectionTraceInfo.Kind.ClassForName);
				m.getActiveBody().validate();
			}
			Set<String> classNewInstanceClassNames = rti.classNewInstanceClassNames(m);
			if(!classNewInstanceClassNames.isEmpty()) {
				m.retrieveActiveBody();
				inlineRelectiveCalls(m.getActiveBody(),classNewInstanceClassNames, ReflectionTraceInfo.Kind.ClassNewInstance);
				m.getActiveBody().validate();
			}
			Set<String> constructorNewInstanceSignatures = rti.constructorNewInstanceSignatures(m);
			if(!constructorNewInstanceSignatures.isEmpty()) {
				m.retrieveActiveBody();
				inlineRelectiveCalls(m.getActiveBody(), constructorNewInstanceSignatures, ReflectionTraceInfo.Kind.ConstructorNewInstance);
				m.getActiveBody().validate();
			}
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
					if(callKind==Kind.ClassForName) {
						freshLocal = localGen.generateLocal(RefType.v("java.lang.Class"));
						replacement = ClassConstant.v(target.replace('.','/'));
					} else if(callKind==Kind.ClassNewInstance) {
						RefType targetType = RefType.v(target);
						freshLocal = localGen.generateLocal(targetType);
						replacement = Jimple.v().newNewExpr(targetType);
					} else throw new InternalError();
					AssignStmt replStmt = Jimple.v().newAssignStmt(freshLocal, replacement);
					newUnits.add(replStmt);
					
					if(callKind==Kind.ClassNewInstance) {
						SootClass targetClass = Scene.v().getSootClass(target);
						SpecialInvokeExpr constrCallExpr = Jimple.v().newSpecialInvokeExpr(freshLocal, Scene.v().makeMethodRef(targetClass, SootMethod.constructorName, Collections.<Type>emptyList(), VoidType.v(), false));
						InvokeStmt constrCallStmt2 = Jimple.v().newInvokeStmt(constrCallExpr);
						newUnits.add(constrCallStmt2);
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
