
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class EnumConstant extends FieldDeclaration implements Cloneable {
    public void flushCache() {
        super.flushCache();
        getTypeAccess_computed = false;
        getTypeAccess_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public EnumConstant clone() throws CloneNotSupportedException {
        EnumConstant node = (EnumConstant)super.clone();
        node.getTypeAccess_computed = false;
        node.getTypeAccess_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public EnumConstant copy() {
      try {
          EnumConstant node = (EnumConstant)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public EnumConstant fullCopy() {
        EnumConstant res = (EnumConstant)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Enums.jrag at line 184

  
  public EnumConstant(Modifiers mods, String name, List<Expr> args, List<BodyDecl> bds) {
	  this(mods, name, args, new Opt<Expr>(new EnumInstanceExpr(createOptAnonymousDecl(bds))));
  }

    // Declared in Enums.jrag at line 223


  /*
    4) The optional class body of an enum constant implicitly defines an anonymous
    class declaration (\ufffd15.9.5) that extends the immediately enclosing enum type.
    The class body is governed by the usual rules of anonymous classes; in
    particular it cannot contain any constructors.

    TODO: work on error messages
  */
  
  private static Opt<TypeDecl> createOptAnonymousDecl(List<BodyDecl> bds) {
    if(bds.getNumChild() == 0)
      return new Opt<TypeDecl>();
    return new Opt<TypeDecl>(
      new AnonymousDecl(
        new Modifiers(),
        "Anonymous",
        bds
      )
    );
  }

    // Declared in Enums.jrag at line 236

  
  // simulate list of body declarations
  public int getNumBodyDecl() {
	  int cnt = 0;
	  ClassInstanceExpr init = (ClassInstanceExpr)getInit();
	  if(!init.hasTypeDecl())
		  return 0;
	  for(BodyDecl bd : init.getTypeDecl().getBodyDecls())
		  if(!(bd instanceof ConstructorDecl))
			  ++cnt;
	  return cnt;
  }

    // Declared in Enums.jrag at line 247

  
  public BodyDecl getBodyDecl(int i) {
	  ClassInstanceExpr init = (ClassInstanceExpr)getInit();
	  if(init.hasTypeDecl())
		  for(BodyDecl bd : init.getTypeDecl().getBodyDecls())
			  if(!(bd instanceof ConstructorDecl))
				  if(i-- == 0)
					  return bd;
	  throw new ArrayIndexOutOfBoundsException(i);
  }

    // Declared in Enums.jrag at line 485


  // generic traversal should traverse NTA as well
  // this should be done automatically by the JastAdd
  public int getNumChild() {
    return 5;
  }

    // Declared in Enums.jrag at line 488

  public ASTNode getChild(int i) {
    switch(i) {
      case 3: return getTypeAccess();
      case 4: return getInitOpt();
      default: return ASTNode.getChild(this, i);
    }
  }

    // Declared in Enums.jrag at line 588


  public void toString(StringBuffer s) {
    s.append(indent());
    getModifiers().toString(s);
    s.append(getID());
    s.append("(");
    if(getNumArg() > 0) {
      getArg(0).toString(s);
      for(int i = 1; i < getNumArg(); i++) {
        s.append(", ");
        getArg(i).toString(s);
      }
    }
    s.append(")");
    if(getNumBodyDecl() > 0) {
      s.append(" {");
      for(int i=0; i < getNumBodyDecl(); i++) {
        BodyDecl d = getBodyDecl(i);
        d.toString(s);
      }
      s.append(indent() + "}");
    }
    s.append(",\n");
  }

    // Declared in Enums.ast at line 3
    // Declared in Enums.ast line 3

    public EnumConstant() {
        super();

        setChild(new List(), 1);
        setChild(new Opt(), 2);

    }

    // Declared in Enums.ast at line 12


    // Declared in Enums.ast line 3
    public EnumConstant(Modifiers p0, String p1, List<Expr> p2, Opt<Expr> p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(null, 3);
    }

    // Declared in Enums.ast at line 21


    // Declared in Enums.ast line 3
    public EnumConstant(Modifiers p0, beaver.Symbol p1, List<Expr> p2, Opt<Expr> p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(null, 3);
    }

    // Declared in Enums.ast at line 29


  protected int numChildren() {
    return 3;
  }

    // Declared in Enums.ast at line 32

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in Enums.ast at line 2
    // Declared in Enums.ast line 3
    public void setModifiers(Modifiers node) {
        setChild(node, 0);
    }

    // Declared in Enums.ast at line 5

    public Modifiers getModifiers() {
        return (Modifiers)getChild(0);
    }

    // Declared in Enums.ast at line 9


    public Modifiers getModifiersNoTransform() {
        return (Modifiers)getChildNoTransform(0);
    }

    // Declared in Enums.ast at line 2
    // Declared in Enums.ast line 3
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in Enums.ast at line 5

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in Enums.ast at line 12

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in Enums.ast at line 2
    // Declared in Enums.ast line 3
    public void setArgList(List<Expr> list) {
        setChild(list, 1);
    }

    // Declared in Enums.ast at line 6


    public int getNumArg() {
        return getArgList().getNumChild();
    }

    // Declared in Enums.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Expr getArg(int i) {
        return (Expr)getArgList().getChild(i);
    }

    // Declared in Enums.ast at line 14


    public void addArg(Expr node) {
        List<Expr> list = (parent == null || state == null) ? getArgListNoTransform() : getArgList();
        list.addChild(node);
    }

    // Declared in Enums.ast at line 19


    public void addArgNoTransform(Expr node) {
        List<Expr> list = getArgListNoTransform();
        list.addChild(node);
    }

    // Declared in Enums.ast at line 24


    public void setArg(Expr node, int i) {
        List<Expr> list = getArgList();
        list.setChild(node, i);
    }

    // Declared in Enums.ast at line 28

    public List<Expr> getArgs() {
        return getArgList();
    }

    // Declared in Enums.ast at line 31

    public List<Expr> getArgsNoTransform() {
        return getArgListNoTransform();
    }

    // Declared in Enums.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Expr> getArgList() {
        List<Expr> list = (List<Expr>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in Enums.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Expr> getArgListNoTransform() {
        return (List<Expr>)getChildNoTransform(1);
    }

    // Declared in Enums.ast at line 2
    // Declared in Enums.ast line 3
    public void setInitOpt(Opt<Expr> opt) {
        setChild(opt, 2);
    }

    // Declared in Enums.ast at line 6


    public boolean hasInit() {
        return getInitOpt().getNumChild() != 0;
    }

    // Declared in Enums.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Expr getInit() {
        return (Expr)getInitOpt().getChild(0);
    }

    // Declared in Enums.ast at line 14


    public void setInit(Expr node) {
        getInitOpt().setChild(node, 0);
    }

    // Declared in Enums.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Expr> getInitOpt() {
        return (Opt<Expr>)getChild(2);
    }

    // Declared in Enums.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Expr> getInitOptNoTransform() {
        return (Opt<Expr>)getChildNoTransform(2);
    }

    // Declared in Enums.ast at line 2
    // Declared in Enums.ast line 3
    public void setTypeAccess(Access node) {
        setChild(node, 3);
    }

    // Declared in Enums.ast at line 5

    public Access getTypeAccessNoTransform() {
        return (Access)getChildNoTransform(3);
    }

    // Declared in Enums.ast at line 9


    protected int getTypeAccessChildPosition() {
        return 3;
    }

    // Declared in Enums.jrag at line 27
 @SuppressWarnings({"unchecked", "cast"})     public boolean isEnumConstant() {
        ASTNode$State state = state();
        boolean isEnumConstant_value = isEnumConstant_compute();
        return isEnumConstant_value;
    }

    private boolean isEnumConstant_compute() {  return true;  }

    // Declared in Enums.jrag at line 176
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPublic() {
        ASTNode$State state = state();
        boolean isPublic_value = isPublic_compute();
        return isPublic_value;
    }

    private boolean isPublic_compute() {  return true;  }

    // Declared in Enums.jrag at line 177
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStatic() {
        ASTNode$State state = state();
        boolean isStatic_value = isStatic_compute();
        return isStatic_value;
    }

    private boolean isStatic_compute() {  return true;  }

    // Declared in Enums.jrag at line 178
 @SuppressWarnings({"unchecked", "cast"})     public boolean isFinal() {
        ASTNode$State state = state();
        boolean isFinal_value = isFinal_compute();
        return isFinal_value;
    }

    private boolean isFinal_compute() {  return true;  }

    protected boolean getTypeAccess_computed = false;
    protected Access getTypeAccess_value;
    // Declared in Enums.jrag at line 180
 @SuppressWarnings({"unchecked", "cast"})     public Access getTypeAccess() {
        if(getTypeAccess_computed) {
            return (Access)ASTNode.getChild(this, getTypeAccessChildPosition());
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getTypeAccess_value = getTypeAccess_compute();
            setTypeAccess(getTypeAccess_value);
        if(isFinal && num == state().boundariesCrossed)
            getTypeAccess_computed = true;
        return (Access)ASTNode.getChild(this, getTypeAccessChildPosition());
    }

    private Access getTypeAccess_compute() {
    return hostType().createQualifiedAccess();
  }

    // Declared in Enums.jrag at line 505
 @SuppressWarnings({"unchecked", "cast"})     public boolean isConstant() {
        ASTNode$State state = state();
        boolean isConstant_value = isConstant_compute();
        return isConstant_value;
    }

    private boolean isConstant_compute() {  return true;  }

    // Declared in EnumsCodegen.jrag at line 14
 @SuppressWarnings({"unchecked", "cast"})     public int sootTypeModifiers() {
        ASTNode$State state = state();
        int sootTypeModifiers_value = sootTypeModifiers_compute();
        return sootTypeModifiers_value;
    }

    private int sootTypeModifiers_compute() {  return super.sootTypeModifiers() | Modifiers.ACC_ENUM;  }

    // Declared in Enums.jrag at line 481
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getTypeAccessNoTransform()) {
            return NameType.TYPE_NAME;
        }
        return super.Define_NameType_nameType(caller, child);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
