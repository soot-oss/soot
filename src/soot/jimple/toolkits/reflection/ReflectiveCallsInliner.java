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

public class ReflectiveCallsInliner extends SceneTransformer {
	private SootMethodRef EQUALS;
	private SootMethodRef ERROR_CONSTRUCTOR;
	private SootMethodRef CLASS_GET_NAME;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void internalTransform(String phaseName, Map options) {
		EQUALS = Scene.v().makeMethodRef(Scene.v().getSootClass("java.lang.Object"), "equals", Collections.<Type>singletonList(RefType.v("java.lang.Object")), BooleanType.v(), false);
		ERROR_CONSTRUCTOR = Scene.v().makeMethodRef(Scene.v().getSootClass("java.lang.Error"), SootMethod.constructorName, Collections.<Type>singletonList(RefType.v("java.lang.String")), VoidType.v(), false);
		CLASS_GET_NAME = Scene.v().makeMethodRef(Scene.v().getSootClass("java.lang.Class"), "getName", Collections.<Type>emptyList(), RefType.v("java.lang.String"), false);
		CGOptions cgOptions = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
		String logFilePath = cgOptions.reflection_log();
		ReflectionTraceInfo rti = new ReflectionTraceInfo(logFilePath);
		for(SootMethod m: rti.methodsContainingReflectiveCalls()) {
			Set<SootClass> classForNameClasses = rti.classForNameClasses(m);
			if(!classForNameClasses.isEmpty()) {
				m.retrieveActiveBody();
				inlineRelectiveCallsToClasses(m.getActiveBody(),classForNameClasses, ReflectionTraceInfo.Kind.ClassForName);
				m.getActiveBody().validate();
			}
			Set<SootClass> classNewInstanceClasses = rti.classNewInstanceClasses(m);
			if(!classNewInstanceClasses.isEmpty()) {
				m.retrieveActiveBody();
				inlineRelectiveCallsToClasses(m.getActiveBody(),classNewInstanceClasses, ReflectionTraceInfo.Kind.ClassNewInstance);
				m.getActiveBody().validate();
			}
			Set<SootMethod> constructorNewInstanceConstructors = rti.constructorNewInstanceConstructors(m);
			if(!constructorNewInstanceConstructors.isEmpty()) {
				m.retrieveActiveBody();
				inlineRelectiveCallsToMethods(m.getActiveBody(), constructorNewInstanceConstructors, ReflectionTraceInfo.Kind.ConstructorNewInstance);
				m.getActiveBody().validate();
			}
		}
	}

	private void inlineRelectiveCallsToMethods(Body activeBody, Set<SootMethod> constructorNewInstanceConstructors, Kind kind) {
		for (SootMethod constr : constructorNewInstanceConstructors) {
			//TODO must compare method or constructor signature to the one used by Soot;
			//too bad they don't have the same format
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
	private void inlineRelectiveCallsToClasses(Body b, Set<SootClass> targetClasses, Kind callKind) {
		PatchingChain<Unit> units = b.getUnits();
		Iterator<Unit> iter = units.snapshotIterator();
		LocalGenerator localGen = new LocalGenerator(b);
		while(iter.hasNext()) {
			Stmt s = (Stmt) iter.next();
			if(s.containsInvokeExpr()) {
				InvokeExpr ie = s.getInvokeExpr();
				Local targetNameLocal = null;
				Unit insertionPoint = s;
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
					units.insertAfter(assignStmt, insertionPoint);
					insertionPoint = assignStmt;
				}
				
				if(targetNameLocal==null) continue; //if the invoke expression is no reflective call, continue
				
				NopStmt endLabel = Jimple.v().newNopStmt();
				for(SootClass sc : targetClasses) {
					VirtualInvokeExpr equalsCall = Jimple.v().newVirtualInvokeExpr(targetNameLocal,EQUALS,StringConstant.v(sc.getName()));
					Local resultLocal = localGen.generateLocal(BooleanType.v());
					AssignStmt equalsAssignStmt = Jimple.v().newAssignStmt(resultLocal, equalsCall);						
					units.insertAfter(equalsAssignStmt, insertionPoint);
					insertionPoint = equalsAssignStmt;
					
					NopStmt jumpTarget = Jimple.v().newNopStmt();
					
					IfStmt ifStmt = Jimple.v().newIfStmt(Jimple.v().newEqExpr(IntConstant.v(0), resultLocal), jumpTarget);
					units.insertAfter(ifStmt, insertionPoint);
					insertionPoint = ifStmt;
					
					Local freshLocal;
					Value replacement=null;
					if(callKind==Kind.ClassForName) {
						freshLocal = localGen.generateLocal(RefType.v("java.lang.Class"));
						replacement = ClassConstant.v(sc.getName().replace('.','/'));
					} else if(callKind==Kind.ClassNewInstance) {
						freshLocal = localGen.generateLocal(sc.getType());
						replacement = Jimple.v().newNewExpr(RefType.v(sc));
					} else throw new InternalError();
					AssignStmt replStmt = Jimple.v().newAssignStmt(freshLocal, replacement);
					units.insertAfter(replStmt, insertionPoint);
					insertionPoint = replStmt;
					
					if(callKind==Kind.ClassNewInstance) {
						SpecialInvokeExpr constrCallExpr = Jimple.v().newSpecialInvokeExpr(freshLocal, Scene.v().makeMethodRef(sc, SootMethod.constructorName, Collections.<Type>emptyList(), VoidType.v(), false));
						InvokeStmt constrCallStmt2 = Jimple.v().newInvokeStmt(constrCallExpr);
						units.insertAfter(constrCallStmt2, insertionPoint);
						insertionPoint = constrCallStmt2;
					}
					
					if(s instanceof AssignStmt) {
						AssignStmt assignStmt = (AssignStmt) s;
						Value leftOp = assignStmt.getLeftOp();
						AssignStmt newAssignStmt = Jimple.v().newAssignStmt(leftOp, freshLocal);
						units.insertAfter(newAssignStmt, insertionPoint);
						insertionPoint = newAssignStmt;
					}
						
					GotoStmt gotoStmt = Jimple.v().newGotoStmt(endLabel);
					units.insertAfter(gotoStmt, insertionPoint);
					insertionPoint = gotoStmt;
					
					units.insertAfter(jumpTarget, insertionPoint);
					insertionPoint = jumpTarget;
				}
				
				Local exceptionLocal = localGen.generateLocal(RefType.v("java.lang.Error"));
				NewExpr newExpr = Jimple.v().newNewExpr(RefType.v("java.lang.Error"));
				AssignStmt newExceptionStmt = Jimple.v().newAssignStmt(exceptionLocal, newExpr);
				units.insertAfter(newExceptionStmt, insertionPoint);
				insertionPoint = newExceptionStmt;
				
				SpecialInvokeExpr constrCall = Jimple.v().newSpecialInvokeExpr(exceptionLocal, ERROR_CONSTRUCTOR, targetNameLocal);
				InvokeStmt constrCallStmt = Jimple.v().newInvokeStmt(constrCall);
				units.insertAfter(constrCallStmt, insertionPoint);
				insertionPoint = constrCallStmt;
				
				ThrowStmt throwStmt = Jimple.v().newThrowStmt(exceptionLocal);
				units.insertAfter(throwStmt, insertionPoint);
				insertionPoint = throwStmt;

				units.insertAfter(endLabel, insertionPoint);

				units.remove(s);
			}
		}
	}

}
