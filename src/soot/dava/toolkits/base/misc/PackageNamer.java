package soot.dava.toolkits.base.misc;

import soot.*;
import java.util.*;
import soot.util.*;
import soot.dava.*;

public class PackageNamer
{
    private PackageNamer() 
    {
	appRoots = new ArrayList();
	otherRoots = new ArrayList();

	keywords = new HashSet();

	class2package = new HashMap();
	properClassName2packageSet = new HashMap();

	fixed = false;
    }

    private static PackageNamer instance = new PackageNamer();

    public static PackageNamer v()
    {
	return instance;
    }
    
    public boolean has_FixedNames()
    {
	return fixed;
    }

    public boolean use_ShortName( String fixedShortClassName)
    {
	if (fixed == false)
	    return false;

	IterableSet classSet = (IterableSet) class2package.get( fixedShortClassName);
	if (classSet == null) {
	    classSet = new IterableSet();
	    class2package.put( fixedShortClassName, classSet);

	    List nhList = (List) properClassName2packageSet.get( fixedShortClassName);
	    if (nhList == null)
		return true;

	    Iterator it = nhList.iterator();
	    while (it.hasNext())
		classSet.add( ((NameHolder) it.next()).get_FixedPackageName());
	}

	IterableSet packageContext = Dava.v().get_CurrentPackageContext();
	if (packageContext == null)
	    return true;
	
	int count = 0;
	Iterator classSetIt = classSet.iterator();
	while (classSetIt.hasNext())
	    if (packageContext.contains( classSetIt.next()))
		if (++count > 1)
		    return false;

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

	public NameHolder get_Parent()
	{
	    return parent;
	}

	public void set_ClassAttr()
	{
	    isClass = true;
	}

	public boolean is_Class()
	{
	    if (children.isEmpty())
		return true;
	    else
		return isClass;
	}

	public boolean is_Package()
	{
	    return (children.isEmpty() == false);
	}

	public String get_PackageName()
	{
	    return packageName;
	}

	public String get_ClassName()
	{
	    return className;
	}

	public void set_PackageName( String packageName)
	{
	    this.packageName = packageName;
	}

	public void set_ClassName( String className)
	{
	    this.className = className;
	}

	public String get_OriginalName()
	{
	    return originalName;
	}

	public ArrayList get_Children()
	{
	    return children;
	}
	
	public String get_FixedPackageName()
	{
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
	    if (is_Class())
		for (int i=0;; i++) {
		    String curName = curPackName + "." + className;

		    if ((keywords.contains( className) == false) &&
			(PackageNamer.v().classClashes_WithOtherPackages( curName) == false)) {

			ArrayList packageSet = (ArrayList) properClassName2packageSet.get( className);
			if (packageSet == null) {
			    packageSet = new ArrayList();
			    properClassName2packageSet.put( className, packageSet);
			}
			packageSet.add( this);
			break;
		    }
		    
		    className = originalName + "_c" + i;
		}

	    Iterator it = children.iterator();
	    while (it.hasNext())
		((NameHolder) it.next()).fix_ClassNames( curPackName + "." + packageName);
	}

	public void fix_PackageNames()
	{
	    if (is_Package())
		for (int i=0;; i++) {
		    if ((keywords.contains( packageName) == false) && 
			(siblingClashes( packageName) == false) &&
			((is_Class() == false) || (className.equals( packageName) == false)))
			
			break;
		    
		    packageName = originalName + "_p" + i;
		}

	    Iterator it = children.iterator();
	    while (it.hasNext())
		((NameHolder) it.next()).fix_PackageNames();
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
	    System.out.print( indentation + "\"" + originalName + "\", \"" + packageName + "\", \"" + className + "\" (");
	    if (is_Class())
		System.out.print("c");
	    if (is_Package())
		System.out.print("p");
	    System.out.println( ")");

	    Iterator it = children.iterator();
	    while (it.hasNext())
		((NameHolder) it.next()).dump( indentation + "  ");
	}
    }

    private boolean fixed;
    private ArrayList appRoots, otherRoots;
    private HashSet keywords;
    private HashMap class2package, properClassName2packageSet;
    
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

	Iterator classIt = Scene.v().getContextClasses().iterator();
	while (classIt.hasNext())
	    add_ClassName( ((SootClass) classIt.next()).getFullName(), otherRoots);

	classIt = Scene.v().getLibraryClasses().iterator();
	while (classIt.hasNext())
	    add_ClassName( ((SootClass) classIt.next()).getFullName(), otherRoots);

	classIt = Scene.v().getApplicationClasses().iterator();
	while (classIt.hasNext())
	    add_ClassName( ((SootClass) classIt.next()).getFullName(), appRoots);

	Iterator arit = appRoots.iterator();
	while (arit.hasNext())
	    ((NameHolder) arit.next()).fix_ClassNames( "");

	arit = appRoots.iterator();
	while (arit.hasNext())
	    ((NameHolder) arit.next()).fix_PackageNames();
	
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

    public boolean classClashes_WithOtherPackages( String s)
    {
	ArrayList children = otherRoots;
	NameHolder curNode = null;

	StringTokenizer st = new StringTokenizer( s, ".");
	while (st.hasMoreTokens()) {
	    String curName = (String) st.nextToken();

	    NameHolder child = null;
	    boolean found = false;
	    Iterator cit = children.iterator();

	    while (cit.hasNext()) {
		child = (NameHolder) cit.next();

		if (child.get_PackageName().equals( curName)) {
		    found = true;
		    break;
		}
	    }
	    
	    if (!found) 
		return false;

	    curNode = child;
	    children = child.get_Children();
	}

	return ((curNode != null) && (curNode.is_Package()));
    }
}
