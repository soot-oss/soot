package soot.jimple.parser;

import soot.baf.*;
import soot.*;
import soot.jimple.*;
import soot.util.*;

import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;

import java.io.*;
import java.util.*;



public class Walker extends DepthFirstAdapter 
{
    boolean debug = false;
    Stack mProductions = new Stack();
    SootClass mSootClass = null;
    Map mLocals = null;
    Value mValue = IntConstant.v(1);

    Map mLabelToStmtMap;  // maps a label to the stmt following it in the jimple source
    Map mLabelToPatchList; // maps a label to the a list of stmts that refer to the label (ie goto lableX)


    public Walker() 
    {	
	if(debug) {
	    mProductions = new Stack() {
		public Object pop(){
		    
		    Object o = super.pop();
		    if(debug) 
			System.out.println("popped: " + o );		
		    return o;
		}
	    };
	}	
    }


    public Walker(SootClass sc) 
    {
	mSootClass = sc;		
    }

    
    public void outStart(Start node)
    {
	SootClass c = (SootClass)  mProductions.pop();	
	//	c.printTo(new PrintWriter(System.out, true));	
	//mSootClass = c;
    }


    public SootClass getSootClass()
    {
	if(mSootClass == null)
	    throw new RuntimeException("did not parse class yet....");
	
	return mSootClass;	
    }


    /*
      file = 
      modifier* file_type class_name extends_clause? implements_clause? file_body; 
    */
    public void inAFile(AFile node)
    {
	if(debug)
	    System.out.println("reading class " + node.getClassName());
    } 


    public void caseAFile(AFile node)
    {
        inAFile(node);
        {
            Object temp[] = node.getModifier().toArray();
            for(int i = 0; i < temp.length; i++)
            {
                ((PModifier) temp[i]).apply(this);
            }
        }
        if(node.getFileType() != null)
        {
            node.getFileType().apply(this);
        }
        if(node.getClassName() != null)
        {
            node.getClassName().apply(this);
        }
	
	String className = (String) mProductions.pop();
	mSootClass = Scene.v().getSootClass(className);




        if(node.getExtendsClause() != null)
        {
            node.getExtendsClause().apply(this);
        }
        if(node.getImplementsClause() != null)
        {
            node.getImplementsClause().apply(this);
        }
        if(node.getFileBody() != null)
        {
            node.getFileBody().apply(this);
        }
        outAFile(node);
    }


    public void outAFile(AFile node)
    {
	// not not pop members; they have been taken care of.
	List implementsList = null;
	String superClass = null;

	String classType = null;
	
	if(node.getImplementsClause() != null) {	   
	    implementsList = (List) mProductions.pop();
	}
	if(node.getExtendsClause() != null) {
	    superClass = (String) mProductions.pop();
	}

	classType = (String) mProductions.pop();
	

	
	int  modifierCount = node.getModifier().size();

	    

	int modifierFlags = processModifiers(node.getModifier());


	if(classType.equals("interface"))
	    modifierFlags |= Modifier.INTERFACE;	

	mSootClass.setModifiers(modifierFlags);
		
	if(superClass != null) {
	    mSootClass.setSuperclass(Scene.v().getSootClass(superClass));
	}
	
	if(implementsList != null) {
	    Iterator implIt = implementsList.iterator();
	    while(implIt.hasNext()) {
		SootClass interfaceClass = Scene.v().getSootClass((String) implIt.next());
		mSootClass.addInterface(interfaceClass);
	    }
	}
	
	mProductions.push(mSootClass);
    } 
    
    /*
      member =
      {field}  modifier* type name semicolon |
      {method} modifier* type name l_paren parameter_list? r_paren throws_clause? method_body;
    */    
    public void outAFieldMember(AFieldMember node)
    {
	int modifier = 0;
	Type type = null;
	String name = null;

	name = (String) mProductions.pop();
	type = (Type) mProductions.pop();

	modifier = processModifiers(node.getModifier());

	SootField f = new SootField(name, type, modifier);	
	mSootClass.addField(f);
    }

    public void outAMethodMember(AMethodMember node)
    {
	int modifier = 0;
	Type type;
	String name;
	List parameterList = null;
	List throwsClause = null;
	JimpleBody methodBody = null;

	if(node.getMethodBody() instanceof AFullMethodBody)
	    methodBody = (JimpleBody) mProductions.pop();
	
	if(node.getThrowsClause() != null)
	    throwsClause = (List) mProductions.pop();
	
	if(node.getParameterList() != null) {
	    parameterList = (List) mProductions.pop();
	}
	else {
	    parameterList = new ArrayList();
	} 

	Object o = mProductions.pop();


	name = (String) o;
	type = (Type) mProductions.pop();

	modifier = processModifiers(node.getModifier());

	SootMethod method;

	if(throwsClause != null)
	  method =  new SootMethod(name, parameterList, type, modifier, throwsClause);
	else 	    
	    method =  new SootMethod(name, parameterList, type, modifier);

	mSootClass.addMethod(method);
	
	if(method.isConcrete()) {
	    methodBody.setMethod(method);
	    method.setActiveBody(methodBody);
	    
	} else if(node.getMethodBody() instanceof AFullMethodBody)
	    throw new RuntimeException("Impossible: !concrete => ! instanceof");

    }
  

    /*
      type =
      {void}   void |
      {novoid} nonvoid_type;
    */

    public void outAVoidType(AVoidType node)
    {
	mProductions.push(VoidType.v());
    }
    

