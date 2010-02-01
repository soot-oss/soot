
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class ConstCase extends Case implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ConstCase clone() throws CloneNotSupportedException {
        ConstCase node = (ConstCase)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ConstCase copy() {
      try {
          ConstCase node = (ConstCase)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ConstCase fullCopy() {
        ConstCase res = (ConstCase)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in NameCheck.jrag at line 401

  
  public void nameCheck() {
    if(getValue().isConstant() && bind(this) != this) {
      error("constant expression " + getValue() + " is multiply declared in two case statements");
    }
  }

    // Declared in PrettyPrint.jadd at line 561


  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("case ");
    getValue().toString(s);
    s.append(":");
  }

    // Declared in TypeCheck.jrag at line 349


  public void refined_TypeCheck_ConstCase_typeCheck() {
    TypeDecl switchType = switchType();
    TypeDecl type = getValue().type();
    if(!type.assignConversionTo(switchType, getValue()))
      error("Constant expression must be assignable to Expression");
    if(!getValue().isConstant() && !getValue().type().isUnknown()) 
      error("Switch expression must be constant");
  }

    // Declared in EnumsCodegen.jrag at line 32


  public void transformation() {
    if(getValue() instanceof VarAccess && getValue().varDecl() instanceof EnumConstant) {
      int i = hostType().createEnumIndex((EnumConstant)getValue().varDecl());
      setValue(new IntegerLiteral(new Integer(i).toString()));
    }
    super.transformation();
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 207

    public ConstCase() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 207
    public ConstCase(Expr p0) {
        setChild(p0, 0);
    }

    // Declared in java.ast at line 14


  protected int numChildren() {
    return 1;
  }

    // Declared in java.ast at line 17

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 207
    public void setValue(Expr node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Expr getValue() {
        return (Expr)getChild(0);
    }

    // Declared in java.ast at line 9


    public Expr getValueNoTransform() {
        return (Expr)getChildNoTransform(0);
    }

    // Declared in Enums.jrag at line 482

  
    public void typeCheck() {
    if(switchType().isEnumDecl() && (!(getValue() instanceof VarAccess) || !(getValue().varDecl() instanceof EnumConstant)))
      error("Unqualified enumeration constant required");
    else
      refined_TypeCheck_ConstCase_typeCheck();
  }

    // Declared in NameCheck.jrag at line 427
private boolean refined_NameCheck_ConstCase_constValue_Case(Case c)
{
    if(!(c instanceof ConstCase) || !getValue().isConstant())
      return false;
    if(!getValue().type().assignableToInt() || !((ConstCase)c).getValue().type().assignableToInt())
      return false;
    return getValue().constant().intValue() == ((ConstCase)c).getValue().constant().intValue();
  }

    // Declared in Enums.jrag at line 488
 @SuppressWarnings({"unchecked", "cast"})     public boolean constValue(Case c) {
        ASTNode$State state = state();
        boolean constValue_Case_value = constValue_compute(c);
        return constValue_Case_value;
    }

    private boolean constValue_compute(Case c) {
    if(switchType().isEnumDecl()) {
      if(!(c instanceof ConstCase) || !getValue().isConstant())
        return false;
      return getValue().varDecl() == ((ConstCase)c).getValue().varDecl();
    }
    else
      return refined_NameCheck_ConstCase_constValue_Case(c);
  }

    // Declared in Enums.jrag at line 477
    public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
        if(caller == getValueNoTransform()) {
            return switchType().isEnumDecl() ? switchType().memberFields(name) : lookupVariable(name);
        }
        return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
