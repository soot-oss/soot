package soot.jimple.toolkits.ide;

import soot.SootMethod;
import soot.Unit;
import heros.EdgeFunction;
import heros.debugsupport.NewEdgeSerializer;
import heros.debugsupport.SerializableEdgeData;

public class EdgeSerializer<D, V> implements NewEdgeSerializer<soot.SootMethod, D, soot.Unit, V> {

	@Override
	public SerializableEdgeData newJumpFunction(SootMethod method, D sourceVal,Unit target, D targetVal, EdgeFunction<V> f) {
		return new SerializableEdgeData(method.getDeclaringClass().getName());
	}

}
