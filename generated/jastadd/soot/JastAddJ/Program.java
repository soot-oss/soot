/* This file was generated with JastAdd2 (http://jastadd.org) version R20130212 (r1031) */
package soot.JastAddJ;

import java.util.HashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;
/**
 * @production Program : {@link ASTNode} ::= <span class="component">{@link CompilationUnit}*</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:1
 */
public class Program extends ASTNode<ASTNode> implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    typeObject_computed = false;
    typeObject_value = null;
    typeCloneable_computed = false;
    typeCloneable_value = null;
    typeSerializable_computed = false;
    typeSerializable_value = null;
    typeBoolean_computed = false;
    typeBoolean_value = null;
    typeByte_computed = false;
    typeByte_value = null;
    typeShort_computed = false;
    typeShort_value = null;
    typeChar_computed = false;
    typeChar_value = null;
    typeInt_computed = false;
    typeInt_value = null;
    typeLong_computed = false;
    typeLong_value = null;
    typeFloat_computed = false;
    typeFloat_value = null;
    typeDouble_computed = false;
    typeDouble_value = null;
    typeString_computed = false;
    typeString_value = null;
    typeVoid_computed = false;
    typeVoid_value = null;
    typeNull_computed = false;
    typeNull_value = null;
    unknownType_computed = false;
    unknownType_value = null;
    hasPackage_String_values = null;
    lookupType_String_String_values = null;
    lookupLibType_String_String_values = null;
    getLibCompilationUnit_String_values = null;
    getLibCompilationUnit_String_list = null;    getPrimitiveCompilationUnit_computed = false;
    getPrimitiveCompilationUnit_value = null;
    unknownConstructor_computed = false;
    unknownConstructor_value = null;
    wildcards_computed = false;
    wildcards_value = null;
  }
  /**
   * @apilevel internal
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Program clone() throws CloneNotSupportedException {
    Program node = (Program)super.clone();
    node.typeObject_computed = false;
    node.typeObject_value = null;
    node.typeCloneable_computed = false;
    node.typeCloneable_value = null;
    node.typeSerializable_computed = false;
    node.typeSerializable_value = null;
    node.typeBoolean_computed = false;
    node.typeBoolean_value = null;
    node.typeByte_computed = false;
    node.typeByte_value = null;
    node.typeShort_computed = false;
    node.typeShort_value = null;
    node.typeChar_computed = false;
    node.typeChar_value = null;
    node.typeInt_computed = false;
    node.typeInt_value = null;
    node.typeLong_computed = false;
    node.typeLong_value = null;
    node.typeFloat_computed = false;
    node.typeFloat_value = null;
    node.typeDouble_computed = false;
    node.typeDouble_value = null;
    node.typeString_computed = false;
    node.typeString_value = null;
    node.typeVoid_computed = false;
    node.typeVoid_value = null;
    node.typeNull_computed = false;
    node.typeNull_value = null;
    node.unknownType_computed = false;
    node.unknownType_value = null;
    node.hasPackage_String_values = null;
    node.lookupType_String_String_values = null;
    node.lookupLibType_String_String_values = null;
    node.getLibCompilationUnit_String_values = null;
    node.getLibCompilationUnit_String_list = null;    node.getPrimitiveCompilationUnit_computed = false;
    node.getPrimitiveCompilationUnit_value = null;
    node.unknownConstructor_computed = false;
    node.unknownConstructor_value = null;
    node.wildcards_computed = false;
    node.wildcards_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Program copy() {
    try {
      Program node = (Program) clone();
      node.parent = null;
      if(children != null)
        node.children = (ASTNode[]) children.clone();
      return node;
    } catch (CloneNotSupportedException e) {
      throw new Error("Error: clone not supported for " +
        getClass().getName());
    }
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Program fullCopy() {
    Program tree = (Program) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        ASTNode child = (ASTNode) children[i];
        if(child != null) {
          child = child.fullCopy();
          tree.setChild(child, i);
        }
      }
    }
    return tree;
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:22
   */
  

  protected BytecodeReader bytecodeReader;
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:23
   */
  public void initBytecodeReader(BytecodeReader r) { bytecodeReader = r; }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:24
   */
  
  protected JavaParser javaParser;
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:25
   */
  public void initJavaParser(JavaParser p) { javaParser = p; }
  /**
   * Add a filename to the list of source files to process.
   * @return The CompilationUnit representing the source file,
   * or <code>null</code> if no such file exists
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:39
   */
  public CompilationUnit addSourceFile(String name) {
    return sourceFiles.addSourceFile(name);
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:44
   */
  public Iterator compilationUnitIterator() {
    initPaths();
    return new Iterator() {
      int index = 0;
      public boolean hasNext() {
        return index < getNumCompilationUnit() || !sourceFiles.isEmpty();
      }
      public Object next() {
        if(getNumCompilationUnit() == index) {
          String typename = (String)sourceFiles.keySet().iterator().next();
          CompilationUnit u = getCompilationUnit(typename);
          if(u != null) {
            addCompilationUnit(u);
            getCompilationUnit(getNumCompilationUnit()-1);
          }
          else
            throw new Error("File " + typename + " not found");
        }
        return getCompilationUnit(index++);
      }
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:73
   */
  public InputStream getInputStream(String name) {
    initPaths();
    try {
      for(Iterator iter = classPath.iterator(); iter.hasNext(); ) {
        PathPart part = (PathPart)iter.next();
        if(part.selectCompilationUnit(name))
          return part.is;
      }
    }
    catch(IOException e) {
    }
    throw new Error("Could not find nested type " + name);
  }
  /**
   * @return <code>true</code> if there is a package with the given name on
   * the path
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:143
   */
  public boolean isPackage(String name) {
    if(sourceFiles.hasPackage(name))
      return true;
    for(Iterator iter = classPath.iterator(); iter.hasNext(); ) {
      PathPart part = (PathPart)iter.next();
      if(part.hasPackage(name))
        return true;
    }
    for(Iterator iter = sourcePath.iterator(); iter.hasNext(); ) {
      PathPart part = (PathPart)iter.next();
      if(part.hasPackage(name))
        return true;
    }
    return false;
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:173
   */
  

  private boolean pathsInitialized = false;
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:174
   */
  
  private java.util.ArrayList classPath;
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:175
   */
  
  private java.util.ArrayList sourcePath;
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:176
   */
  
  private FileNamesPart sourceFiles = new FileNamesPart(this);
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:178
   */
  public void pushClassPath(String name) {
    PathPart part = PathPart.createSourcePath(name, this);
    if(part != null) {
      sourcePath.add(part);
      System.out.println("Pushing source path " + name);
    }
    else
      throw new Error("Could not push source path " + name);
    part = PathPart.createClassPath(name, this);
    if(part != null) {
      classPath.add(part);
      System.out.println("Pushing class path " + name);
    }
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:192
   */
  public void popClassPath() {
    if(sourcePath.size() > 0)
      sourcePath.remove(sourcePath.size()-1);
    if(classPath.size() > 0)
      classPath.remove(classPath.size()-1);
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:199
   */
  public void initPaths() {
    if(!pathsInitialized) {
      pathsInitialized = true;

      //System.err.println("Initializing class paths");
      
      ArrayList classPaths = new ArrayList();
      ArrayList sourcePaths = new ArrayList();
      
      String[] bootclasspaths;
      if(options().hasValueForOption("-bootclasspath"))
        bootclasspaths = options().getValueForOption("-bootclasspath").split(File.pathSeparator);
      else
        bootclasspaths = System.getProperty("sun.boot.class.path").split(File.pathSeparator);
      for(int i = 0; i < bootclasspaths.length; i++) {
        classPaths.add(bootclasspaths[i]);
        //System.err.println("Adding classpath " + bootclasspaths[i]);
      }
      
      String[] extdirs;
      if(options().hasValueForOption("-extdirs"))
        extdirs = options().getValueForOption("-extdirs").split(File.pathSeparator);
      else
        extdirs = System.getProperty("java.ext.dirs").split(File.pathSeparator);
      for(int i = 0; i < extdirs.length; i++) {
        classPaths.add(extdirs[i]);
        //System.err.println("Adding classpath " + extdirs[i]);
      }

      String[] userClasses = null;
      if(options().hasValueForOption("-classpath"))
        userClasses = options().getValueForOption("-classpath").split(File.pathSeparator);
      else if(options().hasValueForOption("-cp"))
        userClasses = options().getValueForOption("-cp").split(File.pathSeparator);
      else {
        userClasses = ".".split(File.pathSeparator);
      }
      if(!options().hasValueForOption("-sourcepath")) {
        for(int i = 0; i < userClasses.length; i++) {
          classPaths.add(userClasses[i]);
          sourcePaths.add(userClasses[i]);
          //System.err.println("Adding classpath/sourcepath " + userClasses[i]);
        }
      }
      else {
        for(int i = 0; i < userClasses.length; i++) {
          classPaths.add(userClasses[i]);
          //System.err.println("Adding classpath " + userClasses[i]);
        }
        userClasses = options().getValueForOption("-sourcepath").split(File.pathSeparator);
        for(int i = 0; i < userClasses.length; i++) {
          sourcePaths.add(userClasses[i]);
          //System.err.println("Adding sourcepath " + userClasses[i]);
        }
      }
        
      classPath = new ArrayList();
      sourcePath = new ArrayList();
      
      for(Iterator iter = classPaths.iterator(); iter.hasNext(); ) {
        String s = (String)iter.next();
        PathPart part = PathPart.createClassPath(s, this);
        if(part != null) {
          classPath.add(part);
          //System.out.println("Adding classpath " + s);
        }
        else if(options().verbose())
          System.out.println("Warning: Could not use " + s + " as class path");
      }
      for(Iterator iter = sourcePaths.iterator(); iter.hasNext(); ) {
        String s = (String)iter.next();
        PathPart part = PathPart.createSourcePath(s, this);
        if(part != null) {
          sourcePath.add(part);
          //System.out.println("Adding sourcepath " + s);
        }
        else if(options().verbose())
          System.out.println("Warning: Could not use " + s + " as source path");
      }
    }
  }
  /**
   * Add a path part to the library class path.
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:284
   */
  public void addClassPath(PathPart pathPart) {
    classPath.add(pathPart);
    pathPart.program = this;
  }
  /**
   * Add a path part to the user class path.
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:292
   */
  public void addSourcePath(PathPart pathPart) {
    sourcePath.add(pathPart);
    pathPart.program = this;
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:298
   */
  public void simpleReset() {
    lookupType_String_String_values = new HashMap();
    hasPackage_String_values = new HashMap();
    List list = new List();
    for(int i = 0; i < getNumCompilationUnit(); i++) {
      CompilationUnit unit = getCompilationUnit(i);
      if(!unit.fromSource()) {
        list.add(unit);
      }
    }
    setCompilationUnitList(list);
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:208
   */
  public void errorCheck(Collection collection) {
    for(Iterator iter = compilationUnitIterator(); iter.hasNext(); ) {
      CompilationUnit cu = (CompilationUnit)iter.next();
      if(cu.fromSource()) {
        cu.collectErrors();
        collection.addAll(cu.errors);
      }
    }
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:217
   */
  public void errorCheck(Collection collection, Collection warn) {
    for(Iterator iter = compilationUnitIterator(); iter.hasNext(); ) {
      CompilationUnit cu = (CompilationUnit)iter.next();
      if(cu.fromSource()) {
        cu.collectErrors();
        collection.addAll(cu.errors);
        warn.addAll(cu.warnings);
      }
    }
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:238
   */
  public boolean errorCheck() {
    Collection collection = new LinkedList();
    errorCheck(collection);
    if(collection.isEmpty())
      return false;
    System.out.println("Errors:");
    for(Iterator iter = collection.iterator(); iter.hasNext(); ) {
      String s = (String)iter.next();
      System.out.println(s);
    }
    return true;
  }
  /**
   * @ast method 
   * @aspect LookupFullyQualifiedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:101
   */
  

  public int classFileReadTime;
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:23
   */
  public void toString(StringBuffer s) {
    for(Iterator iter = compilationUnitIterator(); iter.hasNext(); ) {
      CompilationUnit cu = (CompilationUnit)iter.next();
      if(cu.fromSource()) { 
        cu.toString(s);
      }
    }
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:821
   */
  public String dumpTree() {
    StringBuffer s = new StringBuffer();
    for(Iterator iter = compilationUnitIterator(); iter.hasNext(); ) {
      CompilationUnit cu = (CompilationUnit)iter.next();
      if(cu.fromSource()) { 
        s.append(cu.dumpTree());
      }
    }
    return s.toString();
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:19
   */
  public void jimplify1() {
    for(Iterator iter = compilationUnitIterator(); iter.hasNext(); ) {
      CompilationUnit u = (CompilationUnit)iter.next();
      if(u.fromSource())
        u.jimplify1phase1();
    }
    for(Iterator iter = compilationUnitIterator(); iter.hasNext(); ) {
      CompilationUnit u = (CompilationUnit)iter.next();
      if(u.fromSource())
        u.jimplify1phase2();
    }
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:359
   */
  public void jimplify2() {
    for(Iterator iter = compilationUnitIterator(); iter.hasNext(); ) {
      CompilationUnit u = (CompilationUnit)iter.next();
      if(u.fromSource())
        u.jimplify2();
    }
  }
  /**
   * @ast method 
   * @aspect ClassLoading
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/ClassLoading.jrag:3
   */
  

  public static final int SRC_PREC_JAVA = 1;
  /**
   * @ast method 
   * @aspect ClassLoading
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/ClassLoading.jrag:4
   */
  
  public static final int SRC_PREC_CLASS = 2;
  /**
   * @ast method 
   * @aspect ClassLoading
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/ClassLoading.jrag:5
   */
  
  public static final int SRC_PREC_ONLY_CLASS = 3;
  /**
   * @ast method 
   * @aspect ClassLoading
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/ClassLoading.jrag:7
   */
  

  private int srcPrec = 0;
  /**
   * @ast method 
   * @aspect ClassLoading
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/ClassLoading.jrag:8
   */
  public void setSrcPrec(int i) {
    srcPrec = i;
  }
  /**
   * @ast method 
   * @aspect ClassLoading
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/ClassLoading.jrag:14
   */
  

  private HashMap loadedCompilationUnit = new HashMap();
  /**
   * @ast method 
   * @aspect ClassLoading
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/ClassLoading.jrag:15
   */
  public boolean hasLoadedCompilationUnit(String fileName) {
    return loadedCompilationUnit.containsKey(fileName);
  }
  /**
   * @ast method 
   * @aspect ClassLoading
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/ClassLoading.jrag:18
   */
  public CompilationUnit getCachedOrLoadCompilationUnit(String fileName) {
    if(loadedCompilationUnit.containsKey(fileName))
      return (CompilationUnit)loadedCompilationUnit.get(fileName);
    addSourceFile(fileName);
    return (CompilationUnit)loadedCompilationUnit.get(fileName);
  }
  /**
   * @ast method 
   * @aspect IncrementalJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/IncrementalJimple.jrag:12
   */
  public void releaseCompilationUnitForFile(String fileName) {
	    //clear caches
		lookupType_String_String_values = new HashMap();
		hasPackage_String_values = new HashMap();

		loadedCompilationUnit.remove(fileName);

		List<CompilationUnit> newList = new List<CompilationUnit>();
		for (soot.JastAddJ.CompilationUnit cu : getCompilationUnits()) {
			boolean dontAdd = false;
			if(cu.fromSource()) {
				String pathName = cu.pathName();
				if (pathName.equals(fileName)) {
					dontAdd = true;
				}
			}
			if(!dontAdd) {
				newList.add(cu);
			}
		}
		setCompilationUnitList(newList);		
	}
  /**
   * @ast method 
   * 
   */
  public Program() {
    super();

    is$Final(true);

  }
  /**
   * Initializes the child array to the correct size.
   * Initializes List and Opt nta children.
   * @apilevel internal
   * @ast method
   * @ast method 
   * 
   */
  public void init$Children() {
    children = new ASTNode[1];
    setChild(new List(), 0);
  }
  /**
   * @ast method 
   * 
   */
  public Program(List<CompilationUnit> p0) {
    setChild(p0, 0);
    is$Final(true);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Replaces the CompilationUnit list.
   * @param list The new list node to be used as the CompilationUnit list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setCompilationUnitList(List<CompilationUnit> list) {
    setChild(list, 0);
  }
  /**
   * Retrieves the number of children in the CompilationUnit list.
   * @return Number of children in the CompilationUnit list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumCompilationUnit() {
    return getCompilationUnitList().getNumChild();
  }
  /**
   * Retrieves the number of children in the CompilationUnit list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the CompilationUnit list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumCompilationUnitNoTransform() {
    return getCompilationUnitListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the CompilationUnit list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the CompilationUnit list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public CompilationUnit getCompilationUnit(int i) {
    return (CompilationUnit)getCompilationUnitList().getChild(i);
  }
  /**
   * Append an element to the CompilationUnit list.
   * @param node The element to append to the CompilationUnit list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void refined__Program_addCompilationUnit(CompilationUnit node) {
    List<CompilationUnit> list = (parent == null || state == null) ? getCompilationUnitListNoTransform() : getCompilationUnitList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addCompilationUnitNoTransform(CompilationUnit node) {
    List<CompilationUnit> list = getCompilationUnitListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the CompilationUnit list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setCompilationUnit(CompilationUnit node, int i) {
    List<CompilationUnit> list = getCompilationUnitList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the CompilationUnit list.
   * @return The node representing the CompilationUnit list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<CompilationUnit> getCompilationUnits() {
    return getCompilationUnitList();
  }
  /**
   * Retrieves the CompilationUnit list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the CompilationUnit list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<CompilationUnit> getCompilationUnitsNoTransform() {
    return getCompilationUnitListNoTransform();
  }
  /**
   * Retrieves the CompilationUnit list.
   * @return The node representing the CompilationUnit list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<CompilationUnit> refined__Program_getCompilationUnitList() {
    List<CompilationUnit> list = (List<CompilationUnit>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the CompilationUnit list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the CompilationUnit list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<CompilationUnit> getCompilationUnitListNoTransform() {
    return (List<CompilationUnit>)getChildNoTransform(0);
  }
  /**
   * @ast method 
   * @aspect ClassLoading
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/ClassLoading.jrag:36
   */
    public CompilationUnit getCompilationUnit(String name) {
    initPaths();
    try {
      if(sourceFiles.selectCompilationUnit(name))
        return sourceFiles.getCompilationUnit();
      PathPart sourcePart = null;
      PathPart classPart = null;
      for(Iterator iter = sourcePath.iterator(); iter.hasNext() && sourcePart == null; ) {
        PathPart part = (PathPart)iter.next();
        if(part.selectCompilationUnit(name))
          sourcePart = part;
      }
      for(Iterator iter = classPath.iterator(); iter.hasNext() && classPart == null; ) {
        PathPart part = (PathPart)iter.next();
        if(part.selectCompilationUnit(name))
          classPart = part;
      }
      
      if(sourcePart != null && srcPrec == SRC_PREC_JAVA) {
        CompilationUnit unit = getCachedOrLoadCompilationUnit(new File(sourcePart.pathName).getCanonicalPath());
        int index = name.lastIndexOf('.');
        if(index == -1)
          return unit;
        String pkgName = name.substring(0, index);
        if(pkgName.equals(unit.getPackageDecl()))
          return unit;
      }
      if(classPart != null && srcPrec == SRC_PREC_CLASS) {
        CompilationUnit unit = classPart.getCompilationUnit();
        int index = name.lastIndexOf('.');
        if(index == -1)
          return unit;
        String pkgName = name.substring(0, index);
        if(pkgName.equals(unit.getPackageDecl()))
          return unit;
      }
      if(srcPrec == SRC_PREC_ONLY_CLASS) {
        if(classPart != null) {
          CompilationUnit unit = classPart.getCompilationUnit();
          int index = name.lastIndexOf('.');
          if(index == -1)
            return unit;
          String pkgName = name.substring(0, index);
          if(pkgName.equals(unit.getPackageDecl()))
            return unit;
        }
      }
      else if(sourcePart != null && (classPart == null || classPart.age <= sourcePart.age)) {
        CompilationUnit unit = getCachedOrLoadCompilationUnit(new File(sourcePart.pathName).getCanonicalPath());
        int index = name.lastIndexOf('.');
        if(index == -1)
          return unit;
        String pkgName = name.substring(0, index);
        if(pkgName.equals(unit.getPackageDecl()))
          return unit;
      }
      else if(classPart != null) {
        CompilationUnit unit = classPart.getCompilationUnit();
        int index = name.lastIndexOf('.');
        if(index == -1)
          return unit;
        String pkgName = name.substring(0, index);
        if(pkgName.equals(unit.getPackageDecl()))
          return unit;
      }
      return null;
    }
    catch(IOException e) {
    }
    return null;
  }
  /**
   * @ast method 
   * @aspect ClassLoading
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/ClassLoading.jrag:25
   */
   void addCompilationUnit(CompilationUnit unit) {
    try {
      if(unit.pathName() != null) {
        String fileName = new File(unit.pathName()).getCanonicalPath();
        loadedCompilationUnit.put(fileName, unit);
      }
    } catch (IOException e) {
    }
    refined__Program_addCompilationUnit(unit);
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:87
   */
   public List getCompilationUnitList() {
    initPaths();
    return refined__Program_getCompilationUnitList();
  }
  /**
   * @apilevel internal
   */
  protected boolean typeObject_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeObject_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:15
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeObject() {
    if(typeObject_computed) {
      return typeObject_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeObject_value = typeObject_compute();
      if(isFinal && num == state().boundariesCrossed) typeObject_computed = true;
    return typeObject_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeObject_compute() {  return lookupType("java.lang", "Object");  }
  /**
   * @apilevel internal
   */
  protected boolean typeCloneable_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeCloneable_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:16
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeCloneable() {
    if(typeCloneable_computed) {
      return typeCloneable_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeCloneable_value = typeCloneable_compute();
      if(isFinal && num == state().boundariesCrossed) typeCloneable_computed = true;
    return typeCloneable_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeCloneable_compute() {  return lookupType("java.lang", "Cloneable");  }
  /**
   * @apilevel internal
   */
  protected boolean typeSerializable_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeSerializable_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:17
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeSerializable() {
    if(typeSerializable_computed) {
      return typeSerializable_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeSerializable_value = typeSerializable_compute();
      if(isFinal && num == state().boundariesCrossed) typeSerializable_computed = true;
    return typeSerializable_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeSerializable_compute() {  return lookupType("java.io", "Serializable");  }
  /**
   * @apilevel internal
   */
  protected boolean typeBoolean_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeBoolean_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:22
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeBoolean() {
    if(typeBoolean_computed) {
      return typeBoolean_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeBoolean_value = typeBoolean_compute();
      if(isFinal && num == state().boundariesCrossed) typeBoolean_computed = true;
    return typeBoolean_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeBoolean_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME, "boolean");  }
  /**
   * @apilevel internal
   */
  protected boolean typeByte_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeByte_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:23
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeByte() {
    if(typeByte_computed) {
      return typeByte_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeByte_value = typeByte_compute();
      if(isFinal && num == state().boundariesCrossed) typeByte_computed = true;
    return typeByte_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeByte_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME , "byte");  }
  /**
   * @apilevel internal
   */
  protected boolean typeShort_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeShort_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:24
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeShort() {
    if(typeShort_computed) {
      return typeShort_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeShort_value = typeShort_compute();
      if(isFinal && num == state().boundariesCrossed) typeShort_computed = true;
    return typeShort_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeShort_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME , "short");  }
  /**
   * @apilevel internal
   */
  protected boolean typeChar_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeChar_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:25
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeChar() {
    if(typeChar_computed) {
      return typeChar_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeChar_value = typeChar_compute();
      if(isFinal && num == state().boundariesCrossed) typeChar_computed = true;
    return typeChar_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeChar_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME , "char");  }
  /**
   * @apilevel internal
   */
  protected boolean typeInt_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeInt_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:26
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeInt() {
    if(typeInt_computed) {
      return typeInt_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeInt_value = typeInt_compute();
      if(isFinal && num == state().boundariesCrossed) typeInt_computed = true;
    return typeInt_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeInt_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME , "int");  }
  /**
   * @apilevel internal
   */
  protected boolean typeLong_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeLong_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:27
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeLong() {
    if(typeLong_computed) {
      return typeLong_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeLong_value = typeLong_compute();
      if(isFinal && num == state().boundariesCrossed) typeLong_computed = true;
    return typeLong_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeLong_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME , "long");  }
  /**
   * @apilevel internal
   */
  protected boolean typeFloat_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeFloat_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:28
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeFloat() {
    if(typeFloat_computed) {
      return typeFloat_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeFloat_value = typeFloat_compute();
      if(isFinal && num == state().boundariesCrossed) typeFloat_computed = true;
    return typeFloat_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeFloat_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME , "float");  }
  /**
   * @apilevel internal
   */
  protected boolean typeDouble_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeDouble_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:29
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeDouble() {
    if(typeDouble_computed) {
      return typeDouble_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeDouble_value = typeDouble_compute();
      if(isFinal && num == state().boundariesCrossed) typeDouble_computed = true;
    return typeDouble_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeDouble_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME , "double");  }
  /**
   * @apilevel internal
   */
  protected boolean typeString_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeString_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:30
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeString() {
    if(typeString_computed) {
      return typeString_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeString_value = typeString_compute();
      if(isFinal && num == state().boundariesCrossed) typeString_computed = true;
    return typeString_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeString_compute() {  return lookupType("java.lang", "String");  }
  /**
   * @apilevel internal
   */
  protected boolean typeVoid_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeVoid_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:41
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeVoid() {
    if(typeVoid_computed) {
      return typeVoid_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeVoid_value = typeVoid_compute();
      if(isFinal && num == state().boundariesCrossed) typeVoid_computed = true;
    return typeVoid_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeVoid_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME, "void");  }
  /**
   * @apilevel internal
   */
  protected boolean typeNull_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeNull_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:43
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeNull() {
    if(typeNull_computed) {
      return typeNull_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeNull_value = typeNull_compute();
      if(isFinal && num == state().boundariesCrossed) typeNull_computed = true;
    return typeNull_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeNull_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME, "null");  }
  /**
   * @apilevel internal
   */
  protected boolean unknownType_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl unknownType_value;
  /**
   * @attribute syn
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:46
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl unknownType() {
    if(unknownType_computed) {
      return unknownType_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    unknownType_value = unknownType_compute();
      if(isFinal && num == state().boundariesCrossed) unknownType_computed = true;
    return unknownType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl unknownType_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME, "Unknown");  }
  protected java.util.Map hasPackage_String_values;
  /**
   * @attribute syn
   * @aspect LookupFullyQualifiedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:77
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean hasPackage(String packageName) {
    Object _parameters = packageName;
    if(hasPackage_String_values == null) hasPackage_String_values = new java.util.HashMap(4);
    if(hasPackage_String_values.containsKey(_parameters)) {
      return ((Boolean)hasPackage_String_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean hasPackage_String_value = hasPackage_compute(packageName);
      if(isFinal && num == state().boundariesCrossed) hasPackage_String_values.put(_parameters, Boolean.valueOf(hasPackage_String_value));
    return hasPackage_String_value;
  }
  /**
   * @apilevel internal
   */
  private boolean hasPackage_compute(String packageName) {
    return isPackage(packageName);
  }
  protected java.util.Map lookupType_String_String_values;
  /**
   * Checks from-source compilation units for the given type.
   * If no matching compilation unit is found the library compliation units
   * will be searched.
   * @attribute syn
   * @aspect LookupFullyQualifiedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:158
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupType(String packageName, String typeName) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(packageName);
    _parameters.add(typeName);
    if(lookupType_String_String_values == null) lookupType_String_String_values = new java.util.HashMap(4);
    if(lookupType_String_String_values.containsKey(_parameters)) {
      return (TypeDecl)lookupType_String_String_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    TypeDecl lookupType_String_String_value = lookupType_compute(packageName, typeName);
      if(isFinal && num == state().boundariesCrossed) lookupType_String_String_values.put(_parameters, lookupType_String_String_value);
    return lookupType_String_String_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl lookupType_compute(String packageName, String typeName) {
    String fullName = packageName.equals("") ? typeName : packageName + "." + typeName;
    // Check for type in source
    for(int i = 0; i < getNumCompilationUnit(); i++) {
      for(int j = 0; j < getCompilationUnit(i).getNumTypeDecl(); j++) {
        TypeDecl type = getCompilationUnit(i).getTypeDecl(j);
        if(type.fullName().equals(fullName)) {
          return type;
        }
      }
    }
    // Check for type in library
    return lookupLibType(packageName, typeName);
  }
  protected java.util.Map lookupLibType_String_String_values;
  /**
   * Lookup types in the library
   * @attribute syn
   * @aspect LookupFullyQualifiedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:175
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupLibType(String packageName, String typeName) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(packageName);
    _parameters.add(typeName);
    if(lookupLibType_String_String_values == null) lookupLibType_String_String_values = new java.util.HashMap(4);
    if(lookupLibType_String_String_values.containsKey(_parameters)) {
      return (TypeDecl)lookupLibType_String_String_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    TypeDecl lookupLibType_String_String_value = lookupLibType_compute(packageName, typeName);
      if(isFinal && num == state().boundariesCrossed) lookupLibType_String_String_values.put(_parameters, lookupLibType_String_String_value);
    return lookupLibType_String_String_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl lookupLibType_compute(String packageName, String typeName) {
    String fullName = packageName.equals("") ? typeName : packageName + "." + typeName;
    // Check the primitive types
    if (packageName.equals(PRIMITIVE_PACKAGE_NAME)) {
      PrimitiveCompilationUnit unit = getPrimitiveCompilationUnit();
      if (typeName.equals("boolean")) return unit.typeBoolean();
      if (typeName.equals("byte")) return unit.typeByte();
      if (typeName.equals("short")) return unit.typeShort();
      if (typeName.equals("char")) return unit.typeChar();
      if (typeName.equals("int")) return unit.typeInt();
      if (typeName.equals("long")) return unit.typeLong();
      if (typeName.equals("float")) return unit.typeFloat();
      if (typeName.equals("double")) return unit.typeDouble();
      if (typeName.equals("null")) return unit.typeNull();
      if (typeName.equals("void")) return unit.typeVoid();
      if (typeName.equals("Unknown")) return unit.unknownType(); // Is this needed?
    } 
    // Check the library:
    //  A type may not be in the library but an NTA cannot map to null.
    //  We need to do some double work to step around this.
    //  We check the classpath directly (the same thing the library NTA does)
    //  to prevent that we call the nta for a name that gives null back
    //else if (getCompilationUnit(fullName) != null) { 
    
    // Found a library unit, check it for type
    CompilationUnit libUnit = getLibCompilationUnit(fullName);
    if (libUnit != null) {
      for(int j = 0; j < libUnit.getNumTypeDecl(); j++) {
        TypeDecl type = libUnit.getTypeDecl(j);
        if(type.fullName().equals(fullName)) {
          return type;
        }
      }
    }
    // No type found in the library
    return null;
  }
  /**
   * @apilevel internal
   */
  protected java.util.Map getLibCompilationUnit_String_values;
  /**
   * @apilevel internal
   */
  protected List getLibCompilationUnit_String_list;
  /**
   * @attribute syn
   * @aspect LookupFullyQualifiedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:213
   */
  @SuppressWarnings({"unchecked", "cast"})
  public CompilationUnit getLibCompilationUnit(String fullName) {
    Object _parameters = fullName;
    if(getLibCompilationUnit_String_values == null) getLibCompilationUnit_String_values = new java.util.HashMap(4);
    if(getLibCompilationUnit_String_values.containsKey(_parameters)) {
      return (CompilationUnit)getLibCompilationUnit_String_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    CompilationUnit getLibCompilationUnit_String_value = getLibCompilationUnit_compute(fullName);
    if(getLibCompilationUnit_String_list == null) {
      getLibCompilationUnit_String_list = new List();
      getLibCompilationUnit_String_list.is$Final = true;
      getLibCompilationUnit_String_list.setParent(this);
    }
    getLibCompilationUnit_String_list.add(getLibCompilationUnit_String_value);
    if(getLibCompilationUnit_String_value != null) {
      getLibCompilationUnit_String_value.is$Final = true;
    }
      if(true) getLibCompilationUnit_String_values.put(_parameters, getLibCompilationUnit_String_value);
    return getLibCompilationUnit_String_value;
  }
  /**
   * @apilevel internal
   */
  private CompilationUnit getLibCompilationUnit_compute(String fullName) {
    return getCompilationUnit(fullName);
  }
  /**
   * @apilevel internal
   */
  protected boolean getPrimitiveCompilationUnit_computed = false;
  /**
   * @apilevel internal
   */
  protected PrimitiveCompilationUnit getPrimitiveCompilationUnit_value;
  /**
   * @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:219
   */
  @SuppressWarnings({"unchecked", "cast"})
  public PrimitiveCompilationUnit getPrimitiveCompilationUnit() {
    if(getPrimitiveCompilationUnit_computed) {
      return getPrimitiveCompilationUnit_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getPrimitiveCompilationUnit_value = getPrimitiveCompilationUnit_compute();
    getPrimitiveCompilationUnit_value.setParent(this);
    getPrimitiveCompilationUnit_value.is$Final = true;
      if(true) getPrimitiveCompilationUnit_computed = true;
    return getPrimitiveCompilationUnit_value;
  }
  /**
   * @apilevel internal
   */
  private PrimitiveCompilationUnit getPrimitiveCompilationUnit_compute() {    
    PrimitiveCompilationUnit u = new PrimitiveCompilationUnit();
    u.setPackageDecl(PRIMITIVE_PACKAGE_NAME);
    return u;
  }
  /**
   * @apilevel internal
   */
  protected boolean unknownConstructor_computed = false;
  /**
   * @apilevel internal
   */
  protected ConstructorDecl unknownConstructor_value;
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:245
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ConstructorDecl unknownConstructor() {
    if(unknownConstructor_computed) {
      return unknownConstructor_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    unknownConstructor_value = unknownConstructor_compute();
      if(isFinal && num == state().boundariesCrossed) unknownConstructor_computed = true;
    return unknownConstructor_value;
  }
  /**
   * @apilevel internal
   */
  private ConstructorDecl unknownConstructor_compute() {
    return (ConstructorDecl)unknownType().constructors().iterator().next();
  }
  /**
   * @apilevel internal
   */
  protected boolean wildcards_computed = false;
  /**
   * @apilevel internal
   */
  protected WildcardsCompilationUnit wildcards_value;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1358
   */
  @SuppressWarnings({"unchecked", "cast"})
  public WildcardsCompilationUnit wildcards() {
    if(wildcards_computed) {
      return wildcards_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    wildcards_value = wildcards_compute();
    wildcards_value.setParent(this);
    wildcards_value.is$Final = true;
      if(true) wildcards_computed = true;
    return wildcards_value;
  }
  /**
   * @apilevel internal
   */
  private WildcardsCompilationUnit wildcards_compute() {
    return new WildcardsCompilationUnit(
      "wildcards",
      new List(),
      new List()
    );
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:16
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_superType(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:26
   * @apilevel internal
   */
  public ConstructorDecl Define_ConstructorDecl_constructorDecl(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:19
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_componentType(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return unknownType();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:172
   * @apilevel internal
   */
  public LabeledStmt Define_LabeledStmt_lookupLabel(ASTNode caller, ASTNode child, String name) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:16
   * @apilevel internal
   */
  public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:26
   * @apilevel internal
   */
  public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return true;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:50
   * @apilevel internal
   */
  public boolean Define_boolean_isIncOrDec(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:324
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return true;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:710
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return true;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:13
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeException(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return lookupType("java.lang", "Exception");
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:15
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeRuntimeException(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return lookupType("java.lang", "RuntimeException");
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:17
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeError(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return lookupType("java.lang", "Error");
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:19
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeNullPointerException(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return lookupType("java.lang", "NullPointerException");
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:21
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeThrowable(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return lookupType("java.lang", "Throwable");
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:137
   * @apilevel internal
   */
  public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
     { 
   int childIndex = this.getIndexOfChild(caller);
{
    throw new Error("Operation handlesException not supported");
  }
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:15
   * @apilevel internal
   */
  public Collection Define_Collection_lookupConstructor(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return Collections.EMPTY_LIST;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:24
   * @apilevel internal
   */
  public Collection Define_Collection_lookupSuperConstructor(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return Collections.EMPTY_LIST;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:21
   * @apilevel internal
   */
  public Expr Define_Expr_nestedScope(ASTNode caller, ASTNode child) {
     { 
   int childIndex = this.getIndexOfChild(caller);
{ throw new UnsupportedOperationException(); }
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:33
   * @apilevel internal
   */
  public Collection Define_Collection_lookupMethod(ASTNode caller, ASTNode child, String name) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return Collections.EMPTY_LIST;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:18
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeObject(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeObject();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:19
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeCloneable(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeCloneable();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:20
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeSerializable(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeSerializable();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:31
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeBoolean(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeBoolean();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:32
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeByte(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeByte();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:33
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeShort(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeShort();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:34
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeChar(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeChar();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:35
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeInt(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeInt();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:36
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeLong(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeLong();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:37
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeFloat(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeFloat();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:38
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeDouble(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeDouble();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:39
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeString(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeString();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:42
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeVoid(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeVoid();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:44
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeNull(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeNull();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:47
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_unknownType(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return unknownType();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:81
   * @apilevel internal
   */
  public boolean Define_boolean_hasPackage(ASTNode caller, ASTNode child, String packageName) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return hasPackage(packageName);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:151
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_lookupType(ASTNode caller, ASTNode child, String packageName, String typeName) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return lookupType(packageName, typeName);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:266
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return SimpleSet.emptySet;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:24
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return SimpleSet.emptySet;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:290
   * @apilevel internal
   */
  public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:291
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:292
   * @apilevel internal
   */
  public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:293
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:294
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:295
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeAbstract(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:296
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeVolatile(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:297
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeTransient(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:298
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeStrictfp(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:299
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeSynchronized(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:300
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeNative(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:249
   * @apilevel internal
   */
  public ASTNode Define_ASTNode_enclosingBlock(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:300
   * @apilevel internal
   */
  public VariableScope Define_VariableScope_outerScope(ASTNode caller, ASTNode child) {
     { 
   int childIndex = this.getIndexOfChild(caller);
{
    throw new UnsupportedOperationException("outerScope() not defined");
  }
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:368
   * @apilevel internal
   */
  public boolean Define_boolean_insideLoop(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:375
   * @apilevel internal
   */
  public boolean Define_boolean_insideSwitch(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:425
   * @apilevel internal
   */
  public Case Define_Case_bind(ASTNode caller, ASTNode child, Case c) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:354
   * @apilevel internal
   */
  public String Define_String_typeDeclIndent(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return "";
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:64
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return NameType.NO_NAME;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:219
   * @apilevel internal
   */
  public boolean Define_boolean_isAnonymous(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:233
   * @apilevel internal
   */
  public Variable Define_Variable_unknownField(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return unknownType().findSingleVariable("unknown");
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:237
   * @apilevel internal
   */
  public MethodDecl Define_MethodDecl_unknownMethod(ASTNode caller, ASTNode child) {
     { 
   int childIndex = this.getIndexOfChild(caller);
{
    for(Iterator iter = unknownType().memberMethods("unknown").iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      return m;
    }
    throw new Error("Could not find method unknown in type Unknown");
  }
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:244
   * @apilevel internal
   */
  public ConstructorDecl Define_ConstructorDecl_unknownConstructor(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return unknownConstructor();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:256
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
     {
      int i = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:513
   * @apilevel internal
   */
  public BodyDecl Define_BodyDecl_enclosingBodyDecl(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:531
   * @apilevel internal
   */
  public boolean Define_boolean_isMemberType(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:582
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_hostType(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:360
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_switchType(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return unknownType();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:406
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_returnType(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeVoid();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:506
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingInstance(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:14
   * @apilevel internal
   */
  public String Define_String_methodHost(ASTNode caller, ASTNode child) {
     { 
   int childIndex = this.getIndexOfChild(caller);
{
    throw new Error("Needs extra equation for methodHost()");
  }
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:128
   * @apilevel internal
   */
  public boolean Define_boolean_inExplicitConstructorInvocation(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:137
   * @apilevel internal
   */
  public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:162
   * @apilevel internal
   */
  public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return true;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:68
   * @apilevel internal
   */
  public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:69
   * @apilevel internal
   */
  public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:70
   * @apilevel internal
   */
  public boolean Define_boolean_isExceptionHandlerParameter(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:94
   * @apilevel internal
   */
  public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:180
   * @apilevel internal
   */
  public ElementValue Define_ElementValue_lookupElementTypeValue(ASTNode caller, ASTNode child, String name) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:269
   * @apilevel internal
   */
  public boolean Define_boolean_withinSuppressWarnings(ASTNode caller, ASTNode child, String s) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:371
   * @apilevel internal
   */
  public boolean Define_boolean_withinDeprecatedAnnotation(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:430
   * @apilevel internal
   */
  public Annotation Define_Annotation_lookupAnnotation(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
     {
      int i = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:463
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingAnnotationDecl(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return unknownType();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:39
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_assignConvertedType(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return typeNull();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:267
   * @apilevel internal
   */
  public boolean Define_boolean_inExtendsOrImplements(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1385
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeWildcard(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return wildcards().typeWildcard();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1396
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_lookupWildcardExtends(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return wildcards().lookupWildcardExtends(typeDecl);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1409
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_lookupWildcardSuper(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return wildcards().lookupWildcardSuper(typeDecl);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1431
   * @apilevel internal
   */
  public LUBType Define_LUBType_lookupLUBType(ASTNode caller, ASTNode child, Collection bounds) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return wildcards().lookupLUBType(bounds);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1469
   * @apilevel internal
   */
  public GLBType Define_GLBType_lookupGLBType(ASTNode caller, ASTNode child, ArrayList bounds) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return wildcards().lookupGLBType(bounds);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:46
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_genericDecl(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:24
   * @apilevel internal
   */
  public boolean Define_boolean_variableArityValid(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:64
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_expectedType(ASTNode caller, ASTNode child) {
     {
      int i = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:49
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
    if(caller == getCompilationUnitListNoTransform())  { 
    int i = caller.getIndexOfChild(child);
    {
    throw new Error("condition_false_label not implemented");
  }
  }
    else {      return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:53
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
    if(caller == getCompilationUnitListNoTransform())  { 
    int i = caller.getIndexOfChild(child);
    {
    throw new Error("condition_true_label not implemented");
  }
  }
    else {      return getParent().Define_soot_jimple_Stmt_condition_true_label(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:15
   * @apilevel internal
   */
  public int Define_int_localNum(ASTNode caller, ASTNode child) {
    if(caller == getCompilationUnitListNoTransform())  {
    int index = caller.getIndexOfChild(child);
    return 0;
  }
    else {      return getParent().Define_int_localNum(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:349
   * @apilevel internal
   */
  public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:465
   * @apilevel internal
   */
  public ArrayList Define_ArrayList_exceptionRanges(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:122
   * @apilevel internal
   */
  public boolean Define_boolean_isCatchParam(ASTNode caller, ASTNode child) {
     {
      int i = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:130
   * @apilevel internal
   */
  public CatchClause Define_CatchClause_catchClause(ASTNode caller, ASTNode child) {
     { 
   int i = this.getIndexOfChild(caller);
{
		throw new IllegalStateException("Could not find parent " +
				"catch clause");
	}
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:154
   * @apilevel internal
   */
  public boolean Define_boolean_resourcePreviouslyDeclared(ASTNode caller, ASTNode child, String name) {
     {
      int i = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:70
   * @apilevel internal
   */
  public ClassInstanceExpr Define_ClassInstanceExpr_getClassInstanceExpr(ASTNode caller, ASTNode child) {
     {
      int i = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:462
   * @apilevel internal
   */
  public boolean Define_boolean_isAnonymousDecl(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:479
   * @apilevel internal
   */
  public boolean Define_boolean_isExplicitGenericConstructorAccess(ASTNode caller, ASTNode child) {
     {
      int i = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
