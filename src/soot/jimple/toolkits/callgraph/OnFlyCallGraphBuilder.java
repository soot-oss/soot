/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.jimple.toolkits.callgraph;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.Context;
import soot.DoubleType;
import soot.EntryPoints;
import soot.FastHierarchy;
import soot.FloatType;
import soot.G;
import soot.IntType;
import soot.Kind;
import soot.Local;
import soot.LongType;
import soot.MethodContext;
import soot.MethodOrMethodContext;
import soot.NullType;
import soot.PackManager;
import soot.PhaseOptions;
import soot.PrimType;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
import soot.ShortType;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Transform;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.FieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.spark.pag.AllocDotField;
import soot.jimple.spark.pag.PAG;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis;
import soot.jimple.toolkits.callgraph.ConstantArrayAnalysis.ArrayTypes;
import soot.jimple.toolkits.reflection.ReflectionTraceInfo;
import soot.options.CGOptions;
import soot.options.Options;
import soot.options.SparkOptions;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.LargeNumberedMap;
import soot.util.NumberedString;
import soot.util.SmallNumberedMap;
import soot.util.queue.ChunkedQueue;
import soot.util.queue.QueueReader;

/** Models the call graph.
 * @author Ondrej Lhotak
 */
public final class OnFlyCallGraphBuilder
{ 
	private static final PrimType[] CHAR_NARROWINGS = new PrimType[]{
		CharType.v()
	};
	private static final PrimType[] INT_NARROWINGS = new PrimType[]{
		IntType.v(),
		CharType.v(),
		ShortType.v(),
		ByteType.v(),
		ShortType.v()
	};
	private static final PrimType[] SHORT_NARROWINGS = new PrimType[]{
		ShortType.v(),
		ByteType.v()
	};
	private static final PrimType[] LONG_NARROWINGS = new PrimType[]{
		LongType.v(),
		IntType.v(),
		CharType.v(),
		ShortType.v(),
		ByteType.v(),
		ShortType.v()
	};
	private static final ByteType[] BYTE_NARROWINGS = new ByteType[]{
		ByteType.v()
	};
	private static final PrimType[] FLOAT_NARROWINGS = new PrimType[]{
		FloatType.v(),
		LongType.v(),
		IntType.v(),
		CharType.v(),
		ShortType.v(),
		ByteType.v(),
		ShortType.v(),
	};
	private static final PrimType[] BOOLEAN_NARROWINGS = new PrimType[]{
		BooleanType.v()
	};
	private static final PrimType[] DOUBLE_NARROWINGS = new PrimType[]{
		DoubleType.v(),
		FloatType.v(),
		LongType.v(),
		IntType.v(),
		CharType.v(),
		ShortType.v(),
		ByteType.v(),
		ShortType.v(),	
	};
	public class DefaultReflectionModel implements ReflectionModel {
		
	    protected CGOptions options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
	    
	    protected HashSet<SootMethod> warnedAlready = new HashSet<SootMethod>();

		@Override
		public void classForName(SootMethod source, Stmt s) {
	        List<Local> stringConstants = methodToStringConstants.get(source);
	        if( stringConstants == null )
	            methodToStringConstants.put(source, stringConstants = new ArrayList<Local>());
			InvokeExpr ie = s.getInvokeExpr();
	        Value className = ie.getArg(0);
	        if( className instanceof StringConstant ) {
	            String cls = ((StringConstant) className ).value;
	            constantForName( cls, source, s );
	        } else {
	            Local constant = (Local) className;
	            if( options.safe_forname() ) {
	                for (SootMethod tgt : EntryPoints.v().clinits()) {
	                    addEdge( source, s, tgt, Kind.CLINIT );
	                }
	            } else {
	                for (SootClass cls : Scene.v().dynamicClasses()) {
	                    for (SootMethod clinit : EntryPoints.v().clinitsOf(cls)) {
	                        addEdge( source, s, clinit, Kind.CLINIT);
	                    }
	                }
	                VirtualCallSite site = new VirtualCallSite( s, source, null, null, Kind.CLINIT );
	                List<VirtualCallSite> sites = stringConstToSites.get(constant);
	                if (sites == null) {
	                    stringConstToSites.put(constant, sites = new ArrayList<VirtualCallSite>());
	                    stringConstants.add(constant);
	                }
	                sites.add(site);
	            }
	        }        
		}

		@Override
		public void classNewInstance(SootMethod source, Stmt s) {
			if( options.safe_newinstance() ) {
				for (SootMethod tgt : EntryPoints.v().inits()) {
					addEdge( source, s, tgt, Kind.NEWINSTANCE );
				}
			} else {
				for (SootClass cls : Scene.v().dynamicClasses()) {
					SootMethod sm = cls.getMethodUnsafe(sigInit);
					if( sm != null ) {
						addEdge( source, s, sm, Kind.NEWINSTANCE );
					}
				}

				if( options.verbose() ) {
					G.v().out.println( "Warning: Method "+source+
							" is reachable, and calls Class.newInstance;"+
							" graph will be incomplete!"+
					" Use safe-newinstance option for a conservative result." );
				}
			} 
		}

		@Override
		public void contructorNewInstance(SootMethod source, Stmt s) {
			if( options.safe_newinstance() ) {
				for (SootMethod tgt : EntryPoints.v().allInits()) {
					addEdge( source, s, tgt, Kind.NEWINSTANCE );
				}
			} else {
				for (SootClass cls : Scene.v().dynamicClasses()) {
					for(SootMethod m: cls.getMethods()) {
						if(m.getName().equals("<init>")) {
							addEdge( source, s, m, Kind.NEWINSTANCE );
						}
					}
				}
				if( options.verbose() ) {
					G.v().out.println( "Warning: Method "+source+
							" is reachable, and calls Constructor.newInstance;"+
							" graph will be incomplete!"+
					" Use safe-newinstance option for a conservative result." );
				}
			} 
		}

		@Override
		public void methodInvoke(SootMethod container, Stmt invokeStmt) {
			if( !warnedAlready(container) ) {
				if( options.verbose() ) {
					G.v().out.println( "Warning: call to "+
							"java.lang.reflect.Method: invoke() from "+container+
					"; graph will be incomplete!" );
				}
				markWarned(container);
			}
		}