    /*
      nonvoid_type =
      {base}   base_type_no_name array_brackets*;
      {quoted} quoted_name array_brackets* |
      {ident}  identifier array_brackets* |
      {full_ident} full_identifier array_brackets*;   
    */
    public void outABaseNonvoidType(ABaseNonvoidType node)
    {
	Type t = (Type) mProductions.pop();
	int dim = node.getArrayBrackets().size();
	if(dim > 0) 
	    t = ArrayType.v((BaseType) t, dim);
	mProductions.push(t);
    }

    
    public void outAQuotedNonvoidType(AQuotedNonvoidType node)
    {	
	String typeName = (String) mProductions.pop();
	if(typeName.equals("int")) throw new RuntimeException();
	Type t = RefType.v(typeName);
	int dim = node.getArrayBrackets().size();
	if(dim > 0) 
	    t = ArrayType.v((BaseType) t, dim);
	mProductions.push(t);
    }

    public void outAIdentNonvoidType(AIdentNonvoidType node)
    {	
	String typeName = (String) mProductions.pop();
	if(typeName.equals("int")) throw new RuntimeException();
	Type t = RefType.v(typeName);
	int dim = node.getArrayBrackets().size();
	if(dim > 0)
	    t = ArrayType.v((BaseType) t, dim);
	    mProductions.push(t);
    }


    public void outAFullIdentNonvoidType(AFullIdentNonvoidType node)
    {		
	String typeName = (String) mProductions.pop();
	if(typeName.equals("int")) throw new RuntimeException();
	Type t = RefType.v(typeName);
	Scene.v().addClassToResolve(typeName);
	
	int dim = node.getArrayBrackets().size();
	if(dim > 0)
	    t = ArrayType.v((BaseType)t, dim);		
	mProductions.push(t);
    }
    


    /*      
	    base_type_no_name =
	    {boolean} boolean |
	    {byte}    byte |
	    {char}    char |
	    {short}   short |
	    {int}     int |
	    {long}    long |
	    {float}   float |
	    {double}  double |
	    {null}    null_type;
    */

    public void outABooleanBaseTypeNoName(ABooleanBaseTypeNoName node)
    {
	mProductions.push(BooleanType.v());
    }
    
    public void outAByteBaseTypeNoName(AByteBaseTypeNoName node)
    {
	mProductions.push(ByteType.v());
    }
        

    public void outACharBaseTypeNoName(ACharBaseTypeNoName node)
    {
	mProductions.push(CharType.v());
    }
    
    public void outAShortBaseTypeNoName(AShortBaseTypeNoName node)
    {
	mProductions.push(ShortType.v());
    }


    
    public void outAIntBaseTypeNoName(AIntBaseTypeNoName node)
    {
	mProductions.push(IntType.v());
    }
    
    public void outALongBaseTypeNoName(ALongBaseTypeNoName node)
    {
	mProductions.push(LongType.v());
    }
        

    public void outAFloatBaseTypeNoName(AFloatBaseTypeNoName node)
    {
	mProductions.push(FloatType.v());
    }
    
    public void outADoubleBaseTypeNoName(ADoubleBaseTypeNoName node)
    {
	mProductions.push(DoubleType.v());
    }
    public void outANullBaseTypeNoName(ANullBaseTypeNoName node)
    {
	mProductions.push(NullType.v());
    }
    

    /*
      base_type =
      {boolean} boolean |
      {byte}    byte |
      {char}    char |
      {short}   short |
      {int}     int |
      {long}    long |
      {float}   float |
      {double}  double |
      {null}    null_type | 
      {class_name}    class_name;
    */
    
    
    public void outABooleanBaseType(ABooleanBaseType node)
    {
	mProductions.push(BooleanType.v());
    }
    
    public void outAByteBaseType(AByteBaseType node)
    {
	mProductions.push(ByteType.v());
    }
        

    public void outACharBaseType(ACharBaseType node)
    {
	mProductions.push(CharType.v());
    }
    
    public void outAShortBaseType(AShortBaseType node)
    {
	mProductions.push(ShortType.v());
    }


    
    public void outAIntBaseType(AIntBaseType node)
    {
	mProductions.push(IntType.v());
    }
    
    public void outALongBaseType(ALongBaseType node)
    {
	mProductions.push(LongType.v());
    }
        

    public void outAFloatBaseType(AFloatBaseType node)
    {
	mProductions.push(FloatType.v());
    }
    
    public void outADoubleBaseType(ADoubleBaseType node)
    {
	mProductions.push(DoubleType.v());
    }
    
    public void outANullBaseType(ANullBaseType node)
    {
	mProductions.push(NullType.v());
    }



    public void outAClassNameBaseType(AClassNameBaseType node)
    {
	String type = (String) mProductions.pop();
	if(type.equals("int"))throw new RuntimeException();
	mProductions.push(RefType.v(type));
	Scene.v().addClassToResolve(type);
    }


    /*
      method_body =
      {empty} semicolon |
      {full}  l_brace declaration* statement* catch_clause* r_brace;
    */
  

    public void inAFullMethodBody(AFullMethodBody node)
    {
	mLocals = new HashMap();
	mLabelToStmtMap = new HashMap();
	mLabelToPatchList = new HashMap();
    }

