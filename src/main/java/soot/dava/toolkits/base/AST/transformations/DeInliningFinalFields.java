package soot.dava.toolkits.base.AST.transformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2006 Nomair A. Naeem (nomair.naeem@mail.mcgill.ca)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.HashMap;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.dava.internal.AST.ASTAggregatedCondition;
import soot.dava.internal.AST.ASTBinaryCondition;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTSynchronizedBlockNode;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DStaticFieldRef;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.StringConstantValueTag;

/**
 * Maintained by: Nomair A. Naeem
 */
/**
 * CHANGE LOG: 2nd February 2006:
 *
 */
/**
 * Both static and non-static BUT FINAL fields if initialized with constants get inlined A final initialized with an object
 * (even if its a string) is NOT inlined e.g. public static final String temp = "hello"; //use of temp will get inlined
 * public static final String temp1 = new String("hello"); //use of temp will NOT get inlined
 *
 *
 * If its a static field we can get the info from a tag in the case of a non static we cant decide since the field is
 * initialized inside a constructor and depending on different constructors there coul dbe different
 * values...conservative....
 *
 *
 * Need to be very clear when a SootField can be used It can be used in the following places:
 *
 * a, NOT used inside a Synchronized Block ........ HOWEVER ADD IT SINCE I DONT SEE WHY THIS RESTRICTION EXISTS!!! TICK b,
 * CAN BE USED in a condition TICK c, CAN BE USED in the for init for update TICK d, CAN BE USED in a switch TICK e, CAN BE
 * USED in a stmt TICK
 *
 * These are the exact places to look for constants...a constant is StringConstant DoubleConstant FloatConstant IntConstant
 * (shortype, booltype, charType intType, byteType LongConstant
 */
public class DeInliningFinalFields extends DepthFirstAdapter {

  private SootClass sootClass = null;
  private SootMethod sootMethod = null;
  private HashMap<Comparable, SootField> finalFields;

  // ASTParentNodeFinder parentFinder;

  public DeInliningFinalFields() {
  }

  public DeInliningFinalFields(boolean verbose) {
    super(verbose);
  }

  @Override
  public void inASTMethodNode(ASTMethodNode node) {
    this.sootMethod = node.getDavaBody().getMethod();
    this.sootClass = sootMethod.getDeclaringClass();
    this.finalFields = new HashMap<Comparable, SootField>();
    // System.out.println("Deiniling method: "+sootMethod.getName());

    ArrayList<SootField> fieldChain = new ArrayList<>();
    for (SootClass tempClass : Scene.v().getApplicationClasses()) {
      for (SootField next : tempClass.getFields()) {
        fieldChain.add(next);
      }
    }
    // going through fields
    for (SootField f : fieldChain) {
      if (f.isFinal()) {
        // check for constant value tags
        Type fieldType = f.getType();
        if (fieldType instanceof DoubleType && f.hasTag(DoubleConstantValueTag.NAME)) {
          double val = ((DoubleConstantValueTag) f.getTag(DoubleConstantValueTag.NAME)).getDoubleValue();
          finalFields.put(val, f);
        } else if (fieldType instanceof FloatType && f.hasTag(FloatConstantValueTag.NAME)) {
          float val = ((FloatConstantValueTag) f.getTag(FloatConstantValueTag.NAME)).getFloatValue();
          finalFields.put(val, f);
        } else if (fieldType instanceof LongType && f.hasTag(LongConstantValueTag.NAME)) {
          long val = ((LongConstantValueTag) f.getTag(LongConstantValueTag.NAME)).getLongValue();
          finalFields.put(val, f);
        } else if (fieldType instanceof CharType && f.hasTag(IntegerConstantValueTag.NAME)) {
          int val = ((IntegerConstantValueTag) f.getTag(IntegerConstantValueTag.NAME)).getIntValue();
          finalFields.put(val, f);
        } else if (fieldType instanceof BooleanType && f.hasTag(IntegerConstantValueTag.NAME)) {
          int val = ((IntegerConstantValueTag) f.getTag(IntegerConstantValueTag.NAME)).getIntValue();
          if (val == 0) {
            finalFields.put(false, f);
          } else {
            finalFields.put(true, f);
          }
        } else if ((fieldType instanceof IntType || fieldType instanceof ByteType || fieldType instanceof ShortType)
            && f.hasTag(IntegerConstantValueTag.NAME)) {
          int val = ((IntegerConstantValueTag) f.getTag(IntegerConstantValueTag.NAME)).getIntValue();
          finalFields.put(val, f);
        } else if (f.hasTag(StringConstantValueTag.NAME)) {
          String val = ((StringConstantValueTag) f.getTag(StringConstantValueTag.NAME)).getStringValue();
          // System.out.println("adding string constant"+val);
          finalFields.put(val, f);
        }
      } // end if final
    }
  }

