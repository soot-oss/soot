/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 John Jorgensen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.util.cfgcmd;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import soot.Singletons;
import soot.G;

/**
 * <p>A {@link ClassLoader} that loads specified classes from a
 * different class path than that given by the value of
 * <code>java.class.path</code> in {@link System#getProperties()}.</p>
 *
 * <p>This class is part of Soot's test infrastructure. It allows
 * loading multiple implementations of a class with a given
 * name, and was written to compare different
 * implementations of Soot's CFG representations.</p>
 */
public class AltClassLoader extends ClassLoader {

  private final static boolean DEBUG = false;

  private String[] locations;	// Locations in the alternate
				// classpath.
  private final Map<String, Class> alreadyFound = new HashMap<String, Class>(); // Maps from already loaded
					    // classnames to their
					    // Class objects.

  private final Map<String, String> nameToMangledName = new HashMap<String, String>();// Maps from the names
						// of classes to be
						// loaded from the alternate
						// classpath to mangled
						// names to use for them.

  private final Map<String, String> mangledNameToName = new HashMap<String, String>();// Maps from the mangled names
						// of classes back to their
						// original names.



  /**
   * Constructs an <code>AltClassLoader</code> for inclusion in Soot's
   * global variable manager, {@link G}.
   *
   * @param g guarantees that the constructor may only be called from
   * {@link Singletons}.
   */
  public AltClassLoader(Singletons.Global g) {}


  /**
   * Returns the single instance of <code>AltClassLoader</code>, which
   * loads classes from the classpath set by the most recent call to
   * its {@link #setAltClassPath}.
   *
   * @return Soot's <code>AltClassLoader</code>.
   */
  public static AltClassLoader v() {
    return G.v().soot_util_cfgcmd_AltClassLoader();
  }


  /**
   * Sets the list of locations in the alternate classpath.
   *
   * @param classPath A list of directories and jar files to
   * search for class files, delimited by 
   * {@link File#pathSeparator}.
   */
  public void setAltClassPath(String altClassPath) {
    List<String> locationList = new LinkedList<String>();
    for (StringTokenizer tokens = 
	   new StringTokenizer(altClassPath, File.pathSeparator, false);
	 tokens.hasMoreTokens() ; ) {
	String location = tokens.nextToken();
      locationList.add(location);
    }
    locations = new String[locationList.size()];
    locations = locationList.toArray(locations);
  }


  /**
   * Specifies the set of class names that the <code>AltClassLoader</code>
   * should load from the alternate classpath instead of the 
   * regular classpath.
   *
   * @param classNames[] an array containing the names of classes to
   * be loaded from the AltClassLoader.
   */
  public void setAltClasses(String[] classNames) {
    nameToMangledName.clear();
    for (String origName : classNames) {
      String mangledName = mangleName(origName);
      nameToMangledName.put(origName, mangledName);
      mangledNameToName.put(mangledName, origName);
    }
  }

  /**
   * Mangles a classname so that it will not be found on the system
   * classpath by our parent class loader, even if there is a class
   * with the original name there.  We use a crude heuristic to do this that
   * happens to work with the names we have needed to mangle to date.
   * The heuristic requires that <code>origName</code> include at least
   * two dots (i.e., the class must be in a package, where
   * the package name has at least two components). More sophisticated
   * possibilities certainly exist, but they would require 
   * more thorough parsing of the class file.
   *
   * @param origName the name to be mangled.
   * @return the mangled name.
   * @throws IllegalArgumentException if <code>origName</code> is not
   * amenable to our crude mangling.
   */
  private static String mangleName(String origName) 
  throws IllegalArgumentException {
    final char dot = '.';
    final char dotReplacement = '_';
    StringBuffer mangledName = new StringBuffer(origName);
    int replacements = 0;
    int lastDot = origName.lastIndexOf(dot);
    for (int nextDot = lastDot; 
	 (nextDot = origName.lastIndexOf(dot, nextDot - 1)) >= 0; ) {
      mangledName.setCharAt(nextDot, dotReplacement);
      replacements++;
    }
    if (replacements <= 0) {
      throw new IllegalArgumentException("AltClassLoader.mangleName()'s crude classname mangling cannot deal with " + origName);
    }
    return mangledName.toString();
  }
      