    public void outAFullMethodBody(AFullMethodBody node)
    {
	Object catchClause = null;
	JimpleBody jBody = Jimple.v().newBody();
		
	if(node.getCatchClause() != null) {
	    int size = node.getCatchClause().size();
	    for(int i =0; i < size; i++) 	    
		jBody.getTraps().addFirst((Trap) mProductions.pop());
	}
				
	if(node.getStatement() != null) {
	    int size = node.getStatement().size();
	    Unit lastStmt = null;
	    for(int i = 0; i < size; i++) {
		Object o =  mProductions.pop();
		if(o  instanceof Unit) {
		    jBody.getUnits().addFirst(o);
		    lastStmt = (Unit)  o;
		}
		else if(o instanceof String) {
		    if(lastStmt ==  null) 
			throw new RuntimeException("impossible");
		    mLabelToStmtMap.put(o, lastStmt);
		}
		else
		    throw new RuntimeException("impossible");		
	    }
	}
	
	if(node.getDeclaration() != null) {
	    int size = node.getDeclaration().size();
	    for(int i = 0; i < size; i++) {
		List localList = (List) mProductions.pop();
		
		int listSize = localList.size();
		for(int j = listSize-1; j>=0; j--)
		    jBody.getLocals().addFirst(localList.get(j));	       		
	    }
	}
	
      
	
	Iterator it =  mLabelToPatchList.keySet().iterator();
	while(it.hasNext()) {
	    String label = (String) it.next();
	    Unit target = (Unit) mLabelToStmtMap.get(label); 
	
	    Iterator patchIt = ((List) mLabelToPatchList.get(label)).iterator();
	    while(patchIt.hasNext()) {
		UnitBox box = (UnitBox) patchIt.next();
		box.setUnit(target);
	    }	    
	}
    
	
	/*
	Iterator it = mLabelToStmtMap.keySet().iterator();
	while(it.hasNext()) {
	    String label = (String) it.next();
	    Unit target = (Unit) mLabelToStmtMap.get(label); 
	    
	    List l = 	(List) mLabelToPatchList.get(label);	
	    if(l != null) {
		Iterator patchIt = l.iterator();
		while(patchIt.hasNext()) {
		    UnitBox box = (UnitBox) patchIt.next();
		    box.setUnit(target);
		}	    
	    }
	}
	*/
	
	mProductions.push(jBody);
    }



    public void outANovoidType(ANovoidType node)
    {
    }


    
    /*
      parameter_list =
      {single} parameter |
      {multi}  parameter comma parameter_list;
    */
    
    public void outASingleParameterList(ASingleParameterList node)
    {
	List l = new ArrayList();
	l.add((Type) mProductions.pop());
	mProductions.push(l);
    }

    public void outAMultiParameterList(AMultiParameterList node)
    {
	List l = (List) mProductions.pop();
	l.add(0,(Type) mProductions.pop());
	mProductions.push(l);
    }

    /*
      arg_list =
      {single} immediate |
      {multi}  immediate comma arg_list;
    */
    public void outASingleArgList(ASingleArgList node)
    {
	List l = new ArrayList();

	l.add((Value) mProductions.pop()); 
	mProductions.push(l);
    }

    public void outAMultiArgList(AMultiArgList node)
    {
	List l = (List) mProductions.pop();
	l.add(0,(Value) mProductions.pop());
	mProductions.push(l);
    }

        



    /*  
	class_name_list =
	{class_name_single} class_name | 
	{class_name_multi}  class_name comma class_name_list;
    */

    public void outAClassNameSingleClassNameList(AClassNameSingleClassNameList node)
    {
	List l = new ArrayList();
	l.add((String) mProductions.pop());
	mProductions.push(l);
    }

    public void outAClassNameMultiClassNameList(AClassNameMultiClassNameList node)
    {
	List l = (List) mProductions.pop();
	l.add(0,(String) mProductions.pop());
	mProductions.push(l);	
    }
   
    /*
      file_type =
      {class}     [theclass]:class |
      {interface} interface;
    */
    
    public void outAClassFileType(AClassFileType node)
    {
	mProductions.push("class");
    }
    
    public void outAInterfaceFileType(AInterfaceFileType node)
    {
	mProductions.push("interface");
    }


    /*
      catch_clause =
      catch [name]:class_name from [from_label]:label_name to [to_label]:label_name with [with_label]:label_name semicolon;
    */
    

    // public void caseACatchClause(ACatchClause node){}

    public void outACatchClause(ACatchClause node)
    {
        String exceptionName;
	UnitBox withUnit, fromUnit, toUnit;
	
	withUnit = Jimple.v().newStmtBox(null);
	addBoxToPatch((String) mProductions.pop(), withUnit);

	toUnit = Jimple.v().newStmtBox(null);
	addBoxToPatch((String) mProductions.pop(), toUnit);

	fromUnit = Jimple.v().newStmtBox(null);
	addBoxToPatch((String) mProductions.pop(), fromUnit);

	exceptionName = (String) mProductions.pop();
				
	Trap trap = Jimple.v().newTrap(Scene.v().getSootClass(exceptionName), fromUnit, toUnit, withUnit);	  
	mProductions.push(trap);
    }

    /*
      declaration =
      jimple_type local_name_list semicolon;
    */
        

    public void outADeclaration(ADeclaration node)
    {
	List localNameList = (List) mProductions.pop();
	Type type = (Type) mProductions.pop();
	Iterator it = localNameList.iterator();
	List localList = new ArrayList();
	
	while(it.hasNext()) {	    
	    Local l = Jimple.v().newLocal((String) it.next(),type);	    	    
	    mLocals.put(l.getName(), l);	
	    localList.add(l);
	}			
	mProductions.push(localList);
    }





