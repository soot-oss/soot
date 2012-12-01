package soot.jimple.toolkits.ide;

import java.io.ObjectOutputStream;

import soot.SootMethod;
import soot.Unit;
import heros.EdgeFunction;
import heros.debugsupport.NewEdgeSerializer;
import heros.debugsupport.SerializableEdgeData;

public class EdgeSerializer<D, V> extends NewEdgeSerializer<soot.SootMethod, D, soot.Unit, V> {

	public EdgeSerializer(ObjectOutputStream oos) {
		super(oos);
	}

	@Override
	public SerializableEdgeData serializeJumpFunction(SootMethod method, D sourceVal,Unit target, D targetVal, EdgeFunction<V> f) {
		return new SerializableEdgeData(method.getDeclaringClass().getName());
	}

}
