package soot.util.annotations;

import java.lang.reflect.Array;

/**
 * Loads classes without relying on JBoss.
 * 
 * A general note on dynamically loading classes based on information from
 * target programs: You don't want that. It's a horrible idea, can lead to
 * severe security vulnerabilities, and is bad style. Trust me. But people seem
 * to need it, so this class makes it at least slightly less horrible than the
 * old way. It's still insane, but now it's insanity with style. Somehow.
 * 
 * @author Steven Arzt
 *
 */
public class ClassLoaderUtils {

	/**
	 * Don't call me. Just don't.
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClass(String className) throws ClassNotFoundException {
		return loadClass(className, true);
	}

	/**
	 * Don't call me. Just don't.
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClass(String className, boolean allowPrimitives) throws ClassNotFoundException {
		// Do we have a primitive class
		if (allowPrimitives) {
			switch (className) {
			case "B":
			case "byte":
				return Byte.TYPE;
			case "C":
			case "char":
				return Character.TYPE;
			case "D":
			case "double":
				return Double.TYPE;
			case "F":
			case "float":
				return Float.TYPE;
			case "I":
			case "int":
				return Integer.TYPE;
			case "J":
			case "long":
				return Long.TYPE;
			case "S":
			case "short":
				return Short.TYPE;
			case "Z":
			case "boolean":
				return Boolean.TYPE;
			case "V":
			case "void":
				return Void.TYPE;
			}
		}

		// JNI format
		if (className.startsWith("L") && className.endsWith(";"))
			return loadClass(className.substring(1, className.length() - 1), false);

		int arrayDimension = 0;
		while (className.charAt(arrayDimension) == '[')
			arrayDimension++;

		// If this isn't an array after all
		if (arrayDimension == 0)
			return Class.forName(className);

		// Load the array
		Class<?> baseClass = loadClass(className.substring(arrayDimension));
		return Array.newInstance(baseClass, new int[arrayDimension]).getClass();
	}

}
