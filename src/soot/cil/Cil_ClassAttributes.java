package soot.cil;

import java.util.HashMap;
import java.util.Map;

class Cil_ClassAttributes {
	public static final Map<String, Integer> attributes;
	
	static{
		attributes = new HashMap<String, Integer>();
		attributes.put("abstract", 	0x0400);
		attributes.put("sealed", 	0x0010); // sealed == final
		attributes.put("interface", 0x0200);
		
		attributes.put("private",	0x0002);
		attributes.put("protected", 0x0004);
		attributes.put("public", 	0x0001);
		
		// TODO unhandled ClassAtributes
		attributes.put("ansi", 		0x0000);
		attributes.put("auto", 		0x0000);
		attributes.put("autochar", 	0x0000);
		attributes.put("beforefieldinit", 		0x0000);
		attributes.put("explicit", 	0x0000);
		
		attributes.put("rtspecialname", 	0x0000);
		attributes.put("sequential", 	0x0000);
		attributes.put("autochar", 	0x0000);
		attributes.put("serializable", 	0x0000);
		attributes.put("specialname", 	0x0000);
		attributes.put("unicode", 	0x0000);
		
		attributes.put("nested", 	0x0000);
		attributes.put("famorassem", 	0x0000);
		attributes.put("famandassem", 	0x0000);
		attributes.put("family", 	0x0000);
		attributes.put("assembly", 	0x0000);
		
		attributes.put("+", 	0x0000);
		attributes.put("-", 	0x0000);
		attributes.put("class", 	0x0000);
		attributes.put("valuetype", 	0x0000);
		attributes.put(".ctor", 	0x0000);
	}
}
