package soot.JastAddJ;

import java.util.HashSet;
import java.util.LinkedHashSet;
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
public class FolderPart extends PathPart {

    private HashMap map = new HashMap();


    private File folder;



    public FolderPart(File folder) {
      this.folder = folder;
    }



    public boolean hasPackage(String name) {
      return filesInPackage(name) != null;
    }



    public boolean hasCompilationUnit(String canonicalName) {
      int index = canonicalName.lastIndexOf('.');
      String packageName = index == -1 ? "" : canonicalName.substring(0, index);
      String typeName = canonicalName.substring(index + 1, canonicalName.length());
      Collection c = filesInPackage(packageName);
      boolean result = c != null && c.contains(typeName + fileSuffix());
      return result;
    }


    
    private Collection filesInPackage(String packageName) {
      if(!map.containsKey(packageName)) {
        File f = new File(folder, packageName.replace('.', File.separatorChar));
        Collection c = Collections.EMPTY_LIST;
        if(f.exists() && f.isDirectory()) {
          String[] files = f.list();
          if(files.length > 0) {
            c = new HashSet();
            for(int i = 0; i < files.length; i++)
              c.add(files[i]);
          }
        }
        else
          c = null;
        map.put(packageName, c);
      }
      return (Collection)map.get(packageName);
    }


    
    public boolean selectCompilationUnit(String canonicalName) throws IOException {
      if(hasCompilationUnit(canonicalName)) {
        String fileName = canonicalName.replace('.', File.separatorChar);
        File classFile = new File(folder, fileName + fileSuffix());
        if(classFile.isFile()) {
          is = new FileInputStream(classFile);
          age = classFile.lastModified();
          pathName = classFile.getPath();
          relativeName = fileName + fileSuffix();
          fullName = canonicalName;
          return true;
        }
      }
      return false;
    }


}