    /*
      jimple_type =
      {unknown} unknown |
      {nonvoid} nonvoid_type;
    */
    public void outAUnknownJimpleType(AUnknownJimpleType node)
    {
        mProductions.push(UnknownType.v());
    }


    /*
      local_name_list =
      {single} local_name |
      {multi}  local_name comma local_name_list;
    */
    
    public void outASingleLocalNameList(ASingleLocalNameList node)
    {
	List l = new ArrayList();
	l.add((String) mProductions.pop());
	mProductions.push(l);
    }

    public void outAMultiLocalNameList(AMultiLocalNameList node)
    {
	List l = (List) mProductions.pop();
	l.add(0,(String) mProductions.pop());
	mProductions.push(l);
    }


    /*
      statement =
      {label}        label_name colon |
      {breakpoint}   breakpoint semicolon |
      {entermonitor} entermonitor immediate semicolon |
      {exitmonitor}  exitmonitor immediate semicolon |      
      {switch}       switch l_paren immediate r_paren l_brace case_stmt+ r_brace semicolon |
      {identity}     local_name colon_equals at_identifier type semicolon |
      {identity_no_type}  local_name colon_equals at_identifier semicolon |
      {assign}       variable equals expression semicolon |
      {if}           if bool_expr goto_stmt |
      {goto}         goto_stmt |
      {nop}          nop semicolon |
      {ret}          ret immediate? semicolon |
      {return}       return immediate? semicolon |
      {throw}        throw immediate semicolon |
      {invoke}       invoke_expr semicolon;      
    */

    public void outALabelStatement(ALabelStatement node)
    {	
    }
    
    public void outABreakpointStatement(ABreakpointStatement node)
    {	
	Unit u = Jimple.v().newBreakpointStmt();
	mProductions.push(u);
    }


    public void outAEntermonitorStatement(AEntermonitorStatement node)
    {
	Value op = (Value) mProductions.pop();

	Unit u = Jimple.v().newEnterMonitorStmt(op);
	mProductions.push(u);
    }

    public void outAExitmonitorStatement(AExitmonitorStatement node)
    {
	Value op = (Value) mProductions.pop();

	Unit u = Jimple.v().newExitMonitorStmt(op);
	mProductions.push(u);
    }


    /*
      case_label =
      {constant} case minus? integer_constant |
      {default}  default;
    */
    /*
      case_stmt =
      case_label colon goto_stmt;
    */    
    public void outACaseStmt(ACaseStmt node)
    {	
	String labelName = (String) mProductions.pop();
	UnitBox box = Jimple.v().newStmtBox(null);

	addBoxToPatch(labelName, box);
	
	Value labelValue = null;
	if(node.getCaseLabel() instanceof AConstantCaseLabel)
	    labelValue = (Value) mProductions.pop();	
	
	// if labelValue == null, this is the default label.
	if(labelValue == null)
	    mProductions.push(box);
	else {	    
	    Object[] valueTargetPair = new Object[2];
	    valueTargetPair[0] = labelValue;
	    valueTargetPair[1] = box;
	    mProductions.push(valueTargetPair);
	}	
    }



    public void outATableswitchStatement(ATableswitchStatement node)
    {	
	List targets = new ArrayList();
	UnitBox defaultTarget = null;
	
	int lowIndex = 0, highIndex = 0;
		
	if(node.getCaseStmt() != null) {
	    int size = node.getCaseStmt().size();
	    
	    for(int i = 0; i < size; i++) {
		Object valueTargetPair = mProductions.pop();
		if(valueTargetPair instanceof UnitBox) {
		    if(defaultTarget != null)
			throw new RuntimeException("error: can't ;have more than 1 default stmt");
		    
		    defaultTarget = (UnitBox) valueTargetPair;
		} else {
		    Object[] pair = (Object[]) valueTargetPair;
		    
		    if((i == 0 && defaultTarget == null) || (i==1 && defaultTarget != null)) 
			highIndex = ((IntConstant) pair[0]).value;
		    if(i == (size -1))
			lowIndex = ((IntConstant) pair[0]).value;

		    targets.add(0, pair[1]);		
		}
	    }
	} else {
	    throw new RuntimeException("error: switch stmt has no case stmts");	    
	}
	

	Value key = (Value) mProductions.pop(); 	
	Unit switchStmt  = Jimple.v().newTableSwitchStmt(key, lowIndex, highIndex, targets, defaultTarget);
	
	
	mProductions.push(switchStmt);	
    }
    

    public void outALookupswitchStatement(ALookupswitchStatement node)
    {	
	List lookupValues = new ArrayList();
	List targets = new ArrayList();
	UnitBox defaultTarget = null;
	

	if(node.getCaseStmt() != null) {
	    int size = node.getCaseStmt().size();
	    
	    for(int i = 0; i < size; i++) {
		Object valueTargetPair = mProductions.pop();
		if(valueTargetPair instanceof UnitBox) {
		    if(defaultTarget != null)
			throw new RuntimeException("error: can't ;have more than 1 default stmt");
		    
		    defaultTarget = (UnitBox) valueTargetPair;
		} else {
		    Object[] pair = (Object[]) valueTargetPair;
		    
		    lookupValues.add(0, pair[0]);
		    targets.add(0, pair[1]);		
		}
	    }
	} else {
	    throw new RuntimeException("error: switch stmt has no case stmts");	    
	}
	

	Value key = (Value) mProductions.pop(); 	
	Unit switchStmt  = Jimple.v().newLookupSwitchStmt(key, lookupValues, targets, defaultTarget);
	
	mProductions.push(switchStmt);	
    }
    



