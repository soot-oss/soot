package soot.javaToJimple;

import java.util.*;
import soot.*;

public class PolyglotMethodSource implements MethodSource {

    private polyglot.ast.Block block;
    private List formals;
    private ArrayList fieldInits;
    private ArrayList staticFieldInits;
    private ArrayList initializerBlocks;
    private ArrayList staticInitializerBlocks;
    private soot.Local outerClassThisInit;
    private HashMap privateAccessMap;
    private HashMap localClassMap;
    private HashMap anonClassMap;
    private boolean hasAssert = false;
    
    public PolyglotMethodSource(){
        this.block = null;
        this.formals = null;
    }
    
    public PolyglotMethodSource(polyglot.ast.Block block, List formals){
        this.block = block;
        this.formals = formals;
    }

    public soot.Body getBody(soot.SootMethod sm, String phaseName) {
        JimpleBodyBuilder jbb = new JimpleBodyBuilder();
        soot.jimple.JimpleBody jb = jbb.createJimpleBody(block, formals, sm);
       
        PackManager.v().getTransform("jb.ne").apply(jb);
        PackManager.v().getPack("jb").apply(jb);
        return jb;
    }

    public void setFieldInits(ArrayList fieldInits){
        this.fieldInits = fieldInits;
    }
    
    public void setStaticFieldInits(ArrayList staticFieldInits){
        this.staticFieldInits = staticFieldInits;
    }

    public ArrayList getFieldInits() {
        return fieldInits;
    }
    
    public ArrayList getStaticFieldInits() {
        return staticFieldInits;
    }

    public void setStaticInitializerBlocks(ArrayList staticInits) {
        staticInitializerBlocks = staticInits;
    }
    
    public void setInitializerBlocks(ArrayList inits) {
        initializerBlocks = inits;
    }

    public ArrayList getStaticInitializerBlocks() {
        return staticInitializerBlocks;
    }
    
    public ArrayList getInitializerBlocks() {
        return initializerBlocks;
    }
    
    public void setOuterClassThisInit(soot.Local l) {
        outerClassThisInit = l;
    }

    public soot.Local getOuterClassThisInit(){
        return outerClassThisInit;
    }

    public void setPrivateAccessMap(HashMap map){
        privateAccessMap = map;
    }

    public HashMap getPrivateAccessMap() {
        return privateAccessMap;
    }

    public void setLocalClassMap(HashMap map) {
        localClassMap = map;
    }

    public HashMap getLocalClassMap(){
        return localClassMap;
    }
    
    public void setAnonClassMap(HashMap map) {
        anonClassMap = map;
    }

    public HashMap getAnonClassMap(){
        return anonClassMap;
    }

    public boolean hasAssert(){
        return hasAssert;
    }

    public void hasAssert(boolean val){
        hasAssert = val;
    }

