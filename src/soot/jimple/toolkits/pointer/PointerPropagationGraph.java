package soot.jimple.toolkits.pointer;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.invoke.*;
import soot.*;
import soot.jimple.*;
import java.util.*;
import soot.jimple.internal.*;

public abstract class PointerPropagationGraph extends PointerStmtSwitch
{
    public boolean parmsAsFields = false;
    public boolean returnsAsFields = false;
    public boolean collapseObjects = false;
    public boolean typesForSites = false;
    public boolean mergeStringbuffer = false;
    public boolean simulateNatives = false;

    InvokeGraph ig;

    static int castsSameAsDest = 0;
    static int castsDifferentFromDest = 0;

    public PointerPropagationGraph( InvokeGraph ig ) { this.ig = ig; }

    public static boolean isType( Object o ) {
	return o instanceof RefType || o instanceof ArrayType;
    }
    public void addAnyEdge( Object o, Object p ) {
    }
    public void addSimpleEdge( 
	Object src, Type srcType,
	Object dest, Type destType )
    {
	addAnyEdge( src, dest );
    }
    public void addLoadEdge( 
	Object base, Type baseType,
	Object field, Type fieldType,
	Object dest, Type destType )
    {
	addAnyEdge( new Pair( base, field ), dest );
    }
    public void addStoreEdge( 
	Object src, Type srcType,
	Object base, Type baseType,
	Object field, Type fieldType ) 
    {
	addAnyEdge( src, new Pair( base, field ) );
    }
    public void addNewEdge( 
	Object newExpr, Type newExprType,
	Object dest, Type destType ) 
    {
	addAnyEdge( newExpr, dest );
    }
    protected Stmt stmt;
    protected SootMethod method;
    public void handleStmt( final Stmt stmt, final SootMethod method ) {
	this.stmt = stmt; this.method = method;
	stmt.apply( this );
    }
    final protected static JimpleLocal stringConstant = new JimpleLocal( "$STRINGCONSTANT", RefType.v( "java.lang.String" ) );
    protected void caseAssignConstStmt( Value dest, Constant c ) {
	if( c instanceof StringConstant ) {
	    addNewEdge( c.getType(), c.getType(), 
		stringConstant, stringConstant.getType() );
	    if( dest instanceof Local ) {
		caseCopyStmt( (Local) dest, stringConstant );
	    } else if( dest instanceof InstanceFieldRef ) {
		caseStoreStmt( (InstanceFieldRef) dest, stringConstant );
	    } else if( dest instanceof ArrayRef ) {
		caseArrayStoreStmt( (ArrayRef) dest, stringConstant );
	    } else if( dest instanceof StaticFieldRef ) {
		caseGlobalStoreStmt( (StaticFieldRef) dest, stringConstant );
	    } else throw new RuntimeException( "unhandled stmt "+stmt );
	} else {
	    NullConstant cc = (NullConstant) c;
	}
    }
    protected void caseCastStmt( Local dest, Local src, CastExpr c ) {
	if( dest.getType().equals( c.getCastType() ) ) {
	    addSimpleEdge( src, src.getType(), dest, dest.getType() );
	    castsSameAsDest++;
	} else {
	    Pair castNode = new Pair( stmt, PointerAnalysis.CAST_NODE );
	    addSimpleEdge( src, src.getType(), castNode, c.getType() );
	    addSimpleEdge( castNode, c.getType(), dest, dest.getType() );
	    castsDifferentFromDest++;
	}
    }
    protected void caseCopyStmt( Local dest, Local src ) {
	addSimpleEdge( src, src.getType(), dest, dest.getType() );
    }
    protected void caseIdentityStmt( Local dest, IdentityRef src ) {
	if( src instanceof ThisRef ) {
	    addSimpleEdge( new Pair( method, PointerAnalysis.THIS_NODE ),
		    src.getType(),

		dest, dest.getType() );
	} else if( src instanceof ParameterRef ) {
	    if( method.isStatic() || !parmsAsFields ) {
		addSimpleEdge( 
		    new Pair( method, 
			new Integer( ((ParameterRef) src).getIndex() ) ), 
		    src.getType(), 
		    dest, 
		    dest.getType() );
	    } else {
		addLoadEdge(
		    new Pair( method, PointerAnalysis.THIS_NODE ),
		    method.getDeclaringClass().getType(),

		    new Pair( method.getSubSignature(),
			new Integer( ((ParameterRef) src).getIndex() ) ),
		    src.getType(),

		    dest,
		    dest.getType() );
	    }
	}
    }
    protected void caseLoadStmt( Local dest, InstanceFieldRef src ) {
	if( collapseObjects ) {
	    addSimpleEdge( src.getField(), src.getField().getType(),
		dest, dest.getType() );
	} else {
	    addLoadEdge( src.getBase(), src.getBase().getType(),
		src.getField(), src.getField().getType(),
		dest, dest.getType() );
	}
    }
    protected void caseStoreStmt( InstanceFieldRef dest, Local src ) {
	if( collapseObjects ) {
	    addSimpleEdge( src, src.getType(),
		dest.getField(), dest.getField().getType() );
	} else {
	    addStoreEdge( src, src.getType(),
		dest.getBase(), dest.getBase().getType(),
		dest.getField(), dest.getField().getType() );
	}
    }
    protected void caseArrayLoadStmt( Local dest, ArrayRef src ) {
	addLoadEdge( src.getBase(), src.getBase().getType(),

	    PointerAnalysis.ARRAY_ELEMENTS_NODE, 
	    src.getType(),

	    dest, dest.getType() );
    }
    protected void caseArrayStoreStmt( ArrayRef dest, Local src ) {
	addStoreEdge( src, src.getType(),

	    dest.getBase(), dest.getBase().getType(),

	    PointerAnalysis.ARRAY_ELEMENTS_NODE,
	    dest.getType() );
    }
    protected void caseGlobalLoadStmt( Local dest, StaticFieldRef src ) {
	addSimpleEdge( src.getField(), src.getField().getType(),
	    dest, dest.getType() ); 
    }
    protected void caseGlobalStoreStmt( StaticFieldRef dest, Local src ) {
	addSimpleEdge( src, src.getType(), 
	    dest.getField(), dest.getField().getType() ); 
    }
    protected void caseAnyNewStmtHelper( Object dest, Type destType,
	    Object src, Type srcType ) {
	if( typesForSites ||
		( mergeStringbuffer 
		  && RefType.v( "java.lang.StringBuffer" )
		    .equals( srcType ) ) ) {

	    addNewEdge( srcType, srcType, dest, destType );
	} else {
	    addNewEdge( src, srcType, dest, destType );
	}
    }
    protected void caseAnyNewStmt( Local dest, Expr e ) {
	if( e instanceof NewMultiArrayExpr ) {
	    // do prdele prace!!!
	    NewMultiArrayExpr nmae = (NewMultiArrayExpr) e;
	    Object oldee = dest;
	    Type oldtt = dest.getType();
	    caseAnyNewStmtHelper( dest, dest.getType(), e, e.getType() );
	    for( int d = nmae.getSizeCount() - 1; d > 0; d-- ) {
		Type tt = ArrayType.v( nmae.getBaseType().baseType, d );
		Pair ee = new Pair( nmae, new Integer(d) );

		caseAnyNewStmtHelper( ee, tt, ee, tt );
		addStoreEdge( ee, tt,
			oldee, oldtt,
			PointerAnalysis.ARRAY_ELEMENTS_NODE, tt );
		oldtt = tt;
		oldee = ee;
	    }
	} else {
	    caseAnyNewStmtHelper( dest, dest.getType(), e, e.getType() );
	}
    }
    protected void caseInvokeStmt( Local dest, InvokeExpr e ) {
	if( dest != null ) VarNode.v( dest, dest.getType(), method );
	if( e instanceof InstanceInvokeExpr ) {
	    InstanceInvokeExpr iie = (InstanceInvokeExpr) e;
	    Local base = (Local) iie.getBase();
	    VarNode.v( base, base.getType(), method );
	}
	Iterator it = ig.getTargetsOf( stmt ).iterator();
	while( it.hasNext() ) {
	    SootMethod target = (SootMethod) it.next();
	    Iterator it2 = e.getArgs().iterator();
	    int i = 0;
	    while( it2.hasNext() ) {
		Value v = (Value) it2.next();
		if( v instanceof Local && isType( v.getType() ) ) {
		    if( target.isStatic() || !parmsAsFields ) {
			addSimpleEdge( v, v.getType(), 
			    new Pair( target, new Integer(i) ), target.getParameterType(i) );
		    } else {
			addStoreEdge( 
			    v, v.getType(),

			    new Pair( target, PointerAnalysis.THIS_NODE ),
			    target.getDeclaringClass().getType(),

			    new Pair( target.getSubSignature(), new Integer( i ) ),
			    target.getParameterType(i) );
		    }
		} else if( v instanceof StringConstant ) {
		    if( target.isStatic() || !parmsAsFields ) {
			addSimpleEdge( stringConstant, stringConstant.getType(), 
			    new Pair( target, new Integer(i) ), target.getParameterType(i) );
		    } else {
			addStoreEdge( 
			    stringConstant, stringConstant.getType(),

			    new Pair( target, PointerAnalysis.THIS_NODE ),
			    target.getDeclaringClass().getType(),

			    new Pair( target.getSubSignature(), new Integer( i ) ),
			    target.getParameterType(i) );
		    }
		}
		i++;
	    }
	    if( e instanceof InstanceInvokeExpr ) {
		InstanceInvokeExpr iie = (InstanceInvokeExpr) e;
		addSimpleEdge( iie.getBase(), iie.getBase().getType(),

		    new Pair( target, PointerAnalysis.THIS_NODE ), 
		    target.getDeclaringClass().getType() );
	    }
	    if( dest != null && isType( target.getReturnType() ) ) {
		if( target.isStatic() || !returnsAsFields ) {
		    addSimpleEdge( new Pair( target, PointerAnalysis.RETURN_NODE ),
			target.getReturnType(),
			dest, dest.getType() );
		} else {
		    addLoadEdge( 
			new Pair( target, PointerAnalysis.THIS_NODE ),
			target.getDeclaringClass().getType(),

			new Pair( target.getSubSignature(),
			    PointerAnalysis.RETURN_NODE ),
			target.getReturnType(),
			
			dest, dest.getType() );
		}
	    }
	}
    }
    protected void caseReturnConstStmt( Constant c ) {
	if( c instanceof StringConstant ) {
	    if( method.isStatic() || !returnsAsFields ) {
		addNewEdge( c.getType(), c.getType(), 
		    new Pair( method, PointerAnalysis.RETURN_NODE ),
			method.getReturnType() );
	    } else {
		addNewEdge( c.getType(), c.getType(), 
		    new Pair( method, PointerAnalysis.
			    RETURN_STRING_CONSTANT_NODE ),
			method.getReturnType() );
		addStoreEdge( 
		    new Pair( method, PointerAnalysis.
			    RETURN_STRING_CONSTANT_NODE ),
			method.getReturnType(),
		
		    new Pair( method, PointerAnalysis.THIS_NODE ),
		    method.getDeclaringClass().getType(),

		    new Pair( method.getSubSignature(),
			PointerAnalysis.RETURN_NODE ),
		    method.getReturnType() );
	    }
	} else {
	    NullConstant cc = (NullConstant) c;
	}
    }
    protected void caseReturnStmt( Local val ) {
	if( val != null ) {
	    if( method.isStatic() || !returnsAsFields ) {
		addSimpleEdge( val, val.getType(),
		    new Pair( method, PointerAnalysis.RETURN_NODE ),
			method.getReturnType() );
	    } else {
		addStoreEdge( 
		
		    val, val.getType(),
		
		    new Pair( method, PointerAnalysis.THIS_NODE ),
		    method.getDeclaringClass().getType(),

		    new Pair( method.getSubSignature(),
			PointerAnalysis.RETURN_NODE ),
		    method.getReturnType() );
	    }
	}
    }
    protected void caseThrowStmt( Local thrownException ) {
	addSimpleEdge( thrownException, thrownException.getType(),
		PointerAnalysis.EXCEPTION_NODE, RefType.v("java.lang.Throwable") );
    }
    protected void caseCatchStmt( Local dest, CaughtExceptionRef cer ) {
	addSimpleEdge(
		PointerAnalysis.EXCEPTION_NODE, RefType.v("java.lang.Throwable"),
		dest, dest.getType() );
    }
    public void buildNative( SootMethod m ) {
	throw new RuntimeException( 
	    "Needs to be overridden in subclasses of PointerPropagationGraph" );
    }

