
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class Dot extends AbstractDot implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public Dot clone() throws CloneNotSupportedException {
        Dot node = (Dot)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public Dot copy() {
      try {
          Dot node = (Dot)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public Dot fullCopy() {
        Dot res = (Dot)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in ResolveAmbiguousNames.jrag at line 99


  public Dot lastDot() {
    Dot node = this;
    while(node.getRightNoTransform() instanceof Dot)
      node = (Dot)node.getRightNoTransform();
    return node;
  }

    // Declared in ResolveAmbiguousNames.jrag at line 111

  
  public Dot qualifiesAccess(Access access) {
    Dot lastDot = lastDot();
    Dot dot = new Dot(lastDot.getRightNoTransform(), access);
    lastDot.setRight(dot);
    return this;
  }

    // Declared in ResolveAmbiguousNames.jrag at line 119

  
  // Used when replacing pairs from a list to concatenate the result to the tail of the current location.
  private Access qualifyTailWith(Access expr) {
    if(getRight/*NoTransform*/() instanceof AbstractDot) {
      AbstractDot dot = (AbstractDot)getRight/*NoTransform*/();
      return expr.qualifiesAccess(dot.getRight/*NoTransform*/());
    }
    return expr;
  }

    // Declared in ResolveAmbiguousNames.jrag at line 136

  public Access extractLast() {
    return lastDot().getRightNoTransform();
  }

    // Declared in ResolveAmbiguousNames.jrag at line 139

  public void replaceLast(Access access) {
    lastDot().setRight(access);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 14

    public Dot() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 14
    public Dot(Expr p0, Access p1) {
        setChild(p0, 0);
        setChild(p1, 1);
    }

    // Declared in java.ast at line 15


  protected int numChildren() {
    return 2;
  }

    // Declared in java.ast at line 18

    public boolean mayHaveRewrite() {
        return true;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 13
    public void setLeft(Expr node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Expr getLeft() {
        return (Expr)getChild(0);
    }

    // Declared in java.ast at line 9


    public Expr getLeftNoTransform() {
        return (Expr)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 13
    public void setRight(Access node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Access getRight() {
        return (Access)getChild(1);
    }

    // Declared in java.ast at line 9


    public Access getRightNoTransform() {
        return (Access)getChildNoTransform(1);
    }

public ASTNode rewriteTo() {
    // Declared in ResolveAmbiguousNames.jrag at line 205
    if(!duringSyntacticClassification() && leftSide().isPackageAccess() && rightSide().isPackageAccess()) {
        state().duringResolveAmbiguousNames++;
        ASTNode result = rewriteRule0();
        state().duringResolveAmbiguousNames--;
        return result;
    }

    // Declared in ResolveAmbiguousNames.jrag at line 217
    if(!duringSyntacticClassification() && leftSide().isPackageAccess() && !((Access)leftSide()).hasPrevExpr() && rightSide() instanceof TypeAccess) {
        state().duringResolveAmbiguousNames++;
        ASTNode result = rewriteRule1();
        state().duringResolveAmbiguousNames--;
        return result;
    }

    return super.rewriteTo();
}

    // Declared in ResolveAmbiguousNames.jrag at line 205
    private Access rewriteRule0() {
{
      PackageAccess left = (PackageAccess)leftSide();
      PackageAccess right = (PackageAccess)rightSide();
      left.setPackage(left.getPackage() + "." + right.getPackage());
      left.setEnd(right.end());
      return qualifyTailWith(left);
    }    }
    // Declared in ResolveAmbiguousNames.jrag at line 217
    private Access rewriteRule1() {
{
      PackageAccess left = (PackageAccess)leftSide();
      TypeAccess right = (TypeAccess)rightSide();
      right.setPackage(left.getPackage());
      right.setStart(left.start());
      return qualifyTailWith(right);
    }    }
}