    public void addAssertInits(soot.Body body){
    
        //System.out.println("needed assert method");
        // field ref
        soot.SootField field = body.getMethod().getDeclaringClass().getField("class$"+body.getMethod().getDeclaringClass().getName(), soot.RefType.v("java.lang.Class"));

        soot.Local fieldLocal = soot.jimple.Jimple.v().newLocal("$r0", soot.RefType.v("java.lang.Class"));

        body.getLocals().add(fieldLocal);
        
        soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newStaticFieldRef(field);
        
        soot.jimple.AssignStmt fieldAssignStmt = soot.jimple.Jimple.v().newAssignStmt(fieldLocal, fieldRef);

        body.getUnits().add(fieldAssignStmt);

        // if field not null
        soot.jimple.ConditionExpr cond = soot.jimple.Jimple.v().newNeExpr(fieldLocal, soot.jimple.NullConstant.v());
        
        soot.jimple.NopStmt nop1 = soot.jimple.Jimple.v().newNopStmt();

        soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(cond, nop1);
        body.getUnits().add(ifStmt);

        // if alternative
        soot.Local invokeLocal = soot.jimple.Jimple.v().newLocal("$r1", soot.RefType.v("java.lang.Class"));

        body.getLocals().add(invokeLocal);
        
        ArrayList paramTypes = new ArrayList();
        paramTypes.add(soot.RefType.v("java.lang.String"));
                
        soot.SootMethod methodToInvoke = body.getMethod().getDeclaringClass().getMethod("class$", paramTypes, soot.RefType.v("java.lang.Class"));

        ArrayList params = new ArrayList();
        params.add(soot.jimple.StringConstant.v(body.getMethod().getDeclaringClass().getName()));
        soot.jimple.StaticInvokeExpr invoke = soot.jimple.Jimple.v().newStaticInvokeExpr(methodToInvoke, params);
        //soot.jimple.InvokeStmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(invoke);
        soot.jimple.AssignStmt invokeAssign = soot.jimple.Jimple.v().newAssignStmt(invokeLocal, invoke);
        
        body.getUnits().add(invokeAssign);

        // field ref assign
        soot.jimple.AssignStmt fieldRefAssign = soot.jimple.Jimple.v().newAssignStmt(fieldRef, invokeLocal);

        body.getUnits().add(fieldRefAssign);

        soot.jimple.NopStmt nop2 = soot.jimple.Jimple.v().newNopStmt();

        soot.jimple.GotoStmt goto1 = soot.jimple.Jimple.v().newGotoStmt(nop2);

        body.getUnits().add(goto1);
        // add nop1 - and if consequence
        body.getUnits().add(nop1);

        soot.jimple.AssignStmt fieldRefAssign2 = soot.jimple.Jimple.v().newAssignStmt(invokeLocal, fieldRef);

        body.getUnits().add(fieldRefAssign2);

        body.getUnits().add(nop2);

        // boolean tests
        soot.Local boolLocal1 = soot.jimple.Jimple.v().newLocal("$z0", soot.BooleanType.v());
        body.getLocals().add(boolLocal1);
        soot.Local boolLocal2 = soot.jimple.Jimple.v().newLocal("$z1", soot.BooleanType.v());
        body.getLocals().add(boolLocal2);

        // virtual invoke
        soot.SootMethod vMethodToInvoke = soot.Scene.v().getSootClass("java.lang.Class").getMethod("desiredAssertionStatus", new ArrayList(), soot.BooleanType.v());
        soot.jimple.VirtualInvokeExpr vInvoke = soot.jimple.Jimple.v().newVirtualInvokeExpr(invokeLocal, vMethodToInvoke, new ArrayList());

        //soot.jimple.InvokeStmt vInvokeStmt = soot.jimple.Jimple.v().newInvokeStmt(vInvoke);
        
        soot.jimple.AssignStmt testAssign = soot.jimple.Jimple.v().newAssignStmt(boolLocal1, vInvoke);

        body.getUnits().add(testAssign);
        
        // if
        soot.jimple.ConditionExpr cond2 = soot.jimple.Jimple.v().newNeExpr(boolLocal1, soot.jimple.IntConstant.v(0));

        soot.jimple.NopStmt nop3 = soot.jimple.Jimple.v().newNopStmt();
        
        soot.jimple.IfStmt ifStmt2 = soot.jimple.Jimple.v().newIfStmt(cond2, nop3);
        body.getUnits().add(ifStmt2);

        // alternative
        soot.jimple.AssignStmt altAssign = soot.jimple.Jimple.v().newAssignStmt(boolLocal2, soot.jimple.IntConstant.v(1));

        body.getUnits().add(altAssign);

        soot.jimple.NopStmt nop4 = soot.jimple.Jimple.v().newNopStmt();

        soot.jimple.GotoStmt goto2 = soot.jimple.Jimple.v().newGotoStmt(nop4);

        body.getUnits().add(goto2);

        body.getUnits().add(nop3);
        
        soot.jimple.AssignStmt conAssign = soot.jimple.Jimple.v().newAssignStmt(boolLocal2, soot.jimple.IntConstant.v(0));

        body.getUnits().add(conAssign);

        body.getUnits().add(nop4);
        
        // field assign
        soot.SootField fieldD = body.getMethod().getDeclaringClass().getField("$assertionsDisabled", soot.BooleanType.v());

        soot.jimple.FieldRef fieldRefD = soot.jimple.Jimple.v().newStaticFieldRef(fieldD);
        soot.jimple.AssignStmt fAssign = soot.jimple.Jimple.v().newAssignStmt(fieldRefD, boolLocal2);
        body.getUnits().add(fAssign);
        
    }
    
}
