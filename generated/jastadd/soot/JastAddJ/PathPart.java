
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class PathPart extends java.lang.Object {
    // Declared in ClassPath.jrag at line 280

    public InputStream is;

    // Declared in ClassPath.jrag at line 281

    protected String pathName;

    // Declared in ClassPath.jrag at line 282
 
    protected String relativeName;

    // Declared in ClassPath.jrag at line 283

    protected String fullName;

    // Declared in ClassPath.jrag at line 284

    long age;

    // Declared in ClassPath.jrag at line 285

    Program program;

    // Declared in ClassPath.jrag at line 287

    
    protected PathPart() {
    }

    // Declared in ClassPath.jrag at line 290


    protected boolean isSource;

    // Declared in ClassPath.jrag at line 291

    protected String fileSuffix() {
      return isSource ? ".java" : ".class";
    }

    // Declared in ClassPath.jrag at line 295


    public static PathPart createSourcePath(String fileName, Program program) {
      PathPart p = createPathPart(fileName);
      if(p != null) {
        p.isSource = true;
        p.program = program;
      }
      return p;
    }

    // Declared in ClassPath.jrag at line 304


    public static PathPart createClassPath(String fileName, Program program) {
      PathPart p = createPathPart(fileName);
      if(p != null) {
        p.isSource = false;
        p.program = program;
      }
      return p;
    }

    // Declared in ClassPath.jrag at line 313


    private static PathPart createPathPart(String s) {
      try {
        File f = new File(s);
        if(f.isDirectory())
          return new FolderPart(f);
        else if(f.isFile())
          return new ZipFilePart(new ZipFile(f));
      } catch (IOException e) {
        // error in path
      }
      return null;
    }

    // Declared in ClassPath.jrag at line 327


    // is there a package with the specified name on this path part
    public boolean hasPackage(String name) { return false; }

    // Declared in ClassPath.jrag at line 331

    
    // select a compilation unit from a canonical name
    // returns true of the compilation unit exists on this path
    public boolean selectCompilationUnit(String canonicalName) throws IOException { return false; }

    // Declared in ClassPath.jrag at line 334


    // load the return currently selected compilation unit
    public CompilationUnit getCompilationUnit() {
      long startTime = System.currentTimeMillis();
      if(!isSource) {
        try {
          if(program.options().verbose())
            System.out.print("Loading .class file: " + fullName + " ");

          CompilationUnit u = program.bytecodeReader.read(is, fullName, program);
          //CompilationUnit u = new bytecode.Parser(is, fullName).parse(null, null, program);
          u.setPathName(pathName);
          u.setRelativeName(relativeName);
          u.setFromSource(false);
          
          is.close();
          is = null;
          
          if(program.options().verbose())
            System.out.println("from " + pathName + " in " + (System.currentTimeMillis() - startTime) + " ms");
          return u;
        } catch (Exception e) {
          throw new Error("Error loading " + fullName, e);
        }
      } 
      else {
        try {  
          if(program.options().verbose())
            System.out.print("Loading .java file: " + fullName + " ");
            
          CompilationUnit u = program.javaParser.parse(is, fullName);
          is.close();
          is = null;
          
          u.setPathName(pathName);
          u.setRelativeName(relativeName);
          u.setFromSource(true);

          if(program.options().verbose())
            System.out.println("in " + (System.currentTimeMillis() - startTime) + " ms");
          return u;
        } catch (Exception e) {
          System.err.println("Unexpected error of kind " + e.getClass().getName());
          throw new Error(fullName + ": " + e.getMessage(), e);
        }
      }
    }


}