  /**
   * <p>
   * Loads a class from either the regular classpath, or the alternate
   * classpath, depending on whether it looks like we have already
   * mangled its name.</p>
   *
   * <p> This method follows the steps provided by <a
   * href="http://www.javaworld.com/javaworld/jw-03-2000/jw-03-classload.html#resources">Ken
   * McCrary's ClasssLoader tutorial</a>.</p>
   *
   * @param maybeMangledName A string from which the desired class's
   * name can be determined.  It may have been mangled by {@link
   * AltClassLoader#loadClass(String) AltClassLoader.loadClass()} so
   * that the regular <code>ClassLoader</code> to which we are
   * delegating won't load the class from the regular classpath.
   * @return the loaded class.
   * @throws ClassNotFoundException if the class cannot be loaded.
   *
   */
  protected Class findClass(String maybeMangledName)
    throws ClassNotFoundException {
    if (DEBUG) {
      G.v().out.println("AltClassLoader.findClass(" + maybeMangledName + ')');
    }

    Class result = alreadyFound.get(maybeMangledName);
    if (result != null) {
      return result;
    }

    String name = mangledNameToName.get(maybeMangledName);
    if (name == null) {
      name = maybeMangledName;
    }
    String pathTail = "/" + name.replace('.', File.separatorChar) + ".class";

    for (String element : locations) {
      String path = element + pathTail;
      try {
	FileInputStream stream = new FileInputStream(path);
	byte[] classBytes = new byte[stream.available()];
	stream.read(classBytes);
	replaceAltClassNames(classBytes);
	result = defineClass(maybeMangledName, classBytes, 0, classBytes.length);
	alreadyFound.put(maybeMangledName, result);
	return result;
      } catch (java.io.IOException e) {
	// Try the next location.
      } catch (ClassFormatError e) {
	if (DEBUG) {
	  e.printStackTrace(G.v().out);
	}
	// Try the next location.
      }
    }
    throw new ClassNotFoundException("Unable to find class" + name +
				     " in alternate classpath");
  }


  /**
   * <p>Loads a class, from the alternate classpath if the class's
   * name has been included in the list of alternate classes with
   * {@link #setAltClasses(String[]) setAltClasses()}, from the
   * regular system classpath otherwise.  When a alternate class is
   * loaded, its references to other alternate classes are also
   * resolved to the alternate classpath.
   * 
   * @param name the name of the class to load.
   * @return the loaded class.
   * @throws ClassNotFoundException if the class cannot be loaded.
   */
  public Class loadClass(String name) 
  throws ClassNotFoundException {
    if (DEBUG) {
      G.v().out.println("AltClassLoader.loadClass(" + name + ")");
    }

    String nameForParent = nameToMangledName.get(name);
    if (nameForParent == null) {
      // This is not an alternate class
      nameForParent = name;
    }

    if (DEBUG) {
      G.v().out.println("AltClassLoader.loadClass asking parent for " + 
			nameForParent);
    }
    return super.loadClass(nameForParent, false);
  }


  /**
   * Replaces any occurrences in <code>classBytes</code> of
   * classnames to be loaded from the alternate class path with
   * the corresponding mangled names.  Of course we should really
   * parse the class pool properly, since the simple-minded, brute
   * force replacment done here could produce problems with some
   * combinations of classnames and class contents. But we've got away
   * with this so far!
   */
  private void replaceAltClassNames(byte[] classBytes) {
    for (Object element : nameToMangledName.entrySet()) {
      Map.Entry entry = (Map.Entry) element;
      String origName = (String) entry.getKey();
      origName = origName.replace('.', '/');
      String mangledName = (String) entry.getValue();
      mangledName = mangledName.replace('.', '/');
      findAndReplace(classBytes, stringToUtf8Pattern(origName), 
		     stringToUtf8Pattern(mangledName));
      findAndReplace(classBytes, stringToTypeStringPattern(origName), 
		     stringToTypeStringPattern(mangledName));
    }
  }

