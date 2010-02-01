
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class BytecodeTypeAccess extends TypeAccess implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public BytecodeTypeAccess clone() throws CloneNotSupportedException {
        BytecodeTypeAccess node = (BytecodeTypeAccess)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public BytecodeTypeAccess copy() {
      try {
          BytecodeTypeAccess node = (BytecodeTypeAccess)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public BytecodeTypeAccess fullCopy() {
        BytecodeTypeAccess res = (BytecodeTypeAccess)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in BoundNames.ast at line 3
    // Declared in BoundNames.ast line 10

    public BytecodeTypeAccess() {
        super();


    }

    // Declared in BoundNames.ast at line 10


    // Declared in BoundNames.ast line 10
    public BytecodeTypeAccess(String p0, String p1) {
        setPackage(p0);
        setID(p1);
    }

    // Declared in BoundNames.ast at line 16


    // Declared in BoundNames.ast line 10
    public BytecodeTypeAccess(beaver.Symbol p0, beaver.Symbol p1) {
        setPackage(p0);
        setID(p1);
    }

    // Declared in BoundNames.ast at line 21


  protected int numChildren() {
    return 0;
  }

    // Declared in BoundNames.ast at line 24

    public boolean mayHaveRewrite() {
        return true;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 20
    public void setPackage(String value) {
        tokenString_Package = value;
    }

    // Declared in java.ast at line 5

    public void setPackage(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setPackage is only valid for String lexemes");
        tokenString_Package = (String)symbol.value;
        Packagestart = symbol.getStart();
        Packageend = symbol.getEnd();
    }

    // Declared in java.ast at line 12

    public String getPackage() {
        return tokenString_Package != null ? tokenString_Package : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 20
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 5

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 12

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

public ASTNode rewriteTo() {
    // Declared in BoundNames.jrag at line 95
        state().duringBoundNames++;
        ASTNode result = rewriteRule0();
        state().duringBoundNames--;
        return result;
}

    // Declared in BoundNames.jrag at line 95
    private Access rewriteRule0() {
{
      if(name().indexOf("$") == -1)
        return new TypeAccess(packageName(), name());
      else {
        String[] names = name().split("\\$");
        Access a = null; // the resulting access
        String newName = null; // the subname to try
        TypeDecl type = null; // qualifying type if one
        for(int i = 0; i < names.length; i++) {
          newName = newName == null ? names[i] : (newName + "$" + names[i]);
          SimpleSet set;
          if(type != null)
            set = type.memberTypes(newName);
          else if(packageName().equals(""))
            set = lookupType(newName);
          else {
            TypeDecl typeDecl = lookupType(packageName(), newName);
            set = SimpleSet.emptySet;
            if(typeDecl != null)
              set = set.add(typeDecl);
          }
          if(!set.isEmpty()) {
            a = a == null ? (Access)new TypeAccess(packageName(), newName) : (Access)a.qualifiesAccess(new TypeAccess(newName));
            type = (TypeDecl)set.iterator().next();
            newName = null; // reset subname
          }
        }
        if(a == null) {
          a = new TypeAccess(packageName(), name());
        }
        return a;
      }
    }    }
}