		private void markWarned(SootMethod m) {
			warnedAlready.add(m);
		}

		private boolean warnedAlready(SootMethod m) {
			return warnedAlready.contains(m);
		}
	}
	
	public class TypeBasedReflectionModel extends DefaultReflectionModel {
		@Override
		public void methodInvoke(SootMethod container, Stmt invokeStmt) {
			if(container.getDeclaringClass().isJavaLibraryClass()) {
				super.methodInvoke(container, invokeStmt);
				return;
			}
			InstanceInvokeExpr d = (InstanceInvokeExpr) invokeStmt.getInvokeExpr();
			Value base = d.getArg(0);
			// no support for statics at the moment
			if(base instanceof NullConstant) {
				super.methodInvoke(container, invokeStmt);
				return;
			}
			assert base instanceof Local;
			addInvokeCallSite(invokeStmt, container, d);
		}
	}

	
	public class TraceBasedReflectionModel implements ReflectionModel {
		
		class Guard {
			public Guard(SootMethod container, Stmt stmt, String message) {
				this.container = container;
				this.stmt = stmt;
				this.message = message;
			}
			final SootMethod container;
			final Stmt stmt;
			final String message;
		}
		
		protected Set<Guard> guards;
		
		protected ReflectionTraceInfo reflectionInfo;

		private boolean registeredTransformation = false;
		
		private TraceBasedReflectionModel() {
			guards = new HashSet<Guard>();
			
			String logFile = options.reflection_log();
			if(logFile==null) {
				throw new InternalError("Trace based refection model enabled but no trace file given!?");
			} else {
				reflectionInfo = new ReflectionTraceInfo(logFile);
			}
		}

		/**
		 * Adds an edge to all class initializers of all possible receivers
		 * of Class.forName() calls within source.
		 */
		@Override
		public void classForName(SootMethod container, Stmt forNameInvokeStmt) {
			Set<String> classNames = reflectionInfo.classForNameClassNames(container);
			if(classNames==null || classNames.isEmpty()) {
				registerGuard(container, forNameInvokeStmt, "Class.forName() call site; Soot did not expect this site to be reached");
			} else {
				for (String clsName : classNames) {
		            constantForName( clsName, container, forNameInvokeStmt );
				}
			}
		}

		/**
		 * Adds an edge to the constructor of the target class from this call to
		 * {@link Class#newInstance()}.
		 */
		@Override
		public void classNewInstance(SootMethod container, Stmt newInstanceInvokeStmt) {
			Set<String> classNames = reflectionInfo.classNewInstanceClassNames(container);
			if(classNames==null || classNames.isEmpty()) {
				registerGuard(container, newInstanceInvokeStmt, "Class.newInstance() call site; Soot did not expect this site to be reached");
			} else {
				for (String clsName : classNames) {
					SootClass cls = Scene.v().getSootClass(clsName);
					SootMethod constructor = cls.getMethodUnsafe(sigInit);
					if( constructor != null ) {
						addEdge( container, newInstanceInvokeStmt, constructor, Kind.REFL_CLASS_NEWINSTANCE );
					}
				}
			}
		}

		/** 
		 * Adds a special edge of kind {@link Kind#REFL_CONSTR_NEWINSTANCE} to all possible target constructors
		 * of this call to {@link Constructor#newInstance(Object...)}.
		 * Those kinds of edges are treated specially in terms of how parameters are assigned,
		 * as parameters to the reflective call are passed into the argument array of
		 * {@link Constructor#newInstance(Object...)}.
		 * @see PAG#addCallTarget(Edge) 
		 */
		@Override
		public void contructorNewInstance(SootMethod container, Stmt newInstanceInvokeStmt) {
			Set<String> constructorSignatures = reflectionInfo.constructorNewInstanceSignatures(container);
			if(constructorSignatures==null || constructorSignatures.isEmpty()) {
				registerGuard(container, newInstanceInvokeStmt, "Constructor.newInstance(..) call site; Soot did not expect this site to be reached");
			} else {
				for (String constructorSignature : constructorSignatures) {
					SootMethod constructor = Scene.v().getMethod(constructorSignature);
					addEdge( container, newInstanceInvokeStmt, constructor, Kind.REFL_CONSTR_NEWINSTANCE );
				}
			}
		}

		/** 
		 * Adds a special edge of kind {@link Kind#REFL_INVOKE} to all possible target methods
		 * of this call to {@link Method#invoke(Object, Object...)}.
		 * Those kinds of edges are treated specially in terms of how parameters are assigned,
		 * as parameters to the reflective call are passed into the argument array of
		 * {@link Method#invoke(Object, Object...)}.
		 * @see PAG#addCallTarget(Edge) 
		 */
		@Override
		public void methodInvoke(SootMethod container, Stmt invokeStmt) {
			Set<String> methodSignatures = reflectionInfo.methodInvokeSignatures(container);
			if (methodSignatures == null || methodSignatures.isEmpty()) {
				registerGuard(container, invokeStmt, "Method.invoke(..) call site; Soot did not expect this site to be reached");
			} else {
				for (String methodSignature : methodSignatures) {
					SootMethod method = Scene.v().getMethod(methodSignature);
					addEdge( container, invokeStmt, method, Kind.REFL_INVOKE );
				}
			}
		}

		private void registerGuard(SootMethod container, Stmt stmt, String string) {
			guards.add(new Guard(container,stmt,string));

			if(options.verbose()) {
				G.v().out.println("Incomplete trace file: Class.forName() is called in method '" +
						container+"' but trace contains no information about the receiver class of this call.");
				if(options.guards().equals("ignore")) {
					G.v().out.println("Guarding strategy is set to 'ignore'. Will ignore this problem.");
				} else if(options.guards().equals("print")) {
					G.v().out.println("Guarding strategy is set to 'print'. " +
							"Program will print a stack trace if this location is reached during execution.");
				} else if(options.guards().equals("throw")) {
					G.v().out.println("Guarding strategy is set to 'throw'. Program will throw an " +
							"Error if this location is reached during execution.");
				} else {
					throw new RuntimeException("Invalid value for phase option (guarding): "+options.guards());
				}
			}
			
			if(!registeredTransformation) {
				registeredTransformation=true;
				PackManager.v().getPack("wjap").add(new Transform("wjap.guards",new SceneTransformer() {
					
					@Override
					protected void internalTransform(String phaseName, Map<String, String> options) {
						for (Guard g : guards) {
							insertGuard(g);
						}
					}
				}));
				PhaseOptions.v().setPhaseOption("wjap.guards", "enabled");
			}
		}
		
