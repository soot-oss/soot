

package soot.javaToJimple;

import java.util.*;

public class JimpleBodyBuilder {
    
    soot.jimple.JimpleBody body;    // body of the method being created
    ArrayList exceptionTable;       // list of exceptions
    Stack endControlNoop = new Stack();     // for break
    Stack condControlNoop = new Stack();    // continue
    Stack monitorStack;     // for synchronized blocks
    Stack tryStack; // for try stmts in case of returns
    Stack catchStack; // for catch stmts in case of returns

    HashMap labelBreakMap; // for break label --> nop to jump to
    HashMap labelContinueMap; // for continue label --> nop to jump to
    HashMap localsMap = new HashMap();    // localInst --> soot local 

    HashMap getThisMap = new HashMap(); // type --> local to ret
    soot.Local specialThisLocal;    // === body.getThisLocal();
    soot.Local outerClassParamLocal;    // outer class this
   
    private int paramRefCount = 0;  // counter for param ref stmts
    
    LocalGenerator lg;  // for generated locals not in orig src
    
    /**
     * Jimple Body Creation
     */
    public soot.jimple.JimpleBody createJimpleBody(polyglot.ast.Block block, List formals, soot.SootMethod sootMethod){
     
        createBody(sootMethod);

        lg = new LocalGenerator(body);
        // create this formal except for static methods
        if (!soot.Modifier.isStatic(sootMethod.getModifiers())) {

            soot.RefType type = sootMethod.getDeclaringClass().getType();
            specialThisLocal = soot.jimple.Jimple.v().newLocal("this", type);
            body.getLocals().add(specialThisLocal);
                                                
            soot.jimple.ThisRef thisRef = soot.jimple.Jimple.v().newThisRef(type);
            
            soot.jimple.Stmt thisStmt = soot.jimple.Jimple.v().newIdentityStmt(specialThisLocal, thisRef);
            body.getUnits().add(thisStmt);
            
            // this is causing problems - no this in java code -> no tags
            //Util.addLineTag(thisStmt, block);
        }
        
        int formalsCounter = 0;
        
        //create outer class this param ref for inner classes except for static inner classes - this is not needed
        int outerIndex = sootMethod.getDeclaringClass().getName().lastIndexOf("$");
        int classMod = sootMethod.getDeclaringClass().getModifiers();
            
        if ((outerIndex != -1) && (sootMethod.getName().equals("<init>")) && sootMethod.getDeclaringClass().declaresFieldByName("this$0")){

            // we know its an inner non static class can get outer class
            // from field ref of the this$0 field
            soot.SootClass outerClass = ((soot.RefType)sootMethod.getDeclaringClass().getFieldByName("this$0").getType()).getSootClass();
            soot.Local outerLocal = lg.generateLocal(outerClass.getType());
            
            soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(outerClass.getType(), formalsCounter);
            paramRefCount++;
            soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(outerLocal, paramRef);
            body.getUnits().add(stmt);
            
            ((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).setOuterClassThisInit(outerLocal);
            outerClassParamLocal = outerLocal;
            formalsCounter++;
        }

        // handle formals
        if (formals != null) {
            ArrayList formalNames = new ArrayList();
            Iterator formalsIt = formals.iterator();
            while (formalsIt.hasNext()) { 
                polyglot.ast.Formal formal = (polyglot.ast.Formal)formalsIt.next();
                createFormal(formal, formalsCounter);
                formalNames.add(formal.name());
                formalsCounter++;
            }
            body.getMethod().addTag(new soot.tagkit.ParamNamesTag(formalNames));
        }
        
        // handle final local params
        ArrayList finalsList = ((PolyglotMethodSource)body.getMethod().getSource()).getFinalsList();
        if (finalsList != null){
            Iterator finalsIt = finalsList.iterator();
            while (finalsIt.hasNext()){
                soot.SootField sf = (soot.SootField)finalsIt.next();
                soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(sf.getType(), formalsCounter);
                paramRefCount++;
                soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(lg.generateLocal(sf.getType()), paramRef);
                body.getUnits().add(stmt);
                formalsCounter++;
            }
        }
        
        createBlock(block);

        
        // if method is <clinit> handle static field inits
        if (sootMethod.getName().equals("<clinit>")){
            
            handleAssert(sootMethod);
            handleStaticFieldInits(sootMethod);
            handleStaticInitializerBlocks(sootMethod);
        }
       
        // determine if body has a return stmt
        boolean hasReturn = false;
		if (block != null) {
            Iterator it = block.statements().iterator();
		    while (it.hasNext()){
			    Object next = it.next();
			    if (next instanceof polyglot.ast.Return){
                    hasReturn = true; 
                }
            }
        }
	
        soot.Type retType = body.getMethod().getReturnType();
        // only do this if noexplicit return
	    if ((!hasReturn) && (retType instanceof soot.VoidType)) {
            soot.jimple.Stmt retStmt = soot.jimple.Jimple.v().newReturnVoidStmt();
            body.getUnits().add(retStmt);
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

    private void handleAssert(soot.SootMethod sootMethod){
        if (!((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).hasAssert()) return;
        ((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).addAssertInits(body);
    }

    /**
     * adds any needed field inits 
     */
    private void handleFieldInits(soot.SootMethod sootMethod) {
            
        ArrayList fieldInits = ((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).getFieldInits();
        if (fieldInits != null) {
            handleFieldInits(fieldInits);
        }
    }

    protected void handleFieldInits(ArrayList fieldInits){
        Iterator fieldInitsIt = fieldInits.iterator();
        while (fieldInitsIt.hasNext()) {
            polyglot.ast.FieldDecl field = (polyglot.ast.FieldDecl)fieldInitsIt.next();
            String fieldName = field.name();
            polyglot.ast.Expr initExpr = field.init();
            soot.SootClass currentClass = body.getMethod().getDeclaringClass();
            soot.SootField sootField = currentClass.getField(fieldName, Util.getSootType(field.type().type()));
                
            soot.Local base = specialThisLocal;
                   
            soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(base, sootField);
                
            soot.Value sootExpr;
            if (initExpr instanceof polyglot.ast.ArrayInit) {
                sootExpr = getArrayInitLocal((polyglot.ast.ArrayInit)initExpr, field.type().type());
            }
            else {
                sootExpr = createExpr(initExpr);
            }
            if (sootExpr instanceof soot.jimple.ConditionExpr) {
                sootExpr = handleCondBinExpr((soot.jimple.ConditionExpr)sootExpr); 
            }
                
            soot.jimple.AssignStmt assign;
            if (sootExpr instanceof soot.Local){
                assign = soot.jimple.Jimple.v().newAssignStmt(fieldRef, (soot.Local)sootExpr);
            }
            else if (sootExpr instanceof soot.jimple.Constant){
                assign = soot.jimple.Jimple.v().newAssignStmt(fieldRef, (soot.jimple.Constant)sootExpr);
            }
            else {
                throw new RuntimeException("fields must assign to local or constant only");
            }
            body.getUnits().add(assign);
            Util.addLnPosTags(assign, initExpr.position());
            Util.addLnPosTags(assign.getRightOpBox(), initExpr.position());
        }
    }
        

    /**
     * adds this field for the outer class 
     */
    private void handleOuterClassThisInit(soot.SootMethod sootMethod) {
        // static inner classes are different
        if (body.getMethod().getDeclaringClass().declaresFieldByName("this$0")){
            soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(specialThisLocal, body.getMethod().getDeclaringClass().getFieldByName("this$0"));
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(fieldRef, outerClassParamLocal);
            body.getUnits().add(stmt);
        }
    }
    
    /**
     * adds any needed static field inits
     */
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
                Util.addLnPosTags(assign, initExpr.position());
                
            }
        }
    }

    /**
     * init blocks get created within init methods in Jimple
     */
    private void handleInitializerBlocks(soot.SootMethod sootMethod) {
        ArrayList initializerBlocks = ((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).getInitializerBlocks();

        if (initializerBlocks != null) {
            
            handleStaticBlocks(initializerBlocks);
        }
    }

    protected void handleStaticBlocks(ArrayList initializerBlocks){
        Iterator initBlocksIt = initializerBlocks.iterator();
        while (initBlocksIt.hasNext()) {
            createBlock((polyglot.ast.Block)initBlocksIt.next());
        }
    }
    
    /**
     * static init blocks get created in clinit methods in Jimple
     */
    private void handleStaticInitializerBlocks(soot.SootMethod sootMethod) {
        ArrayList staticInitializerBlocks = ((soot.javaToJimple.PolyglotMethodSource)sootMethod.getSource()).getStaticInitializerBlocks();

        if (staticInitializerBlocks != null) {
        
            Iterator staticInitBlocksIt = staticInitializerBlocks.iterator();
            while (staticInitBlocksIt.hasNext()) {
                createBlock((polyglot.ast.Block)staticInitBlocksIt.next());
            }
        }
    }

    /**
     * create body and make it be active
     */
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

        Util.addLnPosTags(stmt, formal.position());
        Util.addLnPosTags(((soot.jimple.IdentityStmt) stmt).getRightOpBox(), formal.position());

        ArrayList names = new ArrayList();
        names.add(formal.name());
        stmt.addTag(new soot.tagkit.ParamNamesTag(names));
        return formalLocal;
    }
        
    /**
     * Formal creation - method parameters
     */
    private void createFormal(polyglot.ast.Formal formal, int counter){

        soot.Type sootType = Util.getSootType(formal.type().type());
        soot.Local formalLocal = createLocal(formal.localInstance());
        soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(sootType, counter);
        paramRefCount++;
        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(formalLocal, paramRef);
        body.getUnits().add(stmt);
        
        Util.addLnPosTags(((soot.jimple.IdentityStmt) stmt).getRightOpBox(), formal.position());
        Util.addLnPosTags(stmt, formal.position());

    }

    /**
     * Literal Creation
     */
    private soot.Value createLiteral(polyglot.ast.Lit lit) {
		if (lit instanceof polyglot.ast.IntLit) {
			polyglot.ast.IntLit intLit = (polyglot.ast.IntLit)lit;
			long litValue = intLit.value();
			if (intLit.kind() == polyglot.ast.IntLit.INT) {
                return soot.jimple.IntConstant.v((int)litValue);
			}
			else {
				return soot.jimple.LongConstant.v(litValue);
			}
		}
		else if (lit instanceof polyglot.ast.StringLit) {
			String litValue = ((polyglot.ast.StringLit)lit).value();
			return soot.jimple.StringConstant.v(litValue);		
		}
		else if (lit instanceof polyglot.ast.NullLit) {
			return soot.jimple.NullConstant.v();		
		}
		else if (lit instanceof polyglot.ast.FloatLit) {
			polyglot.ast.FloatLit floatLit = (polyglot.ast.FloatLit)lit;
			double litValue = floatLit.value();
			if (floatLit.kind() == polyglot.ast.FloatLit.DOUBLE) {
				return soot.jimple.DoubleConstant.v(floatLit.value());		
			}
			else {
				return soot.jimple.FloatConstant.v((float)(floatLit.value()));		
			}
		}
		else if (lit instanceof polyglot.ast.CharLit) {
			char litValue = ((polyglot.ast.CharLit)lit).value();
            return soot.jimple.IntConstant.v(litValue);
		}
		else if (lit instanceof polyglot.ast.BooleanLit) {
			boolean litValue = ((polyglot.ast.BooleanLit)lit).value();
            if (litValue) return soot.jimple.IntConstant.v(1);
            else return soot.jimple.IntConstant.v(0);
		}
        else if (lit instanceof polyglot.ast.ClassLit){
            return getSpecialClassLitLocal((polyglot.ast.ClassLit)lit);
        }
        else {
            throw new RuntimeException("Unknown Literal - Unhandled: "+lit.getClass());
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
        
        localsMap.put(new polyglot.util.IdentityKey(localInst), sootLocal);
        return sootLocal;
    }
    
    // this should be used for generated locals only
    private soot.Local createLocal(String name, soot.Type sootType) {
        soot.Local sootLocal = soot.jimple.Jimple.v().newLocal(name, sootType);
        body.getLocals().add(sootLocal);
		return sootLocal;
	}

    /**
     * Local Retreival
     */
    private soot.Local getLocal(polyglot.ast.Local local) {

        return getLocal(local.localInstance());
    }
    
    /**
     * Local Retreival
     */
    private soot.Local getLocal(polyglot.types.LocalInstance li) {
        //System.out.println("getting local: "+li+" of type: "+Util.getSootType(li.type())+" in class : "+body.getMethod().getDeclaringClass());
        //System.out.println("who has fields : "+body.getMethod().getDeclaringClass().getFields());
        if (localsMap.containsKey(new polyglot.util.IdentityKey(li))){
            soot.Local sootLocal = (soot.Local)localsMap.get(new polyglot.util.IdentityKey(li));
            return sootLocal;
        }
        else if (body.getMethod().getDeclaringClass().declaresField("val$"+li.name(), Util.getSootType(li.type()))){
            soot.Local fieldLocal = generateLocal(li.type());
            soot.SootField field = body.getMethod().getDeclaringClass().getField("val$"+li.name(), Util.getSootType(li.type()));
            soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(specialThisLocal, field);
            soot.jimple.AssignStmt assign = soot.jimple.Jimple.v().newAssignStmt(fieldLocal, fieldRef);
            body.getUnits().add(assign);
            return fieldLocal;
        }
        else {
            //else create access meth in outer for val$fieldname
            // get the this$0 field to find the type of an outer class - has
            // to have one because local/anon inner can't declare static
            // memebers so for deepnesting not in static context for these
            // cases

            soot.SootClass currentClass = body.getMethod().getDeclaringClass();
            boolean fieldFound = false;
            
            while (!fieldFound){
                if (!currentClass.declaresFieldByName("this$0")){
                    throw new RuntimeException("Trying to get field val$"+li.name()+" from some outer class but can't access the outer class of: "+currentClass.getName()+"!");
                }
                soot.SootClass outerClass = ((soot.RefType)currentClass.getFieldByName("this$0").getType()).getSootClass();
                // look for field of type li.type and name val$li.name in outer 
                // class 
                if (outerClass.declaresField("val$"+li.name(), Util.getSootType(li.type()))){
                    fieldFound = true;
                }
                currentClass = outerClass;
                // repeat until found in some outer class
            }
            // create and add accessor to that outer class (indic as current)
            soot.SootMethod methToInvoke = makeLiFieldAccessMethod(currentClass, li);
            
            // invoke and return
            // generate a local that corresponds to the invoke of that meth
            ArrayList methParams = new ArrayList();
            methParams.add(getThis(currentClass.getType()));
        
            soot.Local res = Util.getPrivateAccessFieldInvoke(methToInvoke, methParams, body, lg);
            return res;
        }
    }
    
    private soot.SootMethod makeLiFieldAccessMethod(soot.SootClass classToInvoke, polyglot.types.LocalInstance li){
        String name = "access$"+soot.javaToJimple.InitialResolver.v().getNextPrivateAccessCounter()+"00";
        ArrayList paramTypes = new ArrayList();
        paramTypes.add(classToInvoke.getType());
        
        soot.SootMethod meth = new soot.SootMethod(name, paramTypes, Util.getSootType(li.type()), soot.Modifier.STATIC);

        classToInvoke.addMethod(meth);
        PrivateFieldAccMethodSource src = new PrivateFieldAccMethodSource();
        src.fieldName("val$"+li.name());
        src.fieldType(Util.getSootType(li.type()));
        src.classToInvoke(classToInvoke);
        meth.setActiveBody(src.getBody(meth, null));
        return meth;
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
    
    private boolean needSootIf(soot.Value sootCond){
        if (sootCond instanceof soot.jimple.IntConstant){
            if (((soot.jimple.IntConstant)sootCond).value == 1){
                return false;
            }
        }
        return true;
    }
    
    /**
     * If Stmts Creation - only add line-number tags to if (the other
     * stmts needing tags are created elsewhere
     */
    private void createIf(polyglot.ast.If ifExpr){

        // handle cond 
        polyglot.ast.Expr condition = ifExpr.cond();
        soot.Value sootCond = createExpr(condition);
        boolean needIf = needSootIf(sootCond);
        if (!(sootCond instanceof soot.jimple.ConditionExpr)) {
            sootCond = soot.jimple.Jimple.v().newEqExpr(sootCond, soot.jimple.IntConstant.v(0));
        }
        else {
            sootCond = reverseCondition((soot.jimple.ConditionExpr)sootCond);
            sootCond = handleDFLCond((soot.jimple.ConditionExpr)sootCond);
        }
       
        // add if
		soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
       
        if (needIf) {
		    soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(sootCond, noop1);
		    body.getUnits().add(ifStmt);
            // add line and pos tags
            Util.addLnPosTags(ifStmt.getConditionBox(), condition.position());
            Util.addLnPosTags(ifStmt, condition.position());
        }
		
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
        boolean needIf = needSootIf(sootCond);
        if (!(sootCond instanceof soot.jimple.ConditionExpr)) {
            sootCond = soot.jimple.Jimple.v().newNeExpr(sootCond, soot.jimple.IntConstant.v(0));
        }
        else {
            sootCond = handleDFLCond((soot.jimple.ConditionExpr)sootCond);
        }

        if (needIf){
            soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(sootCond, noop1);
        
            body.getUnits().add(ifStmt);
            Util.addLnPosTags(ifStmt.getConditionBox(), condition.position());
            Util.addLnPosTags(ifStmt, condition.position());
        }
        else {
            soot.jimple.GotoStmt gotoIf = soot.jimple.Jimple.v().newGotoStmt(noop1);
            body.getUnits().add(gotoIf);
        }
        
        body.getUnits().add((soot.jimple.Stmt)(endControlNoop.pop()));
        
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
        boolean needIf = needSootIf(sootCond);
        if (!(sootCond instanceof soot.jimple.ConditionExpr)) {
            sootCond = soot.jimple.Jimple.v().newNeExpr(sootCond, soot.jimple.IntConstant.v(0));
        }
        else {
            sootCond = handleDFLCond((soot.jimple.ConditionExpr)sootCond);
        }
        if (needIf){
            soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(sootCond, noop1);
            body.getUnits().add(ifStmt);
            Util.addPosTag(ifStmt.getConditionBox(), condition.position());
            Util.addLnPosTags(ifStmt, condition.position());
        }        
        else {
            soot.jimple.GotoStmt gotoIf = soot.jimple.Jimple.v().newGotoStmt(noop1);
            body.getUnits().add(gotoIf);
        }
        body.getUnits().add((soot.jimple.Stmt)(endControlNoop.pop()));
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
        
        polyglot.ast.Expr condition = forStmt.cond();
        if (condition != null) {
            soot.Value sootCond = createExpr(condition);
            boolean needIf = needSootIf(sootCond);
            if (!(sootCond instanceof soot.jimple.ConditionExpr)) {
                sootCond = soot.jimple.Jimple.v().newNeExpr(sootCond, soot.jimple.IntConstant.v(0));
            }
            else {
                sootCond = handleDFLCond((soot.jimple.ConditionExpr)sootCond);
            }
            if (needIf){
                soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(sootCond, noop1);
        
                // add cond
                body.getUnits().add(ifStmt);
        
                // add line and pos tags
                Util.addLnPosTags(ifStmt.getConditionBox(), condition.position());
                Util.addLnPosTags(ifStmt, condition.position());
            }
            else {
                soot.jimple.GotoStmt gotoIf = soot.jimple.Jimple.v().newGotoStmt(noop1);
                body.getUnits().add(gotoIf);
            }
            
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
        
        String name = localDecl.name();
        polyglot.types.LocalInstance localInst = localDecl.localInstance();
        soot.Value lhs = createLocal(localInst);
        polyglot.ast.Expr expr = localDecl.init();
        if (expr != null) {
            soot.Value rhs;
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
            body.getUnits().add(stmt);
            Util.addLineTag(stmt, localDecl);
            Util.addLnPosTags(stmt, localDecl.position()); 
            // this is a special case for position tags
            if ( localDecl.position() != null){
                Util.addLnPosTags(stmt.getLeftOpBox(), localDecl.position().line(), localDecl.position().endLine(),  localDecl.position().endColumn()-name.length(), localDecl.position().endColumn());
                if (expr != null){
                    Util.addLnPosTags(stmt, localDecl.position().line(), expr.position().endLine(), localDecl.position().column(), expr.position().endColumn());
                }
                else {
                    Util.addLnPosTags(stmt, localDecl.position().line(), localDecl.position().endLine(), localDecl.position().column(), localDecl.position().endColumn()); 
                }
            }
            else {
            }
            if (expr != null){    
		        Util.addLnPosTags(stmt.getRightOpBox(), expr.position());
            }
        }
    }
    
    /**
     * Switch Stmts Creation
     */
    private void createSwitch(polyglot.ast.Switch switchStmt) {
        
        polyglot.ast.Expr value = switchStmt.expr();
        soot.Value sootValue = createExpr(value);
        soot.jimple.Stmt defaultTarget = null;
     
        polyglot.ast.Case [] caseArray = new polyglot.ast.Case[switchStmt.elements().size()];
        soot.jimple.Stmt [] targetsArray = new soot.jimple.Stmt[switchStmt.elements().size()];
        
        ArrayList targets = new ArrayList();
        HashMap targetsMap = new HashMap();
        int counter = 0;
        Iterator it = switchStmt.elements().iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof polyglot.ast.Case) {
                soot.jimple.Stmt noop = soot.jimple.Jimple.v().newNopStmt();
                if (!((polyglot.ast.Case)next).isDefault()){
                    targets.add(noop);
                    caseArray[counter] = (polyglot.ast.Case)next;
                    targetsArray[counter] = noop;
                    counter++;
                    targetsMap.put(next, noop);
                }
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
            
        // deal with default
        boolean hasDefaultTarget = true;
        if (defaultTarget == null) {
            soot.jimple.Stmt noop = soot.jimple.Jimple.v().newNopStmt();
            defaultTarget = noop;
            hasDefaultTarget = false;
            
        }
        
        // lookup or tableswitch
        soot.jimple.Stmt sootSwitchStmt;
        if (isLookupSwitch(switchStmt)) {
        
            ArrayList values = new ArrayList();
            for (int i = 0; i < counter; i++) {
                if (!caseArray[i].isDefault()) {
                    values.add(soot.jimple.IntConstant.v((int)caseArray[i].value()));
                }
            }

            
            soot.jimple.LookupSwitchStmt lookupStmt = soot.jimple.Jimple.v().newLookupSwitchStmt(sootValue, values, sortedTargets, defaultTarget);
       
            Util.addLnPosTags(lookupStmt.getKeyBox(), value.position());
            sootSwitchStmt = lookupStmt;
        
        }
        else {
            long lowVal = 0;
            long highVal = 0;
            boolean unknown = true;

            it = switchStmt.elements().iterator();
            while (it.hasNext()){
                Object next = it.next();
                if (next instanceof polyglot.ast.Case) {
                    if (!((polyglot.ast.Case)next).isDefault()){
                        long temp = ((polyglot.ast.Case)next).value();
                        if (unknown){
                            highVal = temp;
                            lowVal = temp;
                            unknown = false;
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

            soot.jimple.TableSwitchStmt tableStmt = soot.jimple.Jimple.v().newTableSwitchStmt(sootValue, (int)lowVal, (int)highVal, sortedTargets, defaultTarget);

            Util.addLnPosTags(tableStmt.getKeyBox(), value.position());
            sootSwitchStmt = tableStmt;

        }
        
        body.getUnits().add(sootSwitchStmt);

        Util.addLnPosTags(sootSwitchStmt, switchStmt.position());
        endControlNoop.push(soot.jimple.Jimple.v().newNopStmt());
        
        it = switchStmt.elements().iterator();
        Iterator targetsIt = targets.iterator();

        while (it.hasNext()){
            Object next = it.next();
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
        
        if (!hasDefaultTarget) {
            body.getUnits().add(defaultTarget);
        }
        body.getUnits().add((soot.jimple.Stmt)(endControlNoop.pop()));
    }

    /**
     * Determine if switch should be lookup or table - this doesn't
     * always get the same result as javac 
     * lookup: non-table
     * table: sequential (no gaps)
     */
    private boolean isLookupSwitch(polyglot.ast.Switch switchStmt){

        int lowest = 0;
        int highest = 0;
        int counter = 0;
        Iterator it = switchStmt.elements().iterator();
        while (it.hasNext()){
            Object next = it.next();
            if (next instanceof polyglot.ast.Case) {
                polyglot.ast.Case caseStmt = (polyglot.ast.Case)next;
                if (caseStmt.isDefault()) continue;
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

        if ((counter-1) == (highest - lowest)) return false;
        return true;
    }
    
    /**
     * Branch Stmts Creation
     */
    private void createBranch(polyglot.ast.Branch branchStmt){
        
        body.getUnits().add(soot.jimple.Jimple.v().newNopStmt());
        if (branchStmt.kind() == polyglot.ast.Branch.BREAK){
            if (branchStmt.label() == null) {
                soot.jimple.Stmt gotoEndNoop = (soot.jimple.Stmt)endControlNoop.pop();
                soot.jimple.Stmt gotoEnd = soot.jimple.Jimple.v().newGotoStmt(gotoEndNoop);
                endControlNoop.push(gotoEndNoop);
                body.getUnits().add(gotoEnd);
                Util.addLnPosTags(gotoEnd, branchStmt.position());
            }
            else {
                soot.jimple.Stmt gotoLabel = soot.jimple.Jimple.v().newGotoStmt((soot.jimple.Stmt)labelBreakMap.get(branchStmt.label()));
                body.getUnits().add(gotoLabel);
                Util.addLnPosTags(gotoLabel, branchStmt.position());
            }
        }
        else if (branchStmt.kind() == polyglot.ast.Branch.CONTINUE){
            if (branchStmt.label() == null) {
                soot.jimple.Stmt gotoCondNoop = (soot.jimple.Stmt)condControlNoop.pop();
                soot.jimple.Stmt gotoCond = soot.jimple.Jimple.v().newGotoStmt(gotoCondNoop);
                condControlNoop.push(gotoCondNoop);
                body.getUnits().add(gotoCond);
                Util.addLnPosTags(gotoCond, branchStmt.position());
            }
            else {
                soot.jimple.Stmt gotoLabel = soot.jimple.Jimple.v().newGotoStmt((soot.jimple.Stmt)labelContinueMap.get(branchStmt.label()));
                body.getUnits().add(gotoLabel);
                Util.addLnPosTags(gotoLabel, branchStmt.position());
            }
            
        }

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
    }
    
    /**
     * Assert Stmt Creation
     */
    private void createAssert(polyglot.ast.Assert assertStmt) {
        
        // check if assertions are disabled
        soot.Local testLocal = lg.generateLocal(soot.BooleanType.v());
        soot.SootField assertField = body.getMethod().getDeclaringClass().getField("$assertionsDisabled", soot.BooleanType.v());
        soot.jimple.FieldRef assertFieldRef = soot.jimple.Jimple.v().newStaticFieldRef(assertField);
        soot.jimple.AssignStmt fieldAssign = soot.jimple.Jimple.v().newAssignStmt(testLocal, assertFieldRef);
        body.getUnits().add(fieldAssign);
        
        soot.jimple.NopStmt nop1 = soot.jimple.Jimple.v().newNopStmt();
        soot.jimple.ConditionExpr cond1 = soot.jimple.Jimple.v().newNeExpr(testLocal, soot.jimple.IntConstant.v(0));
        soot.jimple.IfStmt testIf = soot.jimple.Jimple.v().newIfStmt(cond1, nop1);
        body.getUnits().add(testIf);

        // actual cond test
        soot.Value sootCond = createExpr(assertStmt.cond());
        boolean needIf = needSootIf(sootCond);
        if (!(sootCond instanceof soot.jimple.ConditionExpr)) {
            sootCond = soot.jimple.Jimple.v().newEqExpr(sootCond, soot.jimple.IntConstant.v(0));
        }
        else {
            sootCond = handleDFLCond((soot.jimple.ConditionExpr)sootCond);
        }
       
        if (needIf){
            // add if
		    soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(sootCond, nop1);
            body.getUnits().add(ifStmt);

            Util.addLnPosTags(ifStmt.getConditionBox(), assertStmt.cond().position());
            Util.addLnPosTags(ifStmt, assertStmt.position());
        }
        
        // assertion failure code
        soot.Local failureLocal = lg.generateLocal(soot.RefType.v("java.lang.AssertionError"));
        soot.jimple.NewExpr newExpr = soot.jimple.Jimple.v().newNewExpr(soot.RefType.v("java.lang.AssertionError"));
        soot.jimple.AssignStmt newAssign = soot.jimple.Jimple.v().newAssignStmt(failureLocal, newExpr);
        body.getUnits().add(newAssign);

        soot.SootMethod methToInvoke;
        ArrayList paramTypes = new ArrayList();
        ArrayList params = new ArrayList();
        if (assertStmt.errorMessage() != null){
            soot.Value errorExpr = createExpr(assertStmt.errorMessage());
            soot.Type errorType = errorExpr.getType();
            if (errorType instanceof soot.IntType) {
                paramTypes.add(soot.IntType.v());
            }
            else if (errorType instanceof soot.LongType){
                paramTypes.add(soot.LongType.v());
            }
            else if (errorType instanceof soot.FloatType){
                paramTypes.add(soot.FloatType.v());
            }
            else if (errorType instanceof soot.DoubleType){
                paramTypes.add(soot.DoubleType.v());
            }
            else if (errorType instanceof soot.CharType){
                paramTypes.add(soot.CharType.v());
            }
            else if (errorType instanceof soot.BooleanType){
                paramTypes.add(soot.BooleanType.v());
            }
            else if (errorType instanceof soot.ShortType){
                paramTypes.add(soot.IntType.v());
            }
            else if (errorType instanceof soot.ByteType){
                paramTypes.add(soot.IntType.v());
            }
            else {
                paramTypes.add(soot.Scene.v().getSootClass("java.lang.Object").getType());
            }
            params.add(errorExpr);
        }
        methToInvoke = soot.Scene.v().getSootClass("java.lang.AssertionError").getMethod("<init>", paramTypes, soot.VoidType.v());
        
        soot.jimple.SpecialInvokeExpr invokeExpr = soot.jimple.Jimple.v().newSpecialInvokeExpr(failureLocal, methToInvoke, params);
        soot.jimple.InvokeStmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(invokeExpr);
        body.getUnits().add(invokeStmt);

        soot.jimple.ThrowStmt throwStmt = soot.jimple.Jimple.v().newThrowStmt(failureLocal);
        body.getUnits().add(throwStmt);

        // end
        body.getUnits().add(nop1);
        
    }
    
    /**
     * Synchronized Stmt Creation
     */
    private void createSynchronized(polyglot.ast.Synchronized synchStmt) {
        soot.Value sootExpr = createExpr(synchStmt.expr());
        
        soot.jimple.EnterMonitorStmt enterMon = soot.jimple.Jimple.v().newEnterMonitorStmt(sootExpr);
        body.getUnits().add(enterMon);
        
        if (monitorStack == null){
            monitorStack = new Stack();
        }
        monitorStack.push(sootExpr);
        
        Util.addLnPosTags(enterMon.getOpBox(), synchStmt.expr().position());
        Util.addLnPosTags(enterMon, synchStmt.expr().position());
        
        soot.jimple.Stmt startNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(startNoop);
        
        createBlock(synchStmt.body());

        soot.jimple.ExitMonitorStmt exitMon = soot.jimple.Jimple.v().newExitMonitorStmt(sootExpr);
        body.getUnits().add(exitMon);

        monitorStack.pop();
        Util.addLnPosTags(exitMon.getOpBox(), synchStmt.expr().position());
        Util.addLnPosTags(exitMon, synchStmt.expr().position());
        
        soot.jimple.Stmt endSynchNoop = soot.jimple.Jimple.v().newNopStmt();
        soot.jimple.Stmt gotoEnd = soot.jimple.Jimple.v().newGotoStmt(endSynchNoop);

        soot.jimple.Stmt endNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(endNoop);
        
        body.getUnits().add(gotoEnd);

        soot.jimple.Stmt catchAllBeforeNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(catchAllBeforeNoop);

        // catch all
        soot.Local formalLocal = lg.generateLocal(soot.RefType.v("java.lang.Throwable"));
            
        soot.jimple.CaughtExceptionRef exceptRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(formalLocal, exceptRef);
        body.getUnits().add(stmt);

        // catch
        soot.jimple.Stmt catchBeforeNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(catchBeforeNoop);
        
        soot.Local local = lg.generateLocal(soot.RefType.v("java.lang.Throwable"));
        
        soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(local, formalLocal);

        body.getUnits().add(assign);
        soot.jimple.ExitMonitorStmt catchExitMon = soot.jimple.Jimple.v().newExitMonitorStmt(sootExpr);
        
        body.getUnits().add(catchExitMon);
        Util.addLnPosTags(catchExitMon.getOpBox(), synchStmt.expr().position());
        
        soot.jimple.Stmt catchAfterNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(catchAfterNoop);
        
        // throw
        soot.jimple.Stmt throwStmt = soot.jimple.Jimple.v().newThrowStmt(local);
        body.getUnits().add(throwStmt);
      
        
        body.getUnits().add(endSynchNoop);

        addToExceptionList(startNoop, endNoop, catchAllBeforeNoop, soot.Scene.v().getSootClass("java.lang.Throwable"));
        addToExceptionList(catchBeforeNoop, catchAfterNoop, catchAllBeforeNoop, soot.Scene.v().getSootClass("java.lang.Throwable"));
                
    }
    
    /**
     * Return Stmts Creation
     */
    private void createReturn(polyglot.ast.Return retStmt) {
        polyglot.ast.Expr expr = retStmt.expr();
        soot.Value sootLocal = null;
        if (expr != null){
            sootLocal = createExpr(expr);
        }
        
        // handle monitor exits before return if necessary
        if (monitorStack != null){
            Stack putBack = new Stack();
            while (!monitorStack.isEmpty()){
                soot.Local exitVal = (soot.Local)monitorStack.pop();
                putBack.push(exitVal);
                soot.jimple.ExitMonitorStmt emStmt = soot.jimple.Jimple.v().newExitMonitorStmt(exitVal);
                body.getUnits().add(emStmt);
            }
            while(!putBack.isEmpty()){
                monitorStack.push(putBack.pop());
            }
        }
        
        //handle finally blocks before return if inside try block
        if (tryStack != null && !tryStack.isEmpty()){
            polyglot.ast.Try currentTry = (polyglot.ast.Try)tryStack.pop();
            if (currentTry.finallyBlock() != null){
                createBlock(currentTry.finallyBlock());
                tryStack.push(currentTry);
                // if return stmt contains a return don't create the other return
                ReturnStmtChecker rsc = new ReturnStmtChecker();
                currentTry.finallyBlock().visit(rsc);
                if (rsc.hasRet()){
                    return;
                }
            }
            else {
                tryStack.push(currentTry);
            }
        }
       
        //handle finally blocks before return if inside catch block
        if (catchStack != null && !catchStack.isEmpty()){
            polyglot.ast.Try currentTry = (polyglot.ast.Try)catchStack.pop();
            if (currentTry.finallyBlock() != null){
                createBlock(currentTry.finallyBlock());
                catchStack.push(currentTry);
                // if return stmt contains a return don't create the other return
                ReturnStmtChecker rsc = new ReturnStmtChecker();
                currentTry.finallyBlock().visit(rsc);
                if (rsc.hasRet()){
                    return;
                }
            }
            else {
                catchStack.push(currentTry);
            }
        }
        
        // return
        if (expr == null) {
            soot.jimple.Stmt retStmtVoid = soot.jimple.Jimple.v().newReturnVoidStmt();
            body.getUnits().add(retStmtVoid);
            Util.addLnPosTags(retStmtVoid, retStmt.position());
        }
        else {
            //soot.Value sootLocal = createExpr(expr);
            if (sootLocal instanceof soot.jimple.ConditionExpr) {
                sootLocal = handleCondBinExpr((soot.jimple.ConditionExpr)sootLocal); 
            }
            soot.jimple.ReturnStmt retStmtLocal = soot.jimple.Jimple.v().newReturnStmt(sootLocal);
            body.getUnits().add(retStmtLocal);
            Util.addLnPosTags(retStmtLocal.getOpBox(), expr.position());
            Util.addLnPosTags(retStmtLocal, retStmt.position());
        }
    }
    
    /**
     * Throw Stmt Creation
     */
    private void createThrow(polyglot.ast.Throw throwStmt){
        soot.Value toThrow = createExpr(throwStmt.expr());
        soot.jimple.ThrowStmt throwSt = soot.jimple.Jimple.v().newThrowStmt(toThrow);
        body.getUnits().add(throwSt);
        Util.addLnPosTags(throwSt, throwStmt.position());
        Util.addLnPosTags(throwSt.getOpBox(), throwStmt.expr().position());
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

    /**
     * handles try/catch (try/catch/finally is separate for simplicity)
     */
    private void createTryCatch(polyglot.ast.Try tryStmt){
        
        // try
        polyglot.ast.Block tryBlock = tryStmt.tryBlock();
        
        // this nop is for the fromStmt of try     
        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(noop1);
       
        if (tryStack == null){
            tryStack = new Stack();
        }
        tryStack.push(tryStmt);
        createBlock(tryBlock);
        tryStack.pop();
        
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
          
            if (catchStack == null){
                catchStack = new Stack();
            }
            catchStack.push(tryStmt);
            createBlock(catchBlock.body());
            catchStack.pop();
        
            soot.jimple.Stmt catchEndGoto = soot.jimple.Jimple.v().newGotoStmt(endNoop);
            body.getUnits().add(catchEndGoto);
        

            soot.Type sootType = Util.getSootType(catchBlock.catchType());
           
            addToExceptionList(noop1, noop2, noop3, soot.Scene.v().getSootClass(sootType.toString()));
            
        }

        body.getUnits().add(endNoop);
    }

    /**
     * handles try/catch/finally (try/catch is separate for simplicity)
     */
    private void createTryCatchFinally(polyglot.ast.Try tryStmt){
       
        HashMap gotoMap = new HashMap();
        
        // try
        polyglot.ast.Block tryBlock = tryStmt.tryBlock();
        
        // this nop is for the fromStmt of try     
        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(noop1);
        
        if (tryStack == null){
            tryStack = new Stack();
        }
        tryStack.push(tryStmt);
        createBlock(tryBlock);
        tryStack.pop();
        
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

            if (catchStack == null){
                catchStack = new Stack();
            }
            catchStack.push(tryStmt);
            createBlock(catchBlock.body());
            catchStack.pop();
        
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
        soot.Local formalLocal = lg.generateLocal(soot.RefType.v("java.lang.Throwable"));
            
        body.getUnits().add(catchAllBeforeNoop);
        soot.jimple.CaughtExceptionRef exceptRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(formalLocal, exceptRef);
        body.getUnits().add(stmt);

        // catch all assign
        soot.jimple.Stmt beforeCatchAllAssignNoop = soot.jimple.Jimple.v().newNopStmt();
        body.getUnits().add(beforeCatchAllAssignNoop);
        soot.Local catchAllAssignLocal = lg.generateLocal(soot.RefType.v("java.lang.Throwable"));
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
        throwStmt.addTag(new soot.tagkit.ThrowCreatedByCompilerTag());
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

    /**
     * add exceptions to a list that gets added at end of method
     */
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
        //else if (expr instanceof polyglot.ast.ArrayInit) {
           // array init are special and get created elsewhere 
        //}
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
            System.out.println("Expr: "+expr);
            throw new RuntimeException("Unhandled Expression: ");
        }
       
    }

    private soot.Local handlePrivateFieldSet(polyglot.ast.Assign assign){
        polyglot.ast.Field fLeft = (polyglot.ast.Field)assign.left();
        //soot.Value right = createExpr(assign.right());

        // if assign is not = but +=, -=, *=, /=, >>=, >>>-, <<=, %=, 
        // |= &= or ^= then compute it all into a local first
        //if (assign.operator() != polyglot.ast.Assign.ASSIGN){
        // in this cas can cast to local (never a string const here
        // as it has to be a lhs
        soot.Value right;
        if (assign.operator() == polyglot.ast.Assign.ASSIGN){
            right = getSimpleAssignRightLocal(assign);
        }
        else if ((assign.operator() == polyglot.ast.Assign.ADD_ASSIGN) && assign.type().toString().equals("java.lang.String")){
            right = getStringConcatAssignRightLocal(assign);
        }
        else {
            soot.Local leftLocal = (soot.Local)getFieldLocal(fLeft);
            right = getAssignRightLocal(assign, leftLocal);
        }
        //}
        //else {
        //    right = createExpr(assign.right());
        //}
        
        soot.SootClass containClass = ((soot.RefType)Util.getSootType(fLeft.target().type())).getSootClass();
        soot.SootMethod methToUse = addSetAccessMeth(containClass, fLeft, right);
        ArrayList params = new ArrayList();
        if (!fLeft.flags().isStatic()){
            // this is the this ref if needed
            params.add(getThis(Util.getSootType(fLeft.target().type())));
        }
        params.add(right);
        soot.jimple.InvokeExpr invoke = soot.jimple.Jimple.v().newStaticInvokeExpr(methToUse, params);
        soot.Local retLocal = lg.generateLocal(right.getType());
        soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, invoke);
        body.getUnits().add(assignStmt);

        return retLocal;
    }
    
    private soot.SootMethod addSetAccessMeth(soot.SootClass conClass, polyglot.ast.Field field, soot.Value param){
        String name = "access$"+soot.javaToJimple.InitialResolver.v().getNextPrivateAccessCounter()+"00";
        ArrayList paramTypes = new ArrayList();
        if (!field.flags().isStatic()){
            // add this param type
            paramTypes.add(Util.getSootType(field.target().type()));
        }
        paramTypes.add(param.getType());
        soot.SootMethod meth = new soot.SootMethod(name, paramTypes, param.getType(), soot.Modifier.STATIC);
        PrivateFieldSetMethodSource pfsms = new PrivateFieldSetMethodSource();
        pfsms.fieldName(field.name());
        pfsms.fieldType(Util.getSootType(field.type()));
        pfsms.setFieldInst(field.fieldInstance());

        conClass.addMethod(meth);
        meth.setActiveBody(pfsms.getBody(meth, null));

        return meth;
    }

    private soot.Value getAssignRightLocal(polyglot.ast.Assign assign, soot.Local leftLocal){
    
        if (assign.operator() == polyglot.ast.Assign.ASSIGN){
            return getSimpleAssignRightLocal(assign);
        }
        else if (assign.operator() == polyglot.ast.Assign.ADD_ASSIGN && assign.type().toString().equals("java.lang.String")){
            return getStringConcatAssignRightLocal(assign);
        }
        else {
            return getComplexAssignRightLocal(assign, leftLocal);
        }
    }

    private soot.Value getSimpleAssignRightLocal(polyglot.ast.Assign assign){
        soot.Value right = createExpr(assign.right());
        if (right instanceof soot.jimple.ConditionExpr) {
            right = handleCondBinExpr((soot.jimple.ConditionExpr)right);
        }
        return right;
    }

    private soot.Local getStringConcatAssignRightLocal(polyglot.ast.Assign assign){
        soot.Local sb = (soot.Local)createStringBuffer(assign);
        generateAppends(assign.left(), sb);
        generateAppends(assign.right(), sb);
        soot.Local rLocal = createToString(sb, assign);
        return rLocal;
    }

    private soot.Local getComplexAssignRightLocal(polyglot.ast.Assign assign, soot.Local leftLocal){
        soot.Value right = createExpr(assign.right());
        if (right instanceof soot.jimple.ConditionExpr) {
            right = handleCondBinExpr((soot.jimple.ConditionExpr)right);
        }
        
        soot.jimple.BinopExpr binop = null;
        if (assign.operator() == polyglot.ast.Assign.ADD_ASSIGN) {
            binop = soot.jimple.Jimple.v().newAddExpr(leftLocal, right);
        }
        else if (assign.operator() == polyglot.ast.Assign.SUB_ASSIGN){
            binop = soot.jimple.Jimple.v().newSubExpr(leftLocal, right);
        }
        else if (assign.operator() == polyglot.ast.Assign.MUL_ASSIGN) {
            binop = soot.jimple.Jimple.v().newMulExpr(leftLocal, right);
        }
        else if (assign.operator() == polyglot.ast.Assign.DIV_ASSIGN) {
            binop = soot.jimple.Jimple.v().newDivExpr(leftLocal, right);
        }
        else if (assign.operator() == polyglot.ast.Assign.MOD_ASSIGN) {
            binop = soot.jimple.Jimple.v().newRemExpr(leftLocal, right);
        }
        else if (assign.operator() == polyglot.ast.Assign.SHL_ASSIGN) {
            binop = soot.jimple.Jimple.v().newShlExpr(leftLocal, right);
        }
        else if (assign.operator() == polyglot.ast.Assign.SHR_ASSIGN) {
            binop = soot.jimple.Jimple.v().newShrExpr(leftLocal, right);
        }
        else if (assign.operator() == polyglot.ast.Assign.USHR_ASSIGN) {
            binop = soot.jimple.Jimple.v().newUshrExpr(leftLocal, right);
        }
        else if (assign.operator() == polyglot.ast.Assign.BIT_AND_ASSIGN) {
            binop = soot.jimple.Jimple.v().newAndExpr(leftLocal, right);
        }
        else if (assign.operator() == polyglot.ast.Assign.BIT_OR_ASSIGN) {
            binop = soot.jimple.Jimple.v().newOrExpr(leftLocal, right);
        }
        else if (assign.operator() == polyglot.ast.Assign.BIT_XOR_ASSIGN) {
            binop = soot.jimple.Jimple.v().newXorExpr(leftLocal, right);
        }

        soot.Local retLocal = lg.generateLocal(leftLocal.getType());
        soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, binop);
        body.getUnits().add(assignStmt);
            
        Util.addLnPosTags(binop.getOp1Box(), assign.left().position());
        Util.addLnPosTags(binop.getOp2Box(), assign.right().position());
        
        return retLocal;
    }

    private soot.Value getSimpleAssignLocal(polyglot.ast.Assign assign){
        soot.jimple.AssignStmt stmt;
        soot.Value left = createLHS(assign.left());
        
        soot.Value right = getSimpleAssignRightLocal(assign);
        stmt = soot.jimple.Jimple.v().newAssignStmt(left, right);
        body.getUnits().add(stmt);
        Util.addLnPosTags(stmt, assign.position());
	    Util.addLnPosTags(stmt.getRightOpBox(), assign.right().position());
        Util.addLnPosTags(stmt.getLeftOpBox(), assign.left().position());
        if (left instanceof soot.Local){
            return left;
        }
        else {
            return right;
        }
    
    }
    
    private soot.Value getStrConAssignLocal(polyglot.ast.Assign assign){
        soot.jimple.AssignStmt stmt;
        soot.Value left = createLHS(assign.left());
        
        soot.Value right = getStringConcatAssignRightLocal(assign);
        stmt = soot.jimple.Jimple.v().newAssignStmt(left, right);
        body.getUnits().add(stmt);
        Util.addLnPosTags(stmt, assign.position());
	    Util.addLnPosTags(stmt.getRightOpBox(), assign.right().position());
        Util.addLnPosTags(stmt.getLeftOpBox(), assign.left().position());
        if (left instanceof soot.Local){
            return left;
        }
        else {
            return right;
        }
    
    }
    
    /**
     * Assign Expression Creation
     */
    private soot.Value getAssignLocal(polyglot.ast.Assign assign) {
        
        // handle private access field assigns
        HashMap accessMap = ((PolyglotMethodSource)body.getMethod().getSource()).getPrivateAccessMap();
        if ((assign.left() instanceof polyglot.ast.Field) && (accessMap != null) && accessMap.containsKey(((polyglot.ast.Field)assign.left()).fieldInstance()) && !body.getMethod().getDeclaringClass().declaresField(((polyglot.ast.Field)assign.left()).name(), Util.getSootType(((polyglot.ast.Field)assign.left()).type())) ){
            return handlePrivateFieldSet(assign);    
        }

        if (assign.operator() == polyglot.ast.Assign.ASSIGN){
            return getSimpleAssignLocal(assign);
        }
       
        if ((assign.operator() == polyglot.ast.Assign.ADD_ASSIGN) && assign.type().toString().equals("java.lang.String")){
            return getStrConAssignLocal(assign);
        }
        
        soot.jimple.AssignStmt stmt;
        soot.Value left = createLHS(assign.left());
        soot.Value left2 = (soot.Value)left.clone();
        
        soot.Local leftLocal;
        //soot.Local leftLocal2;
        if (left instanceof soot.Local){
            leftLocal = (soot.Local)left;
            //leftLocal2 = (soot.Local)left2;
            
        }
        else {
            leftLocal = lg.generateLocal(left.getType());
            //leftLocal2 = lg.generateLocal(left2.getType());
            soot.jimple.AssignStmt stmt1 = soot.jimple.Jimple.v().newAssignStmt(leftLocal, left);
            //soot.jimple.AssignStmt stmt11 = soot.jimple.Jimple.v().newAssignStmt(leftLocal2, left2);
            body.getUnits().add(stmt1);
            //body.getUnits().add(stmt11);
            Util.addLnPosTags(stmt1, assign.position());
        }
        
        
        soot.Value right = getAssignRightLocal(assign, leftLocal);
        soot.jimple.AssignStmt stmt2 = soot.jimple.Jimple.v().newAssignStmt(leftLocal, right);
        body.getUnits().add(stmt2);
        Util.addLnPosTags(stmt2, assign.position());
	    Util.addLnPosTags(stmt2.getRightOpBox(), assign.right().position());
        Util.addLnPosTags(stmt2.getLeftOpBox(), assign.left().position());
        
        if (!(left instanceof soot.Local)) {
            soot.jimple.AssignStmt stmt3 = soot.jimple.Jimple.v().newAssignStmt(left2, leftLocal);
            //soot.jimple.AssignStmt stmt31 = soot.jimple.Jimple.v().newAssignStmt(left2, leftLocal2);
            body.getUnits().add(stmt3);
            //body.getUnits().add(stmt31);
            Util.addLnPosTags(stmt3, assign.position());
	        Util.addLnPosTags(stmt3.getRightOpBox(), assign.right().position());
            Util.addLnPosTags(stmt3.getLeftOpBox(), assign.left().position());
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
            throw new RuntimeException("Should go through ClassLit");
        }
        else if ((ms.getPrivateAccessMap() != null) && (ms.getPrivateAccessMap().containsKey(field.fieldInstance())) && !body.getMethod().getDeclaringClass().declaresField(field.name(), Util.getSootType(field.type())) ){
        
            return getPrivateAccessFieldLocal(field);
        }
        if ((field.target() instanceof polyglot.ast.Special) && (((polyglot.ast.Special)field.target()).kind() == polyglot.ast.Special.SUPER) && (((polyglot.ast.Special)field.target()).qualifier() != null)){
                return getSpecialSuperQualifierLocal(field);
        }
        else if (shouldReturnConstant(field)){
            
            return getReturnConstant(field);
            // in this case don't return fieldRef but a string constant
        }
        else {

            soot.jimple.FieldRef fieldRef = getFieldRef(field);
            soot.Local baseLocal = generateLocal(field.type());
            soot.jimple.AssignStmt fieldAssignStmt = soot.jimple.Jimple.v().newAssignStmt(baseLocal, fieldRef);
            
            body.getUnits().add(fieldAssignStmt);
            Util.addLnPosTags(fieldAssignStmt, field.position());
            return baseLocal; 
        }
    }

    private soot.jimple.Constant getReturnConstant(polyglot.ast.Field field){
        //System.out.println("field cons: "+field);
        //System.out.println("field type: "+field.type());
        //System.out.println("field cons type: "+field.fieldInstance().constantValue().getClass());
        if (field.fieldInstance().constantValue() instanceof String){
            return soot.jimple.StringConstant.v((String)field.constantValue());
        }
        else if (field.fieldInstance().constantValue() instanceof Boolean){
            boolean val = ((Boolean)field.constantValue()).booleanValue();
            return soot.jimple.IntConstant.v(val ? 1 : 0);
        }
        else if (field.fieldInstance().constantValue() instanceof Character){
            char val = ((Character)field.constantValue()).charValue();
            return soot.jimple.IntConstant.v(val);
        }
        else {//if (field.fieldInstance().constantValue() instanceof Number){
            Number num = (Number)field.fieldInstance().constantValue();
            num = createConstantCast(field.type(), num);
            if (num instanceof Long) {
                return soot.jimple.LongConstant.v(((Long)num).longValue());
            }
            else if (num instanceof Double) {
                return soot.jimple.DoubleConstant.v(((Double)num).doubleValue());
            }
            else if (num instanceof Float) {
                return soot.jimple.FloatConstant.v(((Float)num).floatValue());
            }
            else if (num instanceof Byte) {
                return soot.jimple.IntConstant.v(((Byte)num).byteValue());
            }
            else if (num instanceof Short) {
                return soot.jimple.IntConstant.v(((Short)num).shortValue());
            }
            else {//if (num instanceof Long) {
                //System.out.println("num class: "+num.getClass());
                return soot.jimple.IntConstant.v(((Integer)num).intValue());
            }
        }
    }
   
    private Number createConstantCast(polyglot.types.Type fieldType, Number constant) {
        if (constant instanceof Integer){
            if (fieldType.isDouble()){
                return new Double((double)((Integer)constant).intValue());
            }
            else if (fieldType.isFloat()){
                return new Float((float)((Integer)constant).intValue());
            }
            else if (fieldType.isLong()){
                return new Long((long)((Integer)constant).intValue());
            }
        }
        return constant;
    }
    
    private boolean shouldReturnConstant(polyglot.ast.Field field){
        if (field.fieldInstance().isConstant()) {// && (field.fieldInstance().constantValue() instanceof String)){
            return true;
        }
        return false;
    }
    
    /**
     *  creates a field ref
     */
    private soot.jimple.FieldRef getFieldRef(polyglot.ast.Field field) {
       
        soot.SootClass receiverClass = ((soot.RefType)Util.getSootType(field.fieldInstance().container())).getSootClass();
         
        soot.SootField receiverField = receiverClass.getField(field.name(), Util.getSootType(field.type()));
         
        soot.jimple.FieldRef fieldRef;
        if (field.fieldInstance().flags().isStatic()) {
            fieldRef = soot.jimple.Jimple.v().newStaticFieldRef(receiverField);
        }
        else {
            soot.Local base;
                base = (soot.Local)getBaseLocal(field.target());
                //System.out.println("got base for field ref: "+base);
            fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(base, receiverField);
        }
            
        if (field.target() instanceof polyglot.ast.Local && fieldRef instanceof soot.jimple.InstanceFieldRef){
            Util.addLnPosTags(((soot.jimple.InstanceFieldRef)fieldRef).getBaseBox(), field.target().position());
        }
        return fieldRef;
    }

    /**
     * For Inner Classes - to access private fields of their outer class
     */
    private soot.Local getPrivateAccessFieldLocal(polyglot.ast.Field field) {
    
        HashMap paMap = ((soot.javaToJimple.PolyglotMethodSource)body.getMethod().getSource()).getPrivateAccessMap();
        soot.SootMethod toInvoke = (soot.SootMethod)paMap.get(field.fieldInstance());

        ArrayList params = new ArrayList();
        if (!field.fieldInstance().flags().isStatic()) {
            params.add((soot.Local)getBaseLocal(field.target()));
        }
     
        return Util.getPrivateAccessFieldInvoke(toInvoke, params, body, lg);
    }


    /**
     * To get the local for the special .class literal
     */
    private soot.Local getSpecialClassLitLocal(polyglot.ast.ClassLit lit) {
      
        if (lit.typeNode().type().isPrimitive()){
            polyglot.types.PrimitiveType primType = (polyglot.types.PrimitiveType)lit.typeNode().type();
            soot.Local retLocal = lg.generateLocal(soot.RefType.v("java.lang.Class"));
            soot.SootField primField = null;
            if (primType.isBoolean()){
                primField = soot.Scene.v().getSootClass("java.lang.Boolean").getField("TYPE", soot.RefType.v("java.lang.Class"));
            }
            else if (primType.isByte()){
                primField = soot.Scene.v().getSootClass("java.lang.Byte").getField("TYPE", soot.RefType.v("java.lang.Class"));
            }
            else if (primType.isChar()){
                primField = soot.Scene.v().getSootClass("java.lang.Character").getField("TYPE", soot.RefType.v("java.lang.Class"));
            }
            else if (primType.isDouble()){
                primField = soot.Scene.v().getSootClass("java.lang.Double").getField("TYPE", soot.RefType.v("java.lang.Class"));
            }
            else if (primType.isFloat()){
                primField = soot.Scene.v().getSootClass("java.lang.Float").getField("TYPE", soot.RefType.v("java.lang.Class"));
            }
            else if (primType.isInt()){
                primField = soot.Scene.v().getSootClass("java.lang.Integer").getField("TYPE", soot.RefType.v("java.lang.Class"));
            }
            else if (primType.isLong()){
                primField = soot.Scene.v().getSootClass("java.lang.Long").getField("TYPE", soot.RefType.v("java.lang.Class"));
            }
            else if (primType.isShort()){
                primField = soot.Scene.v().getSootClass("java.lang.Short").getField("TYPE", soot.RefType.v("java.lang.Class"));
            }
            else if (primType.isVoid()){
                primField = soot.Scene.v().getSootClass("java.lang.Void").getField("TYPE", soot.RefType.v("java.lang.Class"));
            }
            soot.jimple.StaticFieldRef fieldRef = soot.jimple.Jimple.v().newStaticFieldRef(primField);
            soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, fieldRef);
            body.getUnits().add(assignStmt);
            return retLocal;
        }
        else {
            // this class
            soot.SootClass thisClass = body.getMethod().getDeclaringClass();
            String fieldName = Util.getFieldNameForClassLit(lit.typeNode().type());
            soot.Type fieldType = soot.RefType.v("java.lang.Class");
            soot.Local fieldLocal = lg.generateLocal(soot.RefType.v("java.lang.Class"));
            soot.SootField sootField = null;
            if (thisClass.isInterface()){
                HashMap specialAnonMap = InitialResolver.v().specialAnonMap();
                if ((specialAnonMap != null) && (specialAnonMap.containsKey(thisClass))){
                    soot.SootClass specialClass = (soot.SootClass)specialAnonMap.get(thisClass);
                    sootField = specialClass.getField(fieldName, fieldType);
                }
                else {
                    throw new RuntimeException("Class is interface so it must have an anon class to handle class lits but its anon class cannot be found.");
                }
            }   
            else {
                sootField = thisClass.getField(fieldName, fieldType);
            }
            soot.jimple.StaticFieldRef fieldRef = soot.jimple.Jimple.v().newStaticFieldRef(sootField);
            soot.jimple.Stmt fieldAssign = soot.jimple.Jimple.v().newAssignStmt(fieldLocal,  fieldRef);
            body.getUnits().add(fieldAssign);

            soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
            soot.jimple.Expr neExpr = soot.jimple.Jimple.v().newNeExpr(fieldLocal, soot.jimple.NullConstant.v());
            soot.jimple.Stmt ifStmt = soot.jimple.Jimple.v().newIfStmt(neExpr, noop1);
            body.getUnits().add(ifStmt);

            ArrayList paramTypes = new ArrayList();
            paramTypes.add(soot.RefType.v("java.lang.String"));
            soot.SootMethod invokeMeth = null;
            if (thisClass.isInterface()){
                HashMap specialAnonMap = InitialResolver.v().specialAnonMap();
                if ((specialAnonMap != null) && (specialAnonMap.containsKey(thisClass))){
                    soot.SootClass specialClass = (soot.SootClass)specialAnonMap.get(thisClass);
                    invokeMeth  = specialClass.getMethod("class$", paramTypes, soot.RefType.v("java.lang.Class"));
                }
                else {
                    throw new RuntimeException("Class is interface so it must have an anon class to handle class lits but its anon class cannot be found.");
                }
            }   
            else {
                invokeMeth = thisClass.getMethod("class$", paramTypes, soot.RefType.v("java.lang.Class"));
            }
            ArrayList params = new ArrayList();
            params.add(soot.jimple.StringConstant.v(Util.getParamNameForClassLit(lit.typeNode().type())));
            soot.jimple.Expr classInvoke = soot.jimple.Jimple.v().newStaticInvokeExpr(invokeMeth, params);
            soot.Local methLocal = lg.generateLocal(soot.RefType.v("java.lang.Class"));
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
    }

    /**
     *  Array Length local for example a.length w/o brackets gets length 
     *  of array 
     */
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
        soot.Local retLocal = lg.generateLocal(soot.IntType.v());
        soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(retLocal, lengthExpr);
        body.getUnits().add(assign);
        Util.addLnPosTags(assign, field.position());
        Util.addLnPosTags(lengthExpr.getOpBox(), field.target().position());
        return retLocal;
    }
   
    /**
     * Binary Expression Creation
     */
    private soot.Value getBinaryLocal(polyglot.ast.Binary binary) {
           
        soot.Value rhs;
                
        if (binary.operator() == polyglot.ast.Binary.COND_AND) {
            return createCondAnd(binary);
        }
        if (binary.operator() == polyglot.ast.Binary.COND_OR) {
            return createCondOr(binary);
        }

        if (binary.type().toString().equals("java.lang.String")){
            if (areAllStringLits(binary)){
                String result = createStringConstant(binary);
                return soot.jimple.StringConstant.v(result);
            }
            else {
                soot.Local sb = (soot.Local)createStringBuffer(binary);
                generateAppends(binary.left(), sb);
                generateAppends(binary.right(), sb);
                return createToString(sb, binary);
            }
        }
        
        soot.Value lVal = createExpr(binary.left());
        soot.Value rVal = createExpr(binary.right());

        if (isComparisonBinary(binary.operator())) {
            rhs = getBinaryComparisonExpr(lVal, rVal, binary.operator());
        }
        else {
            rhs = getBinaryExpr(lVal, rVal, binary.operator());
        }
        
        if (rhs instanceof soot.jimple.BinopExpr) {
            Util.addLnPosTags(((soot.jimple.BinopExpr)rhs).getOp1Box(), binary.left().position());
		    Util.addLnPosTags(((soot.jimple.BinopExpr)rhs).getOp2Box(), binary.right().position());
        }
        
        if (rhs instanceof soot.jimple.ConditionExpr) {
            return rhs;
        }
        
        soot.Local lhs = generateLocal(binary.type());


        soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(lhs, rhs);
        body.getUnits().add(assignStmt);
            
     
        Util.addLnPosTags(assignStmt.getRightOpBox(), binary.position());
        return lhs;
    } 

    private boolean areAllStringLits(polyglot.ast.Node node){
        if (node instanceof polyglot.ast.StringLit) return true;
        else if ( node instanceof polyglot.ast.Field) {
            if (shouldReturnConstant((polyglot.ast.Field)node)) return true;
            else return false;
        }
        else if (node instanceof polyglot.ast.Binary){
            if (areAllStringLitsBinary((polyglot.ast.Binary)node)) return true;
            return false;
        }
        return false;
    }
   
    private boolean areAllStringLitsBinary(polyglot.ast.Binary binary){
        if (areAllStringLits(binary.left()) && areAllStringLits(binary.right())) return true;
        else return false;
    }

    private String createStringConstant(polyglot.ast.Node node){
        String s = null;
        if (node instanceof polyglot.ast.StringLit){
            s = ((polyglot.ast.StringLit)node).value();
        }
        else if (node instanceof polyglot.ast.Field){
            s = (((polyglot.ast.Field)node).fieldInstance().constantValue()).toString();
        }
        else if (node instanceof polyglot.ast.Binary){
            s = createStringConstantBinary((polyglot.ast.Binary)node);
        }
        else {
            throw new RuntimeException("No other string constant folding done");
        }
        return s;
    }
    
    private String createStringConstantBinary(polyglot.ast.Binary binary){
        String s = createStringConstant(binary.left())+ createStringConstant(binary.right());
        return s;
        
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
    
    /**
     * Creates a binary expression that is not a comparison
     */
	private soot.Value getBinaryExpr(soot.Value lVal, soot.Value rVal, polyglot.ast.Binary.Operator operator){
		
        soot.Value rValue = null;

        if (lVal instanceof soot.jimple.ConditionExpr) {
            lVal = handleCondBinExpr((soot.jimple.ConditionExpr)lVal);
        }
        if (rVal instanceof soot.jimple.ConditionExpr) {
            rVal = handleCondBinExpr((soot.jimple.ConditionExpr)rVal);
        }
        if (operator == polyglot.ast.Binary.ADD){
           
			    rValue = soot.jimple.Jimple.v().newAddExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.SUB){
			rValue = soot.jimple.Jimple.v().newSubExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.MUL){
			rValue = soot.jimple.Jimple.v().newMulExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.DIV){
			rValue = soot.jimple.Jimple.v().newDivExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.SHR){
            if (rVal.getType().equals(soot.LongType.v())){
                soot.Local intVal = lg.generateLocal(soot.IntType.v());
                soot.jimple.CastExpr castExpr = soot.jimple.Jimple.v().newCastExpr(rVal, soot.IntType.v());
                soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(intVal, castExpr);
                body.getUnits().add(assignStmt);
			    rValue = soot.jimple.Jimple.v().newShrExpr(lVal, intVal);
            }
            else {
			    rValue = soot.jimple.Jimple.v().newShrExpr(lVal, rVal);
            }
		}
		else if (operator == polyglot.ast.Binary.USHR){
            if (rVal.getType().equals(soot.LongType.v())){
                soot.Local intVal = lg.generateLocal(soot.IntType.v());
                soot.jimple.CastExpr castExpr = soot.jimple.Jimple.v().newCastExpr(rVal, soot.IntType.v());
                soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(intVal, castExpr);
                body.getUnits().add(assignStmt);
			    rValue = soot.jimple.Jimple.v().newUshrExpr(lVal, intVal);
            }
            else {
			    rValue = soot.jimple.Jimple.v().newUshrExpr(lVal, rVal);
            }
		}
		else if (operator == polyglot.ast.Binary.SHL){
            if (rVal.getType().equals(soot.LongType.v())){
                soot.Local intVal = lg.generateLocal(soot.IntType.v());
                soot.jimple.CastExpr castExpr = soot.jimple.Jimple.v().newCastExpr(rVal, soot.IntType.v());
                soot.jimple.AssignStmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(intVal, castExpr);
                body.getUnits().add(assignStmt);
			    rValue = soot.jimple.Jimple.v().newShlExpr(lVal, intVal);
            }
            else {
			    rValue = soot.jimple.Jimple.v().newShlExpr(lVal, rVal);
            }
		}
		else if (operator == polyglot.ast.Binary.BIT_AND){
			rValue = soot.jimple.Jimple.v().newAndExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.BIT_OR){
			rValue = soot.jimple.Jimple.v().newOrExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.BIT_XOR){
			rValue = soot.jimple.Jimple.v().newXorExpr(lVal, rVal);
		}
		else if (operator == polyglot.ast.Binary.MOD){
			rValue = soot.jimple.Jimple.v().newRemExpr(lVal, rVal);
		}
		else {
			throw new RuntimeException("Binary not yet handled!");
		}

        return rValue;
	}
 
    /**
     * Creates a binary expr that is a comparison 
     */
    private soot.Value getBinaryComparisonExpr(soot.Value lVal, soot.Value rVal, polyglot.ast.Binary.Operator operator) {
		
        soot.Value rValue;
        
            if (operator == polyglot.ast.Binary.EQ){
			    rValue = soot.jimple.Jimple.v().newEqExpr(lVal, rVal);
		    }
		    else if (operator == polyglot.ast.Binary.GE){
			    rValue = soot.jimple.Jimple.v().newGeExpr(lVal, rVal);
		    }
		    else if (operator == polyglot.ast.Binary.GT){
			    rValue = soot.jimple.Jimple.v().newGtExpr(lVal, rVal);
		    }
		    else if (operator == polyglot.ast.Binary.LE){
			    rValue = soot.jimple.Jimple.v().newLeExpr(lVal, rVal);
		    }
		    else if (operator == polyglot.ast.Binary.LT){
			    rValue = soot.jimple.Jimple.v().newLtExpr(lVal, rVal);
		    }
		    else if (operator == polyglot.ast.Binary.NE){
			    rValue = soot.jimple.Jimple.v().newNeExpr(lVal, rVal);
		    }
            else {
                throw new RuntimeException("Unknown Comparison Expr");
            }
       
            return rValue;
    }

    /**
     * in bytecode and Jimple the conditions in conditional binary 
     * expressions are often reversed
     */
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
        return newExpr;
    }
   
    
    /**
     * Special conditions for doubles and floats and longs
     */
    private soot.Value handleDFLCond(soot.jimple.ConditionExpr cond){
        soot.Local result = lg.generateLocal(soot.ByteType.v());
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
    
    /**
     * Creates a conitional AND expr
     */
    private soot.Local createCondAnd(polyglot.ast.Binary binary) {
            
        soot.Local retLocal = lg.generateLocal(soot.BooleanType.v());
            
        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
        
        soot.Value lVal = createExpr(binary.left());
        boolean leftNeedIf = needSootIf(lVal); 
        if (!(lVal instanceof soot.jimple.ConditionExpr)) {
            lVal = soot.jimple.Jimple.v().newEqExpr(lVal, soot.jimple.IntConstant.v(0));
        }
        else {
            lVal = reverseCondition((soot.jimple.ConditionExpr)lVal);
            lVal = handleDFLCond((soot.jimple.ConditionExpr)lVal);
        }
            
        if (leftNeedIf){
            soot.jimple.IfStmt ifLeft = soot.jimple.Jimple.v().newIfStmt(lVal, noop1);
            body.getUnits().add(ifLeft);
            Util.addLnPosTags(ifLeft.getConditionBox(), binary.left().position()); 
        }
        
        soot.jimple.Stmt endNoop = soot.jimple.Jimple.v().newNopStmt();
        soot.Value rVal = createExpr(binary.right());
        boolean rightNeedIf = needSootIf(rVal);
        if (!(rVal instanceof soot.jimple.ConditionExpr)) {
            rVal = soot.jimple.Jimple.v().newEqExpr(rVal, soot.jimple.IntConstant.v(0));
        }
        else {
            rVal = reverseCondition((soot.jimple.ConditionExpr)rVal);
            rVal = handleDFLCond((soot.jimple.ConditionExpr)rVal);
        }
        if (rightNeedIf){
            soot.jimple.IfStmt ifRight = soot.jimple.Jimple.v().newIfStmt(rVal, noop1);
            body.getUnits().add(ifRight);
            Util.addLnPosTags(ifRight.getConditionBox(), binary.right().position());
        }
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

    /**
     * Creates a conditional OR expr
     */
    private soot.Local createCondOr(polyglot.ast.Binary binary) {
        soot.Local retLocal = lg.generateLocal(soot.BooleanType.v());
            
        //end 
        soot.jimple.Stmt endNoop = soot.jimple.Jimple.v().newNopStmt();
         
        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
        soot.Value lVal = createExpr(binary.left());
        boolean leftNeedIf = needSootIf(lVal);
        if (!(lVal instanceof soot.jimple.ConditionExpr)) {
            lVal = soot.jimple.Jimple.v().newEqExpr(lVal, soot.jimple.IntConstant.v(1));
        }
        else {
            lVal = handleDFLCond((soot.jimple.ConditionExpr)lVal);
        }
        
        if (leftNeedIf){
            soot.jimple.IfStmt ifLeft = soot.jimple.Jimple.v().newIfStmt(lVal, noop1);
            body.getUnits().add(ifLeft);
            Util.addLnPosTags(ifLeft, binary.left().position());    
            Util.addLnPosTags(ifLeft.getConditionBox(), binary.left().position());    
        }
        
        soot.Value rVal = createExpr(binary.right());
        boolean rightNeedIf = needSootIf(rVal);
        if (!(rVal instanceof soot.jimple.ConditionExpr)) {
            rVal = soot.jimple.Jimple.v().newEqExpr(rVal, soot.jimple.IntConstant.v(1));
        }
        else {
            rVal = handleDFLCond((soot.jimple.ConditionExpr)rVal);
        }
        if (rightNeedIf){
            soot.jimple.IfStmt ifRight = soot.jimple.Jimple.v().newIfStmt(rVal, noop1);
            body.getUnits().add(ifRight);
            Util.addLnPosTags(ifRight, binary.right().position());    
            Util.addLnPosTags(ifRight.getConditionBox(), binary.right().position());    
        }
        
        soot.jimple.Stmt assign2 = soot.jimple.Jimple.v().newAssignStmt(retLocal, soot.jimple.IntConstant.v(0));
        body.getUnits().add(assign2);
        Util.addLnPosTags(assign2, binary.position());
        soot.jimple.Stmt gotoEnd2 = soot.jimple.Jimple.v().newGotoStmt(endNoop);
        body.getUnits().add(gotoEnd2);
           
        body.getUnits().add(noop1);

        soot.jimple.Stmt assign3 = soot.jimple.Jimple.v().newAssignStmt(retLocal, soot.jimple.IntConstant.v(1));
        body.getUnits().add(assign3);
        Util.addLnPosTags(assign3, binary.position());

        body.getUnits().add(endNoop);

        return retLocal;
    }
    
    private soot.Local handleCondBinExpr(soot.jimple.ConditionExpr condExpr) {
    
        soot.Local boolLocal = lg.generateLocal(soot.BooleanType.v());

        soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
            
        soot.Value newVal;
       
        newVal = reverseCondition(condExpr);
        newVal = handleDFLCond((soot.jimple.ConditionExpr)newVal);

        soot.jimple.Stmt ifStmt = soot.jimple.Jimple.v().newIfStmt(newVal, noop1);
        body.getUnits().add(ifStmt);

        body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(boolLocal, soot.jimple.IntConstant.v(1)));

        soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
        
        soot.jimple.Stmt goto1 = soot.jimple.Jimple.v().newGotoStmt(noop2);

        body.getUnits().add(goto1);

        body.getUnits().add(noop1);
        
        body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(boolLocal, soot.jimple.IntConstant.v(0)));

        body.getUnits().add(noop2);

        return boolLocal;
        
        
    }
    
    private soot.Local createStringBuffer(polyglot.ast.Expr expr){
        // create and add one string buffer 
        soot.Local local = lg.generateLocal(soot.RefType.v("java.lang.StringBuffer"));
        soot.jimple.NewExpr newExpr = soot.jimple.Jimple.v().newNewExpr(soot.RefType.v("java.lang.StringBuffer"));
        soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(local, newExpr);
        
        body.getUnits().add(assign);
        Util.addLnPosTags(assign, expr.position());
        
        soot.SootClass classToInvoke1 = soot.Scene.v().getSootClass("java.lang.StringBuffer");
        soot.SootMethod methodToInvoke1 = getMethodFromClass(classToInvoke1, "<init>", new ArrayList(), soot.VoidType.v()); 
        
        soot.jimple.SpecialInvokeExpr invoke = soot.jimple.Jimple.v().newSpecialInvokeExpr(local, methodToInvoke1);
            
        soot.jimple.Stmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(invoke);
        body.getUnits().add(invokeStmt);
        Util.addLnPosTags(invokeStmt, expr.position());
        
        return local;
    }
    

    private soot.Local createToString(soot.Local sb, polyglot.ast.Expr expr){
        // invoke toString on local (type StringBuffer)
        soot.Local newString = lg.generateLocal(soot.RefType.v("java.lang.String"));
        soot.SootClass classToInvoke2 = soot.Scene.v().getSootClass("java.lang.StringBuffer");
        soot.SootMethod methodToInvoke2 = getMethodFromClass(classToInvoke2, "toString", new ArrayList(), soot.RefType.v("java.lang.String")); 
                 
        soot.jimple.VirtualInvokeExpr toStringInvoke = soot.jimple.Jimple.v().newVirtualInvokeExpr(sb, methodToInvoke2);
                
        soot.jimple.Stmt lastAssign = soot.jimple.Jimple.v().newAssignStmt(newString, toStringInvoke);

        body.getUnits().add(lastAssign);
        Util.addLnPosTags(lastAssign, expr.position());
        
        return newString; 
    }

    

    private boolean isStringConcat(polyglot.ast.Expr expr){
        if (expr instanceof polyglot.ast.Binary) {
            polyglot.ast.Binary bin = (polyglot.ast.Binary)expr;
            if (bin.operator() == polyglot.ast.Binary.ADD){
                if (bin.type().toString().equals("java.lang.String")) return true;
                return false;        
            }
            return false;
        }
        else if (expr instanceof polyglot.ast.Assign) {
            polyglot.ast.Assign assign = (polyglot.ast.Assign)expr;
            if (assign.operator() == polyglot.ast.Assign.ADD_ASSIGN){
                if (assign.type().toString().equals("java.lang.String")) return true;
                return false;
            }
            return false;
        }
        return false;
    }
    
    /**
     * Generates one part of a concatenation String
     */
    private void generateAppends(polyglot.ast.Expr expr, soot.Local sb) {

      
        if (isStringConcat(expr)){
            if (expr instanceof polyglot.ast.Binary){
                generateAppends(((polyglot.ast.Binary)expr).left(), sb);
                generateAppends(((polyglot.ast.Binary)expr).right(), sb);
            }
            else {
                generateAppends(((polyglot.ast.Assign)expr).left(), sb);
                generateAppends(((polyglot.ast.Assign)expr).right(), sb);
            }
        }
        else {
            soot.Value toApp = createExpr(expr);
            soot.Type appendType = null;
            if (toApp instanceof soot.jimple.StringConstant) {
                appendType = soot.RefType.v("java.lang.String");
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
                        appendType = soot.RefType.v("java.lang.String");
                    }
                    else if (((soot.Local)toApp).getType().toString().equals("java.lang.StringBuffer")){
                        appendType = soot.RefType.v("java.lang.StringBuffer");
                    }
                    else{
                        appendType = soot.RefType.v("java.lang.Object");
                    }
                }
                else {
                    // this is for arrays
                    appendType = soot.RefType.v("java.lang.Object");
                }
            }
            else if (toApp instanceof soot.jimple.ConditionExpr) {
                toApp = handleCondBinExpr((soot.jimple.ConditionExpr)toApp);
                appendType = soot.BooleanType.v();
            }

            // handle shorts
            if (appendType instanceof soot.ShortType || appendType instanceof soot.ByteType) {
                soot.Local intLocal = lg.generateLocal(soot.IntType.v());
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
            soot.SootMethod methodToInvoke = getMethodFromClass(classToInvoke, "append", paramsTypes, soot.RefType.v("java.lang.StringBuffer"));

            soot.jimple.VirtualInvokeExpr appendInvoke = soot.jimple.Jimple.v().newVirtualInvokeExpr(sb, methodToInvoke, params);


            soot.jimple.Stmt appendStmt = soot.jimple.Jimple.v().newInvokeStmt(appendInvoke);

            body.getUnits().add(appendStmt);
        
            Util.addLnPosTags(appendStmt, expr.position()); 
        }
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
            body.getUnits().add(preStmt);

            soot.jimple.AddExpr addExpr = soot.jimple.Jimple.v().newAddExpr(sootExpr, getConstant(retLocal.getType(), 1));
            
            Util.addLnPosTags(addExpr.getOp1Box(), expr.position());
            
            soot.Local local = generateLocal(expr.type());
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(local, addExpr);
            body.getUnits().add(stmt);

            Util.addLnPosTags(stmt, expr.position());
            soot.jimple.AssignStmt aStmt = soot.jimple.Jimple.v().newAssignStmt(sootExpr, local);
            body.getUnits().add(aStmt);

            Util.addLnPosTags(aStmt, expr.position());
            Util.addLnPosTags(aStmt, unary.position());
            
            if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                soot.jimple.AssignStmt s = soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, local);
                body.getUnits().add(s);
                Util.addLnPosTags(s, expr.position());
                Util.addLnPosTags(s.getLeftOpBox(), expr.position());
                
            }
            return retLocal;
            
        }
        else if (op == polyglot.ast.Unary.POST_DEC) {
            soot.Local retLocal = generateLocal(expr.type());
            
            soot.Value sootExpr = createExpr(expr);

            soot.jimple.AssignStmt preStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, sootExpr);
            body.getUnits().add(preStmt);
            
            soot.jimple.SubExpr subExpr = soot.jimple.Jimple.v().newSubExpr(sootExpr, getConstant(retLocal.getType(), 1));
            Util.addLnPosTags(subExpr.getOp1Box(), expr.position());
            
            soot.Local local = generateLocal(expr.type());
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(local, subExpr);
            body.getUnits().add(stmt);
            Util.addLnPosTags(stmt, expr.position());
            
            soot.jimple.AssignStmt aStmt = soot.jimple.Jimple.v().newAssignStmt(sootExpr, local);
            body.getUnits().add(aStmt);

            Util.addLnPosTags(aStmt, expr.position());
            Util.addLnPosTags(aStmt, unary.position());
           
            if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                soot.jimple.AssignStmt s = soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, local);
                body.getUnits().add(s);

                Util.addLnPosTags(s, expr.position());
                Util.addLnPosTags(s.getLeftOpBox(), expr.position());
            }

            return retLocal;
            
        }
        else if (op == polyglot.ast.Unary.PRE_INC) {
            
            soot.Value sootExpr = createExpr(expr);
          
            soot.jimple.AddExpr addExpr = soot.jimple.Jimple.v().newAddExpr(sootExpr, getConstant(sootExpr.getType(), 1));
            Util.addLnPosTags(addExpr.getOp1Box(), expr.position());

            soot.Local local = generateLocal(expr.type());
            
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(local, addExpr);

            body.getUnits().add(stmt);
            Util.addLnPosTags(stmt, expr.position());
            
            if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess) || (expr instanceof polyglot.ast.Local)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, local));
            }

