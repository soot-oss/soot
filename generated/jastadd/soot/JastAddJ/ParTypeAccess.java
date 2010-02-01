
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


// parameterized type access

public class ParTypeAccess extends Access implements Cloneable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParTypeAccess clone() throws CloneNotSupportedException {
        ParTypeAccess node = (ParTypeAccess)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParTypeAccess copy() {
      try {
          ParTypeAccess node = (ParTypeAccess)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParTypeAccess fullCopy() {
        ParTypeAccess res = (ParTypeAccess)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Generics.jrag at line 303

  public boolean isRaw() {
    return false;
  }

    // Declared in Generics.jrag at line 411


  public void typeCheck() {
    super.typeCheck();
    if(!genericDecl().isUnknown()) {
      TypeDecl type = type();
      if(!genericDecl().isGenericType()) {
        error(genericDecl().typeName() + " is not a generic type but used as one in " + this);
      }
      else if(!type.isRawType() && type.isNestedType() && type.enclosingType().isRawType())
        error("Can not access a member type of a raw type as a parameterized type");
      else {
        GenericTypeDecl decl = (GenericTypeDecl)genericDecl();
        GenericTypeDecl original = (GenericTypeDecl)decl.original();
        if(original.getNumTypeParameter() != getNumTypeArgument()) {
          error(decl.typeName() + " takes " + original.getNumTypeParameter() + " type parameters, not " + getNumTypeArgument() + " as used in " + this);
        }
        else {
          ParTypeDecl typeDecl = (ParTypeDecl)type();
          for(int i = 0; i < getNumTypeArgument(); i++) {
            if(!getTypeArgument(i).type().instanceOf(original.getTypeParameter(i))) {
              error("type argument " + i + " is of type " + getTypeArgument(i).type().typeName() 
                  + " which is not a subtype of " + original.getTypeParameter(i).typeName());
            }
          }
        }
      }
    }
  }

    // Declared in GenericsPrettyPrint.jrag at line 23


  public void toString(StringBuffer s) {
    getTypeAccess().toString(s);
    s.append("<");
    for(int i = 0; i < getNumTypeArgument(); i++) {
      if(i != 0)
        s.append(", ");
      getTypeArgument(i).toString(s);
    }
    s.append(">");
  }

    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 13

    public ParTypeAccess() {
        super();

        setChild(new List(), 1);

    }

    // Declared in Generics.ast at line 11


    // Declared in Generics.ast line 13
    public ParTypeAccess(Access p0, List<Access> p1) {
        setChild(p0, 0);
        setChild(p1, 1);
    }

    // Declared in Generics.ast at line 16


  protected int numChildren() {
    return 2;
  }

    // Declared in Generics.ast at line 19

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 13
    public void setTypeAccess(Access node) {
        setChild(node, 0);
    }

    // Declared in Generics.ast at line 5

    public Access getTypeAccess() {
        return (Access)getChild(0);
    }

    // Declared in Generics.ast at line 9


    public Access getTypeAccessNoTransform() {
        return (Access)getChildNoTransform(0);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 13
    public void setTypeArgumentList(List<Access> list) {
        setChild(list, 1);
    }

    // Declared in Generics.ast at line 6


    public int getNumTypeArgument() {
        return getTypeArgumentList().getNumChild();
    }

    // Declared in Generics.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getTypeArgument(int i) {
        return (Access)getTypeArgumentList().getChild(i);
    }

    // Declared in Generics.ast at line 14


    public void addTypeArgument(Access node) {
        List<Access> list = (parent == null || state == null) ? getTypeArgumentListNoTransform() : getTypeArgumentList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 19


    public void addTypeArgumentNoTransform(Access node) {
        List<Access> list = getTypeArgumentListNoTransform();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 24


    public void setTypeArgument(Access node, int i) {
        List<Access> list = getTypeArgumentList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 28

    public List<Access> getTypeArguments() {
        return getTypeArgumentList();
    }

    // Declared in Generics.ast at line 31

    public List<Access> getTypeArgumentsNoTransform() {
        return getTypeArgumentListNoTransform();
    }

    // Declared in Generics.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getTypeArgumentList() {
        List<Access> list = (List<Access>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in Generics.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getTypeArgumentListNoTransform() {
        return (List<Access>)getChildNoTransform(1);
    }

    // Declared in Generics.jrag at line 238
 @SuppressWarnings({"unchecked", "cast"})     public Expr unqualifiedScope() {
        ASTNode$State state = state();
        Expr unqualifiedScope_value = unqualifiedScope_compute();
        return unqualifiedScope_value;
    }

    private Expr unqualifiedScope_compute() {  return getParent() instanceof Access ? ((Access)getParent()).unqualifiedScope() : super.unqualifiedScope();  }

    // Declared in Generics.jrag at line 241
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        if(type_computed) {
            return type_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        type_value = type_compute();
        if(isFinal && num == state().boundariesCrossed)
            type_computed = true;
        return type_value;
    }

    private TypeDecl type_compute() {
    TypeDecl typeDecl = genericDecl();
    if(typeDecl instanceof GenericTypeDecl) {
      // use signature in lookup for types that are used in extends and implements clauses
      if(unqualifiedScope().getParent().getParent() instanceof TypeDecl)
        return ((GenericTypeDecl)typeDecl).lookupParTypeDecl(this);
      ArrayList args = new ArrayList();
      for(int i = 0; i < getNumTypeArgument(); i++)
        args.add(getTypeArgument(i).type());
      return ((GenericTypeDecl)typeDecl).lookupParTypeDecl(args);
    }
    return typeDecl;
  }

    // Declared in Generics.jrag at line 254
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl genericDecl() {
        ASTNode$State state = state();
        TypeDecl genericDecl_value = genericDecl_compute();
        return genericDecl_value;
    }

    private TypeDecl genericDecl_compute() {  return getTypeAccess().type();  }

    // Declared in Generics.jrag at line 255
 @SuppressWarnings({"unchecked", "cast"})     public boolean isTypeAccess() {
        ASTNode$State state = state();
        boolean isTypeAccess_value = isTypeAccess_compute();
        return isTypeAccess_value;
    }

    private boolean isTypeAccess_compute() {  return true;  }

    // Declared in Generics.jrag at line 239
    public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
        if(caller == getTypeArgumentListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return unqualifiedScope().lookupType(name);
        }
        return getParent().Define_SimpleSet_lookupType(this, caller, name);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
