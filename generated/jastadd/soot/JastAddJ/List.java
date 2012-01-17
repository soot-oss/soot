package soot.JastAddJ;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;

/**
 * @ast node
 * @declaredat List.ast:0
 */
public class List<T extends ASTNode> extends ASTNode<T> implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
  }
  /**
   * @apilevel internal
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<T> clone() throws CloneNotSupportedException {
    List node = (List)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<T> copy() {
      try {
        List node = (List)clone();
        if(children != null) node.children = (ASTNode[])children.clone();
        return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
  }
  /**
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<T> fullCopy() {
    List res = (List)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:834
   */
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
  /**
   * @ast method 
   * @declaredat List.ast:1
   */
  public List() {
    super();


  }
  /**
   * @ast method 
   * @declaredat List.ast:7
   */
  public List<T> add(T node) {
    addChild(node);
    return this;
  }
  /**
   * @ast method 
   * @declaredat List.ast:12
   */
  public void insertChild(ASTNode node, int i) {
    list$touched = true;
    super.insertChild(node, i);
  }
  /**
   * @ast method 
   * @declaredat List.ast:16
   */
  public void addChild(T node) {
    list$touched = true;
    super.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat List.ast:23
   */
  public void removeChild(int i) {
    list$touched = true;
    super.removeChild(i);
  }
  /**
   * @ast method 
   * @declaredat List.ast:27
   */
  public int getNumChild() {
    if(list$touched) {
      for(int i = 0; i < getNumChildNoTransform(); i++)
        getChild(i);
        list$touched = false;
      }
      return getNumChildNoTransform();
  }
  /**
   * @ast method 
   * @declaredat List.ast:35
   */
  
  private boolean list$touched = true;
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat List.ast:39
   */
  public boolean mayHaveRewrite() {
    return true;
  }
  /**
   * @attribute syn
   * @aspect ImplicitConstructor
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:158
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean requiresDefaultConstructor() {
      ASTNode$State state = state();
    boolean requiresDefaultConstructor_value = requiresDefaultConstructor_compute();
    return requiresDefaultConstructor_value;
  }
  /**
   * @apilevel internal
   */
  private boolean requiresDefaultConstructor_compute() {
    if(getParent() instanceof ClassDecl) {
      ClassDecl c = (ClassDecl)getParent();
      return c.noConstructor() && c.getBodyDeclListNoTransform() == this && !(c instanceof AnonymousDecl);
    }
    return false;
  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:23
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean definesLabel() {
      ASTNode$State state = state();
    boolean definesLabel_value = definesLabel_compute();
    return definesLabel_value;
  }
  /**
   * @apilevel internal
   */
  private boolean definesLabel_compute() {  return getParent().definesLabel();  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    if(list$touched) {
      for(int i = 0 ; i < getNumChildNoTransform(); i++)
        getChild(i);
      list$touched = false;
      return this;
    }
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag at line 186
    if(requiresDefaultConstructor()) {
      state().duringLookupConstructor++;
      ASTNode result = rewriteRule0();
      state().duringLookupConstructor--;
      return result;
    }

    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:186
   * @apilevel internal
   */  private List rewriteRule0() {
{
      ClassDecl c = (ClassDecl)getParent();
      Modifiers m = new Modifiers();
      if(c.isPublic()) m.addModifier(new Modifier("public"));
      else if(c.isProtected()) m.addModifier(new Modifier("protected"));
      else if(c.isPrivate()) m.addModifier(new Modifier("private"));
      ConstructorDecl constructor = new ConstructorDecl(
            m,
            c.name(),
            new List(),
            new List(),
            new Opt(),
            new Block()
      );
      constructor.setDefaultConstructor();
      c.addBodyDecl(constructor);
      return this;
    }  }
}