    public void outAIdentityStatement(AIdentityStatement node)
    {
	Type identityRefType = (Type) mProductions.pop();
	String atClause = (String) mProductions.pop();
	Value local = (Value)  mLocals.get(mProductions.pop()); // the local ref from it's identifier
	
	Value ref = null;
	if(atClause.startsWith("@this")) {
	    ref = Jimple.v().newThisRef((RefType) identityRefType);	    
	} else if(atClause.startsWith("@parameter")) {
	    int index = Integer.parseInt(atClause.substring(10, atClause.length() - 1));
	    
	    ref = Jimple.v().newParameterRef(identityRefType, index);
	} else 
	    throw new RuntimeException("shouldn't @caughtexception be handled by outAIdentityNoTypeStatement: got" + atClause);
	
	Unit u = Jimple.v().newIdentityStmt(local, ref);
	mProductions.push(u);
    }
    
    
    public void outAIdentityNoTypeStatement(AIdentityNoTypeStatement node)
    {
	mProductions.pop();  // get rid of @caughtexception string presently on top of the stack
	Value local = (Value)  mLocals.get(mProductions.pop()); // the local ref from it's identifier
    

	Unit u = Jimple.v().newIdentityStmt(local, Jimple.v().newCaughtExceptionRef());
	mProductions.push(u);
    }

    
    public void outAAssignStatement(AAssignStatement node)
    {
	Value rvalue = (Value) mProductions.pop();
	Value variable =(Value)mProductions.pop();


	Unit u = Jimple.v().newAssignStmt(variable, rvalue);
	mProductions.push(u);
    }

    public void outAIfStatement(AIfStatement node)
    { 
	String targetLabel = (String) mProductions.pop();	    
	Value condition =(Value) mProductions.pop();
	
	UnitBox box = Jimple.v().newStmtBox(null);
	Unit u = Jimple.v().newIfStmt(condition, box); 
	
	addBoxToPatch(targetLabel, box);
       
	mProductions.push(u);
    }

    public void outAReturnStatement(AReturnStatement node) 
    {
	Value v;
	Stmt s = null;
	if(node.getImmediate() != null) {
	    v = (Value) mProductions.pop();
	    s = Jimple.v().newReturnStmt(v);
	} else {
	    s = Jimple.v().newReturnVoidStmt();
	}

	
	mProductions.push(s);	
    }
    
    public void outAGotoStatement(AGotoStatement node)
    {	    
	String targetLabel = (String) mProductions.pop();

	UnitBox box = Jimple.v().newStmtBox(null);
	Unit branch = Jimple.v().newGotoStmt(box);
	    
	addBoxToPatch(targetLabel, box);
	
	mProductions.push(branch);
    }
	
    
    public void outANopStatement(ANopStatement node)
    {
	Unit u = Jimple.v().newNopStmt(); 
	mProductions.push(u);
    }

    public void outARetStatement(ARetStatement node)
    {
	throw new RuntimeException("ret not yet implemented.");
    }
    
    
    public void outAThrowStatement(AThrowStatement node)
    {
	Value op = (Value) mProductions.pop();

	Unit u = Jimple.v().newThrowStmt(op);
	mProductions.push(u);
    }

    public void outAInvokeStatement(AInvokeStatement node)
    {
	Value op = (Value) mProductions.pop();

	Unit u = Jimple.v().newInvokeStmt(op);
	
	mProductions.push(u);
    }



    /*
      case_label =
      {constant} case minus? integer_constant |
      {default}  default;
    */
    public void outAConstantCaseLabel(AConstantCaseLabel node)
    {

	String s = (String) mProductions.pop();
	int sign = 1;
	if(node.getMinus() != null)
	    sign = -1;
	
	if(s.endsWith("L")) {	    
	    
	    mProductions.push(LongConstant.v(sign * Long.parseLong(s.substring(0, s.length()-1))));
	} else
	    mProductions.push(IntConstant.v(sign * Integer.parseInt(s)));
    }
    

    






    /*
      immediate =
      {local}    local_name |
      {constant} constant;
    */
    
    public void outALocalImmediate(ALocalImmediate node)
    {
	String local = (String) mProductions.pop();
	
	Local l = (Local) mLocals.get(local);
	if(l == null) throw new RuntimeException("did not find local: " + local);
	mProductions.push(l);
    }
    
    
    
    /*
      constant =
      {integer} minus? integer_constant |
      {float}   minus? float_constant |
      {string}  string_constant |
      {null}    null;
    */

    
    public void outANullConstant(ANullConstant node)
    {
	mProductions.push(NullConstant.v());
    }


    public void outAIntegerConstant(AIntegerConstant node)
    {
	String s = (String) mProductions.pop();
	
	StringBuffer buf = new StringBuffer();
	if(node.getMinus() != null)
	    buf.append('-');
	buf.append(s);
	
	s = buf.toString();
	if(s.endsWith("L")) {	    	    
	    mProductions.push(LongConstant.v(Long.parseLong(s.substring(0, s.length()-1))));
	} else
	    mProductions.push(IntConstant.v(Integer.parseInt(s)));
    }
    
    public void outAStringConstant(AStringConstant node)
    {
	String s = (String) mProductions.pop();
	mProductions.push(StringConstant.v(s));
	/*
	  try {
	  String t = StringTools.getUnEscapedStringOf(s);
	
	  mProductions.push(StringConstant.v(t));
	  } catch(RuntimeException e) {
	  System.out.println(s);
	  throw e;
	  }
	*/
    }



