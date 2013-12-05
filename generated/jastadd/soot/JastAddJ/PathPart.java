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
  * @ast class
 * 
 */
public class PathPart extends java.lang.Object {

    public InputStream is;


    protected String pathName;

 
    protected String relativeName;


    protected String fullName;


    long age;


    Program program;


    
    protected PathPart() {
    }



    protected boolean isSource;


    protected String fileSuffix() {
      return isSource ? ".java" : ".class";
    }



    public static PathPart createSourcePath(String fileName, Program program) {
      PathPart p = createPathPart(fileName);
      if(p != null) {
        p.isSource = true;
        p.program = program;
      }
      return p;
    }



    public static PathPart createClassPath(String fileName, Program program) {
      PathPart p = createPathPart(fileName);
      if(p != null) {
        p.isSource = false;
        p.program = program;
      }
      return p;
    }



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



    // is there a package with the specified name on this path part
    public boolean hasPackage(String name) { return false; }


    
    // select a compilation unit from a canonical name
    // returns true of the compilation unit exists on this path
    public boolean selectCompilationUnit(String canonicalName) throws IOException { return false; }



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
