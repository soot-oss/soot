package soot.jimple.parser;

import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;

import java.io.*;
import ca.mcgill.sable.util.*;



import soot.jimple.*;
import soot.*;

class Walker extends DepthFirstAdapter 
{
    static int stmtCount = 0;

    private Object mCurrentSubTree = null;
    private SootClass mSootClass = null;


  public void outAIntegerConstant(AIntegerConstant node) 
  {	
    String s = ((node.getMinus() == null) ? "" : "-") + node.getIntegerConstant().getText();
    
    if(s.endsWith("L") || (s.endsWith("l"))) {
      String num = s.substring(0, s.length() -1);
      mCurrentSubTree = LongConstant.v(Long.decode(num).longValue());      
    } else {
      mCurrentSubTree = IntConstant.v(Integer.decode(s).intValue());      
    }
  }


  
  public void outAFloatDegenerateFloatExt(AFloatDegenerateFloatExt node)
  {
      //xxx deal w/ infinity && nan 
      // mCurrentSubTree = ((String)mCurrentSubTree).substring(1);
      mCurrentSubTree = "666";
  }
  
  public void outAFloatConstant(AFloatConstant node) 
  {	        
    String s = ((node.getMinus() == null) ? "" : "-") + mCurrentSubTree;
    
    
    
    if(s.endsWith("F") || (s.endsWith("f"))) {
	String num = s.substring(0, s.length() - 1);
	mCurrentSubTree = FloatConstant.v(Float.parseFloat(num));      
    } else {
	mCurrentSubTree = DoubleConstant.v(Double.parseDouble(s));      
    }
  }


  

  
  public void outAReferenceExpression(AReferenceExpression node)
  {
    mCurrentSubTree = null;
    
  }

    public void outAFile(AFile node)
    {        
	String superClassName = ((AExtendsClause)node.getExtendsClause()).getClassName().toString();
	SootClass superClass = new SootClass(superClassName);
	mSootClass.setSuperclass(superClass);
	

	AImplementsClause implClause;
	
	if((implClause = ((AImplementsClause) node.getImplementsClause())) != null) {
	    PClassNameList list = implClause.getClassNameList();
	    while(list  instanceof AClassNameMultiClassNameList) {

		SootClass interfaceClass = new SootClass(((AClassNameMultiClassNameList)list).getClassName().toString());
		mSootClass.addInterface(interfaceClass);
		list = ((AClassNameMultiClassNameList)list).getClassNameList();
	    }
	    	SootClass interfaceClass = new SootClass(((AClassNameSingleClassNameList)list).getClassName().toString());
		mSootClass.addInterface(interfaceClass);
	}
	
     
	mCurrentSubTree = mSootClass;
	mSootClass.printTo(new PrintWriter(System.out, true));
    }


    public void caseAFileBody(AFileBody node)
    {
	System.out.println("caseAFileBody");
        inAFileBody(node);        
        {
            Object temp[] = node.getMember().toArray();
            for(int i = 0; i < temp.length; i++) {
		((PMember) temp[i]).apply(this);
		
		if(temp[i] instanceof AFieldMember) 
		    mSootClass.addField((SootField) mCurrentSubTree);
	  
	    }
	}
        outAFileBody(node);
    }
    

    public void caseAFieldMember(AFieldMember node)
    {
       
	inAFieldMember(node);
	{
	    String fieldName = node.getName().toString();
	    int fieldModifiers = processModifiers(node.getModifier());
	    mCurrentSubTree = null;
	    node.getType().apply(this);
	    Type fieldType;
	   
	    if(mCurrentSubTree == null) fieldType = LongType.v(); else {
	      try {
		fieldType = (Type) mCurrentSubTree;}
	      catch (ClassCastException e) {
		System.out.println(node);
		System.out.println(mCurrentSubTree);
		return;
	      }
	    }
		
	    mCurrentSubTree = new SootField(fieldName, fieldType, fieldModifiers);	    
	    
	}
	outAFieldMember(node);
    }
    