		private void insertGuard(Guard guard) {
			if(options.guards().equals("ignore")) return;
			
			SootMethod container = guard.container;
			Stmt insertionPoint = guard.stmt;
			if(!container.hasActiveBody()) {
				G.v().out.println("WARNING: Tried to insert guard into "+container+" but couldn't because method has no body.");
			} else {
				Body body = container.getActiveBody();
				
				//exc = new Error
				RefType runtimeExceptionType = RefType.v("java.lang.Error");
				NewExpr newExpr = Jimple.v().newNewExpr(runtimeExceptionType);
				LocalGenerator lg = new LocalGenerator(body);
				Local exceptionLocal = lg.generateLocal(runtimeExceptionType);
				AssignStmt assignStmt = Jimple.v().newAssignStmt(exceptionLocal, newExpr);
				body.getUnits().insertBefore(assignStmt, insertionPoint);
				
				//exc.<init>(message)
				SootMethodRef cref = runtimeExceptionType.getSootClass().getMethod("<init>", Collections.<Type>singletonList(RefType.v("java.lang.String"))).makeRef();
				SpecialInvokeExpr constructorInvokeExpr = Jimple.v().newSpecialInvokeExpr(exceptionLocal, cref, StringConstant.v(guard.message));
				InvokeStmt initStmt = Jimple.v().newInvokeStmt(constructorInvokeExpr);
				body.getUnits().insertAfter(initStmt, assignStmt);
				
				if(options.guards().equals("print")) {
					//exc.printStackTrace();
					VirtualInvokeExpr printStackTraceExpr = Jimple.v().newVirtualInvokeExpr(exceptionLocal, Scene.v().getSootClass("java.lang.Throwable").getMethod("printStackTrace", Collections.<Type>emptyList()).makeRef());
					InvokeStmt printStackTraceStmt = Jimple.v().newInvokeStmt(printStackTraceExpr);
					body.getUnits().insertAfter(printStackTraceStmt, initStmt);
				} else if(options.guards().equals("throw")) {
					body.getUnits().insertAfter(Jimple.v().newThrowStmt(exceptionLocal), initStmt);
				} else {
					throw new RuntimeException("Invalid value for phase option (guarding): "+options.guards());
				}
			}
		}

	}
	
    /** context-insensitive stuff */
    private final CallGraph cicg = new CallGraph();
    private final HashSet<SootMethod> analyzedMethods = new HashSet<SootMethod>();

    private final LargeNumberedMap<Local, List<VirtualCallSite>> receiverToSites = new LargeNumberedMap<Local, List<VirtualCallSite>>( Scene.v().getLocalNumberer() ); // Local -> List(VirtualCallSite)
    private final LargeNumberedMap<SootMethod, List<Local>> methodToReceivers = new LargeNumberedMap<SootMethod, List<Local>>( Scene.v().getMethodNumberer() ); // SootMethod -> List(Local)
    public LargeNumberedMap<SootMethod, List<Local>> methodToReceivers() { return methodToReceivers; }
    
    // type based reflection resolution state
    
    private final LargeNumberedMap<SootMethod, List<Local>> methodToInvokeBases = new LargeNumberedMap<SootMethod, List<Local>>(Scene.v().getMethodNumberer());
    private final LargeNumberedMap<SootMethod, List<Local>> methodToInvokeArgs = new LargeNumberedMap<SootMethod, List<Local>>(Scene.v().getMethodNumberer());
    public LargeNumberedMap<SootMethod, List<Local>> methodToInvokeArgs() { return methodToInvokeArgs; }
    public LargeNumberedMap<SootMethod, List<Local>> methodToInvokeBases() { return methodToInvokeBases; }
    
    private final Map<Local, List<InvokeCallSite>> baseToInvokeSite = new IdentityHashMap<Local, List<InvokeCallSite>>();
    private final Map<Local, List<InvokeCallSite>> invokeArgsToInvokeSite = new IdentityHashMap<Local, List<InvokeCallSite>>();
    private final Map<Local, Set<Integer>> invokeArgsToSize = new IdentityHashMap<Local, Set<Integer>>();
    private final Map<AllocDotField, Set<Local>> allocDotFieldToLocal = new IdentityHashMap<AllocDotField, Set<Local>>();
    private final Map<Local, Set<Type>> reachingArgTypes = new IdentityHashMap<Local, Set<Type>>();
    private final Map<Local, Set<Type>> reachingBaseTypes = new IdentityHashMap<Local, Set<Type>>();
    
    // end type based reflection resolution
    
    private final SmallNumberedMap<List<VirtualCallSite>> stringConstToSites = new SmallNumberedMap<List<VirtualCallSite>>( Scene.v().getLocalNumberer() ); // Local -> List(VirtualCallSite)
    private final LargeNumberedMap<SootMethod, List<Local>> methodToStringConstants = new LargeNumberedMap<SootMethod, List<Local>>( Scene.v().getMethodNumberer() ); // SootMethod -> List(Local)
    public LargeNumberedMap<SootMethod, List<Local>> methodToStringConstants() { return methodToStringConstants; }

    private CGOptions options;

    private boolean appOnly;

    /** context-sensitive stuff */
    private ReachableMethods rm;
    private QueueReader<MethodOrMethodContext> worklist;

    private ContextManager cm;

    private final ChunkedQueue<SootMethod> targetsQueue = new ChunkedQueue<SootMethod>();
    private final QueueReader<SootMethod> targets = targetsQueue.reader();
	private FastHierarchy fh;


