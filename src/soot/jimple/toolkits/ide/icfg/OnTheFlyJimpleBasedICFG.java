package soot.jimple.toolkits.ide.icfg;

import heros.SynchronizedBy;
import heros.solver.IDESolver;

import java.util.Collections;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.FastHierarchy;
import soot.Local;
import soot.NullType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.pointer.LocalMustNotAliasAnalysis;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


public class OnTheFlyJimpleBasedICFG extends AbstractJimpleBasedICFG {

	private static final SootClass OBJECT = Scene.v().getSootClass("java.lang.Object");

	@SynchronizedBy("by use of synchronized LoadingCache class")
	protected final LoadingCache<Body,LocalMustNotAliasAnalysis> bodyToLMNAA = IDESolver.DEFAULT_CACHE_BUILDER.build( new CacheLoader<Body,LocalMustNotAliasAnalysis>() {
					@Override
					public LocalMustNotAliasAnalysis load(Body body) throws Exception {
						return new LocalMustNotAliasAnalysis(getOrCreateUnitGraph(body), body);
					}
				});
	
	@SynchronizedBy("by use of synchronized LoadingCache class")
	protected final LoadingCache<Unit,Set<SootMethod>> unitToCallees =
			IDESolver.DEFAULT_CACHE_BUILDER.build( new CacheLoader<Unit,Set<SootMethod>>() {
				@Override
				public Set<SootMethod> load(Unit u) throws Exception {
					Stmt stmt = (Stmt)u;
					InvokeExpr ie = stmt.getInvokeExpr();
					FastHierarchy fastHierarchy = Scene.v().getFastHierarchy();
					if(ie instanceof InstanceInvokeExpr) {
						if(ie instanceof SpecialInvokeExpr) {
							//special
							return Collections.singleton(ie.getMethod());
						} else {
							//virtual and interface
							InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
							Local base = (Local) iie.getBase();
							RefType concreteType = bodyToLMNAA.getUnchecked(unitToOwner.get(u)).concreteType(base, stmt);
							if(concreteType!=null) {
								//the base variable definitely points to a single concrete type 
								SootMethod singleTargetMethod = fastHierarchy.resolveConcreteDispatch(concreteType.getSootClass(), iie.getMethod());
								return Collections.singleton(singleTargetMethod);
							} else {
								SootClass baseTypeClass;
								if(base.getType() instanceof RefType) {
									RefType refType = (RefType) base.getType();
									baseTypeClass = refType.getSootClass();
								} else if(base.getType() instanceof ArrayType) {
									baseTypeClass = OBJECT;
								} else if(base.getType() instanceof NullType) {
									//if the base is definitely null then there is no call target
									return Collections.emptySet();
								} else {
									throw new InternalError("Unexpected base type:"+base.getType());
								}
								return fastHierarchy.resolveAbstractDispatch(baseTypeClass, iie.getMethod());
							}
						}
					} else {
						//static
						return Collections.singleton(ie.getMethod());
					}
				}
			});
	
	@Override
	public Set<SootMethod> getCalleesOfCallAt(Unit u) {
		return unitToCallees.getUnchecked(u);
	}

	@Override
	public Set<Unit> getCallersOf(SootMethod m) {
		return null;
	}
	
}