    protected int processModifiers(LinkedList modifierList)
    {
	int modifier = 0;
	Object temp[] = modifierList.toArray();
	
	for(int i = 0; i < temp.length; i++) {
	  ((PModifier)temp[i]).apply(this);
	  String str = (String) mCurrentSubTree;

	  if(str.equals("abstract"))
		modifier |= Modifier.ABSTRACT;
	    else if (str.equals("final")) {
		modifier |= Modifier.FINAL;
	    }
	    else if (str.equals("native"))
		modifier |= Modifier.NATIVE;
	    else if (str.equals("public"))
		modifier |= Modifier.PUBLIC;
	    else if (str.equals("protected"))
		modifier |= Modifier.PROTECTED;
	    else if (str.equals("private"))
		modifier |= Modifier.PRIVATE;
	    else if (str.equals("static"))
		modifier |= Modifier.STATIC;
	    else if (str.equals("synchronized"))
		modifier |= Modifier.SYNCHRONIZED;
	    else if (str.equals("transient"))
		modifier |= Modifier.TRANSIENT;
	    else if (str.equals("volitile"))
		modifier |= Modifier.VOLATILE;
	}
	
	return modifier;
    }
    
    






    public void inAFile(AFile node)
    {
        System.out.println("reading class " + node.getClassName());

	int classModifiers = processModifiers(node.getModifier());
	
	if(node.getFileType().toString().equals("interface "))
	    classModifiers |= Modifier.INTERFACE;
	
	mSootClass = new SootClass(node.getClassName().toString(), classModifiers);
	Scene.v().addClass(mSootClass);
    } // inAFile

    public void inAMethodMember(AMethodMember node)
    {
        System.out.println("  reading method " + node.getName());
        stmtCount = 0;
    } // inAMethodMember

    public void outAMethodMember(AMethodMember node)
    {
        // System.out.println("    found " + stmtCount +
        //                    " assignment statements");
    } // outAMethodMember

    public void inAAssignStatement(AAssignStatement node)
    {
        // stmtCount++;
    } // inAAssignStatement


    public void outStart(Start node)
    {
        mCurrentSubTree.toString();
    }
    
    public void outABooleanBaseTypeNoName(ABooleanBaseTypeNoName node)
    {
        mCurrentSubTree = BooleanType.v();
    }

    public void outAByteBaseTypeNoName(AByteBaseTypeNoName node)
    {
	mCurrentSubTree = ByteType.v();        
    }

    public void outACharBaseTypeNoName(ACharBaseTypeNoName node)
    {
        mCurrentSubTree = CharType.v();
    }
    public void outAShortBaseTypeNoName(AShortBaseTypeNoName node)
    {
        mCurrentSubTree = ShortType.v();
    }

    public void outAIntBaseTypeNoName(AIntBaseTypeNoName node)
    {
        mCurrentSubTree = IntType.v();
    }
    
    public void outALongBaseTypeNoName(ALongBaseTypeNoName node)
    {
        mCurrentSubTree = LongType.v();
    }

    public void outAFloatBaseTypeNoName(AFloatBaseTypeNoName node)
    {
        mCurrentSubTree = FloatType.v();
    }
    
    public void outADoubleBaseTypeNoName(ADoubleBaseTypeNoName node)
    {
        mCurrentSubTree = DoubleType.v();
    }

    public void outAClassNameBaseType(AClassNameBaseType node)
    {
	mCurrentSubTree =  RefType.v(node.getClassName().toString());
    }
    
    public void caseAFullIdentNonvoidType(AFullIdentNonvoidType node)
    {
        inAFullIdentNonvoidType(node);
        if(node.getFullIdentifier() != null)
        {
            node.getFullIdentifier().apply(this);
        }

	mCurrentSubTree = RefType.v((String) mCurrentSubTree);
	
	/* xxx 
        {
            Object temp[] = node.getArrayBrackets().toArray();
            for(int i = 0; i < temp.length; i++)
            {
                ((PArrayBrackets) temp[i]).apply(this);
            }
        }
	*/
	
        outAFullIdentNonvoidType(node);
    }

