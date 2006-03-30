/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
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

package soot.dava.toolkits.base.misc;

import soot.*;
import java.io.*;
import java.util.*;
import soot.util.*;
import soot.dava.*;
import java.util.jar.*;

public class PackageNamer
{
    public PackageNamer( Singletons.Global g ) {}
    public static PackageNamer v() { return G.v().soot_dava_toolkits_base_misc_PackageNamer(); }

    public boolean has_FixedNames(){	
    	return fixed;
    }

    public boolean use_ShortName( String fixedPackageName, String fixedShortClassName){
    	if (fixed == false)
    		return false;

    	if (fixedPackageName.equals( Dava.v().get_CurrentPackage()))
    		return true;

    	IterableSet packageContext = Dava.v().get_CurrentPackageContext();
    	if (packageContext == null)
    		return true;

    	packageContext = patch_PackageContext( packageContext);

    	int count = 0;
    	StringTokenizer st = new StringTokenizer( classPath, pathSep);
    	while (st.hasMoreTokens()) {
    		String classpathDir = st.nextToken();
    		
    		Iterator packIt = packageContext.iterator();
    		while (packIt.hasNext()) 
    			if (package_ContainsClass( classpathDir, (String) packIt.next(), fixedShortClassName))
    				if (++count > 1)
    					return false;
    	}

    	return true;
    }

    public String get_FixedClassName( String originalFullClassName)
    {
	if (fixed == false)
	    return originalFullClassName;

	Iterator it = appRoots.iterator();
	while (it.hasNext()) {
	    NameHolder h = (NameHolder) it.next();
	    if (h.contains_OriginalName( new StringTokenizer( originalFullClassName, "."), true))
		return h.get_FixedName( new StringTokenizer( originalFullClassName, "."), true);
	}
	
	return originalFullClassName.substring( originalFullClassName.lastIndexOf( ".") + 1);
    }

    public String get_FixedPackageName( String originalPackageName)
    {
	if (fixed == false)
	    return originalPackageName;

	if (originalPackageName.equals( ""))
	    return "";

	Iterator it = appRoots.iterator();
	while (it.hasNext()) {
	    NameHolder h = (NameHolder) it.next();
	    if (h.contains_OriginalName( new StringTokenizer( originalPackageName, "."), false))
		return h.get_FixedName( new StringTokenizer( originalPackageName, "."), false);
	}

	return originalPackageName;
    }


    private class NameHolder
    {
    	private String originalName, packageName, className;
    	private ArrayList children;
    	private NameHolder parent;
    	private boolean isClass;
	

    	public NameHolder( String name, NameHolder parent, boolean isClass)
    	{
    		originalName = name;
    		className = name;
    		packageName = name;
    		
    		this.parent = parent;
    		this.isClass = isClass;

    		children = new ArrayList();
    	}

    	public NameHolder get_Parent() {
			return parent;
		}

		public void set_ClassAttr() {
			isClass = true;
		}

		public boolean is_Class() {
			if (children.isEmpty())
				return true;
			else
				return isClass;
		}

		public boolean is_Package() {
			return (children.isEmpty() == false);
		}

		public String get_PackageName() {
			return packageName;
		}

		public String get_ClassName() {
			return className;
		}

		public void set_PackageName(String packageName) {
			this.packageName = packageName;
		}

		public void set_ClassName(String className) {
			this.className = className;
		}

		public String get_OriginalName() {
			return originalName;
		}

		public ArrayList get_Children() {
			return children;
		}

		public String get_FixedPackageName() {
			if (parent == null)
				return "";

			return parent.retrieve_FixedPackageName();
		}

	public String retrieve_FixedPackageName()
	{
	    if (parent == null)
		return packageName;
	    
	    return parent.get_FixedPackageName() + "." + packageName;
	}

	public String get_FixedName( StringTokenizer st, boolean forClass)
	{
	    if (st.nextToken().equals( originalName) == false)
		throw new RuntimeException( "Unable to resolve naming.");

	    return retrieve_FixedName( st, forClass);
	}

	private String retrieve_FixedName( StringTokenizer st, boolean forClass)
	{
	    if (st.hasMoreTokens() == false) {
		if (forClass)
		    return className;
		else
		    return packageName;
	    }

	    String subName = st.nextToken();
	    Iterator cit = children.iterator();
	    while (cit.hasNext()) {
	    	NameHolder h = (NameHolder) cit.next();

	    	if (h.get_OriginalName().equals( subName)) {
	    		if (forClass)
	    			return h.retrieve_FixedName( st, forClass);
	    		else 
	    			return packageName + "." + h.retrieve_FixedName( st, forClass);
	    	}
	    }
	    throw new RuntimeException( "Unable to resolve naming.");
	}