    public void build()
    {
        int countMethods = 0;
        int countClasses = 0;
        int countStmts = 0;

	Iterator classesIt = Scene.v().getClasses().iterator();
	while( classesIt.hasNext() )
	{
	    SootClass c = (SootClass) classesIt.next();
            boolean cHasReachableMethods = false;
	    Iterator methodsIt = c.getMethods().iterator();
	    while( methodsIt.hasNext() )
	    {
		SootMethod m = (SootMethod) methodsIt.next();
		if( simulateNatives && m.isNative() ) {
		    buildNative( m );
		}
		if( !m.isConcrete() ) continue;
                if( !ig.mcg.isReachable(m) ) continue;
                countMethods++;
                cHasReachableMethods = true;
		Body b = m.retrieveActiveBody();
                countStmts += b.getUnits().size();
		Iterator unitsIt = b.getUnits().iterator();
		while( unitsIt.hasNext() )
		{
		    handleStmt( (Stmt) unitsIt.next(), m );
		}
	    }
            if( cHasReachableMethods ) countClasses++;

	    addMiscEdges( c );

	}
	
	System.out.println( "Casts same type as dest: "+castsSameAsDest );
	System.out.println( "Casts different type from dest: "+castsDifferentFromDest );
        System.out.println( "Reachable methods: " + countMethods );
        System.out.println( "Reachable classes: " + countClasses );
        System.out.println( "Reachable stmts: " + countStmts );
    }

