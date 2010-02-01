
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public abstract class PostfixExpr extends Unary implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public PostfixExpr clone() throws CloneNotSupportedException {
        PostfixExpr node = (PostfixExpr)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
    // Declared in DefiniteAssignment.jrag at line 63

  
  public void definiteAssignment() {
    if(getOperand().isVariable()) {
      Variable v = getOperand().varDecl();
      if(v != null && v.isFinal()) {
        error("++ and -- can not be applied to final variable " + v);
      }
    }
  }

    // Declared in DefiniteAssignment.jrag at line 472

  
  // 16.2.2 10th bullet
  protected boolean checkDUeverywhere(Variable v) {
    if(getOperand().isVariable() && getOperand().varDecl() == v)
      if(!isDAbefore(v))
        return false;
    return super.checkDUeverywhere(v);
  }

    // Declared in TypeCheck.jrag at line 293


  // 15.14
  public void typeCheck() {
    if(!getOperand().isVariable())
      error("postfix expressions only work on variables");
    else if(!getOperand().type().isNumericType())
      error("postfix expressions only operates on numeric types");
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 149

    public PostfixExpr() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 149
    public PostfixExpr(Expr p0) {
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
    // Declared in java.ast line 139
    public void setOperand(Expr node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Expr getOperand() {
        return (Expr)getChild(0);
    }

    // Declared in java.ast at line 9


    public Expr getOperandNoTransform() {
        return (Expr)getChildNoTransform(0);
    }

    // Declared in DefiniteAssignment.jrag at line 45
    public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
        if(caller == getOperandNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isDest(this, caller);
    }

    // Declared in DefiniteAssignment.jrag at line 53
    public boolean Define_boolean_isIncOrDec(ASTNode caller, ASTNode child) {
        if(caller == getOperandNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isIncOrDec(this, caller);
    }

    // Declared in SyntacticClassification.jrag at line 98
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getOperandNoTransform()) {
            return NameType.EXPRESSION_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
