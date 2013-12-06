/* This file was generated with JastAdd2 (http://jastadd.org) version R20130212 (r1031) */
package soot.JastAddJ;

import java.util.HashSet;
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
 * @production List : {@link ASTNode};
 * @ast node
 * 
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
      List node = (List) clone();
      node.parent = null;
      if(children != null)
        node.children = (ASTNode[]) children.clone();
      return node;
    } catch (CloneNotSupportedException e) {
      throw new Error("Error: clone not supported for " +
        getClass().getName());
    }
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<T> fullCopy() {
    List tree = (List) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        ASTNode child = (ASTNode) children[i];
        if(child != null) {
          child = child.fullCopy();
          tree.setChild(child, i);
        }
      }
    }
    return tree;
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:977
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
   * 
   */
  public List() {
    super();


  }
  /**
   * Initializes the child array to the correct size.
   * Initializes List and Opt nta children.
   * @apilevel internal
   * @ast method
   * @ast method 
   * 
   */
  public void init$Children() {
  }
  /**
   * @ast method 
   * 
   */
  public List<T> add(T node) {
    addChild(node);
    return this;
  }
  /**
   * @ast method 
   * 
   */
  public void insertChild(ASTNode node, int i) {
    list$touched = true;
    super.insertChild(node, i);
  }
  /**
   * @ast method 
   * 
   */
  public void addChild(T node) {
    list$touched = true;
    super.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void removeChild(int i) {
    list$touched = true;
    super.removeChild(i);
  }
  /**
   * @ast method 
   * 
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
   * 
   */
  
  private boolean list$touched = true;
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean mayHaveRewrite() {
    return true;
  }
  /**
   * @attribute syn
   * @aspect ImplicitConstructor
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:158
   */
  public boolean requiresDefaultConstructor() {
    ASTNode$State state = state();
    try {
    if(getParent() instanceof ClassDecl) {
      ClassDecl c = (ClassDecl)getParent();
      return c.noConstructor() && c.getBodyDeclListNoTransform() == this && !(c instanceof AnonymousDecl);
    }
    return false;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:21
   */
  public boolean definesLabel() {
    ASTNode$State state = state();
    try {  return getParent().definesLabel();  }
    finally {
    }
  }
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
      state().duringImplicitConstructor++;
      ASTNode result = rewriteRule0();
      state().duringImplicitConstructor--;
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