    public OnFlyCallGraphBuilder( ContextManager cm, ReachableMethods rm ) {
        this.cm = cm;
        this.rm = rm;
        worklist = rm.listener();
        options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
        if( !options.verbose() ) {
            G.v().out.println( "[Call Graph] For information on where the call graph may be incomplete, use the verbose option to the cg phase." );
        }
        
        if(options.reflection_log()==null || options.reflection_log().length()==0) {
        	if(options.types_for_invoke() && new SparkOptions(PhaseOptions.v().getPhaseOptions("cg.spark")).enabled()) {
        		reflectionModel = new TypeBasedReflectionModel();
        	} else {
        		reflectionModel = new DefaultReflectionModel();
        	}
        } else {
        	reflectionModel = new TraceBasedReflectionModel();
        }
        this.fh = Scene.v().getOrMakeFastHierarchy();
    }
    public OnFlyCallGraphBuilder( ContextManager cm, ReachableMethods rm, boolean appOnly ) {
        this( cm, rm );
        this.appOnly = appOnly;
    }
    public void processReachables() {
        while(true) {
            if( !worklist.hasNext() ) {
                rm.update();
                if( !worklist.hasNext() ) break;
            }
            MethodOrMethodContext momc = worklist.next();
            SootMethod m = momc.method();
            if( appOnly && !m.getDeclaringClass().isApplicationClass() ) continue;
            if( analyzedMethods.add( m ) ) processNewMethod( m );
            processNewMethodContext( momc );
        }
    }
    public boolean wantTypes( Local receiver ) {
        return receiverToSites.get(receiver) != null || baseToInvokeSite.get(receiver) != null;
    }
    
    public void addBaseType(Local base, Context context, Type ty) {
		assert context == null;
		if(!baseToInvokeSite.containsKey(base)) {
			return;
		}
		if(!reachingBaseTypes.containsKey(base)) {
			reachingBaseTypes.put(base, new HashSet<Type>());
		}
		if(reachingBaseTypes.get(base).add(ty)) {
			resolveInvoke(baseToInvokeSite.get(base));
		}
	}
    
    public void addInvokeArgType(Local argArray, Context context, Type t) {
		assert context == null;
		if(!invokeArgsToInvokeSite.containsKey(argArray)) {
			return;
		}
		if(!reachingArgTypes.containsKey(argArray)) {
			reachingArgTypes.put(argArray, new HashSet<Type>());
		}
		if(reachingArgTypes.get(argArray).add(t)) {
			resolveInvoke(invokeArgsToInvokeSite.get(argArray));
		}
	}
    
    public void setArgArrayNonDetSize(Local argArray, Context context) {
    	assert context == null;
    	if(!invokeArgsToInvokeSite.containsKey(argArray)) {
    		return;
    	}
    	if(invokeArgsToSize.containsKey(argArray) && invokeArgsToInvokeSite.get(argArray) == null) {
    		return;
    	}
    	invokeArgsToSize.put(argArray, null);
    	resolveInvoke(invokeArgsToInvokeSite.get(argArray));
	}
    
    public void addPossibleArgArraySize(Local argArray, int value, Context context) {
		assert context == null;
		if(!invokeArgsToInvokeSite.containsKey(argArray)) {
			return;
		}
		// non-det size
		if(invokeArgsToSize.containsKey(argArray) && invokeArgsToSize.get(argArray) == null) {
			return;
		} else {
			if(!invokeArgsToSize.containsKey(argArray)) {
				invokeArgsToSize.put(argArray, new HashSet<Integer>());
			}
			if(invokeArgsToSize.get(argArray).add(value)) {
				resolveInvoke(invokeArgsToInvokeSite.get(argArray));
			}
		}
	}
    
    private void resolveInvoke(List<InvokeCallSite> list) {
		for(InvokeCallSite ics : list) {
			Set<Type> s = reachingBaseTypes.get(ics.base());
			if(s == null || s.isEmpty()) {
				continue;
			}
			if(ics.reachingTypes() != null) {
				assert ics.nullnessCode() != InvokeCallSite.MUST_BE_NULL;
				resolveStaticTypes(s, ics);
				continue;
			}
			boolean mustNotBeNull = ics.nullnessCode() == InvokeCallSite.MUST_NOT_BE_NULL;
			boolean mustBeNull = ics.nullnessCode() == InvokeCallSite.MUST_BE_NULL;
			//  if the arg array may be null and we haven't seen a size or type yet, then generate nullary methods
			if(mustBeNull || (ics.nullnessCode() == InvokeCallSite.MAY_BE_NULL && (!invokeArgsToSize.containsKey(ics.argArray()) || !reachingArgTypes.containsKey(ics.argArray())))) {
				for(Type bType : s) {
					assert bType instanceof RefType;
					SootClass baseClass = ((RefType) bType).getSootClass();
					assert !baseClass.isInterface();
					Iterator<SootMethod> mIt = getPublicNullaryMethodIterator(baseClass);
					while(mIt.hasNext()) {
						SootMethod sm = mIt.next();
						cm.addVirtualEdge(ics.container(), ics.stmt(), sm, Kind.REFL_INVOKE, null);
					}
				}
			} else {
				/*
				 * In this branch, either the invoke arg must not be null, or may be null and we have size and type information.
				 * Invert the above condition:
				 * ~mustBeNull && (~mayBeNull || (has-size && has-type)) 
				 * => (~mustBeNull && ~mayBeNull) || (~mustBeNull && has-size && has-type)
				 * => mustNotBeNull || (~mustBeNull && has-types && has-size)
				 * => mustNotBeNull || (mayBeNull && has-types && has-size) 
				 */
				Set<Type> reachingTypes = reachingArgTypes.get(ics.argArray());
				/* 
				 * the path condition allows must-not-be null without type and size info.
				 * Do nothing in this case. THIS IS UNSOUND if default null values in
				 * an argument array are used.
				 */
				if(reachingTypes == null || !invokeArgsToSize.containsKey(ics.argArray())) {
					assert ics.nullnessCode() == InvokeCallSite.MUST_NOT_BE_NULL : ics;
					return;
				}
				assert reachingTypes != null && invokeArgsToSize.containsKey(ics.argArray());
				Set<Integer> methodSizes = invokeArgsToSize.get(ics.argArray());
				for(Type bType : s) {
					assert bType instanceof RefLikeType;
					// we do not handle static methods or array reflection
					if(bType instanceof NullType || bType instanceof ArrayType) {
						continue;
					} else {
						SootClass baseClass = ((RefType)bType).getSootClass();
						Iterator<SootMethod> mIt = getPublicMethodIterator(baseClass, reachingTypes, methodSizes, mustNotBeNull);
						while(mIt.hasNext()) {
							SootMethod sm = mIt.next();
							cm.addVirtualEdge(ics.container(), ics.stmt(), sm, Kind.REFL_INVOKE, null);
						}
					}
				}
			}
		}
	}
    
