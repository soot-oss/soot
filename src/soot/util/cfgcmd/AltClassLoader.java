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
import java.util.Iterator;
import java.lang.reflect.*;
import soot.Singletons;
import soot.G;

/**
 * <p>This class is part of Soot's test infrastructure. It provides
 * the capacity to load specific classes from a different class path
 * than that given by the value of <tt>java.class.path</tt> in {@link
 * System#getProperties()}.  This allows the loading of different
 * implementations of a class with a given name.</p>
 *
 * <p>The intended use is for the comparison of different
 * implementations of Soot CFG representations.</p>
 */
public class AltClassLoader extends ClassLoader {

  private final static boolean DEBUG = false;

  private String[] locations;	// Locations in the alternate
				// classpath.
  private Map alreadyFound = new HashMap(); // Maps from already loaded
					    // classnames to their
					    // Class objects.

  private Map nameToKludgedName = new HashMap();// Maps from the names
						// of classes to be
						// loaded from the alternate
						// classpath to kludged
						// names to use for them.

  private Map kludgedNameToName = new HashMap();// Maps from the kludged names
						// of classes back to their
						// original names.



  /**
   * Constructs an <tt>AltClassLoader</tt> for inclusion in Soot's
   * global variable manager, {@link G}.
   *
   * @param g guarantees that the constructor may only be called from
   * {@link Singletons}.
   */
  public AltClassLoader(Singletons.Global g) {}


  /**
   * Returns the single instance of <tt>AltClassLoader</tt>, which
   * loads classes from the classpath set by the most recent call to
   * its {@link #setAltClassPath}.
   *
   * @return Soot's <tt>AltClassLoader</tt>.
   */
  public static AltClassLoader v() {
    return G.v().soot_util_cfgcmd_AltClassLoader();
  }


  /**
   * Sets the list of locations in the alternate classpath.
   *
   * @param classPath A list of directories and jar files to
   * search for class files, deliminated by 
   * {@link File#pathSeparator}.
   * @throws java.net.MalformedURLException if some element of the 
   */
  public void setAltClassPath(String altClassPath) {
    List locationList = new LinkedList();
    for (StringTokenizer tokens = 
	   new StringTokenizer(altClassPath, File.pathSeparator, false);
	 tokens.hasMoreTokens() ; ) {
      String location = new String(tokens.nextToken());
      locationList.add(location);
    }
    locations = new String[locationList.size()];
    locations = (String[]) locationList.toArray(locations);
  }


  /**
   * Specifies the set of class names that the <tt>AltClassLoader</tt>
   * should load from the alternate classpath instead of the 
   * regular classpath.
   *
   * @param classNames[] an array containing the names of classes to
   * be loaded from the AltClassLoader.
   */
  public void setAltClasses(String[] classNames) {
    nameToKludgedName.clear();
    for (int i = 0; i < classNames.length; i++) {
      String origName = classNames[i];
      String kludgedName = kludgeName(origName);
      nameToKludgedName.put(origName, kludgedName);
      kludgedNameToName.put(kludgedName, origName);
    }
  }

  /**
   * Mangles a classname so that it will not be found on the system
   * classpath loader by our parent class loader, even if there is a
   * class with this name there.  We use a crude heuristic to do this
   * that happens to work with the names we have needed to kludge to
   * date, which requires that <tt>origName<tt> have at least two dots
   * in its name (i.e., the class must be in a package, where the
   * package name has at least two components). More sophisticated
   * possibilities certainly exist, but they require proper parsing of
   * the class file.
   *
   * @param origName the name to be mangled.
   * @return the mangled name.
   * @throws IllegalArgumentException if <tt>origName</tt> is not
   * amenable to our crude mangling.
   */
  private static String kludgeName(String origName) 
  throws IllegalArgumentException {
    final char dot = '.';
    final char dotReplacement = '_';
    StringBuffer kludgedName = new StringBuffer(origName);
    int replacements = 0;
    int lastDot = origName.lastIndexOf(dot);
    for (int nextDot = lastDot; 
	 (nextDot = origName.lastIndexOf(dot, nextDot - 1)) >= 0; ) {
      kludgedName.setCharAt(nextDot, dotReplacement);
      replacements++;
    }
    if (replacements <= 0) {
      throw new IllegalArgumentException("AltClassLoader.kludgeName()'s crude classname mangling cannot deal with " + origName);
    }
    return kludgedName.toString();
  }
      