  /*
   * StringConstant DoubleConstant FloatConstant IntConstant (shortype, booltype, charType intType, byteType LongConstant
   */
  private boolean isConstant(Value val) {
    return val instanceof StringConstant || val instanceof DoubleConstant || val instanceof FloatConstant
        || val instanceof IntConstant || val instanceof LongConstant;
  }

  /*
   * Notice as things stand synchblocks cant have the use of a SootField
   */
  @Override
  public void inASTSynchronizedBlockNode(ASTSynchronizedBlockNode node) {
    // hence nothing is implemented here
  }

  public void checkAndSwitch(ValueBox valBox) {
    Value val = valBox.getValue();
    Object finalField = check(val);
    if (finalField != null) {
      // System.out.println("Final field with this value exists"+finalField);

      /*
       * If the final field belongs to the same class then we should supress declaring class
       */
      SootField field = (SootField) finalField;
      if (sootClass.declaresField(field.getName(), field.getType())) {
        // this field is of this class so supress the declaring class
        if (valBox.canContainValue(new DStaticFieldRef(field.makeRef(), true))) {
          valBox.setValue(new DStaticFieldRef(field.makeRef(), true));
        }
      } else {
        if (valBox.canContainValue(new DStaticFieldRef(field.makeRef(), true))) {
          valBox.setValue(new DStaticFieldRef(field.makeRef(), false));
        }
      }
    }
    // else
    // System.out.println("Final field not found");
  }

  public Object check(Value val) {
    Object finalField = null;
    if (isConstant(val)) {
      // System.out.println("Found constant in code"+val);

      // can be a byte or short or char......or an int ...in the case of
      // int you also have to check for Booleans
      if (val instanceof StringConstant) {
        String myString = ((StringConstant) val).toString();
        myString = myString.substring(1, myString.length() - 1);
        finalField = finalFields.get(myString);
      } else if (val instanceof DoubleConstant) {
        finalField = finalFields.get(((DoubleConstant) val).value);
      } else if (val instanceof FloatConstant) {
        finalField = finalFields.get(((FloatConstant) val).value);
      } else if (val instanceof LongConstant) {
        finalField = finalFields.get(((LongConstant) val).value);
      } else if (val instanceof IntConstant) {
        String myString = ((IntConstant) val).toString();
        if (myString.length() == 0) {
          return null;
        }

        Integer myInt;
        try {
          if (myString.charAt(0) == '\'') {
            // character
            if (myString.length() < 2) {
              return null;
            }
            myInt = Integer.valueOf(myString.charAt(1));
          } else {
            myInt = Integer.valueOf(myString);
          }
        } catch (Exception e) {
          // System.out.println("exception occured...gracefully exitting method..string was"+myString);
          return finalField;
        }

        Type valType = ((IntConstant) val).getType();
        if (valType instanceof ByteType) {
          finalField = finalFields.get(myInt);
        } else if (valType instanceof IntType) {
          switch (myString) {
            case "false":
              finalField = finalFields.get(false);
              break;
            case "true":
              finalField = finalFields.get(true);
              break;
            default:
              finalField = finalFields.get(myInt);
              break;
          }
        } else if (valType instanceof ShortType) {
          finalField = finalFields.get(myInt);
        }
      }
    }
    return finalField;
  }

