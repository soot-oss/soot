
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class VariableArityParameterDeclaration extends ParameterDeclaration implements Cloneable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public VariableArityParameterDeclaration clone() throws CloneNotSupportedException {
        VariableArityParameterDeclaration node = (VariableArityParameterDeclaration)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public VariableArityParameterDeclaration copy() {
      try {
          VariableArityParameterDeclaration node = (VariableArityParameterDeclaration)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public VariableArityParameterDeclaration fullCopy() {
        VariableArityParameterDeclaration res = (VariableArityParameterDeclaration)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in VariableArityParameters.jrag at line 15

  // 8.4.1

  /* The last formal parameter in a list is special; it may be a variable arity
  parameter, indicated by an elipsis following the type.*/
  public void nameCheck() {
    super.nameCheck();
    if(!variableArityValid())
      error("only the last formal paramater may be of variable arity");
  }

    // Declared in VariableArityParameters.jrag at line 101


  // 15.12.2

/*
A method is applicable if it is either applicable by subtyping (\ufffd15.12.2.2),
applicable by method invocation conversion (\ufffd15.12.2.3), or it is an applicable
variable arity method (\ufffd15.12.2.4).

The process of determining applicability begins by determining the potentially
applicable methods (\ufffd15.12.2.1). The remainder of the process is split into
three phases.

The first phase (\ufffd15.12.2.2) performs overload resolution without permitting
boxing or unboxing conversion, or the use of variable arity method invocation.
If no applicable method is found during this phase then processing continues to
the second phase.

The second phase (\ufffd15.12.2.3) performs overload resolution while allowing
boxing and unboxing, but still precludes the use of variable arity method
invocation. If no applicable method is found during this phase then processing
continues to the third phase.

The third phase (\ufffd15.12.2.4) allows overloading to be combined with variable
arity methods, boxing and unboxing.

Deciding whether a method is applicable will, in the case of generic methods
(\ufffd8.4.4), require that actual type arguments be determined. Actual type
arguments may be passed explicitly or implicitly. If they are passed
implicitly, they must be inferred (\ufffd15.12.2.7) from the types of the argument
expressions.

If several applicable methods have been identified during one of the three
phases of applicability testing, then the most specific one is chosen, as
specified in section \ufffd15.12.2.5. See the following subsections for details.
*/

  public void toString(StringBuffer s) {
    getModifiers().toString(s);
    getTypeAccess().toString(s);
    s.append(" ... " + name());
  }

    // Declared in VariableArityParameters.ast at line 3
    // Declared in VariableArityParameters.ast line 1

    public VariableArityParameterDeclaration() {
        super();


    }

    // Declared in VariableArityParameters.ast at line 10


    // Declared in VariableArityParameters.ast line 1
    public VariableArityParameterDeclaration(Modifiers p0, Access p1, String p2) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
    }

    // Declared in VariableArityParameters.ast at line 17


    // Declared in VariableArityParameters.ast line 1
    public VariableArityParameterDeclaration(Modifiers p0, Access p1, beaver.Symbol p2) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
    }

    // Declared in VariableArityParameters.ast at line 23


  protected int numChildren() {
    return 2;
  }

    // Declared in VariableArityParameters.ast at line 26

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 84
    public void setModifiers(Modifiers node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Modifiers getModifiers() {
        return (Modifiers)getChild(0);
    }

    // Declared in java.ast at line 9


    public Modifiers getModifiersNoTransform() {
        return (Modifiers)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 84
    public void setTypeAccess(Access node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Access getTypeAccess() {
        return (Access)getChild(1);
    }

    // Declared in java.ast at line 9


    public Access getTypeAccessNoTransform() {
        return (Access)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 84
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

    // Declared in VariableArityParameters.jrag at line 30
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

    private TypeDecl type_compute() {  return super.type().arrayType();  }

    // Declared in VariableArityParameters.jrag at line 36
 @SuppressWarnings({"unchecked", "cast"})     public boolean isVariableArity() {
        ASTNode$State state = state();
        boolean isVariableArity_value = isVariableArity_compute();
        return isVariableArity_value;
    }

    private boolean isVariableArity_compute() {  return true;  }

    // Declared in VariableArityParameters.jrag at line 26
 @SuppressWarnings({"unchecked", "cast"})     public boolean variableArityValid() {
        ASTNode$State state = state();
        boolean variableArityValid_value = getParent().Define_boolean_variableArityValid(this, null);
        return variableArityValid_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
