package soot.tagkit;

import java.util.HashMap;
import java.util.Map;

public class TryCatchTag implements soot.tagkit.Tag {
	
	public static final String NAME = "TryCatchTag"; 
	
	protected Map<soot.Unit,soot.Unit> handlerUnitToFallThroughUnit = new HashMap<soot.Unit, soot.Unit>();
	
	public void register(soot.Unit handler, soot.Unit fallThrough) {
		handlerUnitToFallThroughUnit.put(handler, fallThrough);
	}
	
	public soot.Unit getFallThroughUnitOf(soot.Unit handlerUnit) {
		return handlerUnitToFallThroughUnit.get(handlerUnit);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public byte[] getValue() throws AttributeValueException {
		throw new UnsupportedOperationException();
	}
	
}