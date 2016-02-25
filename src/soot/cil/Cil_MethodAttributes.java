package soot.cil;

import java.util.HashMap;
import java.util.Map;

class Cil_MethodAttributes {
	public static final Map<String, Integer> attributes;
	
	static{
		attributes = new HashMap<String, Integer>();		
		attributes.put("private",	0x0002);
		attributes.put("protected", 0x0004);
		attributes.put("public", 	0x0001);
		attributes.put("static", 	0x0008);
		attributes.put("sealed", 	0x0010); // sealed==final
		attributes.put("native", 	0x0100);
	}
}
