/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 * 
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 * 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dexpler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.SootField;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;

public class Util {
    /**
     * Return the dotted class name of a type descriptor, i.e. change Ljava/lang/Object; to java.lang.Object.
     *
     * @raises IllegalArgumentException if classname is not of the form Lpath; or [Lpath;
     * @return the dotted name.
     */
    public static String dottedClassName(String typeDescriptor) {
        if (!isByteCodeClassName(typeDescriptor)) {
          // typeDescriptor may not be a class but something like "[[[[[[[[J"
          String t = typeDescriptor;
          int idx = 0;
          while (idx < t.length() && t.charAt(idx) == '[') {
            idx++;
          }
          String c = t.substring(idx);
          if (c.length() == 1 && (c.startsWith("I") ||
              c.startsWith("B") ||
              c.startsWith("C") ||
              c.startsWith("S") ||
              c.startsWith("J") ||
              c.startsWith("D") ||
              c.startsWith("F") || 
              c.startsWith("Z")) ) {
            return getType (t).toString();
          }
            throw new IllegalArgumentException("typeDescriptor is not a class typedescriptor: '"+ typeDescriptor +"'");
        }
        String t = typeDescriptor;
        int idx = 0;
        while (idx < t.length() && t.charAt(idx) == '[') {
          idx++;
        }
        //Debug.printDbg("t ", t ," idx ", idx);
        String className = typeDescriptor.substring(idx);

        className = className.substring(className.indexOf('L') + 1, className.indexOf(';'));
        
        className = className.replace('/', '.');
//        for (int i = 0; i<idx; i++) {
//          className += "[]";
//        }
        return className;
    }
    
    public static Type getType(String type) {
        System.out.println("get type from '"+ type +"'");
      int idx = 0;
      int arraySize = 0;
      Type returnType = null;
      boolean notFound = true;
      while( idx < type.length() && notFound) {
        switch( type.charAt( idx ) ) {
          case '[':
            while (idx < type.length() && type.charAt(idx) == '[') {
              arraySize++;
              idx++;
            }
            continue;
            //break;

          case 'L':
            String objectName = type.replaceAll("^[^L]*L", "").replaceAll(";$", "");
            returnType = RefType.v (objectName.replace("/","."));
            notFound = false;
            break;

          case 'J':
            returnType = LongType.v();
            notFound = false;
            break;

          case 'S':
            returnType = ShortType.v();
            notFound = false;
            break;

          case 'D':
            returnType = DoubleType.v();
            notFound = false;
            break;

          case 'I':
            returnType = IntType.v();
            notFound = false;
            break;

          case 'F':
            returnType = FloatType.v();
            notFound = false;
            break;

          case 'B':
            returnType = ByteType.v();
            notFound = false;
            break;

          case 'C':
            returnType = CharType.v();
            notFound = false;
            break;

          case 'V':
            returnType = VoidType.v();
            notFound = false;
            break;

          case 'Z':
            returnType = BooleanType.v();
            notFound = false;
            break;

          default:
            Debug.printDbg("unknown type: '", type ,"'");
            Thread.dumpStack();
            System.exit(-1);
            break;
        }
        idx++;
      }
      if (arraySize > 0) {
        returnType = ArrayType.v(returnType, arraySize);
      }
      Debug.printDbg("casttype i:", returnType);
      return returnType;
    }

    /**
     * Check if passed class name is a byte code classname.
     *
     * @param className the classname to check.
     */
    public static boolean isByteCodeClassName(String className) {
        return ((className.startsWith("L") || className.startsWith("["))
                && className.endsWith(";")
                && ((className.indexOf('/') != -1 || className.indexOf('.') == -1)));
    }

    /**
     * Concatenate two arrays.
     *
     * @param first first array
     * @param second second array.
     */
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Returns if the type is a floating point type.
     *
     * @param t the type to test
     */
    public static boolean isFloatLike(Type t) {
        return t.equals(FloatType.v()) ||
               t.equals(DoubleType.v()) ||
               t.equals(RefType.v("java.lang.Float")) ||
               t.equals(RefType.v("java.lang.Double"));
    }
    
    /**
     * Insert a runtime exception before unit u of body b. Useful to analyze broken code (which make reference to inexisting class for instance)
     */
    public static void addRuntimeExceptionAfterUnit (Body b, Unit u ,String m) {
      Local l = Jimple.v().newLocal("myException", RefType.v("java.lang.RuntimeException"));
      b.getLocals().add(l);
      
      List<Unit> newUnits = new ArrayList<Unit>();
      Unit u1 = Jimple.v().newAssignStmt (l, Jimple.v().newNewExpr(RefType.v("java.lang.RuntimeException")));
      Unit u2 = Jimple.v().newInvokeStmt (Jimple.v().newSpecialInvokeExpr(
          l, 
          Scene.v().getMethod("<java.lang.RuntimeException: void <init(java.lang.String)>").makeRef(), 
          StringConstant.v(m)));
      Unit u3 = Jimple.v().newThrowStmt(l);
      newUnits.add(u1);
      newUnits.add(u2);
      newUnits.add(u3);
      
      b.getUnits().insertBefore(newUnits, u);
    }

    public static void addConstantTags(Body b) {
      for (Unit u: b.getUnits()) {
        Stmt s = (Stmt)u;
        if (s instanceof AssignStmt && s.containsFieldRef()) {
          AssignStmt ass = (AssignStmt)s;
          Value r = ass.getRightOp();
          Value l = ass.getLeftOp();
          if (!(l instanceof FieldRef))
            continue;
          if (!(r instanceof Constant))
            continue;
          FieldRef fr = ass.getFieldRef();
          SootField sf = fr.getField();
          if (sf.isFinal())
            addConstantTag(sf, (Constant)r);
        }
      }
    }
    
    private static void addConstantTag(SootField sf, Constant c) {
      if (c instanceof IntConstant){
        sf.addTag(new soot.tagkit.IntegerConstantValueTag(((IntConstant) c).value));
      } else if (c instanceof LongConstant){
        sf.addTag(new soot.tagkit.LongConstantValueTag(((LongConstant) c).value));
      } else if (c instanceof DoubleConstant){
        sf.addTag(new soot.tagkit.DoubleConstantValueTag(((DoubleConstant)c).value));
      } else if (c instanceof FloatConstant){
        sf.addTag(new soot.tagkit.FloatConstantValueTag(((FloatConstant)c).value));
      } else if (c instanceof StringConstant){
        sf.addTag(new soot.tagkit.StringConstantValueTag(((StringConstant) c).value));
      } else {                                                                                                                                                                                 
        //throw new RuntimeException("Expecting static final field to have a constant value! For field: "+field+" of type: "+field.fieldInstance().constantValue().getClass());
      }   

    }
    
    public static List<String> splitParameters(String parameters) {
        List<String> pList = new ArrayList<String>();
        
        int idx = 0;
        int arraySize = 0;
        boolean object = false;
        
        String curr = "";
        while( idx < parameters.length()) {
          char c = parameters.charAt(idx);
          curr += c;
          switch( c ) {
              // array
            case '[':
                break;
              // end of object
            case ';':
                object = false;
                pList.add(curr);
                curr = "";
                break;
              // start of object
            case 'L':
                object = true;
                break;
           default:
               if (object) {
                // caracter part of object
               } else { // primitive
                   pList.add(curr);
                   curr = "";
               }
               break;
            
          }
          idx++;
        }

        return pList;
    }
}
