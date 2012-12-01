package soot.jimple.toolkits.ide;

import heros.EdgeFunction;
import heros.debugsupport.NewEdgeSerializer;
import heros.debugsupport.SerializableEdgeData;

import java.io.ObjectOutputStream;

import soot.Body;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.tagkit.Host;
import soot.tagkit.LineNumberTag;

public class EdgeSerializer<D, V> extends NewEdgeSerializer<soot.SootMethod, D, soot.Unit, V> {

	public EdgeSerializer(ObjectOutputStream oos) {
		super(oos);
	}

	@Override
	public SerializableEdgeData serializeJumpFunction(SootMethod method, D sourceVal,Unit target, D targetVal, EdgeFunction<V> f) {
		return new SerializableEdgeData(method.getDeclaringClass().getName(), getLine(method), getCol(method), getLine(target), getCol(target), f.toString());
	}

	private int getCol(Host h) {
		return -1;
	}

	private int getLine(SootMethod h) {
		if(h.hasActiveBody()) {
			Body b = h.getActiveBody();
			PatchingChain<Unit> units = b.getUnits();
			Unit first = units.getFirst();
			if(first!=null) {
				return getLine(first);
			}
		}
		return -1;
	}
	
	private int getLine(Unit u) {
		LineNumberTag lnTag = (LineNumberTag) u.getTag("LineNumberTag");
		if(lnTag!=null) {
			return lnTag.getLineNumber(); 
		}
		return -1;
	}

}
