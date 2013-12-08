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
 * @apilevel internal
  * @ast class
 * 
 */
public class ASTNode$State extends java.lang.Object {

/**
 * @apilevel internal
 */
  public boolean IN_CIRCLE = false;


/**
 * @apilevel internal
 */
  public int CIRCLE_INDEX = 1;


/**
 * @apilevel internal
 */
  public boolean CHANGE = false;


/**
 * @apilevel internal
 */
  public boolean RESET_CYCLE = false;


  /**
   * @apilevel internal
   */
  static public class CircularValue {
    Object value;
    int visited = -1;
  }


  public static final int REWRITE_CHANGE = 1;


  public static final int REWRITE_NOCHANGE = 2;


  public static final int REWRITE_INTERRUPT = 3;


  public int boundariesCrossed = 0;



  private int[] stack;


  private int pos;


  public ASTNode$State() {
      stack = new int[64];
      pos = 0;
  }


  private void ensureSize(int size) {
      if(size < stack.length)
        return;
      int[] newStack = new int[stack.length * 2];
      System.arraycopy(stack, 0, newStack, 0, stack.length);
      stack = newStack;
  }


  public void push(int i) {
    ensureSize(pos+1);
    stack[pos++] = i;
  }


  public int pop() {
    return stack[--pos];
  }


  public int peek() {
    return stack[pos-1];
  }


  /**
   * @apilevel internal
   */
  static class IdentityHashSet extends java.util.AbstractSet implements java.util.Set {
    public IdentityHashSet(int initialCapacity) {
      map = new java.util.IdentityHashMap(initialCapacity);
      }
    private java.util.IdentityHashMap map;
    private static final Object PRESENT = new Object();
    public java.util.Iterator iterator() { return map.keySet().iterator(); }
    public int size() { return map.size(); }
    public boolean isEmpty() { return map.isEmpty(); }
    public boolean contains(Object o) { return map.containsKey(o); }
    public boolean add(Object o) { return map.put(o, PRESENT)==null; }
    public boolean remove(Object o) { return map.remove(o)==PRESENT; }
    public void clear() { map.clear(); }
  }


  public Options options = new Options();


  public int replacePos = 0;


  protected int duringImplicitConstructor = 0;


  protected int duringBoundNames = 0;


  protected int duringNameResolution = 0;


  protected int duringSyntacticClassification = 0;


  protected int duringAnonymousClasses = 0;


  protected int duringVariableDeclarationTransformation = 0;


  protected int duringLiterals = 0;


  protected int duringDU = 0;


  protected int duringAnnotations = 0;


  protected int duringEnums = 0;


  protected int duringGenericTypeVariables = 0;

public void reset() {
    IN_CIRCLE = false;
    CIRCLE_INDEX = 1;
    CHANGE = false;
    boundariesCrossed = 0;
    if(duringImplicitConstructor != 0) {
      System.out.println("Warning: resetting duringImplicitConstructor");
      duringImplicitConstructor = 0;
    }
    if(duringBoundNames != 0) {
      System.out.println("Warning: resetting duringBoundNames");
      duringBoundNames = 0;
    }
    if(duringNameResolution != 0) {
      System.out.println("Warning: resetting duringNameResolution");
      duringNameResolution = 0;
    }
    if(duringSyntacticClassification != 0) {
      System.out.println("Warning: resetting duringSyntacticClassification");
      duringSyntacticClassification = 0;
    }
    if(duringAnonymousClasses != 0) {
      System.out.println("Warning: resetting duringAnonymousClasses");
      duringAnonymousClasses = 0;
    }
    if(duringVariableDeclarationTransformation != 0) {
      System.out.println("Warning: resetting duringVariableDeclarationTransformation");
      duringVariableDeclarationTransformation = 0;
    }
    if(duringLiterals != 0) {
      System.out.println("Warning: resetting duringLiterals");
      duringLiterals = 0;
    }
    if(duringDU != 0) {
      System.out.println("Warning: resetting duringDU");
      duringDU = 0;
    }
    if(duringAnnotations != 0) {
      System.out.println("Warning: resetting duringAnnotations");
      duringAnnotations = 0;
    }
    if(duringEnums != 0) {
      System.out.println("Warning: resetting duringEnums");
      duringEnums = 0;
    }
    if(duringGenericTypeVariables != 0) {
      System.out.println("Warning: resetting duringGenericTypeVariables");
      duringGenericTypeVariables = 0;
    }
  }


}