  /**
   * <p>
   * Loads a class from either the regular classpath, or the alternate
   * classpath, depending on whether it looks like we have already
   * kludged its name.</p>
   *
   * @param maybeKludgedName A string from which the desired class's name
   * can be determined.  The name has been obfuscated by 
   * {@link loadAltClass()} so that the regular <tt>ClassLoader</tt>
   * to which we are delegating won't load any class with the
   * same name from the regular classpath.
   *
   * <p>
   * This method follows the steps provided by Ken McCrary's
   * tutorial at
   * http://www.javaworld.com/javaworld/jw-03-2000/jw-03-classload.html#resources
   * </p>
   */
  protected Class findClass(String maybeKludgedName)
    throws ClassNotFoundException {
    if (DEBUG) {
      G.v().out.println("KludgedClassLoader asked for " + maybeKludgedName);
    }

    Class result = (Class) alreadyFound.get(maybeKludgedName);
    if (result != null) {
      return result;
    }

    String name = (String) kludgedNameToName.get(maybeKludgedName);
    if (name == null) {
      name = maybeKludgedName;
    }
    String pathTail = "/" + name.replace('.', File.separatorChar) + ".class";

    for (int i = 0; i < locations.length; i++) {
      String path = locations[i] + pathTail;
      try {
	FileInputStream stream = new FileInputStream(path);
	byte[] classBytes = new byte[stream.available()];
	stream.read(classBytes);
	replaceAltClassNames(classBytes);
	result = defineClass(maybeKludgedName, classBytes, 0, classBytes.length);
	alreadyFound.put(maybeKludgedName, result);
	return result;
      } catch (java.io.IOException e) {
	// Try the next location.
      } catch (ClassFormatError e) {
	if (DEBUG) {
	  e.printStackTrace(G.v().out);
	}
      }
    }
    throw new ClassNotFoundException("Unable to find class" + name +
				     "in alternate classpath");
  }


  /**
   * <p>Loads a class, from the alternate classpath if the class's
   * name has been added to the list of alternate classes with 
   * {@link #addAltClass()}; from the regular system classpath
   * otherwise.  When a alternate class is loaded, its references to
   * other alternate classes are also resolved to the alternate
   * classpath.
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

    String nameForParent = (String) nameToKludgedName.get(name);
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
   * Replaces any occurrences of the classnames to be loaded from the
   * alternate class path that occur in <tt>classBytes</tt> with the
   * corresponding kludged name.  Of course we should really parse
   * the class pool properly, since the simple-minded, brute
   * force replacment done here could produce problems with some
   * combinations of classnames and class contents. But we've got
   * away with this so far!.
   */
  private void replaceAltClassNames(byte[] classBytes) {
    for (Iterator it = nameToKludgedName.entrySet().iterator();
	 it.hasNext(); ) {
      Map.Entry entry = (Map.Entry) it.next();
      String origName = (String) entry.getKey();
      origName = origName.replace('.', '/');
      String kludgedName = (String) entry.getValue();
      kludgedName = kludgedName.replace('.', '/');
      findAndReplace(classBytes, stringToUtf8Pattern(origName), 
		     stringToUtf8Pattern(kludgedName));
      findAndReplace(classBytes, stringToTypeStringPattern(origName), 
		     stringToTypeStringPattern(kludgedName));
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
   * Replaces all occurrences of the <tt>pattern</tt> in <tt>text</tt>
   * with <tt>replacement</tt>. 
   * @throws IllegalArgumentException if the lengths of <tt>text</tt>
   * and <tt>replacement</tt> differ.
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
   *         <tt>pattern</tt> in <tt>text</tt> after <tt>start</tt>
   *         begins.  Returns -1 if <tt>pattern</tt> does not occur
   *         in <tt>text</tt> after <tt>start</tt>.
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
   * Replace the <tt>pattern.length</tt> bytes in <tt>text</tt>
   * starting at <tt>start</tt> with the bytes in <tt>pattern</tt>.
   * @throws ArrayIndexOutOfBounds if there are not
   * <tt>pattern.length</tt> remaining after <tt>text[start]</tt>.
   */
  private static void replace(byte[] text, byte[] pattern, int start) {
    int patternLength = pattern.length;
    for (int t=start, p = 0; p < pattern.length; t++, p++) {
      text[t] = pattern[p];
    }
  }

  // A main() entry for basic unit testing.
  // Usage: path class ...
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
