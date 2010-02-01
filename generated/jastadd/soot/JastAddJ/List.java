
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;
public class List<T extends ASTNode> extends ASTNode<T> implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public List<T> clone() throws CloneNotSupportedException {
        List node = (List)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public List<T> copy() {
      try {
          List node = (List)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public List<T> fullCopy() {
        List res = (List)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Generics.jrag at line 830


  public List substitute(Parameterization parTypeDecl) {
    List list = new List();
    for(int i = 0; i < getNumChild(); i++) {
      ASTNode node = getChild(i);
      if(node instanceof Access) {
        Access a = (Access)node;
        list.add(a.type().substitute(parTypeDecl));
      }
      else if(node instanceof VariableArityParameterDeclaration) {
        VariableArityParameterDeclaration p = (VariableArityParameterDeclaration)node;
        list.add(
          new VariableArityParameterDeclarationSubstituted(
            (Modifiers)p.getModifiers().fullCopy(),
            // use the type acces since VariableArity adds to the dimension
            p.getTypeAccess().type().substituteParameterType(parTypeDecl),
            p.getID(),
            p
          )
        );
      }
      else if(node instanceof ParameterDeclaration) {
        ParameterDeclaration p = (ParameterDeclaration)node;
        list.add(
          new ParameterDeclarationSubstituted(
            (Modifiers)p.getModifiers().fullCopy(),
            p.type().substituteParameterType(parTypeDecl),
            p.getID(),
            p
          )
        );
      }
      else {
        throw new Error("Can only substitute lists of access nodes but node number " + i + " is of type " + node.getClass().getName());
      }
    }
    return list;
  }

    // Declared in List.ast at line 3
    // Declared in List.ast line 0

    public List() {
        super();


    }

    // Declared in List.ast at line 9


     public List<T> add(T node) {
          addChild(node);
          return this;
     }

    // Declared in List.ast at line 14


     public void insertChild(T node, int i) {
          list$touched = true;
          super.insertChild(node, i);
     }

    // Declared in List.ast at line 18

     public void addChild(T node) {
          list$touched = true;
          super.addChild(node);
     }

    // Declared in List.ast at line 22

     public void removeChild(int i) {
          list$touched = true;
          super.removeChild(i);
     }

    // Declared in List.ast at line 26

     public int getNumChild() {
          if(list$touched) {
              for(int i = 0; i < getNumChildNoTransform(); i++)
                  getChild(i);
              list$touched = false;
          }
          return getNumChildNoTransform();
     }

    // Declared in List.ast at line 34

     private boolean list$touched = true;

    // Declared in List.ast at line 35

    public boolean mayHaveRewrite() {
        return true;
    }

    // Declared in LookupConstructor.jrag at line 178
 @SuppressWarnings({"unchecked", "cast"})     public boolean requiresDefaultConstructor() {
        ASTNode$State state = state();
        boolean requiresDefaultConstructor_value = requiresDefaultConstructor_compute();
        return requiresDefaultConstructor_value;
    }

    private boolean requiresDefaultConstructor_compute() {
    if(getParent() instanceof ClassDecl) {
      ClassDecl c = (ClassDecl)getParent();
      return c.getBodyDeclList() == this && !(c instanceof AnonymousDecl) && c.noConstructor();
    }
    return false;
  }

    // Declared in BooleanExpressions.jrag at line 23
 @SuppressWarnings({"unchecked", "cast"})     public boolean definesLabel() {
        ASTNode$State state = state();
        boolean definesLabel_value = definesLabel_compute();
        return definesLabel_value;
    }

    private boolean definesLabel_compute() {  return getParent().definesLabel();  }

public ASTNode rewriteTo() {
    if(list$touched) {
        for(int i = 0 ; i < getNumChildNoTransform(); i++)
            getChild(i);
        list$touched = false;
        return this;
    }
    // Declared in LookupConstructor.jrag at line 187
    if(requiresDefaultConstructor()) {
        state().duringLookupConstructor++;
        ASTNode result = rewriteRule0();
        state().duringLookupConstructor--;
        return result;
    }

    return super.rewriteTo();
}

    // Declared in LookupConstructor.jrag at line 187
    private List rewriteRule0() {
{
      ClassDecl c = (ClassDecl)getParent();
      Modifiers m = new Modifiers();
      if(c.isPublic()) m.addModifier(new Modifier("public"));
      else if(c.isProtected()) m.addModifier(new Modifier("protected"));
      else if(c.isPrivate()) m.addModifier(new Modifier("private"));
      c.addBodyDecl(
          new ConstructorDecl(
            m,
            c.name(),
            new List(),
            new List(),
            new Opt(),
            new Block()
          )
      );
      return this;
    }    }
}