  /* ('#' (('-'? 'Infinity') | 'NaN') ('f' | 'F')? ) ; */
    public void outAFloatConstant(AFloatConstant node)
    {
	String s = (String) mProductions.pop();

	boolean isDouble = true;
	float value = 0;
	double dvalue = 0;

	if(s.endsWith("f") || s.endsWith("F")) 
	  isDouble = false;
	  
	if(s.charAt(0) == '#') {
	  if(s.charAt(1) == '-') {
	    if(isDouble)
	      dvalue = Double.NEGATIVE_INFINITY;
	    else
	      value = Float.NEGATIVE_INFINITY;
	  }
	  else if(s.charAt(1) == 'I') {
	    if(isDouble)
              dvalue = Double.POSITIVE_INFINITY;
            else
              value = Float.POSITIVE_INFINITY;
	  }
	  else {
	    if(isDouble)
              dvalue = Double.NaN;
            else
              value = Float.NaN;
	  }
	}
	else {
	  StringBuffer buf = new StringBuffer();	  
	  if(node.getMinus() != null)
	    buf.append('-');
	  buf.append(s);
	  s = buf.toString();
	
	  if(isDouble)
	    dvalue = Double.parseDouble(s);
	  else
	    value =Float.parseFloat(s);	
	}

	Object res;
	if(isDouble)
	  res = DoubleConstant.v(dvalue);
	else
	  res = FloatConstant.v(value);

	mProductions.push(res);
    }


    /*
      binop_expr =
      [left]:immediate binop [right]:immediate;
    */
    

    public void outABinopExpr(ABinopExpr node)
    {
	Value right = (Value) mProductions.pop();
	BinopExpr expr = (BinopExpr) mProductions.pop();
	Value left = (Value) mProductions.pop();

	expr.setOp1(left);
	expr.setOp2(right);
	mProductions.push(expr);
    }

    public void outABinopBoolExpr(ABinopBoolExpr node){
    }
    

    public void outAUnopExpression(AUnopExpression node)
    {
    }




    /*
      binop =
      {and}   and |
      {or}    or |
      {xor}   xor |
      {mod}   mod |

      {cmp}   cmp |
      {cmpg}  cmpg |
      {cmpl}  cmpl |
      {cmpeq} cmpeq |

      {cmpne} cmpne |
      {cmpgt} cmpgt |
      {cmpge} cmpge |
      {cmplt} cmplt |

      {cmple} cmple |
      {shl}   shl |
      {shr}   shr |
      {ushr}  ushr |

      {plus}  plus |
      {minus} minus |
      {mult}  mult |
      {div}   div;
    */


    public void outAAndBinop(AAndBinop node)
    {
        mProductions.push(Jimple.v().newAndExpr(mValue, mValue));
    }
    public void outAOrBinop(AOrBinop node)
    {
        mProductions.push(Jimple.v().newOrExpr(mValue, mValue));
    }

    public void outAXorBinop(AXorBinop node)
    {
        mProductions.push(Jimple.v().newXorExpr(mValue, mValue));
    }
    public void outAModBinop(AModBinop node)
    {
        mProductions.push(Jimple.v().newRemExpr(mValue, mValue));
    }

    public void outACmpBinop(ACmpBinop node)
    {
        mProductions.push(Jimple.v().newCmpExpr(mValue, mValue));
    }

    public void outACmpgBinop(ACmpgBinop node)
    {
        mProductions.push(Jimple.v().newCmpgExpr(mValue, mValue));
    }

    public void outACmplBinop(ACmplBinop node)
    {
        mProductions.push(Jimple.v().newCmplExpr(mValue, mValue));
    }

    public void outACmpeqBinop(ACmpeqBinop node)
    {
        mProductions.push(Jimple.v().newEqExpr(mValue, mValue));
    }


    public void outACmpneBinop(ACmpneBinop node)
    {
        mProductions.push(Jimple.v().newNeExpr(mValue, mValue));
    }

    public void outACmpgtBinop(ACmpgtBinop node)
    {
        mProductions.push(Jimple.v().newGtExpr(mValue, mValue));
    }

    public void outACmpgeBinop(ACmpgeBinop node)
    {
        mProductions.push(Jimple.v().newGeExpr(mValue, mValue));
    }

    public void outACmpltBinop(ACmpltBinop node)
    {
        mProductions.push(Jimple.v().newLtExpr(mValue, mValue));
    }

    public void outACmpleBinop(ACmpleBinop node)
    {
        mProductions.push(Jimple.v().newLeExpr(mValue, mValue));
    }

    public void outAShlBinop(AShlBinop node)
    {
        mProductions.push(Jimple.v().newShlExpr(mValue, mValue));
    }

    public void outAShrBinop(AShrBinop node)
    {
        mProductions.push(Jimple.v().newShrExpr(mValue, mValue));
    }

    public void outAUshrBinop(AUshrBinop node)
    {
        mProductions.push(Jimple.v().newUshrExpr(mValue, mValue));
    }



    public void outAPlusBinop(APlusBinop node)
    {
        mProductions.push(Jimple.v().newAddExpr(mValue, mValue));
    }

    public void outAMinusBinop(AMinusBinop node)
    {
        mProductions.push(Jimple.v().newSubExpr(mValue, mValue));
    }

    public void outAMultBinop(AMultBinop node)
    {
        mProductions.push(Jimple.v().newMulExpr(mValue, mValue));
    }
    public void outADivBinop(ADivBinop node)
    {
        mProductions.push(Jimple.v().newDivExpr(mValue, mValue));
    }

