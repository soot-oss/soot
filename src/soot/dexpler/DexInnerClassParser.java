package soot.dexpler;

import soot.tagkit.InnerClassTag;

/**
 * Utility class for handling inner/outer class references in Dalvik
 * 
 * @author Steven Arzt
 *
 */
public class DexInnerClassParser {
	
	/**
	 * Gets the name of the outer class (in Soot notation) from the given
	 * InnerClassTag
	 * @param icTag The InnerClassTag from which to read the name of the outer
	 * class
	 * @return The nam,e of the outer class (in Soot notation) as specified in
	 * the tag. If the specification is invalid, null is returned.
	 */
	public static String getOuterClassNameFromTag(InnerClassTag icTag) {
		String outerClass;
		
		if (icTag.getOuterClass() == null) { // anonymous and local classes
			String inner = icTag.getInnerClass().replaceAll("/", ".");
			if(inner.contains("$-")) {
				/* This is a special case for generated lambda classes of jack and jill compiler.
				 * Generated lambda classes may contain '$' which do not indicate an inner/outer 
				 * class separator if the '$' occurs after a inner class with a name starting with
				 * '-'. Thus we search for '$-' and anything after it including '-' is the inner
				 * classes name and anything before it is the outer classes name.
				 */
				outerClass = inner.substring(0, inner.indexOf("$-"));
			} else if(inner.contains("$")) {
				//remove everything after the last '$' including the last '$'
				outerClass = inner.substring(0, inner.lastIndexOf('$'));
			} else {
				// This tag points nowhere
				outerClass = null;
			}
		} else {
			outerClass = icTag.getOuterClass().replaceAll("/", ".");
		}
		
		return outerClass;
	}

}
