package soot.cil;

import java.util.List;

import soot.G;
import soot.SootClass;
import soot.SootField;
import soot.SootResolver;
import soot.Type;

class Cil_FieldParser {
	private SootField field;
	private String fieldName;
	private Type fieldType;
	private int field_modifier = 0;

	private List<String> classConstFieldList;

	public Cil_FieldParser(List<String> classConstFieldList) {
		this.classConstFieldList = classConstFieldList;
	}

	private String parseLiteralFields(String str, String type) {
		String ret = "";

		String[] tmp = str.split("\\s+");
		ret += tmp[0] + " " + tmp[1] + " ";

		String value = tmp[2];

		type = Cil_Utils.removeAssemblyRefs(type);
		type = Cil_Utils.getSootType(type.trim()).toString();

		if (type.equals("int")) {
			value = value.substring(value.indexOf("(") + 1, value.lastIndexOf(")"));
			value = value.replace("0x", "");
			long _value = Long.parseLong(value, 16);
			value = Long.toString(_value);
		} else if (type.equals("float")) {
			value = value.substring(value.indexOf("(") + 1, value.lastIndexOf(")"));
			if(value.contains("0x")) {
				value = value.replace("0x", "");
				Long l = Long.parseLong(value, 16);
				float _value = Float.intBitsToFloat(l.intValue());
				value = Float.toString(_value);
			} else {
				float _value = Float.parseFloat(value);
				value = Float.toString(_value);
			}
		} else if (type.equals("double")) {
			value = value.substring(value.indexOf("(") + 1, value.lastIndexOf(")"));
			if(value.contains("0x")) {
				//value = value.replace("0x", "");
				value += "L";
				//Long l = Long.parseLong(value, 16);
				//double _value = Double.longBitsToDouble(l.longValue());
				//value = Double.toString(_value);
			} else {
				double _value = Double.parseDouble(value);
				value = Double.toString(_value);
			}
		} else if (type.equals("char")) {
			value = value.substring(value.indexOf("(") + 1, value.lastIndexOf(")"));
			value = value.replace("0x", "");
			long _value = Long.parseLong(value, 16);
			value = Long.toString(_value);
		} else if (type.equals("bool")) {
			// nothing to do
		} else if (type.equals("System.String")) {
			// nothing to do
		} else {
			// class
			value = "null";
		}

		ret += value;

		return ret;
	}

	public void run(String line) {
		String field_line = Cil_Utils.removeComments(line);
		String type = "";
		
		field_line = Cil_Utils.removeTypeAttributes(field_line);
		field_line = Cil_Utils.replaceGenericPlaceholders(field_line);
		
		if (field_line.contains("literal")) {
			field_line = Cil_Utils.removeTypePrefixes(field_line);

			String firstPart = field_line.substring(0, field_line.indexOf("=")).trim();
			String[] tmp = firstPart.split("\\s+");
			String _type = tmp[tmp.length - 2];
			String secondPart = field_line.substring(firstPart.lastIndexOf(" "), field_line.length()).trim();
			
			_type = G.v().soot_cil_CilNameMangling().doNameMangling(_type);
			
			secondPart = this.parseLiteralFields(secondPart, _type);
			this.classConstFieldList.add(secondPart);
			field_line = firstPart;
		}
		
		String[] field_tokens = field_line.split("\\s+");
		
		if (field_line.contains("`")) {
			field_line = Cil_Utils.removeTokenFromString(field_line, "class");
			field_line = Cil_Utils.removeTokenFromString(field_line, "valuetype");

			//if (field_line.contains("/")) {
			String[] tokens = field_line.split("\\s+");
			String typeName = tokens[tokens.length - 2];
			String arrayPart = "";
			if(typeName.endsWith("]")) {
				arrayPart = typeName.substring(typeName.indexOf("["), typeName.length());
				typeName = typeName.substring(0, typeName.indexOf("["));
			}
			
			SootClass typeClass = SootResolver.v().makeClassRef(typeName);
			typeName = typeClass.getName() + arrayPart;
			fieldType = Cil_Utils.getSootType(typeName);

		} else {			
			type = field_tokens[field_tokens.length - 2];
			fieldType = Cil_Utils.getSootType(type);
		}

		this.fieldName = field_tokens[field_tokens.length - 1];

		for (int i = 0; i < field_tokens.length; ++i) {
			String token = field_tokens[i].trim();

			if (!token.startsWith("/") && !token.equals(".field") && !token.isEmpty()) {
				if (Cil_FieldAttributes.attributes.containsKey(token)) {
					this.field_modifier = this.field_modifier | Cil_FieldAttributes.attributes.get(token);
				}
			}
		}

		this.field = new SootField(this.fieldName, this.fieldType, this.field_modifier);
	}

	public SootField getSootField() {
		return this.field;
	}

}