    public void caseAMethodMember(AMethodMember node)
    {
        inAMethodMember(node);
       
	int modifiers = processModifiers(node.getModifier());
	
	
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
	
	Type returnType = (Type) mCurrentSubTree;
	
        if(node.getName() != null)
        {
            node.getName().apply(this);
        }
	
	String name = (String) mCurrentSubTree;
	if(name == null) throw new RuntimeException();

        if(node.getLParen() != null)
        {
            node.getLParen().apply(this);
        } 
	java.util.List parameterTypes = new java.util.ArrayList();
        if(node.getParameterList() != null)
        {
            node.getParameterList().apply(this);
	    parameterTypes.addAll( (java.util.List) mCurrentSubTree);
        }



        if(node.getRParen() != null)
        {
            node.getRParen().apply(this);
        }
        if(node.getThrowsClause() != null)
        {
            node.getThrowsClause().apply(this);
        }
        if(node.getMethodBody() != null)
        {
            node.getMethodBody().apply(this);
        }
	


	SootMethod method = new SootMethod(name, parameterTypes, returnType, modifiers);	
	mSootClass.addMethod(method);	 

	boolean hasBody = (modifiers & (Modifier.ABSTRACT | Modifier.NATIVE)) == 0;
	if(hasBody) {
	    JimpleBody methodBody = Jimple.v().newBody(method);
	    method.setActiveBody(methodBody);
	}
	
	outAMethodMember(node);
    }
    
    public void outAVoidType(AVoidType node)
    {
        mCurrentSubTree = VoidType.v();
    }

  
    public void defaultCase(Node node)
    {
      /*if(node instanceof Token) 
	mCurrentSubTree = ((Token)node).getText();*/
    }


    public void caseAMultiParameterList(AMultiParameterList node)
    {
        inAMultiParameterList(node);
	

        if(node.getParameter() != null)
        {
            node.getParameter().apply(this);
        }

	Type parameter = (Type) mCurrentSubTree;
        if(node.getComma() != null)
        {
            node.getComma().apply(this);
        }
        if(node.getParameterList() != null)
        {
            node.getParameterList().apply(this);
        }
	((java.util.List)mCurrentSubTree).add(parameter);
        outAMultiParameterList(node);
    }
    
    public void outASingleParameterList(ASingleParameterList node)
    {
        java.util.ArrayList parameterList = new java.util.ArrayList();
	parameterList.add(mCurrentSubTree);
	mCurrentSubTree = parameterList;
    }













  public void outABaseNonvoidType(ABaseNonvoidType node)
  {
    Object temp[] = node.getArrayBrackets().toArray();
    if(temp.length > 0) {
      System.out.println(mCurrentSubTree);
      mCurrentSubTree = ArrayType.v((BaseType) mCurrentSubTree, temp.length);
    }
  }











  // Tokens 
  
  //>> Modifiers
  public void caseTAbstract(TAbstract node)
  {
    mCurrentSubTree = node.getText();
  }

  public void caseTFinal(TFinal node)
  {
    mCurrentSubTree = node.getText();
  }

  public void caseTNative(TNative node)
  {
    mCurrentSubTree = node.getText();
  }
  
 
  public void caseTPublic(TPublic node)
  {
    mCurrentSubTree = node.getText();
  }

  public void caseTProtected(TProtected node)
  {
    mCurrentSubTree = node.getText();
  }

  public void caseTPrivate(TPrivate node)
  {
    mCurrentSubTree = node.getText();
  }
  
 
public void caseTStatic(TStatic node)
  {
    mCurrentSubTree = node.getText();
  }

  public void caseTSynchronized(TSynchronized node)
  {
    mCurrentSubTree = node.getText();
  }

  public void caseTTransient(TTransient node)
  {
    mCurrentSubTree = node.getText();
  }
 
  public void caseTVolatile(TVolatile node)
  {
    mCurrentSubTree = node.getText();
  }
  //< Modifiers
 
    

    public void caseTQuotedName(TQuotedName node)
    {
        mCurrentSubTree = node.getText();
    }

    public void caseTIdentifier(TIdentifier node)
    {
        mCurrentSubTree = node.getText();
    }
  
  
  public void caseTFullIdentifier(TFullIdentifier node) 
  {
    mCurrentSubTree = node.getText();
  }

  public void caseTFloatConstant(TFloatConstant node) 
  {
    mCurrentSubTree = node.getText();
  }
  
  public void caseTFloatDegenerate(TFloatDegenerate node) 
  {
    mCurrentSubTree = node.getText();
  }

} // Walker
