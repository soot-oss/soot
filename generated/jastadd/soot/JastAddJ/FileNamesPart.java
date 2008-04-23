
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;
// load files specified explicitly (on the command line)
public class FileNamesPart extends PathPart {
    // Declared in ClassPath.jrag at line 485

    private HashMap sourceFiles = new HashMap();

    // Declared in ClassPath.jrag at line 486

    private HashSet packages = new HashSet();

    // Declared in ClassPath.jrag at line 488


    public FileNamesPart(Program p) {
      isSource = true;
      program = p;
    }

    // Declared in ClassPath.jrag at line 493


    public boolean hasPackage(String name) { return packages.contains(name); }

    // Declared in ClassPath.jrag at line 494

    public boolean isEmpty() { return sourceFiles.isEmpty(); }

    // Declared in ClassPath.jrag at line 495

    public Collection keySet() { return sourceFiles.keySet(); }

    // Declared in ClassPath.jrag at line 497


    public boolean selectCompilationUnit(String canonicalName) throws IOException {
      if(sourceFiles.containsKey(canonicalName)) {
        String f = (String)sourceFiles.get(canonicalName);
        File classFile = new File(f);
        if(classFile.isFile()) {
          is = new FileInputStream(classFile);
          pathName = classFile.getAbsolutePath(); // TODO: check me"";
          relativeName = f;
          fullName = canonicalName;
          sourceFiles.remove(canonicalName);
          return true;
        }
      }
      return false;
    }

    // Declared in ClassPath.jrag at line 512

    public void addSourceFile(String name) {
      try {
        File classFile = new File(name);
        if(classFile.isFile()) {
          is = new FileInputStream(classFile);
          this.pathName = classFile.getAbsolutePath();
          relativeName = name;
          fullName = name; // is this ok
          CompilationUnit u = getCompilationUnit();
          if(u != null) {
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
            program.addCompilationUnit(u);
          }
        }
      } catch (IOException e) {
      }
    }


}