	public String get_OriginalPackageName( StringTokenizer st)
	{
	    if (st.hasMoreTokens() == false)
		return get_OriginalName();

	    String subName = st.nextToken();

	    Iterator cit = children.iterator();
	    while (cit.hasNext()) {
		NameHolder h = (NameHolder) cit.next();

		if (h.get_PackageName().equals( subName)) {
		    String originalSubPackageName = h.get_OriginalPackageName( st);

		    if (originalSubPackageName == null)
			return null;
		    else
			return get_OriginalName() + "." + originalSubPackageName;
		}
	    }

	    return null;
	}

	public boolean contains_OriginalName( StringTokenizer st, boolean forClass)
	{
	    if (get_OriginalName().equals( st.nextToken()) == false)
		return false;

	    return finds_OriginalName( st, forClass);
	}

	private boolean finds_OriginalName( StringTokenizer st, boolean forClass)
	{
	    if (st.hasMoreTokens() == false)
		return (((forClass) && (is_Class())) || ((!forClass) && (is_Package())));

	    String subName = st.nextToken();
	    Iterator cit = children.iterator();
	    while (cit.hasNext()) {
		NameHolder h = (NameHolder) cit.next();

		if (h.get_OriginalName().equals( subName))
		    return h.finds_OriginalName( st, forClass);
	    }
	    
	    return false;
	}

	public void fix_ClassNames( String curPackName)
	{
	    if ((is_Class()) && (keywords.contains( className))) {
		String tClassName = className;
		
		if (Character.isLowerCase( className.charAt( 0))) {
		    tClassName = tClassName.substring( 0, 1).toUpperCase() + tClassName.substring( 1);
		    className = tClassName;
		}
		
		for (int i=0; keywords.contains( className); i++)
		    className = tClassName + "_c" + i;
	    }

	    Iterator it = children.iterator();
	    while (it.hasNext())
		((NameHolder) it.next()).fix_ClassNames( curPackName + "." + packageName);
	}

	public void fix_PackageNames()
	{
	    if ((is_Package()) && (verify_PackageName() == false)) {
		String tPackageName = packageName;

		if (Character.isUpperCase( packageName.charAt( 0))) {
		    tPackageName = tPackageName.substring( 0, 1).toLowerCase() + tPackageName.substring( 1);
		    packageName = tPackageName;
		}
		
		for (int i=0; verify_PackageName() == false; i++)
		    packageName = tPackageName + "_p" + i;
	    }

	    Iterator it = children.iterator();
	    while (it.hasNext())
		((NameHolder) it.next()).fix_PackageNames();
	}


	public boolean verify_PackageName()
	{
	    return ((keywords.contains( packageName) == false) && 
		    (siblingClashes( packageName) == false) &&
		    ((is_Class() == false) || (className.equals( packageName) == false)));
	}

	public boolean siblingClashes( String name)
	{
	    Iterator it = null;

	    if (parent == null) {

		if (appRoots.contains( this))
		    it = appRoots.iterator();
		else
		    throw new RuntimeException( "Unable to find package siblings.");
	    }
	    else 
		it = parent.get_Children().iterator();

	    while (it.hasNext()) {
		NameHolder sibling = (NameHolder) it.next();

		if (sibling == this)
		    continue;

		if (((sibling.is_Package()) && (sibling.get_PackageName().equals( name))) ||
		    ((sibling.is_Class()) && (sibling.get_ClassName().equals( name))))
		    
		    return true;
	    }

	    return false;
	}

	public void dump( String indentation)
	{
	    G.v().out.print( indentation + "\"" + originalName + "\", \"" + packageName + "\", \"" + className + "\" (");
	    if (is_Class())
		G.v().out.print("c");
	    if (is_Package())
		G.v().out.print("p");
	    G.v().out.println( ")");

	    Iterator it = children.iterator();
	    while (it.hasNext())
	    	((NameHolder) it.next()).dump( indentation + "  ");
		}
    }

    private boolean fixed = false;
    private ArrayList appRoots = new ArrayList();
    private ArrayList otherRoots = new ArrayList();
    private HashSet keywords = new HashSet();
    private HashMap class2package = new HashMap();
    private char fileSep;
    private String classPath, pathSep;
    