    private void resolveStaticTypes(Set<Type> s, InvokeCallSite ics) {
    	ArrayTypes at = ics.reachingTypes();
		for(Type bType : s) {
			SootClass baseClass = ((RefType)bType).getSootClass();
			Iterator<SootMethod> mIt = getPublicMethodIterator(baseClass, at);
			while(mIt.hasNext()) {
				SootMethod sm = mIt.next();
				cm.addVirtualEdge(ics.container(), ics.stmt(), sm, Kind.REFL_INVOKE, null);
			}
		}
	}
	private Iterator<SootMethod> getPublicMethodIterator(SootClass baseClass, final ArrayTypes at) {
		return new AbstractMethodIterator(baseClass) {
			@Override
			protected boolean acceptMethod(SootMethod m) {
				if(!at.possibleSizes.contains(m.getParameterCount())) {
					return false;
				}
				for(int i = 0; i < m.getParameterCount(); i++) {
					if(at.possibleTypes[i].isEmpty()) {
						continue;
					}
					if(!isReflectionCompatible(m.getParameterType(i), at.possibleTypes[i])) {
						return false;
					}
				}
				return true;
			}
			
		};
	}
	
	private PrimType[] narrowings(PrimType f) {
		if(f instanceof IntType) {
			return INT_NARROWINGS;
		} else if(f instanceof ShortType) {
			return SHORT_NARROWINGS;
		} else if(f instanceof LongType) {
			return LONG_NARROWINGS;
		} else if(f instanceof ByteType) {
			return BYTE_NARROWINGS;
		} else if(f instanceof FloatType) {
			return FLOAT_NARROWINGS;
		} else if(f instanceof BooleanType) {
			return BOOLEAN_NARROWINGS;
		} else if(f instanceof DoubleType) {
			return DOUBLE_NARROWINGS;
		} else if(f instanceof CharType) {
			return CHAR_NARROWINGS;
		} else {
			throw new RuntimeException("Unexpected primitive type: " + f);
		}
	}

	private boolean isReflectionCompatible(Type paramType, Set<Type> reachingTypes) {
		/* 
		 * attempting to pass in a null will match any type (although attempting to pass it
		 * to a primitive arg will give an NPE)
		 */
		if(reachingTypes.contains(NullType.v())) {
			return true;
		}
		if(paramType instanceof RefLikeType) {
			for(Type rType : reachingTypes) {
				if(fh.canStoreType(paramType, rType)) {
					return true;
				}
			}
			return false;
		} else if(paramType instanceof PrimType) {
			PrimType primType = (PrimType) paramType;
			/*
			 * It appears, java reflection allows for unboxing followed by widening, so if 
			 * there is a wrapper type that whose corresponding primitive type
			 * can be widened into the expected primitive type, we're set
			 */
			for(PrimType narrowings : narrowings(primType)) {
				if(reachingTypes.contains(narrowings.boxedType())) {
					return true;
				}
			}
			return false;
		} else {
			// impossible?
			return false;
		}
	}

	
	private abstract class AbstractMethodIterator implements Iterator<SootMethod> {
		private SootMethod next;
		private SootClass currClass;
		private Iterator<SootMethod> methodIterator;

		AbstractMethodIterator(SootClass baseClass) {
			this.currClass = baseClass;
			this.next = null;
			this.methodIterator = baseClass.methodIterator();
			this.findNextMethod();
		}
		
		protected void findNextMethod() {
			next = null;
			if(methodIterator == null) {
				return;
			}
			do {
				while(methodIterator.hasNext()) {
					SootMethod n = methodIterator.next();
					if (!n.isPublic()) {
						continue;
					}
					if (n.isStatic() || n.isConstructor() || n.isStaticInitializer() || !n.isConcrete()) {
						continue;
					}
					if(!acceptMethod(n)) {
						continue;
					}
					next = n;
					return;
				}
				if(currClass.hasSuperclass() && !currClass.getSuperclass().isPhantom() && !currClass.getSuperclass().getName().equals("java.lang.Object")) {
					currClass = currClass.getSuperclass();
					methodIterator = currClass.methodIterator();
					continue;
				} else {
					methodIterator = null;
					return;
				}
			} while(true);
		}
		
		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public SootMethod next() {
			SootMethod toRet = next;
			findNextMethod();
			return toRet;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		protected abstract boolean acceptMethod(SootMethod m);
	}
	
	private Iterator<SootMethod> getPublicMethodIterator(final SootClass baseClass, final Set<Type> reachingTypes, final Set<Integer> methodSizes, final boolean mustNotBeNull) {
    	if(baseClass.isPhantom()) {
    		return Collections.emptyIterator();
    	}
    	return new AbstractMethodIterator(baseClass) {
			@Override
			protected boolean acceptMethod(SootMethod n) {
				int nParams = n.getParameterCount();
				if(methodSizes != null) {
					// if the arg array can be null we have to still allow for nullary methods
					boolean compatibleSize = methodSizes.contains(nParams) || (!mustNotBeNull && nParams == 0);
					if(!compatibleSize) {
						return false;
					}
				}
				List<Type> t = n.getParameterTypes();
				for(Type pTy : t) {
					if(!isReflectionCompatible(pTy, reachingTypes)) {
						return false;
					}
				}
				return true;
			}
    		
    	};
	}
	
	private Iterator<SootMethod> getPublicNullaryMethodIterator(final SootClass baseClass) {
    	if(baseClass.isPhantom()) {
    		return Collections.emptyIterator();
    	}
    	return new AbstractMethodIterator(baseClass) {
			@Override
			protected boolean acceptMethod(SootMethod n) {
				int nParams = n.getParameterCount();
				return nParams == 0;
			}
    	};
	}
	
