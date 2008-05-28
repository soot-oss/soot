
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;

public class Body extends java.lang.Object {
    // Declared in EmitJimple.jrag at line 444

    int nextTempIndex = 0;

    // Declared in EmitJimple.jrag at line 445

    soot.jimple.JimpleBody body;

    // Declared in EmitJimple.jrag at line 446

    java.util.Stack chains;

    // Declared in EmitJimple.jrag at line 447

    TypeDecl typeDecl;

    // Declared in EmitJimple.jrag at line 448

    public Body(TypeDecl typeDecl, soot.jimple.JimpleBody body, ASTNode container) {
      this.typeDecl = typeDecl;
      this.body = body;
      chains = new java.util.Stack();
      chains.push(body.getUnits());
      setLine(container);
      if(!body.getMethod().isStatic())
        emitThis(typeDecl);
    }

    // Declared in EmitJimple.jrag at line 457

    public Local getParam(int i)
    {
      return body.getParameterLocal(i);
    }

    // Declared in EmitJimple.jrag at line 461

    public Local newTemp(soot.Type type) {
      Local local = Jimple.v().newLocal("temp$" + nextTempIndex++, type);
      body.getLocals().add(local);
      return local;
    }

    // Declared in EmitJimple.jrag at line 466

    public Local newTemp(soot.Value v) {
      if (v == NullConstant.v())
        throw new UnsupportedOperationException(
            "Cannot create a temporary local for null literal");
      Local local = newTemp(v.getType());
      if(v instanceof soot.jimple.ParameterRef) {
        add(Jimple.v().newIdentityStmt(local, (soot.jimple.ParameterRef)v));
      }
      else {
        add(Jimple.v().newAssignStmt(local, v));
      }
      return local;
    }

    // Declared in EmitJimple.jrag at line 479

    public Local newLocal(String name, soot.Type type) {
      Local local = Jimple.v().newLocal(name, type);
      body.getLocals().add(local);
      if(name.equals("this") && thisName == null)
        thisName = local;
      return local;
    }

    // Declared in EmitJimple.jrag at line 487


    private soot.tagkit.LineNumberTag lineTag;

    // Declared in EmitJimple.jrag at line 488

    public void setLine(ASTNode node)
    {
      lineTag = new soot.tagkit.LineNumberTag(node.lineNumber());
    }

    // Declared in EmitJimple.jrag at line 492

    private soot.tagkit.LineNumberTag currentSourceRangeTag()
    {
      return lineTag;
    }

    // Declared in EmitJimple.jrag at line 497


    public Body add(soot.jimple.Stmt stmt) {
      if(list != null) {
        list.add(stmt);
        list = null;
      }
      stmt.addTag(currentSourceRangeTag());
      soot.PatchingChain chain = (soot.PatchingChain)chains.peek();
      if(stmt instanceof IdentityStmt && chain.size() != 0) {
        IdentityStmt idstmt = (IdentityStmt) stmt;
        if(!(idstmt.getRightOp() instanceof CaughtExceptionRef)) {
          Object s = chain.getFirst();
          while(s instanceof IdentityStmt)
            s = chain.getSuccOf(s);
          if(s != null) {
            chain.insertBefore(stmt, s);
            return this;
          }
        }
      }
      chain.add(stmt);
      return this;
    }

    // Declared in EmitJimple.jrag at line 519

    public void pushBlock(soot.PatchingChain c) {
      chains.push(c);
    }

    // Declared in EmitJimple.jrag at line 522

    public void popBlock() {
      chains.pop();
    }

    // Declared in EmitJimple.jrag at line 526


    public soot.jimple.Stmt newLabel() {
      return soot.jimple.Jimple.v().newNopStmt();
    }

    // Declared in EmitJimple.jrag at line 529

    public Body addLabel(soot.jimple.Stmt label) {
      add(label);
      return this;
    }

    // Declared in EmitJimple.jrag at line 534


    public soot.Local emitThis(TypeDecl typeDecl) {
      if(thisName == null) {
        thisName = newLocal("this", typeDecl.getSootType());
        if(body.getMethod().isStatic())
          add(Jimple.v().newIdentityStmt(thisName, Jimple.v().newParameterRef(typeDecl.getSootType(), 0)));
        else
          add(Jimple.v().newIdentityStmt(thisName, Jimple.v().newThisRef(typeDecl.sootRef())));
      }
      return thisName;
    }

    // Declared in EmitJimple.jrag at line 544

    Local thisName;

    // Declared in EmitJimple.jrag at line 546


    public Body addTrap(TypeDecl type, soot.jimple.Stmt firstStmt, soot.jimple.Stmt lastStmt, soot.jimple.Stmt handler) {
      body.getTraps().add(Jimple.v().newTrap(type.getSootClassDecl(), firstStmt, lastStmt, handler));
      return this;
    }

    // Declared in EmitJimple.jrag at line 551


    public soot.jimple.Stmt previousStmt() {
      PatchingChain<Unit> o = (PatchingChain<Unit>)chains.lastElement();
      return (soot.jimple.Stmt)o.getLast();
    }

    // Declared in EmitJimple.jrag at line 555

    public void addNextStmt(java.util.ArrayList list) {
      this.list = list;
    }

    // Declared in EmitJimple.jrag at line 558

    java.util.ArrayList list = null;


}