  /*
   * The key in a switch stmt can be a local or a SootField or a value which can contain constant
   *
   * Hence the some what indirect approach........notice we will work with valueBoxes so that by changing the value in the
   * value box we can deInline any field
   */
  @Override
  public void inASTSwitchNode(ASTSwitchNode node) {
    Value val = node.get_Key();

    if (isConstant(val)) {
      // find if there is a SootField with this constant
      // System.out.println("Found constant as key to switch");

      checkAndSwitch(node.getKeyBox());
      return;
    }
    // val is not a constant but it might have other constants in it
    for (ValueBox tempBox : val.getUseBoxes()) {
      // System.out.println("Checking useBox of switch key");
      checkAndSwitch(tempBox);
    }
  }

  @Override
  public void inASTStatementSequenceNode(ASTStatementSequenceNode node) {
    for (AugmentedStmt as : node.getStatements()) {
      Stmt s = as.get_Stmt();
      for (ValueBox tempBox : s.getUseBoxes()) {
        // System.out.println("Checking useBox of stmt");
        checkAndSwitch(tempBox);
      }
    }
  }

  @Override
  public void inASTForLoopNode(ASTForLoopNode node) {

    // checking uses in init
    for (AugmentedStmt as : node.getInit()) {
      Stmt s = as.get_Stmt();
      for (ValueBox tempBox : s.getUseBoxes()) {
        // System.out.println("Checking useBox of init stmt");
        checkAndSwitch(tempBox);
      }
    }

    // checking uses in condition
    ASTCondition cond = node.get_Condition();
    checkConditionalUses(cond, node);

    // checking uses in update
    for (AugmentedStmt as : node.getUpdate()) {
      Stmt s = as.get_Stmt();
      for (ValueBox tempBox : s.getUseBoxes()) {
        // System.out.println("Checking useBox of update stmt");
        checkAndSwitch(tempBox);
      }
    }
  }

  /*
   * checking for unary conditions doesnt matter since this was definetly lost.
   */
  public void checkConditionalUses(Object cond, ASTNode node) {
    if (cond instanceof ASTAggregatedCondition) {
      checkConditionalUses((((ASTAggregatedCondition) cond).getLeftOp()), node);
      checkConditionalUses(((ASTAggregatedCondition) cond).getRightOp(), node);
      return;
    } else if (cond instanceof ASTBinaryCondition) {
      // get uses from binaryCondition
      Value val = ((ASTBinaryCondition) cond).getConditionExpr();
      for (ValueBox tempBox : val.getUseBoxes()) {
        // System.out.println("Checking useBox of binary condition");
        checkAndSwitch(tempBox);
      }
    }
  }

  /*
   * The condition of an if node can use a local
   */
  @Override
  public void inASTIfNode(ASTIfNode node) {
    ASTCondition cond = node.get_Condition();
    checkConditionalUses(cond, node);
  }

  /*
   * The condition of an ifElse node can use a local
   */
  @Override
  public void inASTIfElseNode(ASTIfElseNode node) {
    ASTCondition cond = node.get_Condition();
    checkConditionalUses(cond, node);
  }

  /*
   * The condition of a while node can use a local
   */
  @Override
  public void inASTWhileNode(ASTWhileNode node) {
    ASTCondition cond = node.get_Condition();
    checkConditionalUses(cond, node);
  }

  /*
   * The condition of a doWhile node can use a local
   */
  @Override
  public void inASTDoWhileNode(ASTDoWhileNode node) {
    ASTCondition cond = node.get_Condition();
    checkConditionalUses(cond, node);
  }
}
