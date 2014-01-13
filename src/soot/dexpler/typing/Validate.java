package soot.dexpler.typing; 

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethodRef;
import soot.SootResolver;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.dexpler.Debug;
import soot.dexpler.IDalvikTyper;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityRef;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.NewArrayExpr;
import soot.jimple.StringConstant;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.jimple.toolkits.scalar.NopEliminator;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SmartLocalDefs;
import soot.toolkits.scalar.UnusedLocalEliminator;

public class Validate {

    public static void validateArrays(Body b) {
        
        Set<DefinitionStmt> definitions = new HashSet<DefinitionStmt>();
        Set<Unit> unitWithArrayRef = new HashSet<Unit>();

        
        for (Unit u: b.getUnits()) {
            if (u instanceof DefinitionStmt) {
                DefinitionStmt s = (DefinitionStmt)u;
                definitions.add(s);
            }
            List<ValueBox> uses = u.getUseBoxes();
            for (ValueBox vb: uses) {
                Value v = vb.getValue();
                if (v instanceof ArrayRef) {
                    unitWithArrayRef.add(u);
                }
            }
        }
        
        final ExceptionalUnitGraph g = new ExceptionalUnitGraph(b);
        final SmartLocalDefs localDefs = new SmartLocalDefs(g, new SimpleLiveLocals(g));
        
        Set<Unit> toReplace = new HashSet<Unit>();
        
        for (Unit u: unitWithArrayRef) {
            boolean ok = false;
            Debug.printDbg(IDalvikTyper.DEBUG, "handling unit: "+ u);
            List<ValueBox> uses = u.getUseBoxes();
            Debug.printDbg(IDalvikTyper.DEBUG,"uses size: "+ uses.size());
            for (ValueBox vb: uses) {
                Debug.printDbg(IDalvikTyper.DEBUG,"vb use: "+ vb +" class: "+ vb.getClass());
                Value v = vb.getValue();
                if (v instanceof ArrayRef) {
                    ArrayRef ar = (ArrayRef)v;
                    Local base = (Local) ar.getBase();
                    List<Unit> defs = localDefs.getDefsOfAt(base, u);
                    
                    // add aliases
                    Set<Unit> alreadyHandled = new HashSet<Unit>();
                    while (true) {
                        boolean isMore = false;
                        for (Unit d: defs) {
                            if (alreadyHandled.contains(d))
                                continue;
                            if (d instanceof AssignStmt) {
                                AssignStmt ass = (AssignStmt)d;
                                Value r = ass.getRightOp();
                                if (r instanceof Local) {
                                    defs.addAll(localDefs.getDefsOfAt((Local)r, d));
                                    alreadyHandled.add(d);
                                    isMore = true;
                                    break;
                                } else if (r instanceof ArrayRef) {
                                    ArrayRef arrayRef = (ArrayRef)r;
                                    Local l = (Local) arrayRef.getBase();
                                    defs.addAll(localDefs.getDefsOfAt(l, d));
                                    alreadyHandled.add(d);
                                    isMore = true;
                                    break;
                                }
                            }
                        }
                        if (!isMore)
                            break;
                    }
                    
                    //System.out.println("def size "+ defs.size());
                    for (Unit def: defs) {
                        //System.out.println("def u "+ def);
                        Value r = null;
                        if (def instanceof IdentityStmt) {
                            IdentityStmt idstmt = (IdentityStmt)def;
                            r = idstmt.getRightOp();
                        } else if (def instanceof AssignStmt) {
                            AssignStmt assStmt = (AssignStmt)def;
                            r = assStmt.getRightOp();
                        } else {
                            throw new RuntimeException("error: definition statement not an IdentityStmt nor an AssignStmt! "+ def);
                        }
                        
                        Type t = null;
                        if (r instanceof InvokeExpr) {
                            InvokeExpr ie = (InvokeExpr)r;
                            t = ie.getType();
                            //System.out.println("ie type: "+ t +" "+ t.getClass());
                            if (t instanceof ArrayType)
                                ok = true;
                        } else if (r instanceof FieldRef) {
                            FieldRef ref = (FieldRef)r;
                            t = ref.getType();
                            //System.out.println("fr type: "+ t +" "+ t.getClass());
                            if (t instanceof ArrayType)
                                ok = true;
                        } else if (r instanceof IdentityRef) {
                            IdentityRef ir = (IdentityRef)r;
                            t = ir.getType();
                            if (t instanceof ArrayType)
                                ok = true;
                        } else if (r instanceof CastExpr) {
                            CastExpr c = (CastExpr)r;
                            t = c.getType();
                            if (t instanceof ArrayType)
                                ok = true;
                        } else if (r instanceof ArrayRef) {
                            // we also check that this arrayref is correctly defined
                        } else if (r instanceof NewArrayExpr) {
                            ok = true;
                        } else if (r instanceof Local) {
                            
                        } else if (r instanceof Constant) {
                            
                        } else {
                            throw new RuntimeException("error: unknown right hand side of definition stmt "+ def );
                        }
                        
                        if (ok)
                            break;
                      
                    }
                    
                    if (ok)
                        break;
                }
            }
            
            if (!ok) {
                Debug.printDbg(IDalvikTyper.DEBUG, "warning: no valid defs for local used for array: "+ u +" replacing with throw exception instruction...");
                toReplace.add(u);
            }
        }
        
        int i = 0;
        for (Unit u: toReplace) {
            System.out.println("warning: incorrect array def, replacing unit "+ u);
            // new object
            RefType throwableType = RefType.v("java.lang.Throwable");
            Local ttt = Jimple.v().newLocal("ttt_"+ ++i, throwableType);
            b.getLocals().add(ttt);
            Value r = Jimple.v().newNewExpr(throwableType);
            Unit initLocalUnit = Jimple.v().newAssignStmt(ttt, r);
                  
            // call <init> method with a string parameter for message
            List<String> pTypes = new ArrayList<String>();
            pTypes.add("java.lang.String");
            boolean isStatic = false;
            SootMethodRef mRef = Validate.makeMethodRef("java.lang.Throwable", "<init>", "", pTypes, isStatic);
            List<Value> parameters = new ArrayList<Value>();
            parameters.add(StringConstant.v("Soot updated this instruction"));
            InvokeExpr ie = Jimple.v().newSpecialInvokeExpr(ttt, mRef, parameters);
            Unit initMethod = Jimple.v().newInvokeStmt(ie);
            
            // throw exception
            Unit newUnit = Jimple.v().newThrowStmt(ttt);
            
            // change instruction in body
            b.getUnits().swapWith(u, newUnit);
            b.getUnits().insertBefore(initMethod, newUnit);
            b.getUnits().insertBefore(initLocalUnit, initMethod);
            //Exception a = throw new Exception();
        }
        
        DeadAssignmentEliminator.v().transform(b);
        UnusedLocalEliminator.v().transform(b);
        NopEliminator.v().transform(b);
        UnreachableCodeEliminator.v().transform(b);
            
    }
    
    public static SootMethodRef makeMethodRef(String cName, String mName, String rType, List<String> pTypes, boolean isStatic) {
        SootClass sc = SootResolver.v().makeClassRef(cName);
        Type returnType = null;
        if (rType == "")
            returnType = VoidType.v();
        else
            returnType = RefType.v(rType);
        List<Type> parameterTypes = new ArrayList<Type>();
        for (String p: pTypes)
            parameterTypes.add(RefType.v(p));
        return Scene.v().makeMethodRef(sc, mName, parameterTypes, returnType, isStatic);
    }
}