    public void fixNames()
    {
	if (fixed)
	    return;

	String[] keywordArray =
	{
	    "abstract",	    "default",	    "if",            "private",	    "this",	    "boolean",
	    "do",	    "implements",	    "protected",	    "throw",	    "break",
	    "double",	    "import",	    "public",	    "throws",	    "byte",	    "else",
	    "instanceof",	    "return",	    "transient",	    "case",	    "extends",
	    "int",	    "short",	    "try",	    "catch",	    "final",	    "interface",
	    "static",	    "void",             "char",	    "finally",	    "long",	    "strictfp",
	    "volatile",	    "class",	    "float",	    "native",	    "super",	    "while",
	    "const",	    "for",	    "new",	    "switch",	    "continue",	    "goto",
	    "package",	    "synchronized",	    "true",	    "false",	    "null"
	};

	for (int i=0; i<keywordArray.length; i++)
	    keywords.add( keywordArray[i]);

	Iterator classIt = Scene.v().getLibraryClasses().iterator();
	while (classIt.hasNext())
	    add_ClassName( ((SootClass) classIt.next()).getName(), otherRoots);

	classIt = Scene.v().getApplicationClasses().iterator();
	while (classIt.hasNext())
	    add_ClassName( ((SootClass) classIt.next()).getName(), appRoots);

	Iterator arit = appRoots.iterator();
	while (arit.hasNext())
	    ((NameHolder) arit.next()).fix_ClassNames( "");

	arit = appRoots.iterator();
	while (arit.hasNext())
	    ((NameHolder) arit.next()).fix_PackageNames();
	
	fileSep = System.getProperty( "file.separator").charAt(0);
	pathSep = System.getProperty( "path.separator");
	classPath = System.getProperty( "java.class.path");
	
	fixed = true;
    }

    private void add_ClassName( String className, ArrayList roots)
    {
	ArrayList children = roots;
	NameHolder curNode = null;
	
	StringTokenizer st = new StringTokenizer( className, ".");
	while (st.hasMoreTokens()) {
	    String curName = (String) st.nextToken();

	    NameHolder child = null;
	    boolean found = false;
	    Iterator lit = children.iterator();
	    
	    while (lit.hasNext()) {
		child = (NameHolder) lit.next();
		
		if (child.get_OriginalName().equals( curName)) {

		    if (st.hasMoreTokens() == false)
			child.set_ClassAttr();

		    found = true;
		    break;
		}
	    }
		
	    if (!found) {
		child = new NameHolder( curName, curNode, st.hasMoreTokens() == false);
		children.add( child);
	    }
	    
	    curNode = child;
	    children = child.get_Children();
	}
    }

    public boolean package_ContainsClass( String classpathDir, String packageName, String className)
    {
	File p = new File( classpathDir);

	if (p.exists() == false)
	    return false;

	packageName = packageName.replace( '.', fileSep);
	if ((packageName.length() > 0) && (packageName.charAt( packageName.length() - 1) != fileSep))
	    packageName += fileSep;

	String name = packageName + className + ".class";

	if (p.isDirectory()) {
	    if ((classpathDir.length() > 0) && (classpathDir.charAt( classpathDir.length() - 1) != fileSep))
		classpathDir += fileSep;
	    
	    return (new File( classpathDir + name)).exists();
	}

	else {
	    JarFile jf = null;

	    try {
		jf = new JarFile( p);
	    }
	    catch (IOException ioe) {
		return false;
	    }

	    return (jf.getJarEntry( name) != null);
	}
    }

    IterableSet patch_PackageContext( IterableSet currentContext) 
    {
	IterableSet newContext = new IterableSet();

	Iterator it = currentContext.iterator();
	while (it.hasNext()) {
	    String 
		currentPackage = (String) it.next(),
		newPackage = null;

	    StringTokenizer st = new StringTokenizer( currentPackage, ".");
	    
	    if (st.hasMoreTokens() == false) {
		newContext.add( currentPackage);
		continue;
	    }

	    String firstToken = st.nextToken();
	    Iterator arit = appRoots.iterator();
	    while (arit.hasNext()) {
		NameHolder h = (NameHolder) arit.next();

		if (h.get_PackageName().equals( firstToken)) {
		    newPackage = h.get_OriginalPackageName( st);
		    break;
		}
	    }
	    if (newPackage != null)
		newContext.add( newPackage);
	    else
		newContext.add( currentPackage);
	}

	return newContext;
    }
}
