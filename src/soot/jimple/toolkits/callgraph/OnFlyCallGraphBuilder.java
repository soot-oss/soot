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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.Context;
import soot.EntryPoints;
import soot.FastHierarchy;
import soot.G;
import soot.Kind;
import soot.Local;
import soot.MethodContext;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.PhaseOptions;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Transform;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.spark.pag.PAG;
import soot.jimple.toolkits.reflection.ReflectionTraceInfo;
import soot.options.CGOptions;
import soot.options.Options;
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
	public class DefaultReflectionModel implements ReflectionModel {
		
	    protected CGOptions options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
	    
	    protected HashSet<SootMethod> warnedAlready = new HashSet<SootMethod>();

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

		public void classNewInstance(SootMethod source, Stmt s) {
			if( options.safe_newinstance() ) {
				for (SootMethod tgt : EntryPoints.v().inits()) {
					addEdge( source, s, tgt, Kind.NEWINSTANCE );
				}
			} else {
				for (SootClass cls : Scene.v().dynamicClasses()) {
					if( cls.declaresMethod(sigInit) ) {
						addEdge( source, s, cls.getMethod(sigInit), Kind.NEWINSTANCE );
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
		public void classNewInstance(SootMethod container, Stmt newInstanceInvokeStmt) {
			Set<String> classNames = reflectionInfo.classNewInstanceClassNames(container);
			if(classNames==null || classNames.isEmpty()) {
				registerGuard(container, newInstanceInvokeStmt, "Class.newInstance() call site; Soot did not expect this site to be reached");
			} else {
				for (String clsName : classNames) {
					SootClass cls = Scene.v().getSootClass(clsName);
					if( cls.declaresMethod(sigInit) ) {
						SootMethod constructor = cls.getMethod(sigInit);
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

    private final SmallNumberedMap<List<VirtualCallSite>> stringConstToSites = new SmallNumberedMap<List<VirtualCallSite>>( Scene.v().getLocalNumberer() ); // Local -> List(VirtualCallSite)
    private final LargeNumberedMap<SootMethod, List<Local>> methodToStringConstants = new LargeNumberedMap<SootMethod, List<Local>>( Scene.v().getMethodNumberer() ); // SootMethod -> List(Local)
    public LargeNumberedMap<SootMethod, List<Local>> methodToStringConstants() { return methodToStringConstants; }

    private CGOptions options;

    private boolean appOnly;

    /** context-sensitive stuff */
    private ReachableMethods rm;
    private QueueReader worklist;

    private ContextManager cm;

    private final ChunkedQueue targetsQueue = new ChunkedQueue();
    private final QueueReader targets = targetsQueue.reader();


    public OnFlyCallGraphBuilder( ContextManager cm, ReachableMethods rm ) {
        this.cm = cm;
        this.rm = rm;
        worklist = rm.listener();
        options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
        if( !options.verbose() ) {
            G.v().out.println( "[Call Graph] For information on where the call graph may be incomplete, use the verbose option to the cg phase." );
        }
        
        if(options.reflection_log()==null || options.reflection_log().length()==0) {
        	reflectionModel = new DefaultReflectionModel();
        } else {
        	reflectionModel = new TraceBasedReflectionModel();
        }
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
            MethodOrMethodContext momc = (MethodOrMethodContext) worklist.next();
            SootMethod m = momc.method();
            if( appOnly && !m.getDeclaringClass().isApplicationClass() ) continue;
            if( analyzedMethods.add( m ) ) processNewMethod( m );
            processNewMethodContext( momc );
        }
    }
    public boolean wantTypes( Local receiver ) {
        return receiverToSites.get(receiver) != null;
    }
    public void addType( Local receiver, Context srcContext, Type type, Context typeContext ) {
        FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
        for( Iterator siteIt = ((Collection) receiverToSites.get( receiver )).iterator(); siteIt.hasNext(); ) {
            final VirtualCallSite site = (VirtualCallSite) siteIt.next();
            InstanceInvokeExpr iie = site.iie();
            if( site.kind() == Kind.THREAD && !fh.canStoreType( type, clRunnable))
                continue;
            if( site.kind() == Kind.ASYNCTASK && !fh.canStoreType( type, clAsyncTask ))
                continue;

            if( site.iie() instanceof SpecialInvokeExpr && site.kind != Kind.THREAD
            		&& site.kind != Kind.ASYNCTASK ) {
            	SootMethod target = VirtualCalls.v().resolveSpecial( 
                            (SpecialInvokeExpr) site.iie(),
                            site.subSig(),
                            site.container() );
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
                        targetsQueue );
            }
            while(targets.hasNext()) {
                SootMethod target = (SootMethod) targets.next();
                cm.addVirtualEdge(
                        MethodContext.v( site.container(), srcContext ),
                        site.stmt(),
                        target,
                        site.kind(),
                        typeContext );
            }
        }
    }
    public boolean wantStringConstants( Local stringConst ) {
        return stringConstToSites.get(stringConst) != null;
    }
    public void addStringConstant( Local l, Context srcContext, String constant ) {
        for( Iterator siteIt = (stringConstToSites.get( l )).iterator(); siteIt.hasNext(); ) {
            final VirtualCallSite site = (VirtualCallSite) siteIt.next();
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
                    if( !sootcls.isApplicationClass() ) {
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

    /* End of public methods. */

    private void addVirtualCallSite( Stmt s, SootMethod m, Local receiver,
            InstanceInvokeExpr iie, NumberedString subSig, Kind kind ) {
        List<VirtualCallSite> sites = (List<VirtualCallSite>) receiverToSites.get(receiver);
        if (sites == null) {
            receiverToSites.put(receiver, sites = new ArrayList<VirtualCallSite>());
            List<Local> receivers = (List<Local>) methodToReceivers.get(m);
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
        for( Iterator<Unit> sIt = b.getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
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
                    if( subSig == sigExecute  ) {
                        addVirtualCallSite( s, m, receiver, iie, sigDoInBackground,
                                Kind.ASYNCTASK );
                    }
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
        for( Iterator<Unit> sIt = b.getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
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
            Edge e = (Edge) it.next();
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
        if( cls.declaresMethod( methodSubSig ) ) {
            addEdge( src, stmt, cls.getMethod( methodSubSig ), kind );
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
    protected final NumberedString sigObjRun = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Object run()" );
    protected final NumberedString sigDoInBackground = Scene.v().getSubSigNumberer().
            findOrAdd( "java.lang.Object doInBackground(java.lang.Object[])" );
    protected final NumberedString sigForName = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Class forName(java.lang.String)" );
    protected final RefType clRunnable = RefType.v("java.lang.Runnable");
    protected final RefType clAsyncTask = RefType.v("android.os.AsyncTask");
    
}

