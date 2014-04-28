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
public class FileNamesPart extends PathPart {

    private HashMap sourceFiles = new HashMap();


    private HashSet packages = new HashSet();



    public FileNamesPart(Program p) {
      isSource = true;
      program = p;
    }



    public boolean hasPackage(String name) { return packages.contains(name); }


    public boolean isEmpty() { return sourceFiles.isEmpty(); }


    public Collection keySet() { return sourceFiles.keySet(); }



    public boolean selectCompilationUnit(String canonicalName) throws IOException {
      if(sourceFiles.containsKey(canonicalName)) {
        String f = (String)sourceFiles.get(canonicalName);
        File classFile = new File(f);
        if(classFile.isFile()) {
          is = new FileInputStream(classFile);
          pathName = classFile.getPath();
          relativeName = f;
          fullName = canonicalName;
          sourceFiles.remove(canonicalName);
          return true;
        }
      }
      return false;
    }



    /**
     * Add a source file to be parsed.
     * @return The CompilationUnit representing the source file,
     * or <code>null</code> if no such file exists
     */
    public CompilationUnit addSourceFile(String name) {
      try {
        File classFile = new File(name);
        if(classFile.isFile()) {
          is = new FileInputStream(classFile);
          this.pathName = classFile.getPath();
          relativeName = name;
          fullName = name; // is this ok
          CompilationUnit u = getCompilationUnit();
          if(u != null) {
            program.addCompilationUnit(u);
            String packageName = u.getPackageDecl();
            if(packageName != null && !packages.contains(packageName)) {
              packages.add(packageName);
              int pos = 0;
              while(packageName != null && -1 != (pos = packageName.indexOf('.', pos + 1))) {
                String n = packageName.substring(0, pos);
                if(!packages.contains(n))
                  packages.add(n);
              }
            }
          }
          return u;
        }
      } catch (IOException e) {
      }
      return null;
    }


}