	public void addType( Local receiver, Context srcContext, Type type, Context typeContext ) {
        FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
        if(receiverToSites.get(receiver) != null) {
	        for( Iterator<VirtualCallSite> siteIt = receiverToSites.get( receiver ).iterator(); siteIt.hasNext(); ) {
	            final VirtualCallSite site = siteIt.next();
	            if( site.kind() == Kind.THREAD && !fh.canStoreType( type, clRunnable))
	                continue;
	            if( site.kind() == Kind.EXECUTOR && !fh.canStoreType( type, clRunnable))
	                continue;
	            if( site.kind() == Kind.ASYNCTASK && !fh.canStoreType( type, clAsyncTask ))
	                continue;
	
	            if( site.iie() instanceof SpecialInvokeExpr && site.kind != Kind.THREAD
	            		&& site.kind != Kind.EXECUTOR
	            		&& site.kind != Kind.ASYNCTASK ) {
	            	SootMethod target = VirtualCalls.v().resolveSpecial( 
	                            (SpecialInvokeExpr) site.iie(),
	                            site.subSig(),
	                            site.container(),
	                            appOnly );
	            	//if the call target resides in a phantom class then "target" will be null;
	            	//simply do not add the target in that case
	            	if(target!=null) {
	            		targetsQueue.add( target );
	            	}
	            } else {
	                VirtualCalls.v().resolve( type,
	                        receiver.getType(),
	                        site.subSig(),
	                        site.container(), 
	                        targetsQueue,
	                        appOnly);
	            }
	            while(targets.hasNext()) {
	                SootMethod target = targets.next();
	                cm.addVirtualEdge(
	                        MethodContext.v( site.container(), srcContext ),
	                        site.stmt(),
	                        target,
	                        site.kind(),
	                        typeContext );
	            }
	        }
        }
        if(baseToInvokeSite.get(receiver) != null) {
        	addBaseType(receiver, srcContext, type);
        }
    }
    public boolean wantStringConstants( Local stringConst ) {
        return stringConstToSites.get(stringConst) != null;
    }
    public void addStringConstant( Local l, Context srcContext, String constant ) {
        for( Iterator<VirtualCallSite> siteIt = (stringConstToSites.get( l )).iterator(); siteIt.hasNext(); ) {
            final VirtualCallSite site = siteIt.next();
            if( constant == null ) {
                if( options.verbose() ) {
                    G.v().out.println( "Warning: Method "+site.container()+
                        " is reachable, and calls Class.forName on a"+
                        " non-constant String; graph will be incomplete!"+
                        " Use safe-forname option for a conservative result." );
                }
            } else {
                if( constant.length() > 0 && constant.charAt(0) == '[' ) {
                    if( constant.length() > 1 && constant.charAt(1) == 'L' 
                    && constant.charAt(constant.length()-1) == ';' ) {
                        constant = constant.substring(2,constant.length()-1);
                    } else continue;
                }
                if( !Scene.v().containsClass( constant ) ) {
                    if( options.verbose() ) {
                        G.v().out.println( "Warning: Class "+constant+" is"+
                            " a dynamic class, and you did not specify"+
                            " it as such; graph will be incomplete!" );
                    }
                } else {
                    SootClass sootcls = Scene.v().getSootClass( constant );
                    if( !sootcls.isApplicationClass() && !sootcls.isPhantom() ) {
                        sootcls.setLibraryClass();
                    }
                    for (SootMethod clinit : EntryPoints.v().clinitsOf(sootcls)) {
                        cm.addStaticEdge(
                                MethodContext.v( site.container(), srcContext ),
                                site.stmt(),
                                clinit,
                                Kind.CLINIT );
                    }
                }
            }
        }
    }
    
    public boolean wantArrayField(AllocDotField df) {
    	return allocDotFieldToLocal.containsKey(df);
	}
    
    public void addInvokeArgType(AllocDotField df, Context context, Type type) {
		if(!allocDotFieldToLocal.containsKey(df)) {
			return;
		}
		for(Local l : allocDotFieldToLocal.get(df)) {
			addInvokeArgType(l, context, type);
		}
	}
    
	public boolean wantInvokeArg(Local receiver) {
		return invokeArgsToInvokeSite.containsKey(receiver);
	}
	
	public void addInvokeArgDotField(Local receiver, AllocDotField dot) {
		if(!allocDotFieldToLocal.containsKey(dot)) {
			allocDotFieldToLocal.put(dot, new HashSet<Local>());
		}
		allocDotFieldToLocal.get(dot).add(receiver);
	}
	
	private NullnessAnalysis nullnessCache = null;
	private ConstantArrayAnalysis arrayCache = null;
	private SootMethod analysisKey = null;

    /* End of public methods. */
    
    /*
     * How type based reflection resolution works:
     * 
     * In general, for each call to invoke(), we record the local of the 
     * receiver argument and the argument array. Whenever a new type is added 
     * to the points to set of the receiver argument we add that type to the reachingBaseTypes
     * and try to resolve the reflective method call (see addType, addBaseType, and updatedNode() in OnFlyCallGraph).
     * 
     * For added precision, we also record the second argument to invoke. If it is always null, this means the
     * invoke() call resolves only to nullary methods.
     * 
     * When the second argument is a variable that must not be null we can narrow down the called method based
     * on the possible sizes of the argument array and the types it contains. Whenever a new allocation reaches
     * this variable we record the possible size of the array (by looking at the allocation site) and the possible
     * types stored in the array (see updatedNode in OnFlyCallGraph in the branch wantInvokeArg()). If the size of the array
     * isn't statically known, the analysis considers methods of all possible arities.
     * In addition, we track the PAG node
     * corresponding to the array contents. If a new type reaches this node, we update the possible
     * argument types. (see propagate() in PropWorklist and the visitor, and updatedFieldRef in OnFlyCallGraph).
     * 
     * For details on the method resolution process, see resolveInvoke()
     * 
     * Finally, for cases like o.invoke(b, foo, bar, baz); it is very easy to statically determine
     * precisely which types are in which argument positions. This is computed using the
     * ConstantArrayAnalysis and are resolved using resolveStaticTypes().
     */
    private void addInvokeCallSite(Stmt s, SootMethod container, InstanceInvokeExpr d) {
    	Local l = (Local) d.getArg(0);
    	Value argArray = d.getArg(1);
    	InvokeCallSite ics;
    	if(argArray instanceof NullConstant) {
    		ics = new InvokeCallSite(s, container, d, l);
    	} else {
    		if(analysisKey != container) {
    			ExceptionalUnitGraph graph = new ExceptionalUnitGraph(container.getActiveBody());
				nullnessCache = new NullnessAnalysis(graph);
    			arrayCache = new ConstantArrayAnalysis(graph, container.getActiveBody());
    			analysisKey = container;
    		}
    		Local argLocal = (Local) argArray;
    		int nullnessCode;
    		if(nullnessCache.isAlwaysNonNullBefore(s, argLocal)) {
    			nullnessCode = InvokeCallSite.MUST_NOT_BE_NULL;
    		} else if(nullnessCache.isAlwaysNullBefore(s, argLocal)) {
    			nullnessCode = InvokeCallSite.MUST_BE_NULL;
    		} else {
    			nullnessCode = InvokeCallSite.MAY_BE_NULL;
    		}
    		if(nullnessCode != InvokeCallSite.MUST_BE_NULL && arrayCache.isConstantBefore(s, argLocal)) {
    			ArrayTypes reachingArgTypes = arrayCache.getArrayTypesBefore(s, argLocal);
    			if(nullnessCode == InvokeCallSite.MAY_BE_NULL) {
    				reachingArgTypes.possibleSizes.add(0);
    			}
    			ics = new InvokeCallSite(s, container, d, l, reachingArgTypes, nullnessCode);
    		} else {
	    		ics = new InvokeCallSite(s, container, d, l, argLocal, nullnessCode);
	    		if(!invokeArgsToInvokeSite.containsKey(argLocal)) {
	    			invokeArgsToInvokeSite.put(argLocal, new ArrayList<InvokeCallSite>());
	    		}
	    		invokeArgsToInvokeSite.get(argLocal).add(ics);
    		}
    	}
    	if(!baseToInvokeSite.containsKey(l)) {
    		baseToInvokeSite.put(l, new ArrayList<InvokeCallSite>());
    	}
    	baseToInvokeSite.get(l).add(ics);
    }
    