  /**
   * Returns the bytes that correspond to a 
   * CONSTANT_Utf8 constant pool entry containing
   * the passed string.
   */
  private static byte[] stringToUtf8Pattern(String s) {
    byte[] origBytes = s.getBytes();
    int length = origBytes.length;
    final byte CONSTANT_Utf8 = 1;
    byte[] result = new byte[length + 3];
    result[0] = CONSTANT_Utf8;
    result[1] = (byte) (length & 0xff00);
    result[2] = (byte) (length & 0x00ff);
    for (int i = 0; i < length; i++) {
      result[i+3] = origBytes[i];
    }
    return result;
  }

  /**
   * Returns the bytes that correspond to a type signature string
   * containing the passed string.
   */
  private static byte[] stringToTypeStringPattern(String s) {
    byte[] origBytes = s.getBytes();
    int length = origBytes.length;
    byte[] result = new byte[length + 2];
    result[0] = (byte) 'L';
    for (int i = 0; i < length; i++) {
      result[i+1] = origBytes[i];
    }
    result[length+1] = (byte) ';';
    return result;
  }


  /**
   * Replaces all occurrences of the <code>pattern</code> in <code>text</code>
   * with <code>replacement</code>. 
   * @throws IllegalArgumentException if the lengths of <code>text</code>
   * and <code>replacement</code> differ.
   */
  private static void findAndReplace(byte[] text, byte[] pattern, 
			      byte[] replacement) 
  throws IllegalArgumentException {
    int patternLength = pattern.length;
    if (patternLength != replacement.length) {
      throw new IllegalArgumentException("findAndReplace(): The lengths of the pattern and replacement must match.");
    }
    int match = 0;
    while ((match = findMatch(text, pattern, match)) >= 0) {
      replace(text, replacement, match);
      match += patternLength;
    }
  }


  /**
   * A naive string-searching algorithm for finding a pattern
   * in a byte array. 
   *
   * @param text the array to search in.
   * @param pattern the string of bytes to search for.
   * @param start the first position in text to search (0-based).
   * @return the index in text where the first occurrence of
   *         <code>pattern</code> in <code>text</code> after <code>start</code>
   *         begins.  Returns -1 if <code>pattern</code> does not occur
   *         in <code>text</code> after <code>start</code>.
   */
  private static int findMatch(byte[] text, byte[] pattern, int start) {
    int textLength = text.length;
    int patternLength = pattern.length;
    nextBase:
    for (int base = start; base < textLength; base++) {
      for (int t = base, p = 0; p < patternLength; t++, p++) {
	if (text[t] != pattern[p]) {
	  continue nextBase;
	}
      }
      return base;
    }
    return -1;
  }

  /**
   * Replace the <code>replacement.length</code> bytes in <code>text</code>
   * starting at <code>start</code> with the bytes in <code>replacement</code>.
   * @throws ArrayIndexOutOfBounds if there are not
   * <code>replacement.length</code> remaining after <code>text[start]</code>.
   */
  private static void replace(byte[] text, byte[] replacement, int start) {
    for (int t=start, p = 0; p < replacement.length; t++, p++) {
      text[t] = replacement[p];
    }
  }
    
  /**
   * <p>A main() entry for basic unit testing.</p>
   *
   * <p>Usage: path class ...</p>
   */
  public static void main(String[] argv) throws ClassNotFoundException {
    AltClassLoader.v().setAltClassPath(argv[0]);
    for (int i = 1; i < argv.length; i++) {
      AltClassLoader.v().setAltClasses(new String[] {
	argv[i]
      });
      G.v().out.println("main() loadClass(" + argv[i] + ")");
      AltClassLoader.v().loadClass(argv[i]);
    }
  }
    
}
