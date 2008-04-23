
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;

public class PathPart extends java.lang.Object {
    // Declared in ClassPath.jrag at line 275

    public InputStream is;

    // Declared in ClassPath.jrag at line 276

    protected String pathName;

    // Declared in ClassPath.jrag at line 277
 
    protected String relativeName;

    // Declared in ClassPath.jrag at line 278

    protected String fullName;

    // Declared in ClassPath.jrag at line 279

    long age;

    // Declared in ClassPath.jrag at line 280

    Program program;

    // Declared in ClassPath.jrag at line 282

    
    protected PathPart() {
    }

    // Declared in ClassPath.jrag at line 285


    protected boolean isSource;

    // Declared in ClassPath.jrag at line 286

    protected String fileSuffix() {
      return isSource ? ".java" : ".class";
    }

    // Declared in ClassPath.jrag at line 290


    public static PathPart createSourcePath(String fileName, Program program) {
      PathPart p = createPathPart(fileName);
      if(p != null) {
        p.isSource = true;
        p.program = program;
      }
      return p;
    }

    // Declared in ClassPath.jrag at line 299


    public static PathPart createClassPath(String fileName, Program program) {
      PathPart p = createPathPart(fileName);
      if(p != null) {
        p.isSource = false;
        p.program = program;
      }
      return p;
    }

    // Declared in ClassPath.jrag at line 308


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

    // Declared in ClassPath.jrag at line 322


    // is there a package with the specified name on this path part
    public boolean hasPackage(String name) { return false; }

    // Declared in ClassPath.jrag at line 326

    
    // select a compilation unit from a canonical name
    // returns true of the compilation unit exists on this path
    public boolean selectCompilationUnit(String canonicalName) throws IOException { return false; }

    // Declared in ClassPath.jrag at line 329


    // load the return currently selected compilation unit
    public CompilationUnit getCompilationUnit() {
      long startTime = System.currentTimeMillis();
      if(!isSource) {
        try {
          if(Program.verbose())
            System.out.print("Loading .class file: " + fullName + " ");

          CompilationUnit u = program.bytecodeReader.read(is, fullName, program);
          //CompilationUnit u = new bytecode.Parser(is, fullName).parse(null, null, program);
          u.setPathName(pathName);
          u.setRelativeName(relativeName);
          u.setFromSource(false);
          
          is.close();
          is = null;
          
          if(Program.verbose())
            System.out.println("from " + pathName + " in " + (System.currentTimeMillis() - startTime) + " ms");
          return u;
        } catch (Exception e) {
          throw new Error("Error loading " + fullName, e);
        }
      } 
      else {
        try {  
          if(Program.verbose())
            System.out.print("Loading .java file: " + fullName + " ");
            
          CompilationUnit u = program.javaParser.parse(is, fullName);
          is.close();
          is = null;
          
          u.setPathName(pathName);
          u.setRelativeName(relativeName);
          u.setFromSource(true);

          if(Program.verbose())
            System.out.println("in " + (System.currentTimeMillis() - startTime) + " ms");
          return u;
        } catch (Exception e) {
          System.err.println("Unexpected error of kind " + e.getClass().getName());
          throw new Error(fullName + ": " + e.getMessage(), e);
        }
      }
    }


}
