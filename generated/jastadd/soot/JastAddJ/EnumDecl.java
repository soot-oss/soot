
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class EnumDecl extends ClassDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        isStatic_computed = false;
        getSuperClassAccessOpt_computed = false;
        getSuperClassAccessOpt_value = null;
        enumConstants_computed = false;
        enumConstants_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public EnumDecl clone() throws CloneNotSupportedException {
        EnumDecl node = (EnumDecl)super.clone();
        node.isStatic_computed = false;
        node.getSuperClassAccessOpt_computed = false;
        node.getSuperClassAccessOpt_value = null;
        node.enumConstants_computed = false;
        node.enumConstants_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public EnumDecl copy() {
      try {
          EnumDecl node = (EnumDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public EnumDecl fullCopy() {
        EnumDecl res = (EnumDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Enums.jrag at line 46

  
  /*
    12) It is a compile-time error for an enum to declare a finalizer. An instance of
    an enum may never be finalized.
  */
  public void typeCheck() {
    super.typeCheck();
    for(Iterator iter = memberMethods("finalize").iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(m.getNumParameter() == 0 && m.hostType() == this)
        error("an enum may not declare a finalizer");
    }
    checkEnum(this);
  }

    // Declared in Enums.jrag at line 81


  private boolean done = false;

    // Declared in Enums.jrag at line 82

  private boolean done() {
    if(done) return true;
    done = true;
    return false;
  }

    // Declared in Enums.jrag at line 271


  /*
    13) In addition, if E is the name of an enum type, then that type has the
    following implicitly declared static methods:
      public static E[] values();
      public static E valueOf(String name);
  */

  private void addValues() {
    int numConstants = enumConstants().size();
    List initValues = new List();
    for(Iterator iter = enumConstants().iterator(); iter.hasNext(); ) {
      EnumConstant c = (EnumConstant)iter.next();
      initValues.add(c.createBoundFieldAccess());
    }
    FieldDeclaration values = new FieldDeclaration(
      new Modifiers(new List().add(
        new Modifier("private")).add(
        new Modifier("static")).add(
        new Modifier("final")).add(
        new Modifier("synthetic"))
      ),
      arrayType().createQualifiedAccess(),
      "$VALUES",
      new Opt(
          new ArrayCreationExpr(
            new ArrayTypeWithSizeAccess(
              createQualifiedAccess(),
              new IntegerLiteral(Integer.toString(enumConstants().size()))
            ),
            new Opt(
              new ArrayInit(
                initValues
              )
            )
          )
      )
    );
    addBodyDecl(values);
    // public static final Test[] values() { return (Test[])$VALUES.clone(); }
    addBodyDecl(
      new MethodDecl(
        new Modifiers(new List().add(
          new Modifier("public")).add(
          new Modifier("static")).add(
          new Modifier("final")).add(
          new Modifier("synthetic"))
        ),
        arrayType().createQualifiedAccess(),
        "values",
        new List(),
        new List(),
        new Opt(
          new Block(
            new List().add(
              new ReturnStmt(
                new Opt(
                  new CastExpr(
                    arrayType().createQualifiedAccess(),
                    values.createBoundFieldAccess().qualifiesAccess(
                      new MethodAccess(
                        "clone",
                        new List()
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    );
    // public static Test valueOf(String s) { return (Test)java.lang.Enum.valueOf(Test.class, s); }
    addBodyDecl(
      new MethodDecl(
        new Modifiers(new List().add(
          new Modifier("public")).add(
          new Modifier("static")).add(
          new Modifier("synthetic"))
        ),
        createQualifiedAccess(),
        "valueOf",
        new List().add(
          new ParameterDeclaration(
            new Modifiers(new List()),
            typeString().createQualifiedAccess(),
            "s"
          )
        ),
        new List(),
        new Opt(
          new Block(
            new List().add(
              new ReturnStmt(
                new Opt(
                  new CastExpr(
                    createQualifiedAccess(),
                    lookupType("java.lang", "Enum").createQualifiedAccess().qualifiesAccess(
                      new MethodAccess(
                        "valueOf",
                        new List().add(
                          createQualifiedAccess().qualifiesAccess(new ClassAccess())
                        ).add(
                          new VarAccess(
                            "s"
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    );
  }

    // Declared in Enums.jrag at line 415

  protected void checkEnum(EnumDecl enumDecl) {
    for(int i = 0; i < getNumBodyDecl(); i++) {
      if(getBodyDecl(i) instanceof ConstructorDecl)
        getBodyDecl(i).checkEnum(enumDecl);
      else if(getBodyDecl(i) instanceof InstanceInitializer)
        getBodyDecl(i).checkEnum(enumDecl);
      else if(getBodyDecl(i) instanceof FieldDeclaration) {
        FieldDeclaration f = (FieldDeclaration)getBodyDecl(i);
        if(!f.isStatic() && f.hasInit())
          f.checkEnum(enumDecl);
      }
    }
  }

    // Declared in Enums.jrag at line 498


  public void toString(StringBuffer s) {
    getModifiers().toString(s);
    s.append("enum " + name());
    if(getNumImplements() > 0) {
      s.append(" implements ");
      getImplements(0).toString(s);
      for(int i = 1; i < getNumImplements(); i++) {
        s.append(", ");
        getImplements(i).toString(s);
      }
    }
    s.append(" {");
    for(int i=0; i < getNumBodyDecl(); i++) {
      BodyDecl d = getBodyDecl(i);
      if(d instanceof EnumConstant) {
        d.toString(s);
        if(i + 1 < getNumBodyDecl() && !(getBodyDecl(i + 1) instanceof EnumConstant))
          s.append(indent() + ";");
      }
      else if(d instanceof ConstructorDecl) {
        ConstructorDecl c = (ConstructorDecl)d;
        if(!c.isSynthetic()) {
          s.append(indent());
          c.getModifiers().toString(s);
          s.append(c.name() + "(");
          if(c.getNumParameter() > 2) {
            c.getParameter(2).toString(s);
            for(int j = 3; j < c.getNumParameter(); j++) {
              s.append(", ");
              c.getParameter(j).toString(s);
            }
          }
          s.append(")");
          if(c.getNumException() > 0) {
            s.append(" throws ");
            c.getException(0).toString(s);
            for(int j = 1; j < c.getNumException(); j++) {
              s.append(", ");
              c.getException(j).toString(s);
            }
          }
          s.append(" {");
          for(int j = 0; j < c.getBlock().getNumStmt(); j++) {
            c.getBlock().getStmt(j).toString(s);
          }
          s.append(indent());
          s.append("}");
        }
      }
      else if(d instanceof MethodDecl) {
        MethodDecl m = (MethodDecl)d;
        if(!m.isSynthetic())
          m.toString(s);
      }
      else if(d instanceof FieldDeclaration) {
        FieldDeclaration f = (FieldDeclaration)d;
        if(!f.isSynthetic())
          f.toString(s);
      }
      else
        d.toString(s);
    }
    s.append(indent() + "}");
  }

    // Declared in Enums.ast at line 3
    // Declared in Enums.ast line 1

    public EnumDecl() {
        super();

        setChild(new List(), 1);
        setChild(new List(), 2);
        setChild(new Opt(), 3);

    }

    // Declared in Enums.ast at line 13


    // Declared in Enums.ast line 1
    public EnumDecl(Modifiers p0, String p1, List<Access> p2, List<BodyDecl> p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(new Opt(), 3);
    }

    // Declared in Enums.ast at line 22


    // Declared in Enums.ast line 1
    public EnumDecl(Modifiers p0, beaver.Symbol p1, List<Access> p2, List<BodyDecl> p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(new Opt(), 3);
    }

    // Declared in Enums.ast at line 30


  protected int numChildren() {
    return 3;
  }

    // Declared in Enums.ast at line 33

    public boolean mayHaveRewrite() {
        return true;
    }

    // Declared in Enums.ast at line 2
    // Declared in Enums.ast line 1
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
    // Declared in Enums.ast line 1
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
    // Declared in Enums.ast line 1
    public void setImplementsList(List<Access> list) {
        setChild(list, 1);
    }

    // Declared in Enums.ast at line 6


    public int getNumImplements() {
        return getImplementsList().getNumChild();
    }

    // Declared in Enums.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getImplements(int i) {
        return (Access)getImplementsList().getChild(i);
    }

    // Declared in Enums.ast at line 14


    public void addImplements(Access node) {
        List<Access> list = (parent == null || state == null) ? getImplementsListNoTransform() : getImplementsList();
        list.addChild(node);
    }

    // Declared in Enums.ast at line 19


    public void addImplementsNoTransform(Access node) {
        List<Access> list = getImplementsListNoTransform();
        list.addChild(node);
    }

    // Declared in Enums.ast at line 24


    public void setImplements(Access node, int i) {
        List<Access> list = getImplementsList();
        list.setChild(node, i);
    }

    // Declared in Enums.ast at line 28

    public List<Access> getImplementss() {
        return getImplementsList();
    }

    // Declared in Enums.ast at line 31

    public List<Access> getImplementssNoTransform() {
        return getImplementsListNoTransform();
    }

    // Declared in Enums.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getImplementsList() {
        List<Access> list = (List<Access>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in Enums.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getImplementsListNoTransform() {
        return (List<Access>)getChildNoTransform(1);
    }

    // Declared in Enums.ast at line 2
    // Declared in Enums.ast line 1
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 2);
    }

    // Declared in Enums.ast at line 6


    public int getNumBodyDecl() {
        return getBodyDeclList().getNumChild();
    }

    // Declared in Enums.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public BodyDecl getBodyDecl(int i) {
        return (BodyDecl)getBodyDeclList().getChild(i);
    }

    // Declared in Enums.ast at line 14


    public void addBodyDecl(BodyDecl node) {
        List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
        list.addChild(node);
    }

    // Declared in Enums.ast at line 19


    public void addBodyDeclNoTransform(BodyDecl node) {
        List<BodyDecl> list = getBodyDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in Enums.ast at line 24


    public void setBodyDecl(BodyDecl node, int i) {
        List<BodyDecl> list = getBodyDeclList();
        list.setChild(node, i);
    }

    // Declared in Enums.ast at line 28

    public List<BodyDecl> getBodyDecls() {
        return getBodyDeclList();
    }

    // Declared in Enums.ast at line 31

    public List<BodyDecl> getBodyDeclsNoTransform() {
        return getBodyDeclListNoTransform();
    }

    // Declared in Enums.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclList() {
        List<BodyDecl> list = (List<BodyDecl>)getChild(2);
        list.getNumChild();
        return list;
    }

    // Declared in Enums.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(2);
    }

    // Declared in Enums.ast at line 2
    // Declared in Enums.ast line 1
    public void setSuperClassAccessOpt(Opt<Access> opt) {
        setChild(opt, 3);
    }

    // Declared in Enums.ast at line 6


    public boolean hasSuperClassAccess() {
        return getSuperClassAccessOpt().getNumChild() != 0;
    }

    // Declared in Enums.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getSuperClassAccess() {
        return (Access)getSuperClassAccessOpt().getChild(0);
    }

    // Declared in Enums.ast at line 14


    public void setSuperClassAccess(Access node) {
        getSuperClassAccessOpt().setChild(node, 0);
    }

    // Declared in Enums.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Access> getSuperClassAccessOptNoTransform() {
        return (Opt<Access>)getChildNoTransform(3);
    }

    // Declared in Enums.ast at line 21


    protected int getSuperClassAccessOptChildPosition() {
        return 3;
    }

    // Declared in Annotations.jrag at line 132
 @SuppressWarnings({"unchecked", "cast"})     public boolean isValidAnnotationMethodReturnType() {
        ASTNode$State state = state();
        boolean isValidAnnotationMethodReturnType_value = isValidAnnotationMethodReturnType_compute();
        return isValidAnnotationMethodReturnType_value;
    }

    private boolean isValidAnnotationMethodReturnType_compute() {  return true;  }

    // Declared in Enums.jrag at line 17
 @SuppressWarnings({"unchecked", "cast"})     public boolean isEnumDecl() {
        ASTNode$State state = state();
        boolean isEnumDecl_value = isEnumDecl_compute();
        return isEnumDecl_value;
    }

    private boolean isEnumDecl_compute() {  return true;  }

    // Declared in Enums.jrag at line 39
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStatic() {
        if(isStatic_computed) {
            return isStatic_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isStatic_value = isStatic_compute();
        if(isFinal && num == state().boundariesCrossed)
            isStatic_computed = true;
        return isStatic_value;
    }

    private boolean isStatic_compute() {  return isNestedType();  }

    protected boolean getSuperClassAccessOpt_computed = false;
    protected Opt getSuperClassAccessOpt_value;
    // Declared in Enums.jrag at line 60
 @SuppressWarnings({"unchecked", "cast"})     public Opt getSuperClassAccessOpt() {
        if(getSuperClassAccessOpt_computed) {
            return (Opt)ASTNode.getChild(this, getSuperClassAccessOptChildPosition());
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getSuperClassAccessOpt_value = getSuperClassAccessOpt_compute();
        setSuperClassAccessOpt(getSuperClassAccessOpt_value);
        if(isFinal && num == state().boundariesCrossed)
            getSuperClassAccessOpt_computed = true;
        return (Opt)ASTNode.getChild(this, getSuperClassAccessOptChildPosition());
    }

    private Opt getSuperClassAccessOpt_compute() {
    return new Opt(
      new ParTypeAccess(
        new TypeAccess(
          "java.lang",
          "Enum"
        ),
        new List().add(createQualifiedAccess())
      )
    );
  }

    // Declared in Enums.jrag at line 245
 @SuppressWarnings({"unchecked", "cast"})     public boolean isFinal() {
        ASTNode$State state = state();
        boolean isFinal_value = isFinal_compute();
        return isFinal_value;
    }

    private boolean isFinal_compute() {
    for(Iterator iter = enumConstants().iterator(); iter.hasNext(); ) {
      EnumConstant c = (EnumConstant)iter.next();
      ClassInstanceExpr e = (ClassInstanceExpr)c.getInit();
      if(e.hasTypeDecl())
        return false;
    }
    return true;
  }

    protected boolean enumConstants_computed = false;
    protected ArrayList enumConstants_value;
    // Declared in Enums.jrag at line 256
 @SuppressWarnings({"unchecked", "cast"})     public ArrayList enumConstants() {
        if(enumConstants_computed) {
            return enumConstants_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        enumConstants_value = enumConstants_compute();
        if(isFinal && num == state().boundariesCrossed)
            enumConstants_computed = true;
        return enumConstants_value;
    }

    private ArrayList enumConstants_compute() {
    ArrayList list = new ArrayList();
    for(int i = 0; i < getNumBodyDecl(); i++)
      if(getBodyDecl(i).isEnumConstant())
        list.add(getBodyDecl(i));
    return list;
  }

    // Declared in Enums.jrag at line 393
 @SuppressWarnings({"unchecked", "cast"})     public boolean isAbstract() {
        ASTNode$State state = state();
        boolean isAbstract_value = isAbstract_compute();
        return isAbstract_value;
    }

    private boolean isAbstract_compute() {
    for(int i = 0; i < getNumBodyDecl(); i++) {
      if(getBodyDecl(i) instanceof MethodDecl) {
        MethodDecl m = (MethodDecl)getBodyDecl(i);
        if(m.isAbstract())
          return true;
      }
    }
    return false;
  }

    // Declared in EnumsCodegen.jrag at line 13
 @SuppressWarnings({"unchecked", "cast"})     public int sootTypeModifiers() {
        ASTNode$State state = state();
        int sootTypeModifiers_value = sootTypeModifiers_compute();
        return sootTypeModifiers_value;
    }

    private int sootTypeModifiers_compute() {  return super.sootTypeModifiers() | Modifiers.ACC_ENUM;  }

    // Declared in Enums.jrag at line 383
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeString() {
        ASTNode$State state = state();
        TypeDecl typeString_value = getParent().Define_TypeDecl_typeString(this, null);
        return typeString_value;
    }

    // Declared in Enums.jrag at line 33
    public boolean Define_boolean_mayBeAbstract(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return false;
        }
        return super.Define_boolean_mayBeAbstract(caller, child);
    }

    // Declared in Enums.jrag at line 40
    public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return isNestedType();
        }
        return super.Define_boolean_mayBeStatic(caller, child);
    }

    // Declared in Enums.jrag at line 254
    public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return false;
        }
        return super.Define_boolean_mayBeFinal(caller, child);
    }

public ASTNode rewriteTo() {
    // Declared in Enums.jrag at line 88
    if(!done()) {
        state().duringEnums++;
        ASTNode result = rewriteRule0();
        state().duringEnums--;
        return result;
    }

    return super.rewriteTo();
}

    // Declared in Enums.jrag at line 88
    private EnumDecl rewriteRule0() {
{
      if(noConstructor()) {
        List parameterList = new List();
        parameterList.add(
          new ParameterDeclaration(new TypeAccess("java.lang", "String"), "p0")
        );
        parameterList.add(
          new ParameterDeclaration(new TypeAccess("int"), "p1")
        );
        addBodyDecl(
          new ConstructorDecl(
            new Modifiers(new List().add(new Modifier("private")).add(new Modifier("synthetic"))),
            name(),
            parameterList,
            new List(),
            new Opt(
              new ExprStmt(
                new SuperConstructorAccess(
                  "super",
                  new List().add(
                    new VarAccess("p0")
                  ).add(
                    new VarAccess("p1")
                  )
                )
              )
            ),
            new Block(new List())
          )
        );
      }
      else {
        transformEnumConstructors();
      }
      addValues(); // Add the values() and getValue(String s) methods
      return this;
    }    }
}
