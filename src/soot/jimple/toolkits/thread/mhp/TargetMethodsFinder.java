package soot.jimple.toolkits.thread.mhp;

import soot.*;
import soot.jimple.toolkits.callgraph.*;
import java.util.*;

/**
 * Assembles a list of target methods for a given unit and call graph,
 * filtering out static initializers and optionally native methods.
 * Can optionally throw a runtime exception if the list is null.
 */
public class TargetMethodsFinder{
	
	public List<SootMethod> find(Unit unit, CallGraph cg, boolean canBeNullList, boolean canBeNative){
		List<SootMethod> target = new ArrayList<SootMethod>(); 
		Iterator<Edge> it = cg.edgesOutOf(unit);
		while (it.hasNext()){
			Edge edge = it.next();
			SootMethod targetMethod = edge.tgt();
			if (targetMethod.isNative() && !canBeNative )
				continue;
			if (edge.kind() == Kind.CLINIT )
				continue;
			target.add(targetMethod);
		}
		if (target.size() < 1 && !canBeNullList){
			throw new RuntimeException("No target method for: "+unit);
		}
		return target;
	}
}