	private void addVirtualCallSite( Stmt s, SootMethod m, Local receiver,
            InstanceInvokeExpr iie, NumberedString subSig, Kind kind ) {
        List<VirtualCallSite> sites = receiverToSites.get(receiver);
        if (sites == null) {
            receiverToSites.put(receiver, sites = new ArrayList<VirtualCallSite>());
            List<Local> receivers = methodToReceivers.get(m);
            if( receivers == null )
                methodToReceivers.put(m, receivers = new ArrayList<Local>());
            receivers.add(receiver);
        }
        sites.add(new VirtualCallSite(s, m, iie, subSig, kind));
    }
    private void processNewMethod( SootMethod m ) {
        if( m.isNative() || m.isPhantom() ) {
            return;
        }
        Body b = m.retrieveActiveBody();
        getImplicitTargets( m );
        findReceivers(m, b);
    }
    private void findReceivers(SootMethod m, Body b) {
        for( final Unit u : b.getUnits() ) {
            final Stmt s = (Stmt) u;
            if (s.containsInvokeExpr()) {
                InvokeExpr ie = s.getInvokeExpr();

                if (ie instanceof InstanceInvokeExpr) {
                    InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
                    Local receiver = (Local) iie.getBase();
                    NumberedString subSig = 
                        iie.getMethodRef().getSubSignature();
                    addVirtualCallSite( s, m, receiver, iie, subSig,
                            Edge.ieToKind(iie) );
                    if( subSig == sigStart ) {
                        addVirtualCallSite( s, m, receiver, iie, sigRun,
                                Kind.THREAD );
                    }
                    else if( subSig == sigExecutorExecute 
                    		|| subSig == sigHandlerPost
                    		|| subSig == sigHandlerPostAtFrontOfQueue
                    		|| subSig == sigHandlerPostAtTime
                    		|| subSig == sigHandlerPostAtTimeWithToken
                    		|| subSig == sigHandlerPostDelayed ) {
                    	if (iie.getArgCount() > 0) {
                    		Value runnable = iie.getArg(0);
                    		if (runnable instanceof Local)
		                        addVirtualCallSite( s, m, (Local) runnable, iie, sigRun,
		                                Kind.EXECUTOR );
                    	}
                    }
                    else if( subSig == sigExecute  ) {
                        addVirtualCallSite( s, m, receiver, iie, sigDoInBackground,
                                Kind.ASYNCTASK );
                    }
                } else if (ie instanceof DynamicInvokeExpr) {
                	if(options.verbose())
                		G.v().out.println("WARNING: InvokeDynamic to "+ie+" not resolved during call-graph construction.");
                } else {
                	SootMethod tgt = ie.getMethod();
                	if(tgt!=null) {
	                	addEdge(m, s, tgt);
	                	String signature = tgt.getSignature();
	                	if( signature.equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedAction)>" )
	                			||  signature.equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction)>" )
	                			||  signature.equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedAction,java.security.AccessControlContext)>" )
	                			||  signature.equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction,java.security.AccessControlContext)>" ) ) {
	                		
	                		Local receiver = (Local) ie.getArg(0);
	                		addVirtualCallSite( s, m, receiver, null, sigObjRun,
	                				Kind.PRIVILEGED );
	                	}
                	} else {
                		if(!Options.v().ignore_resolution_errors()) {
                			throw new InternalError("Unresolved target "+ie.getMethod()+". Resolution error should have occured earlier.");
                		}
                	}
                }
            }
        }
    }
    
    ReflectionModel reflectionModel;
    
    private void getImplicitTargets( SootMethod source ) {
        final SootClass scl = source.getDeclaringClass();
        if( source.isNative() || source.isPhantom() ) return;
        if( source.getSubSignature().indexOf( "<init>" ) >= 0 ) {
            handleInit(source, scl);
        }
        Body b = source.retrieveActiveBody();
        for (Unit u : b.getUnits()) {
            final Stmt s = (Stmt) u;
            if( s.containsInvokeExpr() ) {
                InvokeExpr ie = s.getInvokeExpr();
                final String methRefSig = ie.getMethodRef().getSignature();
                if( methRefSig.equals( "<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>" ) ) {
                	reflectionModel.methodInvoke(source,s);
                }
                else if( methRefSig.equals( "<java.lang.Class: java.lang.Object newInstance()>" ) ) {
                	reflectionModel.classNewInstance(source,s);
                }
                else if( methRefSig.equals( "<java.lang.reflect.Constructor: java.lang.Object newInstance(java.lang.Object[])>" ) ) {
                	reflectionModel.contructorNewInstance(source, s);
                }
                if( ie.getMethodRef().getSubSignature() == sigForName ) {
                	reflectionModel.classForName(source,s);
                }
                if( ie instanceof StaticInvokeExpr ) {
                	SootClass cl = ie.getMethodRef().declaringClass();
                	for (SootMethod clinit : EntryPoints.v().clinitsOf(cl)) {
                		addEdge( source, s, clinit, Kind.CLINIT );
                	}
                }
            }
            if( s.containsFieldRef() ) {
                FieldRef fr = s.getFieldRef();
                if( fr instanceof StaticFieldRef ) {
                    SootClass cl = fr.getFieldRef().declaringClass();
                    for (SootMethod clinit : EntryPoints.v().clinitsOf(cl)) {
                        addEdge( source, s, clinit, Kind.CLINIT );
                    }
                }
            }
            if( s instanceof AssignStmt ) {
                Value rhs = ((AssignStmt)s).getRightOp();
                if( rhs instanceof NewExpr ) {
                    NewExpr r = (NewExpr) rhs;
                    SootClass cl = r.getBaseType().getSootClass();
                    for (SootMethod clinit : EntryPoints.v().clinitsOf(cl)) {
                        addEdge( source, s, clinit, Kind.CLINIT );
                    }
                } else if( rhs instanceof NewArrayExpr || rhs instanceof NewMultiArrayExpr ) {
                    Type t = rhs.getType();
                    if( t instanceof ArrayType ) t = ((ArrayType)t).baseType;
                    if( t instanceof RefType ) {
                        SootClass cl = ((RefType) t).getSootClass();
                        for (SootMethod clinit : EntryPoints.v().clinitsOf(cl)) {
                            addEdge( source, s, clinit, Kind.CLINIT );
                        }
                    }
                }
            }
        }
    }

    private void processNewMethodContext( MethodOrMethodContext momc ) {
        SootMethod m = momc.method();
        Iterator<Edge> it = cicg.edgesOutOf(m);
        while( it.hasNext() ) {
            Edge e = it.next();
            cm.addStaticEdge( momc, e.srcUnit(), e.tgt(), e.kind() );
        }
    }

    private void handleInit(SootMethod source, final SootClass scl) {
        addEdge( source, null, scl, sigFinalize, Kind.FINALIZE );
    }
    private void constantForName( String cls, SootMethod src, Stmt srcUnit ) {
        if( cls.length() > 0 && cls.charAt(0) == '[' ) {
            if( cls.length() > 1 && cls.charAt(1) == 'L' && cls.charAt(cls.length()-1) == ';' ) {
                cls = cls.substring(2,cls.length()-1);
                constantForName( cls, src, srcUnit );
            }
        } else {
            if( !Scene.v().containsClass( cls ) ) {
                if( options.verbose() ) {
                    G.v().out.println( "Warning: Class "+cls+" is"+
                        " a dynamic class, and you did not specify"+
                        " it as such; graph will be incomplete!" );
                }
            } else {
                SootClass sootcls = Scene.v().getSootClass( cls );
                if (!sootcls.isPhantomClass()) {
	                if( !sootcls.isApplicationClass() ) {
	                    sootcls.setLibraryClass();
	                }
	                for (SootMethod clinit : EntryPoints.v().clinitsOf(sootcls)) {
	                    addEdge( src, srcUnit, clinit, Kind.CLINIT );
	                }
                }

            }
        }
    }

    private void addEdge( SootMethod src, Stmt stmt, SootMethod tgt,
            Kind kind ) {
        cicg.addEdge( new Edge( src, stmt, tgt, kind ) );
    }

    private void addEdge(  SootMethod src, Stmt stmt, SootClass cls, NumberedString methodSubSig, Kind kind ) {
    	SootMethod sm = cls.getMethodUnsafe( methodSubSig );
        if( sm != null ) {
            addEdge( src, stmt, sm, kind );
        }
    }
    private void addEdge( SootMethod src, Stmt stmt, SootMethod tgt ) {
        InvokeExpr ie = stmt.getInvokeExpr();
        addEdge( src, stmt, tgt, Edge.ieToKind(ie) );
    }

    protected final NumberedString sigFinalize = Scene.v().getSubSigNumberer().
        findOrAdd( "void finalize()" );
    protected final NumberedString sigInit = Scene.v().getSubSigNumberer().
        findOrAdd( "void <init>()" );
    protected final NumberedString sigStart = Scene.v().getSubSigNumberer().
        findOrAdd( "void start()" );
    protected final NumberedString sigRun = Scene.v().getSubSigNumberer().
        findOrAdd( "void run()" );
    protected final NumberedString sigExecute = Scene.v().getSubSigNumberer().
            findOrAdd( "android.os.AsyncTask execute(java.lang.Object[])" );
    
    protected final NumberedString sigExecutorExecute = Scene.v().getSubSigNumberer().
            findOrAdd( "void execute(java.lang.Runnable)" );
    
    protected final NumberedString sigHandlerPost = Scene.v().getSubSigNumberer().
            findOrAdd( "boolean post(java.lang.Runnable)" );
    protected final NumberedString sigHandlerPostAtFrontOfQueue = Scene.v().getSubSigNumberer().
            findOrAdd( "boolean postAtFrontOfQueue(java.lang.Runnable)" );
    protected final NumberedString sigHandlerPostAtTime = Scene.v().getSubSigNumberer().
            findOrAdd( "boolean postAtTime(java.lang.Runnable,long)" );
    protected final NumberedString sigHandlerPostAtTimeWithToken = Scene.v().getSubSigNumberer().
            findOrAdd( "boolean postAtTime(java.lang.Runnable,java.lang.Object,long)" );
    protected final NumberedString sigHandlerPostDelayed = Scene.v().getSubSigNumberer().
            findOrAdd( "boolean postDelayed(java.lang.Runnable,long)" );
    
    protected final NumberedString sigObjRun = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Object run()" );
    protected final NumberedString sigDoInBackground = Scene.v().getSubSigNumberer().
            findOrAdd( "java.lang.Object doInBackground(java.lang.Object[])" );
    protected final NumberedString sigForName = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Class forName(java.lang.String)" );
    protected final RefType clRunnable = RefType.v("java.lang.Runnable");
    protected final RefType clAsyncTask = RefType.v("android.os.AsyncTask");
}