    /*
      throws_clause =
      throws class_name_list;
    */    
    public void outAThrowsClause(AThrowsClause node)
    {
	List l = (List) mProductions.pop();
	Iterator it = l.iterator();
	List exceptionClasses = new ArrayList(l.size());
      
	while(it.hasNext()) {	 	  
	    String className = (String) it.next();
	  
	    //	  exceptionClasses.add(new SootClass("dummy exception class"));
	    exceptionClasses.add(Scene.v().getSootClass(className));
	}

	mProductions.push(exceptionClasses);
    }




    /*
      variable =
      {reference} reference |
      {local}     local_name;
    */
    
    public void outALocalVariable(ALocalVariable node)
    {
	String local = (String) mProductions.pop();

	Local l = (Local) mLocals.get(local);
	if(l == null) throw new RuntimeException("did not find local: " + local);
        mProductions.push(l);
    }

    /*
      public void caseAReferenceVariable(AReferenceVariable node)
      {        
      }
    */
       
    
    /*
      array_ref =
      identifier fixed_array_descriptor;
    */

    /*
      public void caseAArrayRef(AArrayRef node)
      {
      mProductions.push(Jimple.v().newLocal("dummy array reference", IntType.v()));
      }*/

    public void outAArrayRef(AArrayRef node)
    {
	Value immediate = (Value) mProductions.pop();
	String identifier = (String) mProductions.pop();
	
	Local l = (Local) mLocals.get(identifier);
	if(l == null) throw new RuntimeException("did not find local: " + identifier);
	
	mProductions.push(Jimple.v().newArrayRef(l, immediate));	
    }
    




    /*
      field_ref =
      {local} local_name dot field_signature |
      {sig}   field_signature;
    */

    public void outALocalFieldRef(ALocalFieldRef node)
    {
	SootField field = (SootField) mProductions.pop();
	String local = (String) mProductions.pop();
	
	Local l = (Local) mLocals.get(local);
	if(l == null) throw new RuntimeException("did not find local: " + local);
	
	mProductions.push(Jimple.v().newInstanceFieldRef(l, field));
    }

    
    public void outASigFieldRef(ASigFieldRef node)
    {
	SootField field = (SootField) mProductions.pop();
	mProductions.push(Jimple.v().newStaticFieldRef(field));	
    }


    /*
      field_signature =
      cmplt [class_name]:class_name [first]:colon type [field_name]:name cmpgt;
    */
    
    public void outAFieldSignature(AFieldSignature node)
    {
        String className, fieldName;
	Type t;
	
	fieldName = (String) mProductions.pop();
	t = (Type) mProductions.pop();
	className  = (String) mProductions.pop();	

	SootClass cl = Scene.v().getSootClass(className);
	SootField field = cl.getField(fieldName, t);
	
	mProductions.push(field);
    }





    /*
      expression =
      {new}         new_expr |
      {cast}        l_paren nonvoid_type r_paren local_name |
      {instanceof}  immediate instanceof nonvoid_type |
      {invoke}      invoke_expr |

      {reference}   reference |
      {binop}       binop_expr |
      {unop}        unop_expr |
      {immediate}   immediate;
    */

    public void outACastExpression(ACastExpression node)
    {
	Value val = (Value) mProductions.pop();
	
        Type type = (Type) mProductions.pop();
	mProductions.push(Jimple.v().newCastExpr(val, type));
    }



    public void outAInstanceofExpression(AInstanceofExpression node)
    {
        Type nonvoidType = (Type) mProductions.pop();
	Value immediate = (Value) mProductions.pop();	
	mProductions.push(Jimple.v().newInstanceOfExpr(immediate, nonvoidType));
    }


    /*
      unop_expr =
      unop immediate;
    */
    public void outAUnopExpr(AUnopExpr node)
    {
	Value v = (Value) mProductions.pop();
	UnopExpr expr = (UnopExpr) mProductions.pop();
	expr.setOp(v);
	mProductions.push(expr);
    }
    
        
    /*
      unop =
      {lengthof} lengthof |
      {neg}      neg;
    */
    public void outALengthofUnop(ALengthofUnop node)
    {
	mProductions.push(Jimple.v().newLengthExpr(mValue));
    }

    public void outANegUnop(ANegUnop node)
    {
       	mProductions.push(Jimple.v().newNegExpr(mValue));
    }


    /*
      invoke_expr = 
      {nonstatic} nonstatic_invoke local_name dot method_signature l_paren arg_list? r_paren |
      {static}    staticinvoke method_signature l_paren arg_list? r_paren;
    */	
	
    public void	 outANonstaticInvokeExpr(ANonstaticInvokeExpr node)
    {
	List args;
	
	if(node.getArgList() != null) 	      	    
	    args = (List) mProductions.pop();
	else
	    args = new ArrayList();
	
	SootMethod method = (SootMethod) mProductions.pop();
	String local = (String) mProductions.pop();
	
       	
	Local l = (Local) mLocals.get(local);
	if(l ==	null) throw new RuntimeException("did not find local: " + local);
		

	Node invokeType = (Node) node.getNonstaticInvoke();
	Expr invokeExpr;

	if(invokeType instanceof ASpecialNonstaticInvoke){
	    invokeExpr = Jimple.v().newSpecialInvokeExpr(l, method, args);
	} else if(invokeType instanceof AVirtualNonstaticInvoke){
	    invokeExpr = Jimple.v().newVirtualInvokeExpr(l, method, args);
	} else {
	    if(debug)if(!(invokeType instanceof AInterfaceNonstaticInvoke)) throw new RuntimeException("expected interface invoke.");
	    invokeExpr = Jimple.v().newInterfaceInvokeExpr(l, method, args);
	}

	mProductions.push(invokeExpr);	  
    }	
    
    