            return local;
            
        }
        else if (op == polyglot.ast.Unary.PRE_DEC) {
            
            soot.Value sootExpr = createExpr(expr);
          
            soot.jimple.SubExpr subExpr = soot.jimple.Jimple.v().newSubExpr(sootExpr, getConstant(sootExpr.getType(), 1));
            Util.addLnPosTags(subExpr.getOp1Box(), expr.position());
            
            soot.Local local = generateLocal(expr.type());
            
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(local, subExpr);

            body.getUnits().add(stmt);
            Util.addLnPosTags(stmt, expr.position());
            
            if ((expr instanceof polyglot.ast.Field) || (expr instanceof polyglot.ast.ArrayAccess) || (expr instanceof polyglot.ast.Local)) {
                soot.Value actualUnaryExpr = createLHS(expr);
                body.getUnits().add(soot.jimple.Jimple.v().newAssignStmt(actualUnaryExpr, local));
            }

            return local;
            
        }
        else if (op == polyglot.ast.Unary.BIT_NOT) {
            soot.jimple.IntConstant int1 = soot.jimple.IntConstant.v(-1);
            
            soot.Local retLocal = generateLocal(expr.type());
            
            soot.Value sootExpr = createExpr(expr);
            
            soot.jimple.XorExpr xor = soot.jimple.Jimple.v().newXorExpr(sootExpr, getConstant(sootExpr.getType(), -1));

            Util.addLnPosTags(xor.getOp1Box(), expr.position());
            soot.jimple.Stmt assign1 = soot.jimple.Jimple.v().newAssignStmt(retLocal, xor);

            body.getUnits().add(assign1);
            
            Util.addLnPosTags(assign1, unary.position());
            
            return retLocal;
        }
        else if (op == polyglot.ast.Unary.NEG) {
            soot.Value sootExpr;
            if (expr instanceof polyglot.ast.NumLit) {
                int intVal = (int)((polyglot.ast.NumLit)expr).longValue();
                sootExpr = soot.jimple.IntConstant.v(-intVal);
            }
            else if (expr instanceof polyglot.ast.FloatLit){
                double doubleVal = ((polyglot.ast.FloatLit)expr).value();
                if (((polyglot.ast.FloatLit)expr).kind() == polyglot.ast.FloatLit.DOUBLE){
                    sootExpr = soot.jimple.DoubleConstant.v(-doubleVal);
                }
                else {
                    sootExpr = soot.jimple.FloatConstant.v(-(float)doubleVal);
                }
            }
            else {
                soot.Value local = createExpr(expr);

                soot.jimple.NegExpr negExpr = soot.jimple.Jimple.v().newNegExpr(local);
                sootExpr = negExpr;
                Util.addLnPosTags(negExpr.getOpBox(), expr.position());
            }
            
            soot.Local retLocal = generateLocal(expr.type());

            soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(retLocal, sootExpr);

            body.getUnits().add(assign);
            
            Util.addLnPosTags(assign, expr.position());
            
            return retLocal;

        }
        else if (op == polyglot.ast.Unary.POS) {
            soot.Local retLocal = generateLocal(expr.type());
            soot.Value sootExpr = createExpr(expr);
            soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(retLocal, sootExpr);
            body.getUnits().add(assign);
            
            Util.addLnPosTags(assign, expr.position());
            
            return retLocal;
        }
        else if (op == polyglot.ast.Unary.NOT) {

            soot.Value local = createExpr(expr);
           
            if (local instanceof soot.jimple.ConditionExpr){
                local = handleCondBinExpr((soot.jimple.ConditionExpr)local);
            }
            soot.jimple.NeExpr neExpr = soot.jimple.Jimple.v().newNeExpr(local, getConstant(local.getType(), 0));

            soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();

            soot.jimple.Stmt ifStmt = soot.jimple.Jimple.v().newIfStmt(neExpr, noop1);
            body.getUnits().add(ifStmt);
            Util.addLnPosTags(ifStmt, expr.position());

            soot.Local retLocal = lg.generateLocal(local.getType());

            soot.jimple.Stmt assign1 = soot.jimple.Jimple.v().newAssignStmt(retLocal, getConstant(retLocal.getType(), 1));

            body.getUnits().add(assign1);
            Util.addLnPosTags(assign1, expr.position());

            soot.jimple.Stmt noop2 = soot.jimple.Jimple.v().newNopStmt();
            
            soot.jimple.Stmt goto1 = soot.jimple.Jimple.v().newGotoStmt(noop2);

            body.getUnits().add(goto1);
            
            body.getUnits().add(noop1);
            
            soot.jimple.Stmt assign2 = soot.jimple.Jimple.v().newAssignStmt(retLocal, getConstant(retLocal.getType(), 0));

            body.getUnits().add(assign2);
            Util.addLnPosTags(assign2, expr.position());

            body.getUnits().add(noop2);
            
            
            return retLocal;
        }
        else { 
            throw new RuntimeException("Unhandled Unary Expr");    
        }

        
    }

    /**
     * Returns a needed constant given a type and val
     */
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
    private soot.Value getCastLocal(polyglot.ast.Cast castExpr){
 
        
        // if its already the right type
        if (castExpr.expr().type().equals(castExpr.type())) {
            return createExpr(castExpr.expr());
        }

        //else
        soot.Value val;
        /*if (castExpr.expr() instanceof polyglot.ast.Cast) {
            val = createExpr(((polyglot.ast.Cast)castExpr.expr()).expr());
        }
        else {*/
            val = createExpr(castExpr.expr());
        //}
        soot.Type type = Util.getSootType(castExpr.type());

        soot.jimple.CastExpr cast = soot.jimple.Jimple.v().newCastExpr(val, type);
        Util.addLnPosTags(cast.getOpBox(), castExpr.expr().position());
        soot.Local retLocal = lg.generateLocal(cast.getCastType());
        soot.jimple.Stmt castAssign = soot.jimple.Jimple.v().newAssignStmt(retLocal, cast);
        body.getUnits().add(castAssign);
        Util.addLnPosTags(castAssign, castExpr.position());

        return retLocal;
    }
    
    /**
     * Procedure Call Helper Methods
     * Returns list of params
     */
    private ArrayList getSootParams(polyglot.ast.ProcedureCall call) {
        
        ArrayList sootParams = new ArrayList();
        Iterator it = call.arguments().iterator();
        while (it.hasNext()) {
            polyglot.ast.Expr next = (polyglot.ast.Expr)it.next();
            soot.Value nextExpr = createExpr(next);
            if (nextExpr instanceof soot.jimple.ConditionExpr){
                nextExpr = handleCondBinExpr((soot.jimple.ConditionExpr)nextExpr);
            }
            sootParams.add(nextExpr);
        }
        return sootParams;
    }
    
    /**
     * Returns list of param types
     */
    private ArrayList getSootParamsTypes(polyglot.ast.ProcedureCall call) {
        
        ArrayList sootParamsTypes = new ArrayList();
        Iterator it = call.procedureInstance().formalTypes().iterator();
        while (it.hasNext()) {
            Object next = it.next();
            sootParamsTypes.add(Util.getSootType((polyglot.types.Type)next));
        }
        return sootParamsTypes;
    }

    /**
     * Gets the Soot Method form the given Soot Class
     */
    private soot.SootMethod getMethodFromClass(soot.SootClass sootClass, String name, ArrayList paramTypes, soot.Type returnType) {
        try {
        //if (sootClass.declaresMethod(name, paramTypes, returnType)){
            return sootClass.getMethod(name, paramTypes, returnType);
        }
        catch(Exception e){
        //else {
            // its possible we're looking for a init method for an inner
            // class and we have a subclass as the outerclass this ref param
            // type but the actually method uses the super class
            if (paramTypes != null && !paramTypes.isEmpty()){
                soot.SootClass firstParam = ((soot.RefType)paramTypes.get(0)).getSootClass();
        
                boolean foundMeth = false;
                while (!foundMeth){
                    paramTypes.set(0, firstParam.getSuperclass().getType());
                    if (sootClass.declaresMethod(name, paramTypes, returnType)){
                        return sootClass.getMethod(name, paramTypes, returnType);
                    }
                    firstParam = firstParam.getSuperclass();
                }
            }
        }
        return null;
    }
  
    /**
     * Adds extra params
     */
    private void handleFinalLocalParams(ArrayList sootParams, ArrayList sootParamTypes, polyglot.types.ClassType keyType){
        
        HashMap finalLocalInfo = soot.javaToJimple.InitialResolver.v().finalLocalInfo();
        if (finalLocalInfo != null){
            if (finalLocalInfo.containsKey(new polyglot.util.IdentityKey(keyType))){
                AnonLocalClassInfo alci = (AnonLocalClassInfo)finalLocalInfo.get(new polyglot.util.IdentityKey(keyType));
                
                ArrayList finalLocals = alci.finalLocals();
                if (finalLocals != null){
                    Iterator it = finalLocals.iterator();
                    while (it.hasNext()){
                      
                        polyglot.types.LocalInstance li = (polyglot.types.LocalInstance)((polyglot.util.IdentityKey)it.next()).object();
                        sootParamTypes.add(Util.getSootType(li.type()));
                        sootParams.add(getLocal(li));
                    }
                }
            }
        }
    }
   
    private soot.Local getThis(soot.Type sootType){

        return Util.getThis(sootType, body, getThisMap, lg);
    }
    

    /**
     * adds outer class params
     */
    private void handleOuterClassParams(ArrayList sootParams, ArrayList sootParamsTypes, polyglot.types.ClassType typeToInvoke){
           

        ArrayList needsRef = soot.javaToJimple.InitialResolver.v().getHasOuterRefInInit();

        if ((needsRef != null) && (needsRef.contains(Util.getSootType(typeToInvoke)))){
            soot.SootClass outerClass = ((soot.RefType)Util.getSootType(typeToInvoke.outer())).getSootClass();
            soot.Local classToInvokeOuterParam = getThis(outerClass.getType());
            sootParamsTypes.add(outerClass.getType());
            sootParams.add(classToInvokeOuterParam);
        }
    }
    
    /**
     * Constructor Call Creation
     */
    private void createConstructorCall(polyglot.ast.ConstructorCall cCall) {
      
        ArrayList sootParams = new ArrayList();
        ArrayList sootParamsTypes = new ArrayList();
        
        polyglot.types.ConstructorInstance cInst = cCall.constructorInstance();
        String containerName = null;
        
        if (cInst.container() instanceof polyglot.types.ClassType) { 
            containerName = ((polyglot.types.ClassType)cInst.container()).fullName();
        }
        
        soot.SootClass classToInvoke;
           
        if (cCall.kind() == polyglot.ast.ConstructorCall.SUPER) {

            classToInvoke = ((soot.RefType)Util.getSootType(cInst.container())).getSootClass();
        }
        else if (cCall.kind() == polyglot.ast.ConstructorCall.THIS) {
            classToInvoke = body.getMethod().getDeclaringClass();
        }
        else {
            throw new RuntimeException("Unknown kind of Constructor Call");
        }
        soot.Local base = specialThisLocal;
        if (cCall.qualifier() != null){
            polyglot.types.ClassType objType = (polyglot.types.ClassType)cInst.container();
            if ((objType.outer() != null) && (body.getMethod().getDeclaringClass().equals(((soot.RefType)Util.getSootType(objType.outer())).getSootClass()))){
                handleOuterClassParams(sootParams, sootParamsTypes, objType);
            }
            else {
                soot.Local qVal = (soot.Local)createExpr(cCall.qualifier());
           
                sootParams.add(qVal);
                sootParamsTypes.add(qVal.getType());
                body.getUnits().add(soot.jimple.Jimple.v().newInvokeStmt(soot.jimple.Jimple.v().newVirtualInvokeExpr(qVal, soot.Scene.v().getSootClass("java.lang.Object").getMethodByName("getClass"), new ArrayList())));
            }
        }
        else {
        
            handleOuterClassParams(sootParams, sootParamsTypes, (polyglot.types.ClassType)cInst.container());
        }
        int index = classToInvoke.getName().lastIndexOf("$");
        sootParams.addAll(getSootParams(cCall));
        sootParamsTypes.addAll(getSootParamsTypes(cCall));
        if (index != -1){
            // fix this to send the final locals list instead of empty array
            handleFinalLocalParams(sootParams, sootParamsTypes, (polyglot.types.ClassType)cCall.constructorInstance().container());
        }
        
        
        soot.SootMethod methodToInvoke = getMethodFromClass(classToInvoke, "<init>", sootParamsTypes, soot.VoidType.v());
        
        soot.jimple.SpecialInvokeExpr specialInvokeExpr = soot.jimple.Jimple.v().newSpecialInvokeExpr(base, methodToInvoke, sootParams);

        soot.jimple.Stmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(specialInvokeExpr);
        
        body.getUnits().add(invokeStmt);
        Util.addLnPosTags(invokeStmt, cCall.position());

        int numParams = 0;
        Iterator invokeParamsIt = cCall.arguments().iterator();
        while (invokeParamsIt.hasNext()) {
            Util.addLnPosTags(specialInvokeExpr.getArgBox(numParams), ((polyglot.ast.Expr)invokeParamsIt.next()).position());
            numParams++;
        }
       
        // if method is <init> handle field inits
        if (body.getMethod().getName().equals("<init>") && (cCall.kind() == polyglot.ast.ConstructorCall.SUPER)){
            
            handleOuterClassThisInit(body.getMethod());
            handleFinalLocalInits(); 
            handleFieldInits(body.getMethod());
            handleInitializerBlocks(body.getMethod());
        }
        
    }

    private void handleFinalLocalInits(){
        ArrayList finalsList = ((PolyglotMethodSource)body.getMethod().getSource()).getFinalsList();
        if (finalsList == null) return;
        int paramCount = paramRefCount -  finalsList.size();
        Iterator it = finalsList.iterator();
        while (it.hasNext()){
            soot.SootField sf = (soot.SootField)it.next();

            soot.jimple.FieldRef fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(specialThisLocal, sf);
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(fieldRef, body.getParameterLocal(paramCount));
            body.getUnits().add(stmt);
            paramCount++;
        }
    }
    
    /**
     * Local Class Decl - Local Inner Class
     */
    private void createLocalClassDecl(polyglot.ast.LocalClassDecl cDecl) {
        BiMap lcMap = InitialResolver.v().getLocalClassMap();
        String name = Util.getSootType(cDecl.decl().type()).toString();
        if (!InitialResolver.v().hasClassInnerTag(body.getMethod().getDeclaringClass(), name)){
            Util.addInnerClassTag(body.getMethod().getDeclaringClass(), name, null, cDecl.decl().name(), Util.getModifier(cDecl.decl().flags()));
            /*body.getMethod().getDeclaringClass().addTag(
                    new soot.tagkit.InnerClassTag(
                    name,
                    null, //"<not a member>",
                    cDecl.decl().name(),
                    Util.getModifier(cDecl.decl().flags()) ));*/
        }
    }
    
    /**
     * New Expression Creation
     */
    private soot.Local getNewLocal(polyglot.ast.New newExpr) {

        // handle parameters/args
        ArrayList sootParams = new ArrayList();
        ArrayList sootParamsTypes = new ArrayList();

        polyglot.types.ClassType objType = (polyglot.types.ClassType)newExpr.objectType().type();

        if (newExpr.anonType() != null){
            objType = newExpr.anonType();
            // add inner class tags for any anon classes created
            //System.out.println("About to add inner class tag for anon class:");
            String name = Util.getSootType(objType).toString();
            //System.out.println("name: "+name); 
            polyglot.types.ClassType outerType = objType.outer();
            if (!InitialResolver.v().hasClassInnerTag(body.getMethod().getDeclaringClass(), name)){
                Util.addInnerClassTag(body.getMethod().getDeclaringClass(), name, null, null, outerType.flags().isInterface() ? soot.Modifier.PUBLIC | soot.Modifier.STATIC : Util.getModifier(objType.flags()));
                /*body.getMethod().getDeclaringClass().addTag(
                    new soot.tagkit.InnerClassTag(
                    name,
                    null,//"<not a member>",
                    null,//"<anonymous>",
                    outerType.flags().isInterface() ? soot.Modifier.PUBLIC | soot.Modifier.STATIC : Util.getModifier(objType.flags()) ));*/
            }
        }
        else {
            // not an anon class but actually invoking a new something
            if (!objType.isTopLevel()){
                String name = Util.getSootType(objType).toString();
                polyglot.types.ClassType outerType = objType.outer();
                if (!InitialResolver.v().hasClassInnerTag(body.getMethod().getDeclaringClass(), name)){
                    Util.addInnerClassTag(body.getMethod().getDeclaringClass(), name, Util.getSootType(outerType).toString(), objType.name(), outerType.flags().isInterface() ? soot.Modifier.PUBLIC | soot.Modifier.STATIC : Util.getModifier(objType.flags()));
                    /*body.getMethod().getDeclaringClass().addTag(
                        new soot.tagkit.InnerClassTag(
                        name,
                        Util.getSootType(outerType).toString(),
                        objType.name(),
                        outerType.flags().isInterface() ? soot.Modifier.PUBLIC | soot.Modifier.STATIC : Util.getModifier(objType.flags()) ));*/
            }
                
            }
        }
        soot.RefType sootType = (soot.RefType)Util.getSootType(objType);
        soot.Local retLocal = lg.generateLocal(sootType);
        soot.jimple.NewExpr sootNew = soot.jimple.Jimple.v().newNewExpr(sootType);

        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, sootNew);
        body.getUnits().add(stmt);
        Util.addLnPosTags(stmt, newExpr.position());
        
        
        soot.SootClass classToInvoke = sootType.getSootClass();

         if (newExpr.qualifier() != null){
            if ((objType.outer() != null) && (body.getMethod().getDeclaringClass().equals(((soot.RefType)Util.getSootType(objType.outer())).getSootClass())) && (!soot.Modifier.isStatic(body.getMethod().getModifiers()))){
                handleOuterClassParams(sootParams, sootParamsTypes, objType);
            }
            else {
                soot.Value qVal = createExpr(newExpr.qualifier());
                sootParams.add(qVal);
                sootParamsTypes.add(qVal.getType());
                body.getUnits().add(soot.jimple.Jimple.v().newInvokeStmt(soot.jimple.Jimple.v().newVirtualInvokeExpr((soot.Local)qVal, soot.Scene.v().getSootClass("java.lang.Object").getMethodByName("getClass"), new ArrayList())));
            }
         }
         else {
             
             handleOuterClassParams(sootParams, sootParamsTypes, objType);
         }

         
        sootParams.addAll(getSootParams(newExpr));
        sootParamsTypes.addAll(getSootParamsTypes(newExpr));

        handleFinalLocalParams(sootParams, sootParamsTypes, (polyglot.types.ClassType)objType);
     
        soot.SootMethod methodToInvoke = getMethodFromClass(classToInvoke, "<init>", sootParamsTypes, soot.VoidType.v());
      
        if (!methodToInvoke.getDeclaringClass().getType().equals(classToInvoke.getType())){
            throw new RuntimeException("created new for type: "+classToInvoke.getType()+" but didn't find needed initializer there instead found initializer in "+methodToInvoke.getDeclaringClass().getType());
        }
        soot.jimple.SpecialInvokeExpr specialInvokeExpr = soot.jimple.Jimple.v().newSpecialInvokeExpr(retLocal, methodToInvoke, sootParams);
                
        soot.jimple.Stmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(specialInvokeExpr);

        body.getUnits().add(invokeStmt);
        Util.addLnPosTags(invokeStmt, newExpr.position());
        
        int numParams = 0;
        Iterator invokeParamsIt = newExpr.arguments().iterator();
        while (invokeParamsIt.hasNext()) {
            Util.addLnPosTags(specialInvokeExpr.getArgBox(numParams), ((polyglot.ast.Expr)invokeParamsIt.next()).position());
            numParams++;
        }
        
        
        return retLocal;
    }

    
    /**
     * Call Expression Creation
     */
    private soot.Local getCallLocal(polyglot.ast.Call call){
        
        // handle name
		String name = call.name();
        // handle receiver/target
		polyglot.ast.Receiver receiver = call.target();
        soot.Local baseLocal;
        if ((receiver instanceof polyglot.ast.Special) && (((polyglot.ast.Special)receiver).kind() == polyglot.ast.Special.SUPER) && (((polyglot.ast.Special)receiver).qualifier() != null)){
            baseLocal = getSpecialSuperQualifierLocal(call);
            return baseLocal;

        }
        baseLocal = (soot.Local)getBaseLocal(receiver);
        
        soot.Type sootRecType = Util.getSootType(receiver.type());
        soot.SootClass receiverTypeClass = soot.Scene.v().getSootClass("java.lang.Object");
        if (sootRecType instanceof soot.RefType){
             receiverTypeClass = ((soot.RefType)sootRecType).getSootClass();
        }
        
		polyglot.types.MethodInstance methodInstance = call.methodInstance();
        soot.Type sootRetType = Util.getSootType(methodInstance.returnType());
        ArrayList sootParamsTypes = getSootParamsTypes(call);
        ArrayList sootParams = getSootParams(call);
     
        soot.SootMethod callMethod = getMethodFromClass(receiverTypeClass, methodInstance.name(), sootParamsTypes, sootRetType);

        boolean isPrivateAccess = false;
        soot.javaToJimple.PolyglotMethodSource ms = (soot.javaToJimple.PolyglotMethodSource)body.getMethod().getSource();
        if ((ms.getPrivateAccessMap() != null) && (ms.getPrivateAccessMap().containsKey(call.methodInstance()))){
            callMethod = (soot.SootMethod)ms.getPrivateAccessMap().get(call.methodInstance());
            if (!call.methodInstance().flags().isStatic()){
                sootParams.add(baseLocal);
            }
            isPrivateAccess = true;
        }

        soot.jimple.InvokeExpr invokeExpr; 
        if (isPrivateAccess){
            // for accessing private methods in outer class -> always static
            invokeExpr = soot.jimple.Jimple.v().newStaticInvokeExpr(callMethod, sootParams);
        }
        else if (soot.Modifier.isInterface(receiverTypeClass.getModifiers()) && methodInstance.flags().isAbstract()) {
            // if reciever class is interface and method is abstract -> interface
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
            // receiver is special super -> special
            invokeExpr = soot.jimple.Jimple.v().newSpecialInvokeExpr(baseLocal, callMethod, sootParams);
        }   
        else {
            // else virtual invoke
            invokeExpr = soot.jimple.Jimple.v().newVirtualInvokeExpr(baseLocal, callMethod, sootParams);

        }

        int numParams = 0;
        Iterator callParamsIt = call.arguments().iterator();
        while (callParamsIt.hasNext()) {
            Util.addLnPosTags(invokeExpr.getArgBox(numParams), ((polyglot.ast.Expr)callParamsIt.next()).position());
            numParams++;
        }

        if (invokeExpr instanceof soot.jimple.InstanceInvokeExpr) {
            Util.addLnPosTags(((soot.jimple.InstanceInvokeExpr)invokeExpr).getBaseBox(), call.target().position());
        }
        
        // create an assign stmt so invoke can be used somewhere else

        if (invokeExpr.getMethod().getReturnType().equals(soot.VoidType.v())) {
            soot.jimple.Stmt invoke = soot.jimple.Jimple.v().newInvokeStmt(invokeExpr);
            body.getUnits().add(invoke);
            Util.addLnPosTags(invoke, call.position());
            return null;
        }
        else {
            soot.Local retLocal = lg.generateLocal(invokeExpr.getMethod().getReturnType());
        
            soot.jimple.Stmt assignStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, invokeExpr);
        
            // add assign stmt to body
            body.getUnits().add(assignStmt);
            
		    Util.addLnPosTags(assignStmt, call.position());
            return retLocal;
        }
	}
   

    private soot.Value getBaseLocal(polyglot.ast.Receiver receiver) {
      
        if (receiver instanceof polyglot.ast.TypeNode) {
            return generateLocal(((polyglot.ast.TypeNode)receiver).type());
        }
        else {
            soot.Value val = createExpr((polyglot.ast.Expr)receiver);
            if (val instanceof soot.jimple.Constant) {
                soot.Local retLocal = lg.generateLocal(val.getType());
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

        soot.Type sootType = Util.getSootType(newArrExpr.type());

        soot.jimple.Expr expr;
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
                Util.addLnPosTags(newArrayExpr.getSizeBox(), ((polyglot.ast.Expr)newArrExpr.dims().get(0)).position());
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
            soot.jimple.NewMultiArrayExpr newMultiArrayExpr = soot.jimple.Jimple.v().newNewMultiArrayExpr((soot.ArrayType)sootType, valuesList);

            
            expr = newMultiArrayExpr;
            Iterator sizeBoxIt = newArrExpr.dims().iterator();
            int counter = 0;
            while (sizeBoxIt.hasNext()){
                Util.addLnPosTags(newMultiArrayExpr.getSizeBox(counter), ((polyglot.ast.Expr)sizeBoxIt.next()).position());
                counter++;
            }
        }

        soot.Local retLocal = lg.generateLocal(sootType);
        
        soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, expr);
        
        body.getUnits().add(stmt);
            
        Util.addLnPosTags(stmt, newArrExpr.position());
        Util.addLnPosTags(stmt.getRightOpBox(), newArrExpr.position());
        
        // handle array init if one exists
        if (newArrExpr.init() != null) {
            soot.Value initVal = getArrayInitLocal(newArrExpr.init(), newArrExpr.type());
            soot.jimple.AssignStmt initStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, initVal);
        
            body.getUnits().add(initStmt);
            
        }
        
        return retLocal;
  
    }

    /**
     * create ArrayInit given init and the array local
     */
    private soot.Local getArrayInitLocal(polyglot.ast.ArrayInit arrInit, polyglot.types.Type lhsType) {
  
        
        soot.Local local = generateLocal(lhsType);

        
        soot.jimple.NewArrayExpr arrExpr = soot.jimple.Jimple.v().newNewArrayExpr(((soot.ArrayType)local.getType()).getElementType(), soot.jimple.IntConstant.v(arrInit.elements().size()));

        soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(local, arrExpr);
        
        body.getUnits().add(assign);
        Util.addLnPosTags(assign, arrInit.position());
       

        Iterator it = arrInit.elements().iterator();
        int index = 0;
        
        while (it.hasNext()){
        
            polyglot.ast.Expr elemExpr = (polyglot.ast.Expr)it.next();
            soot.Value elem;
            if (elemExpr instanceof polyglot.ast.ArrayInit){
               
                if (((polyglot.ast.ArrayInit)elemExpr).type() instanceof polyglot.types.NullType) {
                    if (lhsType instanceof polyglot.types.ArrayType){
                    elem = getArrayInitLocal((polyglot.ast.ArrayInit)elemExpr, ((polyglot.types.ArrayType)lhsType).base());
                    }
                    else {
                        elem = getArrayInitLocal((polyglot.ast.ArrayInit)elemExpr, lhsType);
                        
                    }
                }
                else {
                    elem = getArrayInitLocal((polyglot.ast.ArrayInit)elemExpr, ((polyglot.ast.ArrayInit)elemExpr).type());
                }
            }
            else {
                elem = createExpr(elemExpr);
            }
            soot.jimple.ArrayRef arrRef = soot.jimple.Jimple.v().newArrayRef(local, soot.jimple.IntConstant.v(index));
            
            soot.jimple.AssignStmt elemAssign = soot.jimple.Jimple.v().newAssignStmt(arrRef, elem);
            body.getUnits().add(elemAssign);
            Util.addLnPosTags(elemAssign, elemExpr.position());
            Util.addLnPosTags(elemAssign.getRightOpBox(), elemExpr.position()); 
            
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
        polyglot.ast.Expr array = arrayRefExpr.array();
        polyglot.ast.Expr access = arrayRefExpr.index();
        
        soot.Local arrLocal = (soot.Local)createExpr(array);
        soot.Value arrAccess = createExpr(access);

        soot.Local retLocal = generateLocal(arrayRefExpr.type());

        soot.jimple.ArrayRef ref = soot.jimple.Jimple.v().newArrayRef(arrLocal, arrAccess);
  
        Util.addLnPosTags(ref.getBaseBox(), arrayRefExpr.array().position());
        Util.addLnPosTags(ref.getIndexBox(), arrayRefExpr.index().position());
        return ref;
    }

    /**
     * Array Ref Expression Creation
     */
    private soot.Value getArrayRefLocal(polyglot.ast.ArrayAccess arrayRefExpr) {
    
        polyglot.ast.Expr array = arrayRefExpr.array();
        polyglot.ast.Expr access = arrayRefExpr.index();
        
        soot.Local arrLocal = (soot.Local)createExpr(array);
        soot.Value arrAccess = createExpr(access);

        soot.Local retLocal = generateLocal(arrayRefExpr.type());

        soot.jimple.ArrayRef ref = soot.jimple.Jimple.v().newArrayRef(arrLocal, arrAccess);
        
        Util.addLnPosTags(ref.getBaseBox(), arrayRefExpr.array().position());
        Util.addLnPosTags(ref.getIndexBox(), arrayRefExpr.index().position());

        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, ref);
        body.getUnits().add(stmt);
        Util.addLnPosTags(stmt, arrayRefExpr.position());
        

        return retLocal;
    }
    
  
    private soot.Local getSpecialSuperQualifierLocal(polyglot.ast.Expr expr){
        soot.SootClass classToInvoke;
        ArrayList methodParams = new ArrayList();
        if (expr instanceof polyglot.ast.Call){
            polyglot.ast.Special target = (polyglot.ast.Special)((polyglot.ast.Call)expr).target();
            classToInvoke = ((soot.RefType)Util.getSootType(target.qualifier().type())).getSootClass();
            methodParams = getSootParams((polyglot.ast.Call)expr);
        }
        else if (expr instanceof polyglot.ast.Field){
            polyglot.ast.Special target = (polyglot.ast.Special)((polyglot.ast.Field)expr).target();
            classToInvoke = ((soot.RefType)Util.getSootType(target.qualifier().type())).getSootClass();
        }
        else {
            throw new RuntimeException("Trying to create special super qualifier for: "+expr+" which is not a field or call");
        }
        // make an access method 
        soot.SootMethod methToInvoke = makeSuperAccessMethod(classToInvoke, expr); 
        // invoke it
        soot.Local classToInvokeLocal = Util.getThis(classToInvoke.getType(), body, getThisMap, lg);
        methodParams.add(0, classToInvokeLocal);
        
        soot.jimple.InvokeExpr invokeExpr = soot.jimple.Jimple.v().newStaticInvokeExpr(methToInvoke, methodParams);
        
        // return the local of return type if  not void
        if (!methToInvoke.getReturnType().equals(soot.VoidType.v())){
            soot.Local retLocal = lg.generateLocal(methToInvoke.getReturnType());
            soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, invokeExpr);
            body.getUnits().add(stmt);
        
            return retLocal;
        }
        else {
            body.getUnits().add(soot.jimple.Jimple.v().newInvokeStmt(invokeExpr));
            return null;
        }
    }
    
    /**
     * Special Expression Creation
     */
    private soot.Local getSpecialLocal(polyglot.ast.Special specialExpr) {
       
        if (specialExpr.kind() == polyglot.ast.Special.SUPER) {
            if (specialExpr.qualifier() == null){
                return specialThisLocal;
            }
            else {
                // this isn't enough
                // need to getThis for the type which may be several levels up
                // add access$N method to class of the type which returns
                // field or method wanted
                // invoke it
                // and it needs to be called specially when getting fields 
                // or calls because need to know field or method to access
                // as it access' a field or meth in the super class of the 
                // outer class refered to by the qualifier
                return getThis(Util.getSootType(specialExpr.qualifier().type()));
            }
        }
        else if (specialExpr.kind() == polyglot.ast.Special.THIS) {
            if (specialExpr.qualifier() == null) {
                return specialThisLocal;
            }
            else {
                return getThis(Util.getSootType(specialExpr.qualifier().type()));
            }
        }
        else {
            throw new RuntimeException("Unknown Special");
        }
    }
    

    private soot.SootMethod makeSuperAccessMethod(soot.SootClass classToInvoke, Object memberToAccess){
        String name = "access$"+soot.javaToJimple.InitialResolver.v().getNextPrivateAccessCounter()+"00";
        ArrayList paramTypes = new ArrayList();
        paramTypes.add(classToInvoke.getType());
        
        soot.SootMethod meth;
        soot.MethodSource src;
        if (memberToAccess instanceof polyglot.ast.Field){
            polyglot.ast.Field fieldToAccess = (polyglot.ast.Field)memberToAccess;
            meth = new soot.SootMethod(name, paramTypes, Util.getSootType(fieldToAccess.type()), soot.Modifier.STATIC);
            PrivateFieldAccMethodSource fSrc = new PrivateFieldAccMethodSource();
            fSrc.fieldName(fieldToAccess.name());
            fSrc.fieldType(Util.getSootType(fieldToAccess.type()));
            fSrc.classToInvoke(((soot.RefType)Util.getSootType(fieldToAccess.target().type())).getSootClass());
            src = fSrc;
        }
        else if (memberToAccess instanceof polyglot.ast.Call){
            polyglot.ast.Call methToAccess = (polyglot.ast.Call)memberToAccess;
            paramTypes.addAll(getSootParamsTypes(methToAccess));
            meth = new soot.SootMethod(name, paramTypes, Util.getSootType(methToAccess.methodInstance().returnType()), soot.Modifier.STATIC);
            PrivateMethodAccMethodSource mSrc = new PrivateMethodAccMethodSource();
            mSrc.setMethodInst(methToAccess.methodInstance());
            src = mSrc;
        }
        else {
            throw new RuntimeException("trying to access unhandled member type: "+memberToAccess);
        }
        classToInvoke.addMethod(meth);
        meth.setActiveBody(src.getBody(meth, null));
        return meth;
    }
    
    /**
     * InstanceOf Expression Creation
     */
    private soot.Local getInstanceOfLocal(polyglot.ast.Instanceof instExpr) {
        
        soot.Type sootType = Util.getSootType(instExpr.compareType().type());

        soot.Value local = createExpr(instExpr.expr());

        soot.jimple.InstanceOfExpr instOfExpr = soot.jimple.Jimple.v().newInstanceOfExpr(local, sootType);

        soot.Local lhs = lg.generateLocal(soot.BooleanType.v());

        soot.jimple.Stmt instAssign = soot.jimple.Jimple.v().newAssignStmt(lhs, instOfExpr);
        body.getUnits().add(instAssign);
        Util.addLnPosTags(instAssign, instExpr.position());

        Util.addLnPosTags(instOfExpr.getOpBox(), instExpr.expr().position());
        return lhs;
    }

    /**
     * Condition Expression Creation - can maybe merge with If
     */
    private soot.Local getConditionalLocal(polyglot.ast.Conditional condExpr){

        // handle cond 
        polyglot.ast.Expr condition = condExpr.cond();
        soot.Value sootCond = createExpr(condition); 
		boolean needIf = needSootIf(sootCond);
        if (!(sootCond instanceof soot.jimple.ConditionExpr)) {
            sootCond = soot.jimple.Jimple.v().newEqExpr(sootCond, soot.jimple.IntConstant.v(0));
        }
        else {
            sootCond = reverseCondition((soot.jimple.ConditionExpr)sootCond);
            sootCond = handleDFLCond((soot.jimple.ConditionExpr)sootCond);
        }
        
		soot.jimple.Stmt noop1 = soot.jimple.Jimple.v().newNopStmt();
		if (needIf){
            soot.jimple.IfStmt ifStmt = soot.jimple.Jimple.v().newIfStmt(sootCond, noop1);
        
		    body.getUnits().add(ifStmt);
	        Util.addLnPosTags(ifStmt, condExpr.position());
            Util.addLnPosTags(ifStmt.getConditionBox(), condition.position());
        }
       
        soot.Local retLocal = generateLocal(condExpr.alternative().type());
        
		// handle consequence
		polyglot.ast.Expr consequence = condExpr.consequent();
        
        soot.Value conseqVal = createExpr(consequence);
        soot.jimple.AssignStmt conseqAssignStmt = soot.jimple.Jimple.v().newAssignStmt(retLocal, conseqVal);
        body.getUnits().add(conseqAssignStmt);
        Util.addLnPosTags(conseqAssignStmt, condExpr.position());
        Util.addLnPosTags(conseqAssignStmt.getRightOpBox(), consequence.position());
		
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
            Util.addLnPosTags(altAssignStmt, condExpr.position());
            Util.addLnPosTags(altAssignStmt, alternative.position());
            Util.addLnPosTags(altAssignStmt.getRightOpBox(), alternative.position());
        }
        body.getUnits().add(noop2);

        
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
        return lg.generateLocal(type);
    }

}
