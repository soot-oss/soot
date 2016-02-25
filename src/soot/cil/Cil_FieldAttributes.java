package soot.cil;

import java.util.HashMap;
import java.util.Map;

class Cil_FieldAttributes {
	public static final Map<String, Integer> attributes;

	static {
		attributes = new HashMap<String, Integer>();
		attributes.put("private", 0x0002);
		attributes.put("protected", 0x0004);
		attributes.put("public", 0x0001);
		attributes.put("static", 0x0008);
		attributes.put("sealed", 0x0010); // sealed == final
		attributes.put("literal", 0x0010);
		attributes.put("initonly", 0x0010);

		// Field attributes not considers currently
		attributes.put("assembly", 0x0000);
		attributes.put("famandassem", 0x0000);
		attributes.put("family", 0x0000);
		attributes.put("famorassem", 0x0000);
		attributes.put("initonly", 0x0000);
		attributes.put("literal", 0x0000);
		attributes.put("marshal", 0x0000);
		attributes.put("notserialized", 0x0000);
		attributes.put("compilercontrolled", 0x0000);
		attributes.put("rtspecialname", 0x0000);
		attributes.put("specialname", 0x0000);
	}
}