    public void outAStaticInvokeExpr(AStaticInvokeExpr node)
    {
	List args;
	
	if(node.getArgList() != null) 	      	    
	    args = (List) mProductions.pop();
	else
	    args = new ArrayList();
	
	SootMethod method = (SootMethod) mProductions.pop();
	
	mProductions.push(Jimple.v().newStaticInvokeExpr(method, args));     
    }

    /*
      method_signature =
      cmplt [class_name]:class_name [first]:colon type [method_name]:name  l_paren parameter_list? r_paren cmpgt;
    */
    public void outAMethodSignature(AMethodSignature node)
    {
	String className, methodName;
	List parameterList = new ArrayList();
	Type returnType;

	if(node.getParameterList() != null)
	    parameterList =  (List) mProductions.pop();
	
	methodName = (String) mProductions.pop();
	Type type = (Type) mProductions.pop();
	className = (String) mProductions.pop();

	SootClass sootClass =  Scene.v().getSootClass(className);
	SootMethod sootMethod = sootClass.getMethod(methodName, parameterList, type);

	mProductions.push(sootMethod);
    }

    
    /*    
	  new_expr =
	  {simple} new base_type |
	  {array}  newarray l_paren nonvoid_type r_paren fixed_array_descriptor |
	  {multi}  newmultiarray l_paren base_type r_paren array_descriptor+;
    */
    public void outASimpleNewExpr(ASimpleNewExpr node)
    {
	mProductions.push(Jimple.v().newNewExpr((RefType)mProductions.pop()));
    }
    
    public void outAArrayNewExpr(AArrayNewExpr node)
    {
	Value size = (Value) mProductions.pop();
	Type type = (Type) mProductions.pop();
	mProductions.push(Jimple.v().newNewArrayExpr(type, size));
    }


    public void outAMultiNewExpr(AMultiNewExpr node)
    {
	
	LinkedList arrayDesc =  node.getArrayDescriptor();

	int descCnt = arrayDesc.size();
	List sizes = new LinkedList(); 
	
	Iterator it = arrayDesc.iterator();
	while(it.hasNext()) {
	    AArrayDescriptor o = (AArrayDescriptor) it.next();
	    if(o.getImmediate() != null)
		sizes.add(0,(Value) mProductions.pop());
	    else 
		break;
	}
	    		
	BaseType type = (BaseType) mProductions.pop();
	ArrayType arrayType = ArrayType.v(type, descCnt);
	
	mProductions.push(Jimple.v().newNewMultiArrayExpr(arrayType, sizes));	
    }

    public void defaultCase(Node node)
    {
	if(node instanceof TQuotedName ||
	   node instanceof TFullIdentifier ||
	   node instanceof TIdentifier ||
	   node instanceof TStringConstant ||

	   node instanceof TIntegerConstant ||
	   node instanceof TFloatConstant ||
	   node instanceof TAtIdentifier

	   ) {
	    if(debug) 
		System.out.println("Default case -pushing token:" + ((Token) node).getText());
	    String tokenString = ((Token) node).getText();
	    if(node instanceof TStringConstant || node instanceof TQuotedName) {
		tokenString = tokenString.substring(1, tokenString.length() -1 );		
	    } 
	    
	    if(node instanceof TIdentifier || node instanceof TFullIdentifier || node instanceof TQuotedName || node instanceof TStringConstant) {
	      try {
		tokenString = StringTools.getUnEscapedStringOf(tokenString);

	      } catch(RuntimeException e) {
		System.out.println(tokenString);
		throw e;
	      }
	    }
	    mProductions.push(tokenString);
	} 
    }




  protected int processModifiers(List l)
  {
    int modifier = 0;
    Iterator it = l.iterator();
   
    while(it.hasNext()) {
      Object  t = it.next();
      if(t instanceof AAbstractModifier)
	modifier |= Modifier.ABSTRACT;
      else if(t instanceof AFinalModifier)
	modifier |= Modifier.FINAL;
      else if(t instanceof ANativeModifier)
	modifier |= Modifier.NATIVE;
      else if(t instanceof APublicModifier)
	modifier |= Modifier.PUBLIC;
      else if(t instanceof AProtectedModifier)
	modifier |= Modifier.PROTECTED;
      else if(t instanceof APrivateModifier)
	modifier |= Modifier.PRIVATE;
      else if(t instanceof AStaticModifier)
	modifier |= Modifier.STATIC;
      else if(t instanceof ASynchronizedModifier)
	modifier |= Modifier.SYNCHRONIZED;
      else if(t instanceof ATransientModifier)
	modifier |= Modifier.TRANSIENT;
      else if(t instanceof AVolatileModifier)
	modifier |= Modifier.VOLATILE;
      else
	throw new RuntimeException("Impossible");
    }	
    
	return modifier;
  }

    
    private void addBoxToPatch(String aLabelName, UnitBox aUnitBox)
    {
	List patchList =  (List) mLabelToPatchList.get(aLabelName);
	if(patchList == null) {
	    patchList = new ArrayList();
	    mLabelToPatchList.put(aLabelName, patchList);
	}
	
	patchList.add(aUnitBox);	
    }


}