    static final RefType string = RefType.v("java.lang.String");
    static final ArrayType strAr = ArrayType.v(string, 1);
    static final List strArL = Collections.singletonList( strAr );
    static final String main = SootMethod.getSubSignature( "main", strArL, VoidType.v() );
    static final String exit = SootMethod.getSubSignature( "exit", Collections.EMPTY_LIST, VoidType.v() );
    static final String run = SootMethod.getSubSignature( "run", Collections.EMPTY_LIST, VoidType.v() );
    static final String finalize = SootMethod.getSubSignature( "finalize", Collections.EMPTY_LIST, VoidType.v() );
    protected void addMiscEdges( SootClass c ) {
	// Add node for parameter (String[]) in main method
	if( c.declaresMethod( main ) ) {
	    SootMethod m = c.getMethod( main );
	    addNewEdge( new Pair( m, PointerAnalysis.STRING_ARRAY_NODE ),
		strAr, new Pair( m, new Integer( 0 ) ), strAr );
	    addNewEdge( new Pair( m, PointerAnalysis.STRING_NODE ),
		string, new Pair( m, PointerAnalysis.STRING_NODE_LOCAL ),
		string );
	    addStoreEdge( new Pair( m, PointerAnalysis.STRING_NODE_LOCAL ),
				string,
		new Pair( m, new Integer( 0 ) ), strAr,
		PointerAnalysis.ARRAY_ELEMENTS_NODE, string );
	}

	// Add objects reaching this of run() methods
	if( Scene.v().getOrMakeFastHierarchy().canStoreType(
	    c.getType(), RefType.v("java.lang.Runnable") ) ) {
	    if( c.declaresMethod( run ) ) {
		SootMethod runM = c.getMethod( run );
		addNewEdge( AnyType.v(), AnyType.v(), 
		    new Pair( runM, PointerAnalysis.THIS_NODE ), c.getType() );
		if( c.declaresMethod( exit ) ) {
		    SootMethod exitM = c.getMethod( exit );
		    addSimpleEdge( runM, c.getType(), exitM, c.getType() );
		}
	    }
	}
	if( c.declaresMethod( finalize ) ) {
	    // In VTA, there was the comment:
	    // I have no clue whether or not this is right.
	    for( Iterator mIt = c.getMethods().iterator(); mIt.hasNext(); ){
		SootMethod m = (SootMethod) mIt.next();
		if( !m.getName().equals("<init>") ) continue;
		addSimpleEdge( new Pair( m, PointerAnalysis.THIS_NODE ),
		    c.getType(),

		    new Pair( c.getMethod( finalize ), 
			PointerAnalysis.THIS_NODE ),
		    c.getType() );
	    }
	}
    }
}

