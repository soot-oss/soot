

package soot.javaToJimple;

import java.util.*;

public class JimpleBodyBuilder {
    
    soot.jimple.JimpleBody body;
    ArrayList exceptionTable;
    Stack endControlNoop = new Stack();
    Stack condControlNoop = new Stack();
    
    HashMap labelBreakMap; //= new HashMap();
    HashMap labelContinueMap;// = new HashMap();
    HashMap localsMap = new HashMap();    
    
    soot.Local specialThisLocal;
    soot.Local outerClassParamLocal;
    
    /**
     * Jimple Body Creation
     */
    public soot.jimple.JimpleBody createJimpleBody(polyglot.ast.Block block, List formals, soot.SootMethod sootMethod){
     
        System.out.println("Creating body: "+sootMethod.getName());
        if (block != null){
            //System.out.println("Block: "+block+" len: "+block.toString().length());
            //Util.addPosTag(body, block.position());
        }
        createBody(sootMethod);
        
        // create this formal except for static methods
        if (!soot.Modifier.isStatic(sootMethod.getModifiers())) {

            soot.RefType type = sootMethod.getDeclaringClass().getType();
            specialThisLocal = soot.jimple.Jimple.v().newLocal("this", type);
            body.getLocals().add(specialThisLocal);
                                                
            soot.jimple.ThisRef thisRef = soot.jimple.Jimple.v().newThisRef(type);
            
            soot.jimple.Stmt thisStmt = soot.jimple.Jimple.v().newIdentityStmt(specialThisLocal, thisRef);
            body.getUnits().add(thisStmt);
            ////System.out.println(thisStmt); 
            // **** this is weird because theres no this in the original source
            Util.addLineTag(thisStmt, block);
        }
        
        // handle formals
        int formalsCounter = 0;
        if (formals != null) {
            Iterator formalsIt = formals.iterator();
            while (formalsIt.hasNext()) { 
                polyglot.ast.Formal formal = (polyglot.ast.Formal)formalsIt.next();
                createFormal(formal, formalsCounter);
                formalsCounter++;
            }
        }
        
        //create outer class this param ref for inner classes
        int outerIndex = sootMethod.getDeclaringClass().getName().indexOf("$");
        if ((outerIndex != -1) && (sootMethod.getName().equals("<init>"))){
            String outerClassName = sootMethod.getDeclaringClass().getName().substring(0, outerIndex);
            soot.SootClass outerClass = soot.Scene.v().getSootClass(outerClassName);
            soot.Local outerLocal = generateLocal(outerClass.getType());
            
            soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(outerClass.getType(), formalsCounter);
            soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(outerLocal, paramRef);
            body.getUnits().add(stmt);
            
            ((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).setOuterClassThisInit(outerLocal);
            outerClassParamLocal = outerLocal;
        }

        
        createBlock(block);

        
        // if method is <clinit> handle static field inits
        if (sootMethod.getName().equals("<clinit>")){
    
            handleStaticFieldInits(sootMethod);
            handleStaticInitializerBlocks(sootMethod);
        }
       
        // determine if body has a return stmt
        boolean hasReturn = false;
        /*ReturnStmtChecker rsc = new ReturnStmtChecker();
        if (block != null){
            block.visit(rsc);
        }
        if (rsc.hasRet()){
            hasReturn = true;
        }*/

        soot.Type retType = sootMethod.getReturnType();
		if (block != null) {
            Iterator it = block.statements().iterator();
		    while (it.hasNext()){
			    Object next = it.next();
			    if (next instanceof polyglot.ast.Return){
                    hasReturn = true; 
                }
            }
        }
		
        // only do this if noexplicit return
	    if ((!hasReturn) && (retType instanceof soot.VoidType)) {
            soot.jimple.Stmt retStmt = soot.jimple.Jimple.v().newReturnVoidStmt();
            body.getUnits().add(retStmt);
            ////System.out.println(retStmt); 
            // **** this is weird because theres no ret in the original source
            //Util.addLineTag(retStmt, block);
        }

        // add exceptions from exceptionTable
        if (exceptionTable != null) {
            Iterator trapsIt = exceptionTable.iterator();
            while (trapsIt.hasNext()){
                body.getTraps().add((soot.Trap)trapsIt.next());
            }
        }
        return body;
    
    }

    private void handleFieldInits(soot.SootMethod sootMethod) {
            
        ArrayList fieldInits = ((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).getFieldInits();
        if (fieldInits != null) {
            Iterator fieldInitsIt = fieldInits.iterator();
            while (fieldInitsIt.hasNext()) {
                polyglot.ast.FieldDecl field = (polyglot.ast.FieldDecl)fieldInitsIt.next();
                String fieldName = field.name();
                polyglot.ast.Expr initExpr = field.init();
                soot.SootClass currentClass = body.getMethod().getDeclaringClass();
                soot.SootField sootField = currentClass.getField(fieldName, Util.getSootType(field.type().type()));
                soot.Local base = specialThisLocal;
                    
                soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(base, sootField);
                //System.out.println("fieldRef: "+fieldRef);
                
                soot.Value sootExpr;
                if (initExpr instanceof polyglot.ast.ArrayInit) {
                    sootExpr = getArrayInitLocal((polyglot.ast.ArrayInit)initExpr, field.type().type());
                }
                else {
                    sootExpr = createExpr(initExpr);
                }
                //System.out.println("sootExpr: "+sootExpr);
                soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(fieldRef, sootExpr);
                //System.out.println("assign: "+assign);
                body.getUnits().add(assign);
                Util.addLineTag(assign, initExpr);

            }
        }
        
    }

    private void handleOuterClassThisInit(soot.SootMethod sootMethod) {
        soot.Local local = ((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).getOuterClassThisInit();
        if (local != null) {
            soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(specialThisLocal, body.getMethod().getDeclaringClass().getField("this$0", local.getType()));
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(fieldRef, local);
            body.getUnits().add(stmt);
        }
    }
    
    private void handleStaticFieldInits(soot.SootMethod sootMethod) {
            
        ArrayList staticFieldInits = ((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).getStaticFieldInits();
        if (staticFieldInits != null) {
            Iterator staticFieldInitsIt = staticFieldInits.iterator();
            while (staticFieldInitsIt.hasNext()) {
                polyglot.ast.FieldDecl field = (polyglot.ast.FieldDecl)staticFieldInitsIt.next();
                String fieldName = field.name();
                polyglot.ast.Expr initExpr = field.init();
                soot.SootClass currentClass = body.getMethod().getDeclaringClass();
                soot.SootField sootField = currentClass.getField(fieldName, Util.getSootType(field.type().type()));
                soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newStaticFieldRef(sootField);

                soot.Value sootExpr;
                if (initExpr instanceof polyglot.ast.ArrayInit) {
                    sootExpr = getArrayInitLocal((polyglot.ast.ArrayInit)initExpr, field.type().type());
                }
                else {
                    sootExpr = createExpr(initExpr);
                }

                soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(fieldRef, sootExpr);

                body.getUnits().add(assign);
            }
        }
    }

    private void handleInitializerBlocks(soot.SootMethod sootMethod) {
        ArrayList initializerBlocks = ((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).getInitializerBlocks();

        if (initializerBlocks != null) {
        
            Iterator initBlocksIt = initializerBlocks.iterator();
            while (initBlocksIt.hasNext()) {
                createBlock((polyglot.ast.Block)initBlocksIt.next());
            }
        }
    }
    
    private void handleStaticInitializerBlocks(soot.SootMethod sootMethod) {
        ArrayList staticInitializerBlocks = ((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).getStaticInitializerBlocks();

        if (staticInitializerBlocks != null) {
        
            Iterator staticInitBlocksIt = staticInitializerBlocks.iterator();
            while (staticInitBlocksIt.hasNext()) {
                createBlock((polyglot.ast.Block)staticInitBlocksIt.next());
            }
        }
    }
    
    private void createBody(soot.SootMethod sootMethod) {
		body = soot.jimple.Jimple.v().newBody(sootMethod);
		sootMethod.setActiveBody(body);
		
	}
	

    /**
     * Block creation
     */
    private void createBlock(polyglot.ast.Block block){
        
        if (block == null) return;
        
		// handle stmts
		Iterator it = block.statements().iterator();
		while (it.hasNext()){
			Object next = it.next();
            if (next instanceof polyglot.ast.Stmt){
				createStmt((polyglot.ast.Stmt)next);
			}
			else {
                System.out.println(next.getClass().toString());
				throw new RuntimeException("Unexpected - Unhandled Node");
			}
		}
    }
    
    /**
     * Catch Formal creation - method parameters
     */
    private soot.Local createCatchFormal(polyglot.ast.Formal formal){

        soot.Type sootType = Util.getSootType(formal.type().type());
        soot.Local formalLocal = createLocal(formal.localInstance());
        soot.jimple.CaughtExceptionRef exceptRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(formalLocal, exceptRef);
        body.getUnits().add(stmt);
        ////System.out.println(stmt); 
        // **** this is weird because the formals actually come from the 
        // method signaturere in the source and don't have there own line
        //Util.addLineTag(stmt, formal);

        return formalLocal;
    }
        
    /**
     * Formal creation - method parameters
     */
    private void createFormal(polyglot.ast.Formal formal, int counter){

        soot.Type sootType = Util.getSootType(formal.type().type());
        soot.Local formalLocal = createLocal(formal.localInstance());
        soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(sootType, counter);
        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(formalLocal, paramRef);
        body.getUnits().add(stmt);
        ////System.out.println(stmt);

        
        // **** this is weird because the formals actually come from the 
        // method signaturere in the source and don't have there own line
        Util.addLineTag(stmt, formal);
        
    }

    /**
     * Literal Creation
     */
    private soot.jimple.Constant createLiteral(polyglot.ast.Lit lit) {
		if (lit instanceof polyglot.ast.IntLit) {
			polyglot.ast.IntLit intLit = (polyglot.ast.IntLit)lit;
			long litValue = intLit.value();
			if (intLit.kind() == polyglot.ast.IntLit.INT) {
				return soot.jimple.IntConstant.v((int)litValue);		
			}
			else {
                /*String longString = (new Long(litValue)).toString();
                //System.out.println(longString);
                if ((longString.indexOf("L") == -1) && (longString.indexOf("l") == -1)) {
                    longString = longString+"L";
                    litValue = (new Long(longString)).longValue();
                }*/
				return soot.jimple.LongConstant.v(litValue);
			}
		}
		else if (lit instanceof polyglot.ast.StringLit) {
            //System.out.println("String lit found: "+lit);
			String litValue = ((polyglot.ast.StringLit)lit).value();
			return soot.jimple.StringConstant.v(litValue);		
		}
		else if (lit instanceof polyglot.ast.NullLit) {
			return soot.jimple.NullConstant.v();		
		}
		else if (lit instanceof polyglot.ast.FloatLit) {
			polyglot.ast.FloatLit floatLit = (polyglot.ast.FloatLit)lit;
			double litValue = floatLit.value();
            //System.out.println("Float Lit value: "+litValue);
			if (floatLit.kind() == polyglot.ast.FloatLit.DOUBLE) {
				return soot.jimple.DoubleConstant.v(floatLit.value());		
			}
			else {
				return soot.jimple.FloatConstant.v((float)(floatLit.value()));		
			}
		}
		else if (lit instanceof polyglot.ast.CharLit) {
			char litValue = ((polyglot.ast.CharLit)lit).value();
            return soot.jimple.IntConstant.v((int)litValue);
		}
		else if (lit instanceof polyglot.ast.BooleanLit) {
			boolean litValue = ((polyglot.ast.BooleanLit)lit).value();
            if (litValue) return soot.jimple.IntConstant.v(1);
            else return soot.jimple.IntConstant.v(0);
		}
		else {
            throw new RuntimeException("Unknown Literal - Unhandled");
        }
    }
    
    /**
     * Local Creation
     */
   
    // this should be used for polyglot locals and formals
    private soot.Local createLocal(polyglot.types.LocalInstance localInst) {
        soot.Type sootType = Util.getSootType(localInst.type());
        String name = localInst.name();
        soot.Local sootLocal = createLocal(name, sootType);
        
        localsMap.put(localInst, sootLocal);
        return sootLocal;
    }
    
    // this should be used for generated locals only
    private soot.Local createLocal(String name, soot.Type sootType) {
        if (sootType instanceof soot.CharType) {
            sootType = soot.IntType.v();
        }
        soot.Local sootLocal = soot.jimple.Jimple.v().newLocal(name, sootType);
        body.getLocals().add(sootLocal);
		return sootLocal;
	}

    /**
     * Local Retreival
     */
    private soot.Local getLocal(polyglot.ast.Local local) {

        soot.Local sootLocal = (soot.Local)localsMap.get(local.localInstance());
        return sootLocal;
    }
    
    /**
     * Stmt creation
     */
    private void createStmt(polyglot.ast.Stmt stmt) {
        if (stmt instanceof polyglot.ast.Eval) {
			createExpr(((polyglot.ast.Eval)stmt).expr());  
        }
        else if (stmt instanceof polyglot.ast.If) {
           createIf((polyglot.ast.If)stmt);
        }
		else if (stmt instanceof polyglot.ast.LocalDecl) {
			createLocalDecl((polyglot.ast.LocalDecl)stmt);
		}
		else if (stmt instanceof polyglot.ast.Block) {
			createBlock((polyglot.ast.Block)stmt);
		}
		else if (stmt instanceof polyglot.ast.While) {
		    createWhile((polyglot.ast.While)stmt);
		}
		else if (stmt instanceof polyglot.ast.Do) {
			createDo((polyglot.ast.Do)stmt);
		}
		else if (stmt instanceof polyglot.ast.For) {
		    createForLoop((polyglot.ast.For)stmt);
		}
		else if (stmt instanceof polyglot.ast.Switch) {
			createSwitch((polyglot.ast.Switch)stmt);
		}
		else if (stmt instanceof polyglot.ast.Return) {
			createReturn((polyglot.ast.Return)stmt);
		}
		else if (stmt instanceof polyglot.ast.Branch) {
			createBranch((polyglot.ast.Branch)stmt);
		}
		else if (stmt instanceof polyglot.ast.ConstructorCall) {
			createConstructorCall((polyglot.ast.ConstructorCall)stmt);
		}
		else if (stmt instanceof polyglot.ast.Empty) {
		    // do nothing empty stmt
        }
		/*else if (stmt instanceof polyglot.ast.Case) {
			CaseTranslator.createDoStmt((polyglot.ast.Case)stmt, body);
		}*/
		else if (stmt instanceof polyglot.ast.Throw) {
			createThrow((polyglot.ast.Throw)stmt);
		}
		else if (stmt instanceof polyglot.ast.Try) {
			createTry((polyglot.ast.Try)stmt);
		}
		else if (stmt instanceof polyglot.ast.Labeled) {
			createLabeled((polyglot.ast.Labeled)stmt);
		}
		else if (stmt instanceof polyglot.ast.Synchronized) {
			createSynchronized((polyglot.ast.Synchronized)stmt);
		}
		else if (stmt instanceof polyglot.ast.Assert) {
			createAssert((polyglot.ast.Assert)stmt);
		}
        else if (stmt instanceof polyglot.ast.LocalClassDecl) {
            createLocalClassDecl((polyglot.ast.LocalClassDecl)stmt);
        }
        else {
            System.out.println("Unhandled Stmt: "+stmt.getClass().toString());
            throw new RuntimeException("Unhandled Stmt");
        }
    }
    
    /**
     * If Stmts Creation - only add line-number tags to if (the other
     * stmts needing tags are created elsewhere
     */
    private void createIf(polyglot.ast.If ifExpr){

        // handle cond 
        polyglot.ast.Expr condition = ifExpr.cond();
        soot.Value sootCond = createExpr(condition); 
        if (!(sootCond instanceof soot.jimple.ConditionExpr)) {
            sootCond = soot.jimple.Jimple.v().newEqExpr(sootCond, soot.jimple.IntConstant.v(0));
        }
        else {
            sootCond = reverseCondition((soot.jimple.ConditionExpr)sootCond);
            sootCond = handleDFLCond((soot.jimple.ConditionExpr)sootCond);
        }
       
        // add if
		soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
		soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(sootCond, noop1);
        
		body.getUnits().add(ifStmt);
        Util.addLineTag(ifStmt, ifExpr);
		
        // add consequence
		polyglot.ast.Stmt consequence = ifExpr.consequent();
        createStmt(consequence);
		
        soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
        soot.jimple.Stmt goto1 = soot.jimple.Jimple.v().newGotoStmt(noop2);
		body.getUnits().add(goto1);
      
        body.getUnits().add(noop1);
        
        // handle alternative
        polyglot.ast.Stmt alternative = ifExpr.alternative();
		if (alternative != null){
			createStmt(alternative);
        }
		
		body.getUnits().add(noop2);

        Util.addPosTag(ifStmt.getConditionBox(), condition.position());

    }
    
    /**
     * While Stmts Creation
     */
    private void createWhile(polyglot.ast.While whileStmt){

        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();

        // these are for break and continue
        endControlNoop.push(soot.jimple.Jimple.v().newNopStmt());
        condControlNoop.push(soot.jimple.Jimple.v().newNopStmt());
        
        // handle body
        
        soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
        soot.jimple.Stmt goto1 = soot.jimple.Jimple.v().newGotoStmt(noop2);
        body.getUnits().add(goto1);
        body.getUnits().add(noop1);
        createStmt(whileStmt.body());

        body.getUnits().add(noop2);
        
        // handle cond
        body.getUnits().add((soot.jimple.Stmt)(condControlNoop.pop()));
        
        polyglot.ast.Expr condition = whileStmt.cond();
        soot.Value sootCond = createExpr(condition);
        if (!(sootCond instanceof soot.jimple.ConditionExpr)) {
            sootCond = soot.jimple.Jimple.v().newNeExpr(sootCond, soot.jimple.IntConstant.v(0));
        }
        else {
            sootCond = handleDFLCond((soot.jimple.ConditionExpr)sootCond);
        }
        soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(sootCond, noop1);
        
        body.getUnits().add(ifStmt);

        body.getUnits().add((soot.jimple.Stmt)(endControlNoop.pop()));
        Util.addLineTag(ifStmt, whileStmt.cond());
        
        Util.addPosTag(ifStmt.getConditionBox(), condition.position());
    }
    
    /**
     * DoWhile Stmts Creation
     */
    private void createDo(polyglot.ast.Do doStmt){
   
        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(noop1);
        
        // these are for break and continue
        endControlNoop.push(soot.jimple.Jimple.v().newNopStmt());
        condControlNoop.push(soot.jimple.Jimple.v().newNopStmt());
        
        // handle body
        createStmt(doStmt.body());
                
        // handle cond
        body.getUnits().add((soot.jimple.Stmt)(condControlNoop.pop()));
        
        polyglot.ast.Expr condition = doStmt.cond();
        soot.Value sootCond = createExpr(condition);
        if (!(sootCond instanceof soot.jimple.ConditionExpr)) {
            sootCond = soot.jimple.Jimple.v().newNeExpr(sootCond, soot.jimple.IntConstant.v(0));
        }
        else {
            sootCond = handleDFLCond((soot.jimple.ConditionExpr)sootCond);
        }
        //soot.jimple.ConditionExpr condExpr = soot.jimple.Jimple.v().newNeExpr(sootCond, soot.jimple.IntConstant.v(0));
        //soot.jimple.Stmt ifStmt = soot.jimple.Jimple.v().newIfStmt(condExpr, noop1);
        soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(sootCond, noop1);
        body.getUnits().add(ifStmt);
        
        body.getUnits().add((soot.jimple.Stmt)(endControlNoop.pop()));
        Util.addLineTag(ifStmt, doStmt.cond());    
        
        Util.addPosTag(ifStmt.getConditionBox(), condition.position());
    }
    
    /**
     * For Loop Stmts Creation
     */
    private void createForLoop(polyglot.ast.For forStmt){
        
        // these ()are for break and continue
        endControlNoop.push(soot.jimple.Jimple.v().newNopStmt());
        condControlNoop.push(soot.jimple.Jimple.v().newNopStmt());
        
        // handle for inits
        Iterator initsIt = forStmt.inits().iterator();
        while (initsIt.hasNext()){
            createStmt((polyglot.ast.Stmt)initsIt.next());
        }
        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
        
        // handle body
        soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
        soot.jimple.Stmt goto1 = soot.jimple.Jimple.v().newGotoStmt(noop2);
        body.getUnits().add(goto1);
        body.getUnits().add(noop1);
        createStmt(forStmt.body());
        
        // handle continue
        body.getUnits().add((soot.jimple.Stmt)(condControlNoop.pop()));

        // handle iters
        Iterator itersIt = forStmt.iters().iterator();
        while (itersIt.hasNext()){
            createStmt((polyglot.ast.Stmt)itersIt.next());
        }
        body.getUnits().add(noop2);
        
        // handle cond
        //body.getUnits().add((soot.jimple.Stmt)(condControlNoop.pop()));
        
        polyglot.ast.Expr condition = forStmt.cond();
        if (condition != null) {
            //System.out.println("condition type: "+condition.type());
            soot.Value sootCond = createExpr(condition);
            if (!(sootCond instanceof soot.jimple.ConditionExpr)) {
                sootCond = soot.jimple.Jimple.v().newNeExpr(sootCond, soot.jimple.IntConstant.v(0));
            }
            else {
                sootCond = handleDFLCond((soot.jimple.ConditionExpr)sootCond);
            }
            //soot.jimple.Stmt ifStmt = soot.jimple.Jimple.v().newIfStmt(condExpr, noop1);
            soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(sootCond, noop1);
        
            // add cond
            body.getUnits().add(ifStmt);
        
            Util.addLineTag(ifStmt, forStmt.cond());
            Util.addPosTag(ifStmt.getConditionBox(), condition.position());
        }
        else {
            soot.jimple.Stmt goto2 = soot.jimple.Jimple.v().newGotoStmt(noop1);
            body.getUnits().add(goto2);
           
        }
        
        body.getUnits().add((soot.jimple.Stmt)(endControlNoop.pop()));
        
    }
    
    /**
     * Local Decl Creation
     */
    private void createLocalDecl(polyglot.ast.LocalDecl localDecl) {
        //System.out.println("Local Decl: "+localDecl);
        String name = localDecl.name();
        //System.out.println("Local Decl: name: "+name);
        polyglot.types.LocalInstance localInst = localDecl.localInstance();
        soot.Value lhs = createLocal(localInst);
        //System.out.println("Local Decl: local: "+lhs);
        polyglot.ast.Expr expr = localDecl.init();
        if (expr != null) {
            //System.out.println("Local Decl expr: "+expr);
            soot.Value rhs;;
            if (expr instanceof polyglot.ast.ArrayInit){
                rhs = getArrayInitLocal((polyglot.ast.ArrayInit)expr, localInst.type());
            }
            else {
                rhs = createExpr(expr);
            }
            if (rhs instanceof soot.jimple.ConditionExpr) {
                rhs = handleCondBinExpr((soot.jimple.ConditionExpr)rhs);
            }
		    soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(lhs, rhs);
           //stmt.addTag(new soot.tagkit.LineNumberTag(localDecl.position().line()));
           body.getUnits().add(stmt);
           Util.addLineTag(stmt, localDecl);
           Util.addPosTag(stmt.getLeftOpBox(), localDecl.position().line(), localDecl.position().column()+localDecl.toString().indexOf(' ')+1);
           if (isLitOrLocal(expr)) {
		        Util.addPosTag(stmt.getRightOpBox(), expr.position());
           }
        }
    }
    
    /**
     * Switch Stmts Creation
     */
    private void createSwitch(polyglot.ast.Switch switchStmt) {
        
        Iterator it = switchStmt.elements().iterator();
        while (it.hasNext()){
            Object next = it.next();
            //System.out.println("Switch Element: "+next.getClass().toString()+" value: "+next);
        }

        polyglot.ast.Expr value = switchStmt.expr();
        soot.Value sootValue = createExpr(value);
        soot.jimple.Stmt defaultTarget = null;
     
        /*int [] pairs = new int[switchstmt.elements().size()];
        int counter = 0;
        it = switchStmt.elements().iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof polyglot.ast.Case) {
                pairs[counter] = 1;
            }
            else {
                pairs[counter] = 0;
            }
            counter++;
        }
        
        HashMap cToSbMap = new HashMap();
        counter = 0;
        it = switchStmt.elements().iterator();
        while (it.hasNext()) {
            Object cNext = it.next();
            if (pairs[counter] == 1) {
                if ((counter+1 <= pairs.length) && (pairs[counter+1] == 0)){
                    Object sbNext = it.next();
                    cTSbMap.put(cNext, sbNext);
                    counter = counter + 2;
                }
                else {
                    cTSbMap.put(cNext, null);
                    counter++
                }
            }
        }

        
        for (int i=0; i < pairs.length; i++) {
        }*/
        polyglot.ast.Case [] caseArray = new polyglot.ast.Case[switchStmt.elements().size()];
        soot.jimple.Stmt [] targetsArray = new soot.jimple.Stmt[switchStmt.elements().size()];
        
        ArrayList targets = new ArrayList();
        HashMap targetsMap = new HashMap();
        int counter = 0;
        it = switchStmt.elements().iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof polyglot.ast.Case) {
                soot.jimple.Stmt noop = soot.jimple.Jimple.v().newNopStmt();
                if (!((polyglot.ast.Case)next).isDefault()){
                    targets.add(noop);
                    caseArray[counter] = (polyglot.ast.Case)next;
                    //System.out.println("case val: "+caseArray[counter].value());
                    //System.out.println("counter: "+counter);
                    targetsArray[counter] = noop;
                    counter++;
                    targetsMap.put(next, noop);
                }
                //if (((polyglot.ast.Case)next).isDefault()) {
                else {
                    defaultTarget = noop;
                }
            }
        }
        
        // sort targets map
        int lowIndex = 0;
        int highIndex = 0;

       
        for (int i = 0; i < counter; i++) {
            for (int j = i+1; j < counter; j++) {
                if (caseArray[j].value() < caseArray[i].value()) {
                    polyglot.ast.Case tempCase = caseArray[i];
                    soot.jimple.Stmt tempTarget = targetsArray[i]; 
                    caseArray[i] = caseArray[j];
                    targetsArray[i] = targetsArray[j];
                    caseArray[j] = tempCase;
                    targetsArray[j] = tempTarget;
                }
            }
        }
        
        ArrayList sortedTargets = new ArrayList();

        for (int i = 0; i < counter; i++) {
            sortedTargets.add(targetsArray[i]);
        }
            
        //System.out.println("defaultTarget: "+defaultTarget);
        boolean hasDefaultTarget = true;
        if (defaultTarget == null) {
            soot.jimple.Stmt noop = soot.jimple.Jimple.v().newNopStmt();
            defaultTarget = noop;
            hasDefaultTarget = false;
            
        }
        
        soot.jimple.Stmt sootSwitchStmt;
        if (isLookupSwitch(switchStmt)) {
        
            ArrayList values = new ArrayList();
            //it = switchStmt.elements().iterator();
            //while (it.hasNext()) {
                //Object next = it.next();
                //if (next instanceof polyglot.ast.Case) {
            for (int i = 0; i < counter; i++) {
                //if (!((polyglot.ast.Case)next).isDefault()){
                //        //System.out.println("Case Label: "+((polyglot.ast.Case)next).value());
                 //       values.add(soot.jimple.IntConstant.v((int)(((polyglot.ast.Case)next).value())));
                   // }
                //}
                if (!caseArray[i].isDefault()) {
                    values.add(soot.jimple.IntConstant.v((int)caseArray[i].value()));
                }
            }

            
            soot.jimple.LookupSwitchStmt lookupStmt = soot.jimple.Jimple.v().newLookupSwitchStmt(sootValue, values, sortedTargets, defaultTarget);
        
            Util.addPosTag(lookupStmt.getKeyBox(), value.position());
            sootSwitchStmt = lookupStmt;
        
        }
        else {
            long lowVal = 0;
            long highVal = 0;

            it = switchStmt.elements().iterator();
            while (it.hasNext()){
                Object next = it.next();
                if (next instanceof polyglot.ast.Case) {
                    if (!((polyglot.ast.Case)next).isDefault()){
                        long temp = ((polyglot.ast.Case)next).value();
                        //System.out.println("high: "+highVal+" low: "+lowVal+" temp: "+temp);
                        if ((highVal == 0) && (lowVal == 0)){
                            highVal = temp;
                            lowVal = temp;
                        }
                        if (temp > highVal) {
                            highVal = temp;
                        }
                        if (temp < lowVal) {
                            lowVal = temp;
                        }
                    }
                }
                
            }

            //System.out.println("high: "+highVal);
            //System.out.println("low: "+lowVal);
            soot.jimple.TableSwitchStmt tableStmt = soot.jimple.Jimple.v().newTableSwitchStmt(sootValue, (int)lowVal, (int)highVal, sortedTargets, defaultTarget);

            Util.addPosTag(tableStmt.getKeyBox(), value.position());
            sootSwitchStmt = tableStmt;
        }
        
        body.getUnits().add(sootSwitchStmt);

        Util.addLineTag(sootSwitchStmt, switchStmt);
        endControlNoop.push(soot.jimple.Jimple.v().newNopStmt());
       
        /*it = switchStmt.elements().iterator();
        ArrayList caseToSwitchBlock = new ArrayList();
        polyglot.ast.SwitchElement previous = null;
        while (it.hasNext()){
            Object next = it.next();
            //System.out.println("setting up map: "+next);
            if (previous instanceof polyglot.ast.Case) {
                if (next instanceof polyglot.ast.SwitchBlock) {
                    //System.out.println("adding: "+previous);
                    caseToSwitchBlock.add(previous);
                    //System.out.println("adding: "+next);
                    caseToSwitchBlock.add(next);
                }
                else {
                    //System.out.println("adding: "+previous);
                    caseToSwitchBlock.add(previous);
                    //System.out.println("adding: null");
                    caseToSwitchBlock.add(null);
                }
            }
            previous = (polyglot.ast.SwitchElement)next;
        }*/
        
        it = switchStmt.elements().iterator();
        ////System.out.println("caseToSwitchBlock list");
        //while (it.hasNext()) {
        //    //System.out.println(it.next());
        //}
        //create case blocks
        //it = caseToSwitchBlock.iterator();
        Iterator targetsIt = targets.iterator();

        while (it.hasNext()){
            Object next = it.next();
            //System.out.println(next);
            if (next instanceof polyglot.ast.Case) {
                if (!((polyglot.ast.Case)next).isDefault()){ 
                    body.getUnits().add((soot.jimple.Stmt)targetsMap.get(next));
                }
                else { 
                    body.getUnits().add(defaultTarget);
                }
            }
            else {
                polyglot.ast.SwitchBlock blockStmt = (polyglot.ast.SwitchBlock)next;
                createBlock(blockStmt);
                
            }
        }
         /*           Object nextBlock = it.next();    
                    //System.out.println(nextBlock);
                    if (nextBlock instanceof polyglot.ast.SwitchBlock) {
                        polyglot.ast.SwitchBlock blockStmt = (polyglot.ast.SwitchBlock)nextBlock;
                        body.getUnits().add((soot.jimple.Stmt)targetsIt.next());
                        
                        createBlock(blockStmt);
                    }
                    /*else {
                        body.getUnits().add(defaultTarget); 
                    }*/
           //     }
             /*   else {
                    Object nextBlock = it.next();    
                    if (nextBlock instanceof polyglot.ast.SwitchBlock) {
                        polyglot.ast.SwitchBlock blockStmt = (polyglot.ast.SwitchBlock)nextBlock;
                        body.getUnits().add(defaultTarget);
                        
                        createBlock(blockStmt);
                    }
                    /*else {
                        body.getUnits().add(defaultTarget); 
                    }*/
               // }
            //}
                
        //}
        
        if (!hasDefaultTarget) {
            body.getUnits().add(defaultTarget);
        }
        body.getUnits().add((soot.jimple.Stmt)(endControlNoop.pop()));
        
        //System.out.println("Switch Stmt: isLookupSwitch: "+isLookupSwitch(switchStmt));
    }

    private boolean isLookupSwitch(polyglot.ast.Switch switchStmt){

        int lowest = 0;
        int highest = 0;
        //BitSet bits = new BitSet();
        int counter = 0;
        Iterator it = switchStmt.elements().iterator();
        while (it.hasNext()){
            Object next = it.next();
            if (next instanceof polyglot.ast.Case) {
                polyglot.ast.Case caseStmt = (polyglot.ast.Case)next;
                //System.out.println("case val before isDefault: "+caseStmt.value());
                if (caseStmt.isDefault()) continue;
                //System.out.println("case val: "+caseStmt.value());
                int caseValue = (int)caseStmt.value();
                if (caseValue <= lowest || counter == 0 ) {
                    lowest = caseValue;
                }
                if (caseValue >= highest || counter == 0) {
                    highest = caseValue;
                }
                counter++;
            }
        }

        //System.out.println("counter: "+counter+" highest: "+highest+" lowest: "+lowest);
        if ((counter-1) == (highest - lowest)) return false;
        /*//System.out.println("counter : "+counter);
        for (int i = 0; i < counter; i++) {
            if (bits.get(i)) continue;
            else return true;
        }*/
        return true;
    }
    
    /**
     * Branch Stmts Creation
     */
    private void createBranch(polyglot.ast.Branch branchStmt){
        // need to write this
        
        body.getUnits().add(soot.jimple.Jimple.v().newNopStmt());
        if (branchStmt.kind() == polyglot.ast.Branch.BREAK){
            if (branchStmt.label() == null) {
                soot.jimple.Stmt gotoEndNoop = (soot.jimple.Stmt)endControlNoop.pop();
                soot.jimple.Stmt gotoEnd = soot.jimple.Jimple.v().newGotoStmt(gotoEndNoop);
                endControlNoop.push(gotoEndNoop);
                body.getUnits().add(gotoEnd);
            }
            else {
                soot.jimple.Stmt gotoLabel = soot.jimple.Jimple.v().newGotoStmt((soot.jimple.Stmt)labelBreakMap.get(branchStmt.label()));
                body.getUnits().add(gotoLabel);
            }
        }
        else if (branchStmt.kind() == polyglot.ast.Branch.CONTINUE){
            if (branchStmt.label() == null) {
                soot.jimple.Stmt gotoCondNoop = (soot.jimple.Stmt)condControlNoop.pop();
                soot.jimple.Stmt gotoCond = soot.jimple.Jimple.v().newGotoStmt(gotoCondNoop);
                condControlNoop.push(gotoCondNoop);
                body.getUnits().add(gotoCond);
            }
            else {
                soot.jimple.Stmt gotoLabel = soot.jimple.Jimple.v().newGotoStmt((soot.jimple.Stmt)labelContinueMap.get(branchStmt.label()));
                body.getUnits().add(gotoLabel);
            }
            
        }

        //body.getUnits().add(afterBranchNoop);
    }

    /**
     * Labeled Stmt Creation
     */
    private void createLabeled(polyglot.ast.Labeled labeledStmt){
        String label = labeledStmt.label();
        polyglot.ast.Stmt stmt = labeledStmt.statement();

        soot.jimple.Stmt noop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(noop);

        if (labelBreakMap == null) {
            labelBreakMap = new HashMap();
        }

        if (labelContinueMap == null) {
            labelContinueMap = new HashMap();
        }
        
        labelContinueMap.put(label, noop);
        soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
        labelBreakMap.put(label, noop2);
        
        createStmt(stmt);
        
        body.getUnits().add(noop2);
        
        // the idea here is to make a map of labels to the first
        // jimple stmt of the stmt (a noop) to be created - so 
        // there is something to look up for breaks and continues 
        // with labels
        //throw new RuntimeException("Labeled not yet Implemented");
    }
    
    /**
     * Assert Stmt Creation
     */
    private void createAssert(polyglot.ast.Assert assertStmt) {
        throw new RuntimeException("Assert not yet Implemented");
    }
    
    /**
     * Synchronized Stmt Creation
     */
    private void createSynchronized(polyglot.ast.Synchronized synchStmt) {
        soot.Value sootExpr = createExpr(synchStmt.expr());
        
        soot.jimple.EnterMonitorStmt enterMon = soot.jimple.Jimple.v().newEnterMonitorStmt(sootExpr);
        body.getUnits().add(enterMon);
        Util.addPosTag(enterMon.getOpBox(), synchStmt.expr().position());
        
        soot.jimple.Stmt startNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(startNoop);
        
        createBlock(synchStmt.body());

        soot.jimple.ExitMonitorStmt exitMon = soot.jimple.Jimple.v().newExitMonitorStmt(sootExpr);
        body.getUnits().add(exitMon);
        Util.addPosTag(exitMon.getOpBox(), synchStmt.expr().position());
        
        soot.jimple.Stmt endSynchNoop = soot.jimple.Jimple.v().newNopStmt();
        soot.jimple.Stmt gotoEnd = soot.jimple.Jimple.v().newGotoStmt(endSynchNoop);

        soot.jimple.Stmt endNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(endNoop);
        
        body.getUnits().add(gotoEnd);

        soot.jimple.Stmt catchAllBeforeNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(catchAllBeforeNoop);

        // catch all
        soot.Local formalLocal = generateLocal(soot.RefType.v(soot.Scene.v().getSootClass("java.lang.Throwable")));
            
        soot.jimple.CaughtExceptionRef exceptRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(formalLocal, exceptRef);
        body.getUnits().add(stmt);

        // catch
        soot.jimple.Stmt catchBeforeNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(catchBeforeNoop);
        
        soot.Local local = generateLocal(soot.RefType.v(soot.Scene.v().getSootClass("java.lang.Throwable")));
        
        soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(local, formalLocal);

        body.getUnits().add(assign);
        soot.jimple.ExitMonitorStmt catchExitMon = soot.jimple.Jimple.v().newExitMonitorStmt(sootExpr);
        
        body.getUnits().add(catchExitMon);
        Util.addPosTag(catchExitMon.getOpBox(), synchStmt.expr().position());
        
        soot.jimple.Stmt catchAfterNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(catchAfterNoop);
        
        // throw
        soot.jimple.Stmt throwStmt = soot.jimple.Jimple.v().newThrowStmt(local);
        body.getUnits().add(throwStmt);
      
        
        body.getUnits().add(endSynchNoop);

        addToExceptionList(startNoop, endNoop, catchAllBeforeNoop, soot.Scene.v().getSootClass("java.lang.Throwable"));
        addToExceptionList(catchBeforeNoop, catchAfterNoop, catchAllBeforeNoop, soot.Scene.v().getSootClass("java.lang.Throwable"));
                
        
        //throw new RuntimeException("Synchronized not yet Implemented");
    }
    
    /**
     * Return Stmts Creation
     */
    private void createReturn(polyglot.ast.Return retStmt) {
        polyglot.ast.Expr expr = retStmt.expr();
        if (expr == null) {
            soot.jimple.Stmt retStmtVoid = soot.jimple.Jimple.v().newReturnVoidStmt();
            body.getUnits().add(retStmtVoid);
            Util.addLineTag(retStmtVoid, retStmt);
        }
        else {
            ////System.out.println("Return Expr: "+expr);
            soot.Value sootLocal = createExpr(expr);
            if (sootLocal instanceof soot.jimple.ConditionExpr) {
                sootLocal = handleCondBinExpr((soot.jimple.ConditionExpr)sootLocal); 
            }
            soot.jimple.ReturnStmt retStmtLocal = soot.jimple.Jimple.v().newReturnStmt(sootLocal);
            body.getUnits().add(retStmtLocal);
            Util.addLineTag(retStmtLocal, retStmt);
            Util.addPosTag(retStmtLocal.getOpBox(), expr.position());
        }
    }
    
    /**
     * Throw Stmt Creation
     */
    private void createThrow(polyglot.ast.Throw throwStmt){
        soot.Value toThrow = createExpr(throwStmt.expr());
        soot.jimple.ThrowStmt throwSt = soot.jimple.Jimple.v().newThrowStmt(toThrow);
        body.getUnits().add(throwSt);
        Util.addLineTag(throwSt, throwStmt);
        Util.addPosTag(throwSt.getOpBox(), throwStmt.expr().position());
    }
   
    /**
     * Try Stmt Creation
     */
    private void createTry(polyglot.ast.Try tryStmt) {
    
        polyglot.ast.Block finallyBlock = tryStmt.finallyBlock();
        
        if (finallyBlock == null) {
            createTryCatch(tryStmt);
        }
        else {
            createTryCatchFinally(tryStmt);
        }
    }

    private void createTryCatch(polyglot.ast.Try tryStmt){
        
        // try
        polyglot.ast.Block tryBlock = tryStmt.tryBlock();
        
        // this nop is for the fromStmt of try     
        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(noop1);
        
        createBlock(tryBlock);
        
        // this nop is for the toStmt of try
        soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(noop2);
    
        // create end nop for after entire try/catch
        soot.jimple.Stmt endNoop = soot.jimple.Jimple.v().newNopStmt();

        soot.jimple.Stmt tryEndGoto = soot.jimple.Jimple.v().newGotoStmt(endNoop);
        body.getUnits().add(tryEndGoto);
        
        Iterator it = tryStmt.catchBlocks().iterator();
        while (it.hasNext()) {
            
            soot.jimple.Stmt noop3 = soot.jimple.Jimple.v().newNopStmt();
            body.getUnits().add(noop3);

            // create catch stmts
            polyglot.ast.Catch catchBlock = (polyglot.ast.Catch)it.next();
            
            // create catch ref
            createCatchFormal(catchBlock.formal());
          
            createBlock(catchBlock.body());
        
            soot.jimple.Stmt catchEndGoto = soot.jimple.Jimple.v().newGotoStmt(endNoop);
            body.getUnits().add(catchEndGoto);
        

            soot.Type sootType = Util.getSootType(catchBlock.catchType());
           
            addToExceptionList(noop1, noop2, noop3, soot.Scene.v().getSootClass(sootType.toString()));
            
        }

        body.getUnits().add(endNoop);
    }

    private void createTryCatchFinally(polyglot.ast.Try tryStmt){
        
        HashMap gotoMap = new HashMap();
        
        // try
        polyglot.ast.Block tryBlock = tryStmt.tryBlock();
        
        // this nop is for the fromStmt of try     
        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(noop1);
        
        createBlock(tryBlock);
        
        // this nop is for the toStmt of try
        soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(noop2);
    
        // create end nop for after entire try/catch
        soot.jimple.Stmt endNoop = soot.jimple.Jimple.v().newNopStmt();
        
        // to finally
        soot.jimple.Stmt tryGotoFinallyNoop = soot.jimple.Jimple.v().newNopStmt();

        body.getUnits().add(tryGotoFinallyNoop);
        soot.jimple.Stmt tryFinallyNoop = soot.jimple.Jimple.v().newNopStmt();
        
        soot.jimple.Stmt tryGotoFinally = soot.jimple.Jimple.v().newGotoStmt(tryFinallyNoop);
        body.getUnits().add(tryGotoFinally);
        
        // goto end stmts
        soot.jimple.Stmt beforeEndGotoNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(beforeEndGotoNoop);
        soot.jimple.Stmt tryEndGoto = soot.jimple.Jimple.v().newGotoStmt(endNoop);
        body.getUnits().add(tryEndGoto);
        
        gotoMap.put(tryFinallyNoop, beforeEndGotoNoop);
            

       
        // catch section
        soot.jimple.Stmt catchAllBeforeNoop = soot.jimple.Jimple.v().newNopStmt();
        Iterator it = tryStmt.catchBlocks().iterator();
        while (it.hasNext()) {
            
            soot.jimple.Stmt noop3 = soot.jimple.Jimple.v().newNopStmt();
            body.getUnits().add(noop3);

            // create catch stmts
            polyglot.ast.Catch catchBlock = (polyglot.ast.Catch)it.next();
            
            // create catch ref
            soot.jimple.Stmt catchRefNoop = soot.jimple.Jimple.v().newNopStmt();
            body.getUnits().add(catchRefNoop);
            
            createCatchFormal(catchBlock.formal());
          
            soot.jimple.Stmt catchStmtsNoop = soot.jimple.Jimple.v().newNopStmt();
            body.getUnits().add(catchStmtsNoop);
            createBlock(catchBlock.body());
        
            // to finally
            soot.jimple.Stmt catchGotoFinallyNoop = soot.jimple.Jimple.v().newNopStmt();
            body.getUnits().add(catchGotoFinallyNoop);
            soot.jimple.Stmt catchFinallyNoop = soot.jimple.Jimple.v().newNopStmt();
        
            soot.jimple.Stmt catchGotoFinally = soot.jimple.Jimple.v().newGotoStmt(catchFinallyNoop);
            body.getUnits().add(catchGotoFinally);
            
            // goto end stmts
            soot.jimple.Stmt beforeCatchEndGotoNoop = soot.jimple.Jimple.v().newNopStmt();
            body.getUnits().add(beforeCatchEndGotoNoop);
            soot.jimple.Stmt catchEndGoto = soot.jimple.Jimple.v().newGotoStmt(endNoop);
            body.getUnits().add(catchEndGoto);
        

            gotoMap.put(catchFinallyNoop, beforeCatchEndGotoNoop);

            soot.Type sootType = Util.getSootType(catchBlock.catchType());
           
            addToExceptionList(noop1, noop2, noop3, soot.Scene.v().getSootClass(sootType.toString()));
            addToExceptionList(catchStmtsNoop, beforeCatchEndGotoNoop, catchAllBeforeNoop, soot.Scene.v().getSootClass("java.lang.Throwable"));
        }
        
        // catch all ref
        soot.Local formalLocal = generateLocal(soot.RefType.v(soot.Scene.v().getSootClass("java.lang.Throwable")));
            
        body.getUnits().add(catchAllBeforeNoop);
        soot.jimple.CaughtExceptionRef exceptRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(formalLocal, exceptRef);
        body.getUnits().add(stmt);

        // catch all assign
        soot.jimple.Stmt beforeCatchAllAssignNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(beforeCatchAllAssignNoop);
        soot.Local catchAllAssignLocal = generateLocal(soot.RefType.v(soot.Scene.v().getSootClass("java.lang.Throwable")));
        soot.jimple.Stmt catchAllAssign = soot.jimple.Jimple.v().newAssignStmt(catchAllAssignLocal, formalLocal);

        body.getUnits().add(catchAllAssign);

        // catch all finally
        soot.jimple.Stmt catchAllFinallyNoop = soot.jimple.Jimple.v().newNopStmt();
        soot.jimple.Stmt catchAllGotoFinally = soot.jimple.Jimple.v().newGotoStmt(catchAllFinallyNoop);
        body.getUnits().add(catchAllGotoFinally);

        // catch all throw
        soot.jimple.Stmt catchAllBeforeThrowNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(catchAllBeforeThrowNoop);
        soot.jimple.Stmt throwStmt = soot.jimple.Jimple.v().newThrowStmt(catchAllAssignLocal);
        body.getUnits().add(throwStmt);

        gotoMap.put(catchAllFinallyNoop, catchAllBeforeThrowNoop);
        
        // catch all goto end
        soot.jimple.Stmt catchAllGotoEnd = soot.jimple.Jimple.v().newGotoStmt(endNoop);
        body.getUnits().add(catchAllGotoEnd);
        
        addToExceptionList(beforeCatchAllAssignNoop, catchAllBeforeThrowNoop ,catchAllBeforeNoop, soot.Scene.v().getSootClass("java.lang.Throwable"));
        
        // create finally's 
        Iterator finallyIt = gotoMap.keySet().iterator();
        while (finallyIt.hasNext()) {
        
            soot.jimple.Stmt noopStmt = (soot.jimple.Stmt)finallyIt.next();
            body.getUnits().add(noopStmt);

            createBlock(tryStmt.finallyBlock());    
            soot.jimple.Stmt backToStmt = (soot.jimple.Stmt)gotoMap.get(noopStmt);
            soot.jimple.Stmt backToGoto = soot.jimple.Jimple.v().newGotoStmt(backToStmt);
            body.getUnits().add(backToGoto);
        }
        body.getUnits().add(endNoop);
    
        addToExceptionList(noop1, beforeEndGotoNoop, catchAllBeforeNoop, soot.Scene.v().getSootClass("java.lang.Throwable"));
    }

    
    private void addToExceptionList(soot.jimple.Stmt from, soot.jimple.Stmt to, soot.jimple.Stmt with, soot.SootClass exceptionClass) {
        if (exceptionTable == null) {
            exceptionTable = new ArrayList();
        }
        soot.Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, from, to, with);
        exceptionTable.add(trap);
    }
    
    /**
     * Expression Creation
     */
    private soot.Value createExpr(polyglot.ast.Expr expr){

        if (expr instanceof polyglot.ast.Assign) {
            return getAssignLocal((polyglot.ast.Assign)expr);
        }
        else if (expr instanceof polyglot.ast.Lit) {
            return createLiteral((polyglot.ast.Lit)expr);
        }
        else if (expr instanceof polyglot.ast.Local) {
            return getLocal((polyglot.ast.Local)expr);
        }
        else if (expr instanceof polyglot.ast.Binary) {
            return getBinaryLocal((polyglot.ast.Binary)expr);
        }
        else if (expr instanceof polyglot.ast.Unary) {
            return getUnaryLocal((polyglot.ast.Unary)expr);
        }
        else if (expr instanceof polyglot.ast.Cast) {
            return getCastLocal((polyglot.ast.Cast)expr);
        }
        /*else if (expr instanceof polyglot.ast.ArrayInit) {
            return getArrayInitLocal((polyglot.ast.ArrayInit)expr);
            
        }*/
        else if (expr instanceof polyglot.ast.ArrayAccess) {
            return getArrayRefLocal((polyglot.ast.ArrayAccess)expr);
        }
        else if (expr instanceof polyglot.ast.NewArray) {
            return getNewArrayLocal((polyglot.ast.NewArray)expr);
        }
        else if (expr instanceof polyglot.ast.Call) {
            return getCallLocal((polyglot.ast.Call)expr);
        }
        else if (expr instanceof polyglot.ast.New) {
            return getNewLocal((polyglot.ast.New)expr);
        }
        else if (expr instanceof polyglot.ast.Special) {
            return getSpecialLocal((polyglot.ast.Special)expr);
        }
        else if (expr instanceof polyglot.ast.Instanceof) {
            return getInstanceOfLocal((polyglot.ast.Instanceof)expr);
        }
        else if (expr instanceof polyglot.ast.Conditional) {
            return getConditionalLocal((polyglot.ast.Conditional)expr);
        }
        else if (expr instanceof polyglot.ast.Field) {
            return getFieldLocal((polyglot.ast.Field)expr);
        }
        else {
            //System.out.println("Unhandled Expression: "+expr.getClass().toString());
            throw new RuntimeException("Unhandled Expression");
        }
       
    }

    /**
     * Assign Expression Creation
     */
    private soot.Value getAssignLocal(polyglot.ast.Assign assign) {
        
        //System.out.println("getAssignLocal: "+assign);
        soot.jimple.AssignStmt stmt;
           
        soot.Value left = createLHS(assign.left());
        soot.Value right = createExpr(assign.right());
        //soot.Value leftExpr = createExpr(assign.left());
       
        if (right instanceof soot.jimple.ConditionExpr) {
            right = handleCondBinExpr((soot.jimple.ConditionExpr)right);
        }
        
        //soot.Value leftLocal = left;
        ////System.out.println("r type: "+right.getClass()+" l type: "+left.getClass());
        soot.Local leftLocal;
        if (left instanceof soot.Local) {
            //System.out.println("in assign and left is local");
            leftLocal = (soot.Local)left;
        }
        else {
            leftLocal = generateLocal(left.getType());
            //stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            //body.getUnits().add(stmt);
        }
        
        polyglot.ast.Assign.Operator op = assign.operator();
        if (op == polyglot.ast.Assign.ASSIGN) {

            
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, right);
            body.getUnits().add(stmt);
            
            /*stmt = soot.jimple.Jimple.v().newAssignStmt(leftExpr, right);
            body.getUnits().add(stmt);
            
          stmt = soot.jimple.Jimple.v().newAssignStmt(left, leftExpr);
            body.getUnits().add(stmt);*/
        }
        else if (op == polyglot.ast.Assign.ADD_ASSIGN) {
        
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            body.getUnits().add(stmt);
            ////System.out.println("leftExpr type: "+leftExpr.getType());
            if (((leftLocal instanceof soot.jimple.StringConstant) || (right instanceof soot.jimple.StringConstant)) || ((leftLocal.getType().toString().equals("java.lang.String")) || (right.getType().toString().equals("java.lang.String")))){
                //System.out.println("Is concat!");
                soot.Value rValue = getStringConcatLocal(leftLocal, right);
                stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, rValue);
                body.getUnits().add(stmt);
                
            }
            else {
            ////System.out.println("left: "+left+"left type: "+left.getType());
            ////System.out.println("leftExpr: "+leftExpr+"leftExpr type: "+leftExpr.getType());
                soot.jimple.AddExpr addExpr = soot.jimple.Jimple.v().newAddExpr(leftLocal, right);
            ////System.out.println("addExpr: "+addExpr);
            ////System.out.println("left: "+left+"left type: "+left.getType());
                stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, addExpr);
                body.getUnits().add(stmt);

            }
        }
        else if (op == polyglot.ast.Assign.SUB_ASSIGN) {

            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            body.getUnits().add(stmt);
            soot.jimple.SubExpr subExpr = soot.jimple.Jimple.v().newSubExpr(leftLocal, right);
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, subExpr);
            body.getUnits().add(stmt);
            
           
        }
        else if (op == polyglot.ast.Assign.MUL_ASSIGN) {

            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            body.getUnits().add(stmt);
            soot.jimple.MulExpr mulExpr = soot.jimple.Jimple.v().newMulExpr(leftLocal, right);
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, mulExpr);
            body.getUnits().add(stmt);
            
           
        }
        else if (op == polyglot.ast.Assign.DIV_ASSIGN) {

            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            body.getUnits().add(stmt);
            soot.jimple.DivExpr divExpr = soot.jimple.Jimple.v().newDivExpr(leftLocal, right);
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, divExpr);
            body.getUnits().add(stmt);
            
           
        }
        else if (op == polyglot.ast.Assign.MOD_ASSIGN) {

            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            body.getUnits().add(stmt);
            soot.jimple.RemExpr remExpr = soot.jimple.Jimple.v().newRemExpr(leftLocal, right);
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, remExpr);
            body.getUnits().add(stmt);
            
        }
        else if (op == polyglot.ast.Assign.SHL_ASSIGN) {

            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            body.getUnits().add(stmt);
            soot.jimple.ShlExpr shlExpr = soot.jimple.Jimple.v().newShlExpr(leftLocal, right);
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, shlExpr);
            body.getUnits().add(stmt);
            
           
        }
        else if (op == polyglot.ast.Assign.SHR_ASSIGN) {

            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            body.getUnits().add(stmt);
            soot.jimple.ShrExpr shrExpr = soot.jimple.Jimple.v().newShrExpr(leftLocal, right);
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, shrExpr);
            body.getUnits().add(stmt);
            
           
        }
        else if (op == polyglot.ast.Assign.USHR_ASSIGN) {

            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            body.getUnits().add(stmt);
            soot.jimple.UshrExpr ushrExpr = soot.jimple.Jimple.v().newUshrExpr(leftLocal, right);
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, ushrExpr);
            body.getUnits().add(stmt);
           
        }
        else if (op == polyglot.ast.Assign.BIT_AND_ASSIGN) {

            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            body.getUnits().add(stmt);
            soot.jimple.AndExpr andExpr = soot.jimple.Jimple.v().newAndExpr(leftLocal, right);
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, andExpr);
            body.getUnits().add(stmt);
           
        }
        else if (op == polyglot.ast.Assign.BIT_OR_ASSIGN) {

            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            body.getUnits().add(stmt);
            soot.jimple.OrExpr orExpr = soot.jimple.Jimple.v().newOrExpr(leftLocal, right);
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, orExpr);
            body.getUnits().add(stmt);
           
        }
        else if (op == polyglot.ast.Assign.BIT_XOR_ASSIGN) {

            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            body.getUnits().add(stmt);
            soot.jimple.XorExpr xorExpr = soot.jimple.Jimple.v().newXorExpr(leftLocal, right);
            stmt = soot.jimple.Jimple.v().newAssignStmt(leftLocal, xorExpr);
            body.getUnits().add(stmt);
           
        }
        else {
            throw new RuntimeException("Unhandled Assign Operator");
        }
        
        if (isLitOrLocal(assign.right())){
		    Util.addPosTag(stmt.getRightOpBox(), assign.right().position());
	    }
        else {
            Util.addPosTag(stmt.getRightOpBox(), assign.right().position());
        }
            
		Util.addPosTag(stmt.getLeftOpBox(), assign.left().position());
            
        //body.getUnits().add(stmt);
        Util.addLineTag(stmt, assign);
   
        if (!(left instanceof soot.Local)) {
            stmt = soot.jimple.Jimple.v().newAssignStmt(left, leftLocal);
            body.getUnits().add(stmt);
        }
        
        return leftLocal;
    
    }

    
    /**
     * Field Expression Creation - LHS
     */
    private soot.Value getFieldLocalLeft(polyglot.ast.Field field){
        polyglot.ast.Receiver receiver = field.target();
        if ((field.name().equals("length")) && (receiver.type() instanceof polyglot.types.ArrayType)){
            return getSpecialArrayLengthLocal(field);
        }
        else {
            return getFieldRef(field);
        }
    }
   
    /**
     * Field Expression Creation
     */
    private soot.Value getFieldLocal(polyglot.ast.Field field){
    
        polyglot.ast.Receiver receiver = field.target();
        
        soot.javaToJimple.PolyglotMethodSource ms = (soot.javaToJimple.PolyglotMethodSource)body.getMethod().getSource();
        
        if ((field.name().equals("length")) && (receiver.type() instanceof polyglot.types.ArrayType)){
            return getSpecialArrayLengthLocal(field);
        }
        else if (field.name().equals("class")){
            return getSpecialClassLitLocal(field);
        }
        else if ((ms.getPrivateAccessMap() != null) && (ms.getPrivateAccessMap().containsKey(field.fieldInstance()))){
        
            return getPrivateAccessFieldLocal(field);
        }
        else {

            soot.jimple.FieldRef fieldRef = getFieldRef(field);
        
            soot.Local baseLocal = generateLocal(field.type());
            soot.jimple.Stmt fieldAssignStmt = soot.jimple.Jimple.v().newAssignStmt(baseLocal, fieldRef);
            
            body.getUnits().add(fieldAssignStmt);
            Util.addLineTag(fieldAssignStmt, field);
            return baseLocal; 
        }
    }
    private soot.jimple.FieldRef getFieldRef(polyglot.ast.Field field) {
       
        //System.out.println("field.fieldInstance: "+field.fieldInstance());
        //System.out.println("field.name: "+field.name());
        //System.out.println("field.type: "+field.type());
        soot.SootClass receiverClass = soot.Scene.v().getSootClass(getReceiverClassName(field.fieldInstance()));
            
        soot.SootField receiverField = receiverClass.getField(field.name(), Util.getSootType(field.type()));
         
        soot.jimple.FieldRef fieldRef;
        if (field.fieldInstance().flags().isStatic()) {
            fieldRef = soot.jimple.Jimple.v().newStaticFieldRef(receiverField);
        }
        else {
            System.out.println("target: "+field.target());
            soot.Local base = (soot.Local)getBaseLocal(field.target());
            System.out.println("base: "+base);
            fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(base, receiverField);
        }
        return fieldRef;
    }

    private soot.Local getPrivateAccessFieldLocal(polyglot.ast.Field field) {
    
        HashMap paMap = ((soot.javaToJimple.PolyglotMethodSource)body.getMethod().getSource()).getPrivateAccessMap();
        soot.SootMethod toInvoke = (soot.SootMethod)paMap.get(field.fieldInstance());

        ArrayList params = new ArrayList();
        if (!field.fieldInstance().flags().isStatic()) {
            params.add((soot.Local)getBaseLocal(field.target()));
        }
        
        soot.jimple.InvokeExpr invoke = soot.jimple.Jimple.v().newStaticInvokeExpr(toInvoke, params);

        soot.Local retLocal = generateLocal(field.type());

        soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, invoke);
        body.getUnits().add(stmt);

        return retLocal;
    }

    private soot.Local getSpecialClassLitLocal(polyglot.ast.Field field) {
        
        // this class
        soot.SootClass thisClass = body.getMethod().getDeclaringClass();
        
        // field
        /*polyglot.ast.Receiver receiver = field.target();
        String fieldName = "class$";
        if (receiver instanceof polyglot.ast.TypeNode) {
            String type = ((polyglot.ast.TypeNode)receiver).type().toString();
            type = type.replace('.', '$');
            fieldName = fieldName+type;
        }
        else {
            throw new RuntimeException("class literal only valid on type nodes");
        }
        soot.Type fieldType = soot.RefType.v("java.lang.Class");
        if (!thisClass.declaresField(fieldName, fieldType)){
            soot.SootField sootField = new soot.SootField(fieldName, fieldType, soot.Modifier.STATIC);
            thisClass.addField(sootField);
        }

        // method
        String methodName = "class$";
        soot.Type methodRetType = soot.RefType.v("java.lang.Class");
        ArrayList paramTypes = new ArrayList();
        paramTypes.add(soot.RefType.v("java.lang.String"));
        if (!thisClass.declaresMethod(methodName, paramTypes, methodRetType)){
            //System.out.println("adding class$ method");
            soot.SootMethod sootMethod = new soot.SootMethod(methodName, paramTypes, methodRetType, soot.Modifier.STATIC);
            thisClass.addMethod(sootMethod);

            soot.Body classBody = soot.jimple.Jimple.v().newBody(sootMethod);
            //sootMethod.setActiveBody(classBody);

            // static invoke of forName
            soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(soot.RefType.v("java.lang.String"), 0);

            
            soot.Local paramLocal = soot.jimple.Jimple.v().newLocal("$r0", soot.RefType.v("java.lang.String"));
            classBody.getLocals().add(paramLocal);
            soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(paramLocal, paramRef);
            classBody.getUnits().add(stmt);

            soot.SootMethod methodToInvoke = soot.Scene.v().getSootClass("java.lang.Class").getMethod("forName", paramTypes, soot.RefType.v("java.lang.Class"));
            soot.Local invokeLocal = soot.jimple.Jimple.v().newLocal("$r1", soot.RefType.v("java.lang.Class"));
            classBody.getLocals().add(invokeLocal);
            ArrayList params = new ArrayList();
            params.add(paramLocal);
            soot.jimple.Expr invokeExpr = soot.jimple.Jimple.v().newStaticInvokeExpr(methodToInvoke, params);
            soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(invokeLocal, invokeExpr);
            classBody.getUnits().add(assign);
            
            // return
            soot.jimple.Stmt retStmt = soot.jimple.Jimple.v().newReturnStmt(invokeLocal);
            classBody.getUnits().add(retStmt);

            // catch
            soot.Local catchRefLocal = soot.jimple.Jimple.v().newLocal("$r2", soot.RefType.v("java.lang.ClassNotFoundException"));
            classBody.getLocals().add(catchRefLocal);
            soot.jimple.CaughtExceptionRef caughtRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
            soot.jimple.Stmt caughtIdentity = soot.jimple.Jimple.v().newIdentityStmt(catchRefLocal, caughtRef);
            classBody.getUnits().add(caughtIdentity);
         
            // new no class def found error
            soot.Local throwLocal = soot.jimple.Jimple.v().newLocal("$r3", soot.RefType.v("java.lang.NoClassDefFoundError"));
            classBody.getLocals().add(throwLocal);
            soot.jimple.Expr newExpr = soot.jimple.Jimple.v().newNewExpr(soot.RefType.v("java.lang.NoClassDefFoundError"));
            soot.jimple.Stmt throwAssign = soot.jimple.Jimple.v().newAssignStmt(throwLocal, newExpr);
            classBody.getUnits().add(throwAssign);

            // get exception message
            soot.Local messageLocal = soot.jimple.Jimple.v().newLocal("$r4", soot.RefType.v("java.lang.String"));
            classBody.getLocals().add(messageLocal);
            //params = new ArrayList();
            //params.add(catchRefLocal);
            soot.SootMethod messageMethToInvoke = soot.Scene.v().getSootClass("java.lang.Throwable").getMethod("getMessage", new ArrayList(), soot.RefType.v("java.lang.String"));

            soot.jimple.Expr messageInvoke = soot.jimple.Jimple.v().newVirtualInvokeExpr(catchRefLocal, messageMethToInvoke, new ArrayList());
            soot.jimple.Stmt messageAssign = soot.jimple.Jimple.v().newAssignStmt(messageLocal, messageInvoke);
            classBody.getUnits().add(messageAssign);

            // no class def found init
            paramTypes = new ArrayList();
            paramTypes.add(soot.RefType.v("java.lang.String"));
            soot.SootMethod initMethToInvoke = soot.Scene.v().getSootClass("java.lang.NoClassDefFoundError").getMethod("<init>", paramTypes, soot.VoidType.v());
            params = new ArrayList();
            params.add(messageLocal);
            soot.jimple.Expr initInvoke = soot.jimple.Jimple.v().newSpecialInvokeExpr(throwLocal, initMethToInvoke, params);
            soot.jimple.Stmt initStmt = soot.jimple.Jimple.v().newInvokeStmt(initInvoke);
            classBody.getUnits().add(initStmt);
            
            // throw            
            soot.jimple.Stmt throwStmt = soot.jimple.Jimple.v().newThrowStmt(throwLocal);
            classBody.getUnits().add(throwStmt);
        
            // trap
            soot.Trap trap = soot.jimple.Jimple.v().newTrap(soot.Scene.v().getSootClass("java.lang.ClassNotFoundException"), assign, retStmt, caughtIdentity);
            classBody.getTraps().add(trap);
            
            sootMethod.setActiveBody(classBody);
        }*/

        String fieldName = "class$";
        String typeName = null;
        if (field.target() instanceof polyglot.ast.TypeNode) {
            String type = ((polyglot.ast.TypeNode)field.target()).type().toString();
            typeName = type;
            type = type.replace('.', '$');
            fieldName = fieldName+type;
        }
        else {
            throw new RuntimeException("class literal only valid on type nodes");
        }
        soot.Type fieldType = soot.RefType.v("java.lang.Class");
        soot.Local fieldLocal = generateLocal(soot.RefType.v("java.lang.Class"));
        soot.jimple.StaticFieldRef fieldRef = soot.jimple.Jimple.v().newStaticFieldRef(thisClass.getField(fieldName, fieldType));
        soot.jimple.Stmt fieldAssign = soot.jimple.Jimple.v().newAssignStmt(fieldLocal,  fieldRef);
        body.getUnits().add(fieldAssign);

        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
        soot.jimple.Expr neExpr = soot.jimple.Jimple.v().newNeExpr(fieldLocal, soot.jimple.NullConstant.v());
        soot.jimple.Stmt ifStmt = soot.jimple.Jimple.v().newIfStmt(neExpr, noop1);
        body.getUnits().add(ifStmt);

        ArrayList paramTypes = new ArrayList();
        paramTypes.add(soot.RefType.v("java.lang.String"));
        soot.SootMethod invokeMeth = thisClass.getMethod("class$", paramTypes, soot.RefType.v("java.lang.Class"));
        ArrayList params = new ArrayList();
        params.add(soot.jimple.StringConstant.v(typeName));
        //System.out.println("invokeMeth: "+invokeMeth+" className: "+thisClass.getName());
        soot.jimple.Expr classInvoke = soot.jimple.Jimple.v().newStaticInvokeExpr(invokeMeth, params);
        soot.Local methLocal = generateLocal(soot.RefType.v("java.lang.Class"));
        soot.jimple.Stmt invokeAssign = soot.jimple.Jimple.v().newAssignStmt(methLocal, classInvoke);
        body.getUnits().add(invokeAssign);

        soot.jimple.Stmt assignField = soot.jimple.Jimple.v().newAssignStmt(fieldRef, methLocal);
        body.getUnits().add(assignField);

        soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
        soot.jimple.Stmt goto1 = soot.jimple.Jimple.v().newGotoStmt(noop2);
        body.getUnits().add(goto1);
        
        body.getUnits().add(noop1);
        fieldAssign = soot.jimple.Jimple.v().newAssignStmt(methLocal,  fieldRef);
        body.getUnits().add(fieldAssign);
        body.getUnits().add(noop2);

        return methLocal;

    }

    private soot.Local getSpecialArrayLengthLocal(polyglot.ast.Field field) {
            
        soot.Local localField;
        polyglot.ast.Receiver receiver = field.target();
        if (receiver instanceof polyglot.ast.Local) {
            localField = getLocal((polyglot.ast.Local)receiver);
        }
        else if (receiver instanceof polyglot.ast.Expr){
            localField = (soot.Local)createExpr((polyglot.ast.Expr)receiver);
        }
        else {
            localField = generateLocal(receiver.type());
        }
        soot.jimple.LengthExpr lengthExpr = soot.jimple.Jimple.v().newLengthExpr(localField);
        soot.Local retLocal = generateLocal(soot.IntType.v());
        soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(retLocal, lengthExpr);
        body.getUnits().add(assign);
        Util.addLineTag(assign, field);
        return retLocal;
    }
    
    private String getReceiverClassName(polyglot.types.MemberInstance mi) {
        if (mi.container() instanceof polyglot.types.ArrayType) {
            return "java.lang.String";
        }
        else {
            //System.out.println("in getReceiverClassName: mi: "+mi);
            //System.out.println("in getReceiverClassName: mi.container: "+mi.container());
            if (mi.container() instanceof polyglot.types.ClassType) {
                if (((polyglot.types.ClassType)mi.container()).isNested()){
                    //System.out.println("mi.container is inner");
                    return fixInnerClassName((polyglot.types.ClassType)mi.container());
                }
            }
            return mi.container().toString();
        }
    }
    
    
    /**
     * Binary Expression Creation
     */
    private soot.Value getBinaryLocal(polyglot.ast.Binary binary) {
            
        soot.Value rhs;
		
       /* if (binary.operator() == polyglot.ast.Binary.COND_AND) {
        }
        else if (binary.operator() == polyglot.ast.Binary.COND_OR) {
        }
        else {*/
        ////System.out.println("Binary expr: "+binary+" type: "+binary.type());
          //  polyglot.ast.Expr rExp = binary.right();
		  //  polyglot.ast.Expr lExp = binary.left();

           // //System.out.println("in getBinaryLocal: lExp: "+lExp+" rExp: "+rExp);
		    
        //soot.Value lVal = createExpr(binary.left());
	    //lVal = createCastIfNecessary(lVal, binary);
        //soot.Value rVal = createExpr(binary.right());
	    //rVal = createCastIfNecessary(rVal, binary);
                
        if (binary.operator() == polyglot.ast.Binary.COND_AND) {
            return createCondAnd(binary);
        }
        if (binary.operator() == polyglot.ast.Binary.COND_OR) {
            return createCondOr(binary);
        }
        soot.Value lVal = createExpr(binary.left());
	    //lVal = createCastIfNecessary(lVal, binary);
        soot.Value rVal = createExpr(binary.right());
	    //rVal = createCastIfNecessary(rVal, binary);
		   /* if (isLitOrLocal(lExp) && isLitOrLocal(rExp)) {
			    // both simple
                
                if (binary.operator() == polyglot.ast.Binary.COND_AND) {
                    return createCondAnd(binary);
                }
                else if (binary.operator() == polyglot.ast.Binary.COND_OR) {
                    return createCondOr(binary);
                }
                else {
		            rhs = getBinaryRhs(lExp, rExp, binary.operator());
                }
            }
		    else {
			
                if (binary.operator() == polyglot.ast.Binary.COND_AND) {
                    return createCondAnd(binary);
                }
                if (binary.operator() == polyglot.ast.Binary.COND_OR) {
                    return createCondOr(binary);
                }
                // left
                //System.out.println("lExp: "+lExp+" type: "+lExp.type());
                //soot.Local lTemp = generateLocal(lExp.type());
                //if (lTemp instanceof soot.jimple.StringConstant){
                //    //System.out.println("lTemp is instanceof string Constant");
                //}
                
                soot.Value leftRValue = createExpr(lExp);
                leftRValue = createCastIfNecessary(leftRValue, binary);
                //System.out.println("leftRVal created: "+leftRValue+" type: "+leftRValue.getType());

                if (leftRValue instanceof soot.jimple.StringConstant){
                    //System.out.println("leftRValue is a soot string const");
                }
                //soot.jimple.Stmt lAssign = soot.jimple.Jimple.v().newAssignStmt(lTemp, leftRValue);
                //body.getUnits().add(lAssign);
                //Util.addLineTag(lAssign, binary);
            
                // right        
			    //soot.Value rTemp = generateLocal(rExp.type());

                //System.out.println("about to create rExpr: "+rExp);
                soot.Value rightRValue = createExpr(rExp);
                rightRValue = createCastIfNecessary(rightRValue, binary);
                //System.out.println("created rExpr: "+rightRValue);
            
                //soot.jimple.Stmt rAssign = soot.jimple.Jimple.v().newAssignStmt(rTemp, rightRValue);
                //body.getUnits().add(rAssign);
                //Util.addLineTag(rAssign, binary);
            
			    rhs = getBinaryExpr(leftRValue, rightRValue, binary.operator());
            }*/

        if (isComparisonBinary(binary.operator())) {
            rhs = getBinaryComparisonExpr(lVal, rVal, binary.operator());
        }
        else {
            rhs = getBinaryExpr(lVal, rVal, binary.operator());
        }
        
        if (rhs instanceof soot.jimple.BinopExpr) {
            Util.addPosTag(((soot.jimple.BinopExpr)rhs).getOp1Box(), binary.left().position());
		    Util.addPosTag(((soot.jimple.BinopExpr)rhs).getOp2Box(), binary.right().position());
        }
        
        if (rhs instanceof soot.jimple.ConditionExpr) {
            return rhs;
                //rhs = handleCondBinExpr((soot.jimple.ConditionExpr)rhs);
        }
        //}
        
        /*if (rhs instanceof soot.jimple.BinopExpr) {
            Util.addPosTag(((soot.jimple.BinopExpr)rhs).getOp1Box(), binary.left().position());
		    Util.addPosTag(((soot.jimple.BinopExpr)rhs).getOp2Box(), binary.right().position());
        }*/
       
        //System.out.println("rhs: "+rhs+" type: "+rhs.getType());
        
        soot.Local lhs = generateLocal(binary.type());


        soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(lhs, rhs);
        body.getUnits().add(assignStmt);
            
        Util.addLineTag(assignStmt, binary);
     
        Util.addPosTag(assignStmt.getRightOpBox(), binary.position());
        return lhs;
       // }
    } 
    
    private boolean isComparisonBinary(polyglot.ast.Binary.Operator op) {
        if ((op == polyglot.ast.Binary.EQ) || (op == polyglot.ast.Binary.NE) ||
           (op == polyglot.ast.Binary.GE) || (op == polyglot.ast.Binary.GT) ||
           (op == polyglot.ast.Binary.LE) || (op == polyglot.ast.Binary.LT)) {
            
            return true;
        }
        else {
            return false;
        }

        
    }
    
    private soot.Value createCastIfNecessary(soot.Value val, polyglot.ast.Binary binary) {
  
        if ((binary.type() != polyglot.types.PrimitiveType.DOUBLE) && (binary.type() != polyglot.types.PrimitiveType.FLOAT)) {
            return val;
        }
        // if the types dont match make a cast statement
        if (!val.getType().equals(Util.getSootType(binary.type()))) {
            soot.Local temp = generateLocal(binary.type());
            soot.Local rtemp = generateLocal(val.getType());
            soot.jimple.Stmt rAssign = soot.jimple.Jimple.v().newAssignStmt(rtemp, val);
            body.getUnits().add(rAssign);
            soot.jimple.CastExpr cast = soot.jimple.Jimple.v().newCastExpr(rtemp, Util.getSootType(binary.type()));
            soot.jimple.Stmt castAssign = soot.jimple.Jimple.v().newAssignStmt(temp, cast);
            body.getUnits().add(castAssign);
            return temp;
        }
        else {
            return val;
        }
    }
    
	/*private soot.Value getBinaryRhs(polyglot.ast.Expr lExp, polyglot.ast.Expr rExp, polyglot.ast.Binary.Operator operator){

		soot.Value lVal;
		soot.Value rVal;

		if (lExp instanceof polyglot.ast.Lit) {
			lVal = createLiteral((polyglot.ast.Lit)lExp);
		}
		else {
			lVal = getLocal((polyglot.ast.Local)lExp);
		}

		if (rExp instanceof polyglot.ast.Lit) {
			rVal = createLiteral((polyglot.ast.Lit)rExp);
            
		}
		else {
			rVal = getLocal((polyglot.ast.Local)rExp);
		}
         

        soot.Value binExpr = getBinaryExpr(lVal, rVal, operator);

        if (binExpr instanceof soot.jimple.BinopExpr) {
            Util.addPosTag(((soot.jimple.BinopExpr)binExpr).getOp1Box(), lExp.position());
		    Util.addPosTag(((soot.jimple.BinopExpr)binExpr).getOp2Box(), rExp.position());
        }
        return binExpr;

	}*/
    
	
	private soot.Value getBinaryExpr(soot.Value lVal, soot.Value rVal, polyglot.ast.Binary.Operator operator){
		
        soot.Value rValue = null;
		
		//System.out.println("in getBinaryExpr: l type: "+lVal.getType()+" r type: "+rVal.getType());
		//System.out.println("in getBinaryExpr: l : "+lVal+" r : "+rVal+" op: "+operator);

        if (lVal instanceof soot.jimple.ConditionExpr) {
            lVal = handleCondBinExpr((soot.jimple.ConditionExpr)lVal);
        }
        if (rVal instanceof soot.jimple.ConditionExpr) {
            rVal = handleCondBinExpr((soot.jimple.ConditionExpr)rVal);
        }
        if (operator == polyglot.ast.Binary.ADD){
            
            // this is not a jimple add expr its a string concat
            if (((lVal instanceof soot.jimple.StringConstant) || (rVal instanceof soot.jimple.StringConstant)) || ((lVal.getType().toString().equals("java.lang.String")) || (rVal.getType().toString().equals("java.lang.String")))){
               //System.out.println("Is concat!");
               rValue = getStringConcatLocal(lVal, rVal);
            }
		    else {	
            ////System.out.println("add operator");
                //System.out.println("is not concat");
			    rValue = soot.jimple.Jimple.v().newAddExpr(lVal, rVal);
            }
		}
		else if (operator == polyglot.ast.Binary.SUB){
			////System.out.println("sub operator");
			rValue = soot.jimple.Jimple.v().newSubExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.MUL){
			////System.out.println("mul operator");
            //System.out.println("mul binary left type: "+lVal.getType()+" right type: "+rVal.getType());
			rValue = soot.jimple.Jimple.v().newMulExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.DIV){
			////System.out.println("div operator");
            //System.out.println("div binary left type: "+lVal.getType()+" right type: "+rVal.getType());
			rValue = soot.jimple.Jimple.v().newDivExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.SHR){
			////System.out.println("shr operator");
			rValue = soot.jimple.Jimple.v().newShrExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.USHR){
			////System.out.println("ushr operator");
			rValue = soot.jimple.Jimple.v().newUshrExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.SHL){
			////System.out.println("shl operator");
			rValue = soot.jimple.Jimple.v().newShlExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.BIT_AND){
			////System.out.println("bit_and operator");
			rValue = soot.jimple.Jimple.v().newAndExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.BIT_OR){
			////System.out.println("bit_or operator");
			rValue = soot.jimple.Jimple.v().newOrExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.BIT_XOR){
			////System.out.println("bit_or operator");
			rValue = soot.jimple.Jimple.v().newXorExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.MOD){
			////System.out.println("mod - rem operator");
			rValue = soot.jimple.Jimple.v().newRemExpr(lVal, rVal);
		}
		else {
			throw new RuntimeException("Binary not yet handled!");
		}

        return rValue;
	}
 
    private soot.Value getBinaryComparisonExpr(soot.Value lVal, soot.Value rVal, polyglot.ast.Binary.Operator operator) {
		
        soot.Value rValue;
        
        //if ( !( isFDL( lVal ) ) && !(isFDL( rVal ) ) ) {
            if (operator == polyglot.ast.Binary.EQ){
			    ////System.out.println("eq operator");
			    rValue = soot.jimple.Jimple.v().newEqExpr(lVal, rVal);
		    }
		    else if (operator == polyglot.ast.Binary.GE){
			    ////System.out.println("ge operator");
			    rValue = soot.jimple.Jimple.v().newGeExpr(lVal, rVal);
		    }
		    else if (operator == polyglot.ast.Binary.GT){
			    ////System.out.println("gt operator");
			    rValue = soot.jimple.Jimple.v().newGtExpr(lVal, rVal);
		    }
		    else if (operator == polyglot.ast.Binary.LE){
			    ////System.out.println("le operator");
			    rValue = soot.jimple.Jimple.v().newLeExpr(lVal, rVal);
		    }
		    else if (operator == polyglot.ast.Binary.LT){
			    ////System.out.println("lt operator");
			    rValue = soot.jimple.Jimple.v().newLtExpr(lVal, rVal);
		    }
		    else if (operator == polyglot.ast.Binary.NE){
		    	////System.out.println("ne operator");
			    rValue = soot.jimple.Jimple.v().newNeExpr(lVal, rVal);
		    }
            else {
                throw new RuntimeException("Unknown Comparison Expr");
            }
       
            return rValue;
    }

    private soot.Value reverseCondition(soot.jimple.ConditionExpr cond) {
    
        soot.jimple.ConditionExpr newExpr;
        if (cond instanceof soot.jimple.EqExpr) {
            newExpr = soot.jimple.Jimple.v().newNeExpr(cond.getOp1(), cond.getOp2());
        }
        else if (cond instanceof soot.jimple.NeExpr) {
            newExpr = soot.jimple.Jimple.v().newEqExpr(cond.getOp1(), cond.getOp2());
        }
        else if (cond instanceof soot.jimple.GtExpr) {
            newExpr = soot.jimple.Jimple.v().newLeExpr(cond.getOp1(), cond.getOp2());
        }
        else if (cond instanceof soot.jimple.GeExpr) {
            newExpr = soot.jimple.Jimple.v().newLtExpr(cond.getOp1(), cond.getOp2());
        }
        else if (cond instanceof soot.jimple.LtExpr) {
            newExpr = soot.jimple.Jimple.v().newGeExpr(cond.getOp1(), cond.getOp2());
        }
        else if (cond instanceof soot.jimple.LeExpr) {
            newExpr = soot.jimple.Jimple.v().newGtExpr(cond.getOp1(), cond.getOp2());
        }
        else {
            throw new RuntimeException("Unknown Condition Expr");
        }


        newExpr.getOp1Box().addAllTagsOf(cond.getOp1Box());
        newExpr.getOp2Box().addAllTagsOf(cond.getOp2Box());
        //newExpr.addAllTagsOf(cond);
        return newExpr;
    }
        /*}
        else {
            if (isDouble(lVal) && isDouble(rVal)) {
                // no casts needed
            }
            else if (isFloat(lVal) && isFloat(rVal)) {
                // no casts needed
            }
            else if (isLong(lVal) && isLong(rVal)) {
                // no casts needed
            }
            else if (isDouble(lVal)) {
                // cast rVal to Double
                soot.Local newDouble = generateLocal(soot.DoubleType.v());
                soot.jimple.CastExpr cast = soot.jimple.Jimple.v().newCastExpr(rVal, soot.DoubleType.v());
                soot.jimple.Stmt castAssign = soot.jimple.Jimple.v().newAssignStmt(newDouble, cast);
                body.getUnits().add(castAssign);
                rVal = newDouble;
            }
            else if (isDouble(rVal)) {
                // cast lVal to Double
                soot.Local newDouble = generateLocal(soot.DoubleType.v());
                soot.jimple.CastExpr cast = soot.jimple.Jimple.v().newCastExpr(lVal, soot.DoubleType.v());
                soot.jimple.Stmt castAssign = soot.jimple.Jimple.v().newAssignStmt(newDouble, cast);
                body.getUnits().add(castAssign);
                lVal = newDouble;
            }
            else if (isFloat(lVal)) {
                // cast rVal to Float
                soot.Local newFloat = generateLocal(soot.FloatType.v());
                soot.jimple.CastExpr cast = soot.jimple.Jimple.v().newCastExpr(rVal, soot.FloatType.v());
                soot.jimple.Stmt castAssign = soot.jimple.Jimple.v().newAssignStmt(newFloat, cast);
                body.getUnits().add(castAssign);
                rVal = newFloat;
            }
            else if (isFloat(rVal)) {
                // cast lVal to Float
                soot.Local newFloat = generateLocal(soot.FloatType.v());
                soot.jimple.CastExpr cast = soot.jimple.Jimple.v().newCastExpr(lVal, soot.FloatType.v());
                soot.jimple.Stmt castAssign = soot.jimple.Jimple.v().newAssignStmt(newFloat, cast);
                body.getUnits().add(castAssign);
                lVal = newFloat;
            }
            else if (isLong(lVal)) {
                // cast rVal to Long
                soot.Local newLong = generateLocal(soot.LongType.v());
                soot.jimple.CastExpr cast = soot.jimple.Jimple.v().newCastExpr(rVal, soot.LongType.v());
                soot.jimple.Stmt castAssign = soot.jimple.Jimple.v().newAssignStmt(newLong, cast);
                body.getUnits().add(castAssign);
                rVal = newLong;
            }
            else if (isLong(rVal)) {
                // cast lVal to Long
                soot.Local newLong = generateLocal(soot.LongType.v());
                soot.jimple.CastExpr cast = soot.jimple.Jimple.v().newCastExpr(lVal, soot.LongType.v());
                soot.jimple.Stmt castAssign = soot.jimple.Jimple.v().newAssignStmt(newLong, cast);
                body.getUnits().add(castAssign);
                lVal = newLong;
            }
            else {
                // no casts needed
            }

*/
    private soot.Value handleDFLCond(soot.jimple.ConditionExpr cond){
        soot.Local result = generateLocal(soot.ByteType.v());
        soot.jimple.Expr cmExpr = null;
        if (isDouble(cond.getOp1()) || isDouble(cond.getOp2()) || isFloat(cond.getOp1()) || isFloat(cond.getOp2())) {
            // use cmpg and cmpl
            if ((cond instanceof soot.jimple.GeExpr) || (cond instanceof soot.jimple.GtExpr)) {
                // use cmpg
                cmExpr = soot.jimple.Jimple.v().newCmpgExpr(cond.getOp1(), cond.getOp2());
            }
            else {
                // use cmpl
                cmExpr = soot.jimple.Jimple.v().newCmplExpr(cond.getOp1(), cond.getOp2());
            }
        }
        else if (isLong(cond.getOp1()) || isLong(cond.getOp2())) {
            // use cmp
            cmExpr = soot.jimple.Jimple.v().newCmpExpr(cond.getOp1(), cond.getOp2());
        }
        else {
            return cond;
        }
        soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(result, cmExpr);
        body.getUnits().add(assign);

        if (cond instanceof soot.jimple.EqExpr){
	        cond = soot.jimple.Jimple.v().newEqExpr(result, soot.jimple.IntConstant.v(0));
		}
		else if (cond instanceof soot.jimple.GeExpr){
			cond = soot.jimple.Jimple.v().newGeExpr(result, soot.jimple.IntConstant.v(0));
		}
		else if (cond instanceof soot.jimple.GtExpr){
			cond = soot.jimple.Jimple.v().newGtExpr(result, soot.jimple.IntConstant.v(0));
		}
		else if (cond instanceof soot.jimple.LeExpr){
		    cond = soot.jimple.Jimple.v().newLeExpr(result, soot.jimple.IntConstant.v(0));
		}
		else if (cond instanceof soot.jimple.LtExpr){
		    cond = soot.jimple.Jimple.v().newLtExpr(result, soot.jimple.IntConstant.v(0));
		}
		else if (cond instanceof soot.jimple.NeExpr){
	        cond = soot.jimple.Jimple.v().newNeExpr(result, soot.jimple.IntConstant.v(0));
		}
        else {
            throw new RuntimeException("Unknown Comparison Expr");
        }
    
        return cond;
    }

    private boolean isDouble(soot.Value val) {
        if (val.getType() instanceof soot.DoubleType) return true;
        return false;
    }
    
    private boolean isFloat(soot.Value val) {
        if (val.getType() instanceof soot.FloatType) return true;
        return false;
    }
    
    private boolean isLong(soot.Value val) {
        if (val.getType() instanceof soot.LongType) return true;
        return false;
    }
    
    /*private boolean isFDL(soot.Value val) {
        if ((val.getType() instanceof soot.FloatType) ||
           (val.getType() instanceof soot.DoubleType) ||
           (val.getType() instanceof soot.LongType)) {
            return true;
        }
        else {
            return false;
        }
    }
    
    private void handleFloatsAndDoubles(soot.Value left, soot.Value right, polyglot.ast.Binary.Operator op) {
        if ((left.getType().equals(soot.DoubleType.v())) || (right.getType().equals(soot.FloatType.v()))) {
        }
        else if ((left.getType().equals(soot.DoubleType.v())) || (right.getType().equals(soot.DoubleType.v()))) {
            
        }
        else {
        }
    }*/
    
    private soot.Local createCondAnd(polyglot.ast.Binary binary) {
            
        soot.Local retLocal = generateLocal(soot.BooleanType.v());
            
        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
        
        soot.Value lVal = createExpr(binary.left());
        
        if (!(lVal instanceof soot.jimple.ConditionExpr)) {
            lVal = soot.jimple.Jimple.v().newEqExpr(lVal, soot.jimple.IntConstant.v(0));
        }
        else {
            lVal = reverseCondition((soot.jimple.ConditionExpr)lVal);
            lVal = handleDFLCond((soot.jimple.ConditionExpr)lVal);
        }
            
        soot.jimple.Stmt ifLeft = soot.jimple.Jimple.v().newIfStmt(lVal, noop1);
        body.getUnits().add(ifLeft);
        
        //soot.jimple.Stmt assign1 = soot.jimple.Jimple.v().newAssignStmt(retLocal, soot.jimple.IntConstant.v(0));
        //body.getUnits().add(assign1);

        soot.jimple.Stmt endNoop = soot.jimple.Jimple.v().newNopStmt();
        //soot.jimple.Stmt gotoEnd1 = soot.jimple.Jimple.v().newGotoStmt(endNoop);
        //body.getUnits().add(gotoEnd1);
            
        //body.getUnits().add(noop1);
            
        //soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
        soot.Value rVal = createExpr(binary.right());
        if (!(rVal instanceof soot.jimple.ConditionExpr)) {
            rVal = soot.jimple.Jimple.v().newEqExpr(rVal, soot.jimple.IntConstant.v(0));
        }
        else {
            rVal = reverseCondition((soot.jimple.ConditionExpr)rVal);
            rVal = handleDFLCond((soot.jimple.ConditionExpr)rVal);
        }
        
        soot.jimple.Stmt ifRight = soot.jimple.Jimple.v().newIfStmt(rVal, noop1);
        body.getUnits().add(ifRight);
        
        soot.jimple.Stmt assign1 = soot.jimple.Jimple.v().newAssignStmt(retLocal, soot.jimple.IntConstant.v(1));
        body.getUnits().add(assign1);
        soot.jimple.Stmt gotoEnd1 = soot.jimple.Jimple.v().newGotoStmt(endNoop);
        body.getUnits().add(gotoEnd1);
        
        body.getUnits().add(noop1);

        soot.jimple.Stmt assign2 = soot.jimple.Jimple.v().newAssignStmt(retLocal, soot.jimple.IntConstant.v(0));
        body.getUnits().add(assign2);

        body.getUnits().add(endNoop);
        
        return retLocal;
    }

    private soot.Local createCondOr(polyglot.ast.Binary binary) {
        soot.Local retLocal = generateLocal(soot.BooleanType.v());
            
        //end 
        soot.jimple.Stmt endNoop = soot.jimple.Jimple.v().newNopStmt();
         
        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
        soot.Value lVal = createExpr(binary.left());
        if (!(lVal instanceof soot.jimple.ConditionExpr)) {
            lVal = soot.jimple.Jimple.v().newEqExpr(lVal, soot.jimple.IntConstant.v(1));
        }
        else {
            lVal = handleDFLCond((soot.jimple.ConditionExpr)lVal);
        }
        
        soot.jimple.Stmt ifLeft = soot.jimple.Jimple.v().newIfStmt(lVal, noop1);
        body.getUnits().add(ifLeft);
        Util.addLineTag(ifLeft, binary.left());    
           
        soot.Value rVal = createExpr(binary.right());
        if (!(rVal instanceof soot.jimple.ConditionExpr)) {
            rVal = soot.jimple.Jimple.v().newEqExpr(rVal, soot.jimple.IntConstant.v(1));
        }
        else {
            rVal = handleDFLCond((soot.jimple.ConditionExpr)rVal);
        }
        soot.jimple.Stmt ifRight = soot.jimple.Jimple.v().newIfStmt(rVal, noop1);
        body.getUnits().add(ifRight);
        Util.addLineTag(ifRight, binary.right());    
        
        soot.jimple.Stmt assign2 = soot.jimple.Jimple.v().newAssignStmt(retLocal, soot.jimple.IntConstant.v(0));
        body.getUnits().add(assign2);
        Util.addLineTag(assign2, binary);
        soot.jimple.Stmt gotoEnd2 = soot.jimple.Jimple.v().newGotoStmt(endNoop);
        body.getUnits().add(gotoEnd2);
           
        body.getUnits().add(noop1);

        soot.jimple.Stmt assign3 = soot.jimple.Jimple.v().newAssignStmt(retLocal, soot.jimple.IntConstant.v(1));
        body.getUnits().add(assign3);
        Util.addLineTag(assign3, binary);

        body.getUnits().add(endNoop);

        return retLocal;
    }
    
    private soot.Local handleCondBinExpr(soot.jimple.ConditionExpr condExpr) {
    
        soot.Local boolLocal = generateLocal(soot.BooleanType.v());

        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();

        soot.jimple.Stmt ifStmt = soot.jimple.Jimple.v().newIfStmt(condExpr, noop1);
        body.getUnits().add(ifStmt);

        body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(boolLocal, soot.jimple.IntConstant.v(0)));

        soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
        
        soot.jimple.Stmt goto1 = soot.jimple.Jimple.v().newGotoStmt(noop2);

        body.getUnits().add(goto1);

        body.getUnits().add(noop1);
        
        body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(boolLocal, soot.jimple.IntConstant.v(1)));

        body.getUnits().add(noop2);

        return boolLocal;
        
        
    }
    
    private soot.Local getStringConcatLocal(soot.Value lVal, soot.Value rVal) {
   
        //System.out.println("Will concat: "+lVal+" and "+rVal);
        // create new StringBuffer and invoke it
        soot.Local local = generateLocal(soot.RefType.v(soot.Scene.v().getSootClass("java.lang.StringBuffer")));
        soot.jimple.NewExpr newExpr = soot.jimple.Jimple.v().newNewExpr(soot.RefType.v(soot.Scene.v().getSootClass("java.lang.StringBuffer")));
        soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(local, newExpr);
        
        body.getUnits().add(assign);
        soot.SootClass classToInvoke1 = soot.Scene.v().getSootClass("java.lang.StringBuffer");
        soot.SootMethod methodToInvoke1 = getMethodFromClass(classToInvoke1, "<init>", new ArrayList(), soot.VoidType.v()); 
        
        soot.jimple.SpecialInvokeExpr invoke = soot.jimple.Jimple.v().newSpecialInvokeExpr(local, methodToInvoke1);
            
        body.getUnits().add(soot.jimple.Jimple.v().newInvokeStmt(invoke));


        // generate appends for left and right
        local = generateAppendStmts(lVal, local);
        local = generateAppendStmts(rVal, local);
        
        // invoke toString on local (type StringBuffer)
        soot.Local newString = generateLocal(soot.RefType.v(soot.Scene.v().getSootClass("java.lang.String")));
        soot.SootClass classToInvoke2 = soot.Scene.v().getSootClass("java.lang.StringBuffer");
        soot.SootMethod methodToInvoke2 = getMethodFromClass(classToInvoke2, "toString", new ArrayList(), soot.RefType.v(soot.Scene.v().getSootClass("java.lang.String"))); 
                 
        soot.jimple.VirtualInvokeExpr toStringInvoke = soot.jimple.Jimple.v().newVirtualInvokeExpr(local, methodToInvoke2);
                
        soot.jimple.Stmt lastAssign = soot.jimple.Jimple.v().newAssignStmt(newString, toStringInvoke);

        body.getUnits().add(lastAssign);
        
        return newString; 
    }

    private soot.Local generateAppendStmts(soot.Value toApp, soot.Local base) {

        soot.Type appendType = null;
        if (toApp instanceof soot.jimple.StringConstant) {
            appendType = soot.RefType.v(soot.Scene.v().getSootClass("java.lang.String"));
        }
        else if (toApp instanceof soot.jimple.Constant) {
            appendType = toApp.getType();
        }
        else if (toApp instanceof soot.Local) {
            if (((soot.Local)toApp).getType() instanceof soot.PrimType) {
                appendType = ((soot.Local)toApp).getType();   
            }
            else if (((soot.Local)toApp).getType() instanceof soot.RefType) {
                if (((soot.Local)toApp).getType().toString().equals("java.lang.String")){
                    appendType = soot.RefType.v(soot.Scene.v().getSootClass("java.lang.String"));
                }
                else if (((soot.Local)toApp).getType().toString().equals("java.lang.StringBuffer")){
                    appendType = soot.RefType.v(soot.Scene.v().getSootClass("java.lang.StringBuffer"));
                }
                else{
                    appendType = soot.RefType.v(soot.Scene.v().getSootClass("java.lang.Object"));
                }
            }
            //else {
                // invoke toString and return
                /*soot.Local newString = generateLocal(soot.RefType.v(soot.Scene.v().getSootClass("java.lang.String")));
                soot.SootClass classToInvoke = soot.Scene.v().getSootClass("java.lang.String");
                soot.SootMethod methodToInvoke = getMethodFromClass(classToInvoke, "toString", new ArrayList(), soot.RefType.v(soot.Scene.v().getSootClass("java.lang.String"))); 
                 
                soot.jimple.VirtualInvokeExpr toStringInvoke = soot.jimple.Jimple.v().newVirtualInvokeExpr((soot.Local)toApp, methodToInvoke);
                
                soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(newString, toStringInvoke);

                body.getUnits().add(assign);
                appendType = soot.RefType.v(soot.Scene.v().getSootClass("java.lang.String"));
                toApp = newString;*/
            //    appendType = toApp.getType();
            //}
        }
        else if (toApp instanceof soot.jimple.ConditionExpr) {
            //System.out.println("toApp is CondExpr");
            toApp = handleCondBinExpr((soot.jimple.ConditionExpr)toApp);
            //System.out.println("toApp: "+toApp+" type: "+toApp.getType());
            appendType = soot.BooleanType.v();
        }

        // handle shorts
        if (appendType instanceof soot.ShortType) {
            soot.Local intLocal = generateLocal(soot.IntType.v());
            soot.jimple.Expr cast = soot.jimple.Jimple.v().newCastExpr(toApp, soot.IntType.v());
            soot.jimple.Stmt castAssign = soot.jimple.Jimple.v().newAssignStmt(intLocal, cast);
            body.getUnits().add(castAssign);
            toApp = intLocal;
            appendType = soot.IntType.v();
        }
        
        ArrayList paramsTypes = new ArrayList();
        paramsTypes.add(appendType);
        ArrayList params = new ArrayList();
        params.add(toApp);

        soot.SootClass classToInvoke = soot.Scene.v().getSootClass("java.lang.StringBuffer");
        soot.SootMethod methodToInvoke = getMethodFromClass(classToInvoke, "append", paramsTypes, soot.RefType.v(soot.Scene.v().getSootClass("java.lang.StringBuffer")));

        soot.jimple.VirtualInvokeExpr appendInvoke = soot.jimple.Jimple.v().newVirtualInvokeExpr(base, methodToInvoke, params);

        soot.Local nextSB = generateLocal(soot.RefType.v(soot.Scene.v().getSootClass("java.lang.StringBuffer")));

        soot.jimple.Stmt appendAssign = soot.jimple.Jimple.v().newAssignStmt(nextSB, appendInvoke);

        body.getUnits().add(appendAssign);

        return nextSB;
    }

    /**
     * Unary Expression Creation
     */
    private  soot.Local getUnaryLocal(polyglot.ast.Unary unary) {
        
        polyglot.ast.Expr expr = unary.expr();
        polyglot.ast.Unary.Operator op = unary.operator();

        if (op == polyglot.ast.Unary.POST_INC){
            soot.Local retLocal = generateLocal(expr.type());
            soot.Value sootExpr = createExpr(expr);

            soot.jimple.AssignStmt preStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, sootExpr);
            //Util.addPosTag(preStmt.getRightOpBox(), expr.position());
            body.getUnits().add(preStmt);

            soot.jimple.AddExpr addExpr = soot.jimple.Jimple.v().newAddExpr(sootExpr, getConstant(retLocal.getType(), 1));
            
            Util.addPosTag(addExpr.getOp1Box(), expr.position());
            
            soot.Local local = generateLocal(expr.type());
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(local, addExpr);
            body.getUnits().add(stmt);

            body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(sootExpr, local));

            if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, local));
            }
            return retLocal;
            
        }
        else if (op == polyglot.ast.Unary.POST_DEC) {
            soot.Local retLocal = generateLocal(expr.type());
            
            soot.Value sootExpr = createExpr(expr);

            soot.jimple.AssignStmt preStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, sootExpr);
            body.getUnits().add(preStmt);
            
            soot.jimple.SubExpr subExpr = soot.jimple.Jimple.v().newSubExpr(sootExpr, getConstant(retLocal.getType(), 1));
            Util.addPosTag(subExpr.getOp1Box(), expr.position());
            
            soot.Local local = generateLocal(expr.type());
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(local, subExpr);
            body.getUnits().add(stmt);

            body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(sootExpr, local));
            if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, local));
            }

            return retLocal;
            
        }
        else if (op == polyglot.ast.Unary.PRE_INC) {
            //System.out.println("Unary op: PRE_INC");
            
            soot.Value sootExpr = createExpr(expr);
          
            soot.jimple.AddExpr addExpr = soot.jimple.Jimple.v().newAddExpr(sootExpr, getConstant(sootExpr.getType(), 1));
            Util.addPosTag(addExpr.getOp1Box(), expr.position());

            soot.Local local = generateLocal(expr.type());
            
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(local, addExpr);

            body.getUnits().add(stmt);
            
            if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess) || (expr instanceof polyglot.ast.Local)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, local));
            }

            return local;
            
        }
        else if (op == polyglot.ast.Unary.PRE_DEC) {
            //System.out.println("Unary op: PRE_DEC");
            
            soot.Value sootExpr = createExpr(expr);
          
            soot.jimple.SubExpr subExpr = soot.jimple.Jimple.v().newSubExpr(sootExpr, getConstant(sootExpr.getType(), 1));
            Util.addPosTag(subExpr.getOp1Box(), expr.position());
            
            soot.Local local = generateLocal(expr.type());
            
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(local, subExpr);

            body.getUnits().add(stmt);
            
            if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess) || (expr instanceof polyglot.ast.Local)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, local));
            }

            return local;
            
        }
        else if (op == polyglot.ast.Unary.BIT_NOT) {
            //System.out.println("Unary op: BIT_NOT");
            soot.jimple.IntConstant int1 = soot.jimple.IntConstant.v(-1);
            
            soot.Local retLocal = generateLocal(expr.type());
            
            soot.Value sootExpr = createExpr(expr);
            
            soot.jimple.Stmt assign1 = soot.jimple.Jimple.v().newAssignStmt(retLocal, soot.jimple.Jimple.v().newXorExpr(sootExpr, getConstant(sootExpr.getType(), -1)));

            body.getUnits().add(assign1);
            
            /*if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess) || (expr instanceof polyglot.ast.Local)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, retLocal));
            }*/
            
            return retLocal;
        }
        else if (op == polyglot.ast.Unary.NEG) {
            //System.out.println("Unary op: NEG");
            //System.out.println("Expr is: "+expr.getClass());
            soot.Value sootExpr;
            if (expr instanceof polyglot.ast.NumLit) {
                int intVal = (int)((polyglot.ast.NumLit)expr).longValue();
                sootExpr = soot.jimple.IntConstant.v(-intVal);
            }
            else if (expr instanceof polyglot.ast.FloatLit){
                double doubleVal = ((polyglot.ast.FloatLit)expr).value();
                sootExpr = soot.jimple.DoubleConstant.v(-doubleVal);
            }
            else {
                soot.Value local = createExpr(expr);

                soot.jimple.NegExpr negExpr = soot.jimple.Jimple.v().newNegExpr(local);
                sootExpr = negExpr;
                Util.addPosTag(negExpr.getOpBox(), expr.position());
            }
            
            soot.Local retLocal = generateLocal(expr.type());

            soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(retLocal, sootExpr);

            body.getUnits().add(assign);
            
            /*if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess) || (expr instanceof polyglot.ast.Local)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, retLocal));
            }*/
            
            return retLocal;

        }
        else if (op == polyglot.ast.Unary.POS) {
            //System.out.println("Unary op: POS");
            soot.Local retLocal = generateLocal(expr.type());
            soot.Value sootExpr = createExpr(expr);
            soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(retLocal, sootExpr);
            body.getUnits().add(assign);
            
            /*if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess) || (expr instanceof polyglot.ast.Local)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, retLocal));
            }*/
            
            return retLocal;
        }
        else if (op == polyglot.ast.Unary.NOT) {
            //System.out.println("Unary op: NOT");

            soot.Value local = createExpr(expr);
            
            soot.jimple.NeExpr neExpr = soot.jimple.Jimple.v().newNeExpr(local, getConstant(local.getType(), 0));

            soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();

            soot.jimple.Stmt ifStmt = soot.jimple.Jimple.v().newIfStmt(neExpr, noop1);
            body.getUnits().add(ifStmt);

            soot.Local retLocal = generateLocal(local.getType());

            soot.jimple.Stmt assign1 = soot.jimple.Jimple.v().newAssignStmt(retLocal, getConstant(retLocal.getType(), 1));

            body.getUnits().add(assign1);

            soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
            
            soot.jimple.Stmt goto1 = soot.jimple.Jimple.v().newGotoStmt(noop2);

            body.getUnits().add(goto1);
            
            body.getUnits().add(noop1);
            
            soot.jimple.Stmt assign2 = soot.jimple.Jimple.v().newAssignStmt(retLocal, getConstant(retLocal.getType(), 0));

            body.getUnits().add(assign2);

            body.getUnits().add(noop2);
            
            /*if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, local));
            }*/
            
            return retLocal;
        }
        else { 
            //System.out.println("Unhandled Unary Expr: "+op.getClass());
            //if (op == polyglot.ast.Unary.POST_DEC)
            throw new RuntimeException("Unhandled Unary Expr");    
        }

        
    }

    private soot.jimple.Constant getConstant(soot.Type type, int val) {
    
        if (type instanceof soot.DoubleType) {
            return soot.jimple.DoubleConstant.v(val);
        }
        else if (type instanceof soot.FloatType) {
            return soot.jimple.FloatConstant.v(val);
        }
        else if (type instanceof soot.LongType) {
            return soot.jimple.LongConstant.v(val);
        }
        else {
            return soot.jimple.IntConstant.v(val);
        }
    }
    /**
     * Cast Expression Creation
     */
    private soot.Local getCastLocal(polyglot.ast.Cast castExpr){
   
        //System.out.println("getting cast local: "+castExpr.expr()+" type: "+castExpr.type());

        soot.Value val;
        if (castExpr.expr() instanceof polyglot.ast.Cast) {
            val = createExpr(((polyglot.ast.Cast)castExpr.expr()).expr());
        }
        else {
            val = createExpr(castExpr.expr());
        }
        soot.Type type = Util.getSootType(castExpr.type());

        soot.jimple.CastExpr cast = soot.jimple.Jimple.v().newCastExpr(val, type);
        Util.addPosTag(cast.getOpBox(), castExpr.position().line(), castExpr.position().column() + castExpr.toString().indexOf(')') );
        soot.Local retLocal = generateLocal(cast.getCastType());

        soot.jimple.Stmt castAssign = soot.jimple.Jimple.v().newAssignStmt(retLocal, cast);
        body.getUnits().add(castAssign);
        Util.addLineTag(castAssign, castExpr);

        return retLocal;
    }
    
    /**
     * Procedure Call Helper Methods
     */
    private ArrayList getSootParams(polyglot.ast.ProcedureCall call) {
        
        ArrayList sootParams = new ArrayList();
        Iterator it = call.arguments().iterator();
        while (it.hasNext()) {
            polyglot.ast.Expr next = (polyglot.ast.Expr)it.next();
            //System.out.println("will create sootParam from: "+next+" type: "+next.type());
            sootParams.add(createExpr(next));
        }
        return sootParams;
    }
    
    private ArrayList getSootParamsTypes(polyglot.ast.ProcedureCall call) {
        
        ArrayList sootParamsTypes = new ArrayList();
        //System.out.println("getting param types: call: "+call);
        Iterator it = call.procedureInstance().formalTypes().iterator();
        while (it.hasNext()) {
            Object next = it.next();
            //System.out.println("next type: "+next);
            sootParamsTypes.add(Util.getSootType((polyglot.types.Type)next));
        }
        return sootParamsTypes;
    }

    private soot.SootMethod getMethodFromClass(soot.SootClass sootClass, String name, ArrayList paramTypes, soot.Type returnType) {
        //System.out.println("in getMethodFromClass: sootClass: "+sootClass+" paramTypes.size() "+paramTypes.size());
        return sootClass.getMethod(name, paramTypes, returnType);
    }
    
    /**
     * Constructor Call Creation
     */
    private void createConstructorCall(polyglot.ast.ConstructorCall cCall) {
        
        ArrayList sootParams = getSootParams(cCall);
        ArrayList sootParamsTypes = getSootParamsTypes(cCall);
        
        ////System.out.println("cCall.kind: "+cCall.kind());
        ////System.out.println("cCall.qualifier: "+cCall.qualifier());
        polyglot.types.ConstructorInstance cInst = cCall.constructorInstance();
        String containerName = null;
        if (cInst.container() instanceof polyglot.types.ClassType) { 
            containerName = ((polyglot.types.ClassType)cInst.container()).fullName();
            if (((polyglot.types.ClassType)cInst.container()).isNested()){
                containerName = fixInnerClassName((polyglot.types.ClassType)cInst.container());
            }
          //  //System.out.println("cInst: "+containerName);
        }
        
        soot.SootClass classToInvoke;
           
        if (cCall.kind() == polyglot.ast.ConstructorCall.SUPER) {

            classToInvoke = soot.Scene.v().getSootClass(containerName);
        }
        else if (cCall.kind() == polyglot.ast.ConstructorCall.THIS) {
            classToInvoke = body.getMethod().getDeclaringClass();
        }
        else {
            throw new RuntimeException("Unknown kind of Constructor Call");
        }
            
        soot.Local base = specialThisLocal;
        
       
        int index = classToInvoke.getName().indexOf("$");
        if (index != -1) {
            soot.SootClass outerClass = soot.Scene.v().getSootClass(classToInvoke.getName().substring(0, index));
            sootParamsTypes.add(outerClass.getType());
            soot.Local fieldRefLocal = generateLocal(outerClass.getType());
            if (body.getMethod().getDeclaringClass().getName().indexOf("$") == -1) {
                // not an inner class but your invoking one
                sootParams.add(specialThisLocal);
            }
            else {
                if (body.getMethod().getDeclaringClass().getName().equals(classToInvoke.getName())) {
                    // are an inner class and your invoking yourself
                    System.out.println("Are an inner class and invoking self"); 
                    //soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(specialThisLocal, body.getMethod().getDeclaringClass().getField("this$0", outerClass.getType()));
                    //soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(fieldRefLocal, fieldRef);
                    //body.getUnits().add(assignStmt);
                    sootParams.add(outerClassParamLocal);
                }
                else {
                    // are an inner class and your invoking some other inner class
                    sootParams.add(specialThisLocal);
                }
            }
        }
        soot.SootMethod methodToInvoke = getMethodFromClass(classToInvoke, "<init>", sootParamsTypes, soot.VoidType.v());
        
        soot.jimple.SpecialInvokeExpr specialInvokeExpr = soot.jimple.Jimple.v().newSpecialInvokeExpr(base, methodToInvoke, sootParams);

        soot.jimple.Stmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(specialInvokeExpr);
        
        body.getUnits().add(invokeStmt);
        Util.addLineTag(invokeStmt, cCall);
        
        int numParams = 0;
        Iterator invokeParamsIt = cCall.arguments().iterator();
        while (invokeParamsIt.hasNext()) {
            Util.addPosTag(specialInvokeExpr.getArgBox(numParams), ((polyglot.ast.Expr)invokeParamsIt.next()).position());
            numParams++;
        }
       
        // handle init of outer class this if inner class
        //if (body.getMethod().getName().equals("<init>")){
       //     handleOuterClassThisInit(body.getMethod());
        //}
        
        // if method is <init> handle field inits
        if (body.getMethod().getName().equals("<init>") && (cCall.kind() == polyglot.ast.ConstructorCall.SUPER)){
            
            handleOuterClassThisInit(body.getMethod());
    
            handleFieldInits(body.getMethod());
            handleInitializerBlocks(body.getMethod());
        }
        
    }
    /**
     * Local Class Decl - Local Inner Class
     */
    private void createLocalClassDecl(polyglot.ast.LocalClassDecl cDecl) {
        polyglot.ast.ClassBody cBody = cDecl.decl().body();
        Iterator it = cBody.members().iterator();
        while(it.hasNext()){
            Object next = it.next();
            System.out.println("Local Class Decl: "+next);
            if (next instanceof polyglot.ast.ConstructorDecl) {
                Iterator formalsIt = ((polyglot.ast.ConstructorDecl)next).formals().iterator();
                while (formalsIt.hasNext()) {
                    System.out.println(formalsIt.next());
                }
            }
        }
    }
    
    /**
     * New Expression Creation
     */
    private soot.Local getNewLocal(polyglot.ast.New newExpr) {

        if (newExpr.body() != null) {
            Iterator it = newExpr.body().members().iterator();
            while (it.hasNext()) {
                Object next = it.next();
                System.out.println(next);
            }
        }
        // handle parameters/args
        ArrayList sootParams = getSootParams(newExpr);
        ArrayList sootParamsTypes = getSootParamsTypes(newExpr);
       
        System.out.println(newExpr.objectType());
        soot.RefType sootType = (soot.RefType)Util.getSootType(newExpr.objectType().type());
        soot.Local retLocal = generateLocal(sootType);
        soot.jimple.NewExpr sootNew = soot.jimple.Jimple.v().newNewExpr(sootType);

        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, sootNew);
        body.getUnits().add(stmt);
        Util.addLineTag(stmt, newExpr);

        //System.out.println("newExpr objetcType: "+newExpr.objectType().toString());
        //System.out.println("newExpr constructor inst container: "+newExpr.constructorInstance().container());
        
        //System.out.println("newExpr procedure inst container: "+newExpr.procedureInstance().container());
        
        String className = newExpr.objectType().toString();

        if (newExpr.objectType().type() instanceof polyglot.types.ClassType) {
            //System.out.println("obj type is Class");
            polyglot.types.ClassType classType = (polyglot.types.ClassType)newExpr.objectType().type();
            if (classType.isNested()) {
                className = fixInnerClassName(classType);
            }
        }
        //System.out.println("className: "+className);
        soot.SootClass classToInvoke = soot.Scene.v().getSootClass(className);
        
        int index = classToInvoke.getName().indexOf("$");
        if (index != -1) {
            soot.SootClass outerClass = soot.Scene.v().getSootClass(classToInvoke.getName().substring(0, index));
            sootParamsTypes.add(outerClass.getType());
            soot.Local fieldRefLocal = generateLocal(outerClass.getType());
            System.out.println("this class: "+body.getMethod().getDeclaringClass().getName());
            if (body.getMethod().getDeclaringClass().getName().indexOf("$") == -1) {
                // not an inner class but your invoking one
                sootParams.add(specialThisLocal);
            }
            else {
                if (body.getMethod().getDeclaringClass().getName().equals(classToInvoke.getName())) {
                    // are an inner class and your invoking yourself
                
                    soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(specialThisLocal, body.getMethod().getDeclaringClass().getField("this$0", outerClass.getType()));
                    soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(fieldRefLocal, fieldRef);
                    body.getUnits().add(assignStmt);
                    sootParams.add(fieldRefLocal);
                }
                else {
                    // are an inner class and your invoking some other inner class
                    sootParams.add(specialThisLocal);
                }
            }
        }
        
        soot.SootMethod methodToInvoke = getMethodFromClass(classToInvoke, "<init>", sootParamsTypes, soot.VoidType.v());
        
        soot.jimple.SpecialInvokeExpr specialInvokeExpr = soot.jimple.Jimple.v().newSpecialInvokeExpr(retLocal, methodToInvoke, sootParams);

        soot.jimple.Stmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(specialInvokeExpr);

        body.getUnits().add(invokeStmt);
        Util.addLineTag(invokeStmt, newExpr);
        
        int numParams = 0;
        Iterator invokeParamsIt = newExpr.arguments().iterator();
        while (invokeParamsIt.hasNext()) {
            Util.addPosTag(specialInvokeExpr.getArgBox(numParams), ((polyglot.ast.Expr)invokeParamsIt.next()).position());
            numParams++;
        }
        
        
        return retLocal;
    }
	
    /**
     * Call Expression Creation
     */
    private soot.Local getCallLocal(polyglot.ast.Call call){
        
        ////System.out.println("Call:"+call.toString());
		

        // handle args/parameters
        //ArrayList sootParamsTypes = getSootParamsTypes(call);
        //ArrayList sootParams = getSootParams(call);
        
        // handle name
		String name = call.name();
		////System.out.println("Call Name: "+name);

        ////System.out.println("Call method container: "+call.methodInstance().container());
        ////System.out.println("Call procedure container: "+call.procedureInstance().container());
        // handle receiver/target
		polyglot.ast.Receiver receiver = call.target();
        ////System.out.println("receiver className: "+getClassName(receiver));
        ////System.out.println("receiver type: "+receiver.type());
       
        
        //String className = getClassName(receiver);
        //String className = receiver.type().toString();
        ////System.out.println("receiver className: "+getClassName(receiver));
        ////System.out.println("receiver type: "+receiver.type());
        ////System.out.println("Call: receiver className: "+className);
        //soot.SootClass receiverClass = soot.Scene.v().getSootClass(className);
         
        soot.Local baseLocal = (soot.Local)getBaseLocal(receiver);
        ////System.out.println("Call: receiver: "+receiver); 
       
        String receiverTypeClassName;
        if (receiver.type() instanceof polyglot.types.ClassType) {
            polyglot.types.ClassType ct = (polyglot.types.ClassType)receiver.type();
            if (ct.isNested()) {
                receiverTypeClassName = fixInnerClassName(ct);
                
            }
            else {
                receiverTypeClassName = receiver.type().toString();
            }
            
        }
        else if (receiver.type() instanceof polyglot.types.ArrayType) {
            receiverTypeClassName = "java.lang.Object";
        }
        else {
            receiverTypeClassName = receiver.type().toString();
        }
        soot.SootClass receiverTypeClass = soot.Scene.v().getSootClass(receiverTypeClassName);
        
        //createReceiver(receiver, receiverClass, baseLocal);
        
        // create the actual invoke
        
		polyglot.types.MethodInstance methodInstance = call.methodInstance();
		////System.out.println("Call MethodInstance: "+methodInstance);
		////System.out.println("Return Type: "+methodInstance.returnType().toString());

        soot.Type sootRetType = Util.getSootType(methodInstance.returnType());
		////System.out.println("Name: "+methodInstance.name());
		////System.out.println("Signature: "+methodInstance.signature());
        
        //
        // overrides
        ////System.out.println("Formals:");
        //Iterator formalsIt = methodInstance.formalTypes().iterator();
        //while (formalsIt.hasNext()){
        //    //System.out.println(((polyglot.types.Type)formalsIt.next()));
        //}
        //soot.SootMethod callMethod = receiverTypeClass.getMethod(methodInstance.signature());
        // handle args/parameters
        ArrayList sootParamsTypes = getSootParamsTypes(call);
        ArrayList sootParams = getSootParams(call);
      
        soot.SootMethod callMethod = getMethodFromClass(receiverTypeClass, methodInstance.name(), sootParamsTypes, sootRetType);
        //soot.SootMethod callMethod = receiverTypeClass.getMethod(methodInstance.name(), sootParamsTypes, sootRetType);
        ////System.out.println("Call soot method: "+callMethod);

        boolean isPrivateAccess = false;
        soot.javaToJimple.PolyglotMethodSource ms = (soot.javaToJimple.PolyglotMethodSource)body.getMethod().getSource();
        if ((ms.getPrivateAccessMap() != null) && (ms.getPrivateAccessMap().containsKey(call.methodInstance()))){
            callMethod = (soot.SootMethod)ms.getPrivateAccessMap().get(call.methodInstance());
            if (!call.methodInstance().flags().isStatic()){
                sootParams.add(baseLocal);
            }
            isPrivateAccess = true;
        }

        // decide which invoke 
		
        soot.jimple.InvokeExpr invokeExpr; 
        ////System.out.println("receiverClass: modifiers: "+receiverClass.getModifiers());
        ////System.out.println("receiverClass: "+receiverClass.getName());
        ////System.out.println("receiverTypeClass: "+receiverTypeClass.getName());
        if (isPrivateAccess){
            invokeExpr = soot.jimple.Jimple.v().newStaticInvokeExpr(callMethod, sootParams);
        }
        else if (soot.Modifier.isInterface(receiverTypeClass.getModifiers())) {
            invokeExpr = soot.jimple.Jimple.v().newInterfaceInvokeExpr(baseLocal, callMethod, sootParams);
        }
        else if (methodInstance.flags().isStatic()){
            // if flag isStatic -> static invoke
            invokeExpr = soot.jimple.Jimple.v().newStaticInvokeExpr(callMethod, sootParams);
        }
        else if (methodInstance.flags().isPrivate()){
            // if flag isPrivate -> special invoke
            invokeExpr = soot.jimple.Jimple.v().newSpecialInvokeExpr(baseLocal, callMethod, sootParams);
        }
        else if ((receiver instanceof polyglot.ast.Special) &&
            (((polyglot.ast.Special)receiver).kind() == polyglot.ast.Special.SUPER)){
            invokeExpr = soot.jimple.Jimple.v().newSpecialInvokeExpr(baseLocal, callMethod, sootParams);
        }   
        else {
            // else virtual invoke
            invokeExpr = soot.jimple.Jimple.v().newVirtualInvokeExpr(baseLocal, callMethod, sootParams);

            ////System.out.println("VirtualInvoke: "+invokeExpr);
        }

        int numParams = 0;
        Iterator callParamsIt = call.arguments().iterator();
        while (callParamsIt.hasNext()) {
            Util.addPosTag(invokeExpr.getArgBox(numParams), ((polyglot.ast.Expr)callParamsIt.next()).position());
            numParams++;
        }
        
        // create an assign stmt so invoke can be used somewhere else

        if (invokeExpr.getMethod().getReturnType().equals(soot.VoidType.v())) {
            soot.jimple.Stmt invoke = soot.jimple.Jimple.v().newInvokeStmt(invokeExpr);
            body.getUnits().add(invoke);
            Util.addLineTag(invoke, call);
            return null;
        }
        else {
            soot.Local retLocal = generateLocal(invokeExpr.getMethod().getReturnType());
        
            soot.jimple.Stmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, invokeExpr);
        
            // add assign stmt to body
            body.getUnits().add(assignStmt);
		    Util.addLineTag(assignStmt, call);
            
	    	////System.out.println("Flags: "+methodInstance.flags().toString());
		    ////System.out.println("End Call");
            //createJimple();
        
            ////System.out.println("End of Call: retLocal type: "+retLocal.getType());
            return retLocal;
        }
	}
   
    private String getClassNameForField(String f) {
        return f.substring(0, f.lastIndexOf("."));
    }
    
    private String getClassName(polyglot.ast.Receiver receiver) {
       
        //System.out.println("reciever type in getClassName: "+receiver.type());
        if (receiver instanceof polyglot.ast.Field) {
            String result = getClassNameForField(receiver.toString());
            if (result.equals("this")) {
                soot.Local local = specialThisLocal;
                return local.getType().toString();
            }
            else {
                return receiver.type().toString();
                //return result;
            }
        }
        else if (receiver.type() instanceof polyglot.types.ClassType) {
            polyglot.types.ClassType ct = (polyglot.types.ClassType)receiver.type();
            if (ct.isNested()){
                //System.out.println("type is inner class");

                return fixInnerClassName(ct);
            }
            
            return receiver.type().toString();
        }
        else if (receiver.type() instanceof polyglot.types.ArrayType){
            return "java.lang.Object";
        }
        else {
            
            return receiver.type().toString();
        }
    }
    

    private String fixInnerClassName(polyglot.types.ClassType innerClass){
                
        String fullName = innerClass.fullName();
                
        while (innerClass.isNested()){
                    
            StringBuffer sb = new StringBuffer(fullName);
                    
            int lastDot = fullName.lastIndexOf(".");
            if (lastDot != -1) {
                sb.replace(lastDot, lastDot+1, "$");
                fullName = sb.toString();
            }
            innerClass = innerClass.outer();
        }

        return fullName;
    }

    private soot.Value getBaseLocal(polyglot.ast.Receiver receiver) {
      
        System.out.println("receiver class: "+receiver.getClass().toString());
        if (receiver instanceof polyglot.ast.TypeNode) {
            return generateLocal(((polyglot.ast.TypeNode)receiver).type());
        }
        else {
            soot.Value val = createExpr((polyglot.ast.Expr)receiver);
            if (val instanceof soot.jimple.Constant) {
                soot.Local retLocal = generateLocal(val.getType());
                soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, val);
                body.getUnits().add(stmt);
                return retLocal;
            }
            return val;
        }
    }

    /**
     * NewArray Expression Creation
     */
    private soot.Local getNewArrayLocal(polyglot.ast.NewArray newArrExpr) {
    
        //System.out.println("New Array Expr before getType: "+newArrExpr.type());
        soot.Type sootType = Util.getSootType(newArrExpr.type());

        //System.out.println("New Array Expr got Type: "+sootType.getClass().toString());
        soot.jimple.Expr expr;
        //System.out.println("newArrExpr: "+newArrExpr);
        //System.out.println("newArrExpr.numDims(): "+newArrExpr.numDims());
        //System.out.println("newArrExpr.dims().size(): "+newArrExpr.dims().size());
        if (newArrExpr.numDims() == 1) {
           
            soot.Value dimLocal;
            if (newArrExpr.additionalDims() == 1) {
                dimLocal = soot.jimple.IntConstant.v(1);
            }
            else {
                dimLocal = createExpr((polyglot.ast.Expr)newArrExpr.dims().get(0));
            }
            
            soot.jimple.NewArrayExpr newArrayExpr = soot.jimple.Jimple.v().newNewArrayExpr(((soot.ArrayType)sootType).getElementType(), dimLocal);
            expr = newArrayExpr;
            if (newArrExpr.additionalDims() != 1){
                Util.addPosTag(newArrayExpr.getSizeBox(), ((polyglot.ast.Expr)newArrExpr.dims().get(0)).position());
            }
        }
        else {
        
            ArrayList valuesList = new ArrayList();
            Iterator it = newArrExpr.dims().iterator();
            while (it.hasNext()){
                valuesList.add(createExpr((polyglot.ast.Expr)it.next()));
            }

            if (newArrExpr.additionalDims() != 0) {
                valuesList.add(soot.jimple.IntConstant.v(newArrExpr.additionalDims()));
            }
            //System.out.println("Type for new multi array: "+sootType);
            //System.out.println("class: "+sootType.getClass().toString());
            soot.jimple.NewMultiArrayExpr newMultiArrayExpr = soot.jimple.Jimple.v().newNewMultiArrayExpr((soot.ArrayType)sootType, valuesList);

            
            expr = newMultiArrayExpr;
            Iterator sizeBoxIt = newArrExpr.dims().iterator();
            int counter = 0;
            while (sizeBoxIt.hasNext()){
                Util.addPosTag(newMultiArrayExpr.getSizeBox(counter), ((polyglot.ast.Expr)sizeBoxIt.next()).position());
                counter++;
            }
        }

        soot.Local retLocal = generateLocal(sootType);
        
        soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, expr);
        
        body.getUnits().add(stmt);
            
        Util.addLineTag(stmt, newArrExpr);
        Util.addPosTag(stmt.getRightOpBox(), newArrExpr.position());
        
        // handle array init if one exists
        if (newArrExpr.init() != null) {
            soot.Value initVal = getArrayInitLocal(newArrExpr.init(), newArrExpr.type());
            soot.jimple.AssignStmt initStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, initVal);
        
            body.getUnits().add(initStmt);
            
        }
        
        return retLocal;
  
    }

    /**
     * create ArrayIniit given init and the array local
     */
    private soot.Local getArrayInitLocal(polyglot.ast.ArrayInit arrInit, polyglot.types.Type lhsType) {
   
        //System.out.println("arrInit: "+arrInit);
        //System.out.println("lhsType: "+lhsType);

        //Iterator elemIt = arrInit.elements().iterator();
        //while (elemIt.hasNext()) {
        //    Object next = elemIt.next();
        //    System.out.println(next.getClass().toString());
        //    System.out.println(((polyglot.ast.ArrayInit)next).type());
        //}
        /*try {
            arrInit.typeCheckElements(lhsType);
        }
        catch (polyglot.types.SemanticException e) {
            throw new RuntimeException("Array element types must match array base type.");
        }*/
        //System.out.println("arrInit.type: "+arrInit.type());
        soot.Local local = generateLocal(lhsType);
        System.out.println(Util.getSootType(lhsType));

        soot.jimple.NewArrayExpr arrExpr = soot.jimple.Jimple.v().newNewArrayExpr(((soot.ArrayType)local.getType()).getElementType(), soot.jimple.IntConstant.v(arrInit.elements().size()));

        soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(local, arrExpr);
        
        body.getUnits().add(assign);
        Util.addLineTag(assign, arrInit);
       

        Iterator it = arrInit.elements().iterator();
        int index = 0;
        
        while (it.hasNext()){
        
            polyglot.ast.Expr elemExpr = (polyglot.ast.Expr)it.next();
            soot.Value elem;
            if (elemExpr instanceof polyglot.ast.ArrayInit){
                elem = getArrayInitLocal((polyglot.ast.ArrayInit)elemExpr, ((polyglot.ast.ArrayInit)elemExpr).type());
            }
            else {
                elem = createExpr(elemExpr);
            }
            soot.jimple.ArrayRef arrRef = soot.jimple.Jimple.v().newArrayRef(local, soot.jimple.IntConstant.v(index));
            
            soot.jimple.AssignStmt elemAssign = soot.jimple.Jimple.v().newAssignStmt(arrRef, elem);
            body.getUnits().add(elemAssign);
            Util.addLineTag(elemAssign, elemExpr);
            Util.addPosTag(elemAssign.getRightOpBox(), elemExpr.position()); 
            
            index++;
        }

        return local;
    }
    

    /**
     * create LHS expressions
     */
    private soot.Value createLHS(polyglot.ast.Expr expr) {
        if (expr instanceof polyglot.ast.Local) {
            return getLocal((polyglot.ast.Local)expr);
        }
        else if (expr instanceof polyglot.ast.ArrayAccess) {
            return getArrayRefLocalLeft((polyglot.ast.ArrayAccess)expr);
        }
        else if (expr instanceof polyglot.ast.Field) {
            return getFieldLocalLeft((polyglot.ast.Field)expr);
        }
        else {
            throw new RuntimeException("Unhandled LHS");
        }
    }

    /**
     * Array Ref Expression Creation - LHS
     */
    private soot.Value getArrayRefLocalLeft(polyglot.ast.ArrayAccess arrayRefExpr) {
        //System.out.println("arrayRefExpr: "+arrayRefExpr);
        polyglot.ast.Expr array = arrayRefExpr.array();
        //System.out.println("array: "+array);
        polyglot.ast.Expr access = arrayRefExpr.index();
        //System.out.println("access: "+access);
        
        soot.Local arrLocal = (soot.Local)createExpr(array);
        //System.out.println("arrLocal: "+arrLocal);
        soot.Value arrAccess = createExpr(access);

        soot.Local retLocal = generateLocal(arrayRefExpr.type());

        soot.jimple.ArrayRef ref = soot.jimple.Jimple.v().newArrayRef(arrLocal, arrAccess);
  
        Util.addPosTag(ref.getBaseBox(), arrayRefExpr.array().position());
        Util.addPosTag(ref.getIndexBox(), arrayRefExpr.index().position());
        return ref;
    }

    /**
     * Array Ref Expression Creation
     */
    private soot.Value getArrayRefLocal(polyglot.ast.ArrayAccess arrayRefExpr) {
    
        //System.out.println("arrayRefExpr: "+arrayRefExpr);
        polyglot.ast.Expr array = arrayRefExpr.array();
        //System.out.println("array: "+array);
        polyglot.ast.Expr access = arrayRefExpr.index();
        //System.out.println("access: "+access);
        
        soot.Local arrLocal = (soot.Local)createExpr(array);
        //System.out.println("arrLocal: "+arrLocal);
        soot.Value arrAccess = createExpr(access);

        soot.Local retLocal = generateLocal(arrayRefExpr.type());

        soot.jimple.ArrayRef ref = soot.jimple.Jimple.v().newArrayRef(arrLocal, arrAccess);
        
        Util.addPosTag(ref.getBaseBox(), arrayRefExpr.array().position());
        Util.addPosTag(ref.getIndexBox(), arrayRefExpr.index().position());

        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, ref);
        body.getUnits().add(stmt);
        Util.addLineTag(stmt, arrayRefExpr);
        
        //soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(arrLocal, retLocal);

        //body.getUnits().add(assign);
        return retLocal;
        //return ref;
    }
    
   
    /**
     * Special Expression Creation
     */
    private soot.Local getSpecialLocal(polyglot.ast.Special specialExpr) {
        System.out.println("in getSpecialLocal: qualifier: "+specialExpr.qualifier());
        
        if (specialExpr.kind() == polyglot.ast.Special.SUPER) {
           return specialThisLocal;
        }
        else if (specialExpr.kind() == polyglot.ast.Special.THIS) {
            if (specialExpr.qualifier() == null) {
                return specialThisLocal;
            }
            else {
                if (outerClassParamLocal == null) {
                    soot.SootField outerThisField = body.getMethod().getDeclaringClass().getFieldByName("this$0");
                    soot.Local fieldLocal = generateLocal(outerThisField.getType());
                    soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(specialThisLocal, outerThisField);
                    soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(fieldLocal, fieldRef);
                    body.getUnits().add(stmt);
                    return fieldLocal;
                }
                else {
                    return outerClassParamLocal;
                }
            }
        }
        else {
            throw new RuntimeException("Unknown Special");
        }
    }
    
    /**
     * InstanceOf Expression Creation
     */
    private soot.Local getInstanceOfLocal(polyglot.ast.Instanceof instExpr) {
        
        soot.Type sootType = Util.getSootType(instExpr.compareType().type());

        soot.Value local = createExpr(instExpr.expr());

        soot.jimple.InstanceOfExpr instOfExpr = soot.jimple.Jimple.v().newInstanceOfExpr(local, sootType);
        //body.getUnits().add(soot.jimple.Jimple.v().newInstanceOfExpr(local, sootType));

        soot.Local lhs = generateLocal(soot.BooleanType.v());

        soot.jimple.Stmt instAssign = soot.jimple.Jimple.v().newAssignStmt(lhs, instOfExpr);
        body.getUnits().add(instAssign);
        Util.addLineTag(instAssign, instExpr);

        //if (isLitOrLocal(instExpr.expr())) {
        Util.addPosTag(instOfExpr.getOpBox(), instExpr.expr().position());
        //}
        return lhs;
    }

    /**
     * Condition Expression Creation - can maybe merge with If
     */
    private soot.Local getConditionalLocal(polyglot.ast.Conditional condExpr){

        // handle cond 
        polyglot.ast.Expr condition = condExpr.cond();
        soot.Value sootCond = createExpr(condition); 
		if (!(sootCond instanceof soot.jimple.ConditionExpr)) {
            sootCond = soot.jimple.Jimple.v().newEqExpr(sootCond, soot.jimple.IntConstant.v(0));
        }
        else {
            sootCond = reverseCondition((soot.jimple.ConditionExpr)sootCond);
            sootCond = handleDFLCond((soot.jimple.ConditionExpr)sootCond);
        }
		soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
		soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(sootCond, noop1);
        
		body.getUnits().add(ifStmt);
	    Util.addLineTag(ifStmt, condExpr);
        
        soot.Local retLocal = generateLocal(condExpr.alternative().type());
        
		// handle consequence
		polyglot.ast.Expr consequence = condExpr.consequent();
        
        soot.Value conseqVal = createExpr(consequence);
        soot.jimple.AssignStmt conseqAssignStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, conseqVal);
        body.getUnits().add(conseqAssignStmt);
        Util.addLineTag(conseqAssignStmt, condExpr);
        Util.addPosTag(conseqAssignStmt.getRightOpBox(), consequence.position());
		
        soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();	
        soot.jimple.Stmt goto1 = soot.jimple.Jimple.v().newGotoStmt(noop2);
		body.getUnits().add(goto1);
        
        // handle alternative
        
        body.getUnits().add(noop1);
		
        polyglot.ast.Expr alternative = condExpr.alternative();
		if (alternative != null){
			soot.Value altVal = createExpr(alternative);
            soot.jimple.AssignStmt altAssignStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, altVal);
            body.getUnits().add(altAssignStmt);
            Util.addLineTag(altAssignStmt, condExpr);
            Util.addPosTag(altAssignStmt.getRightOpBox(), alternative.position());
        }
        body.getUnits().add(noop2);

        Util.addPosTag(ifStmt.getConditionBox(), condition.position());
        
        return retLocal;
    }
    
    /**
     * Utility methods
     */
    private boolean isLitOrLocal(polyglot.ast.Expr exp) {
        if (exp instanceof polyglot.ast.Lit) return true;
        if (exp instanceof polyglot.ast.Local) return true;
        else return false;
    }
	
    /**
     * Extra Local Variables Generation
     */
    private soot.Local generateLocal(polyglot.types.Type polyglotType) {
		soot.Type type = Util.getSootType(polyglotType);
        //System.out.println("polyglot type to generate local: "+polyglotType);
        return generateLocal(type);
    }

    private soot.Local generateLocal(soot.Type type){
        
		String name = "v";
		if (type instanceof soot.IntType) {
			name = nextIntName();
		}
        else if (type instanceof soot.ByteType) {
			name = nextByteName();
		}
        else if (type instanceof soot.ShortType) {
			name = nextShortName();
		}
        else if (type instanceof soot.BooleanType) {
			name = nextBooleanName();
		}
        else if (type instanceof soot.VoidType) {
			name = nextVoidName();
		}
        else if (type instanceof soot.CharType) {
            name = nextIntName();
            type = soot.IntType.v();
        }
		else if (type instanceof soot.DoubleType) {
			name = nextDoubleName();
		}
		else if (type instanceof soot.FloatType) {
			name = nextFloatName();
		}
		else if (type instanceof soot.LongType) {
			name = nextLongName();
		}
        else if (type instanceof soot.RefLikeType) {
            name = nextRefLikeTypeName();
        }
        else {
            //System.out.println("Unhandled Type of local to generate: "+type);
            throw new RuntimeException("Unhandled Type of Local variable to Generate - Not Implemented");
        }
		
		return createLocal(name, type);
		
	}

	private int tempInt = -1;
	private int tempVoid = -1;
	private int tempBoolean = -1;
	private int tempLong = -1;
	private int tempDouble = -1;
	private int tempFloat = -1;
    private int tempRefLikeType = -1;
    private int tempByte = -1;
    private int tempShort = -1;
	
    private String nextIntName(){
		tempInt++;
		return "$i"+tempInt;
	}

	private String nextVoidName(){
		tempVoid++;
		return "$v"+tempVoid;
	}

	private String nextByteName(){
		tempByte++;
		return "$b"+tempByte;
	}

	private String nextShortName(){
		tempShort++;
		return "$s"+tempShort;
	}

	private String nextBooleanName(){
		tempBoolean++;
		return "$z"+tempBoolean;
	}

	private String nextDoubleName(){
		tempDouble++;
		return "$d"+tempDouble;
	}
    
	private String nextFloatName(){
		tempFloat++;
		return "$f"+tempFloat;
	}

	private String nextLongName(){
		tempLong++;
		return "$l"+tempLong;
	}

    private String nextRefLikeTypeName(){
        tempRefLikeType++;
        return "$r"+tempRefLikeType;
    }
}
