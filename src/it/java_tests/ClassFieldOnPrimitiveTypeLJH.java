/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
class Tester {
  public static void checkEqual(String a, String b, String c) {}
  public static void check(boolean b, String s) {}
}

public class ClassFieldOnPrimitiveTypeLJH
{
    public static void main(String[] args) { test(); }

    public static void test() {
        Tester.checkEqual(checkBoolean(), "boolean", "boolean");
        Tester.checkEqual(checkChar(), "char", "char");
        Tester.checkEqual(checkByte(), "byte", "byte");
        Tester.checkEqual(checkShort(), "short", "short");
        Tester.checkEqual(checkLong(), "long", "long");
        Tester.checkEqual(checkFloat(), "float", "float");
        Tester.checkEqual(checkDouble(), "double", "double");
        Tester.checkEqual(checkIntArray(), "[Z", "boolean[]");
        checkIntArray();
    }
    
    public static String checkVoid() {
        Class c = void.class;
        return c.getName();
    }
    
    public static String checkBoolean() {
        Class c = boolean.class;  
        Tester.check(c.isPrimitive(), "check isPrimitive");
        return c.getName();
    }
    
    public static String checkChar() {
        Class c = char.class;  
        return c.getName();
    }
    
    public static String checkByte() {
        Class c = byte.class;  
        return c.getName();
    }
    
    public static String checkShort() {
        Class c = short.class;  
        return c.getName();
    }
    
    public static String checkInt() {
        Class c = int.class;  
        Tester.check( c == Integer.TYPE, "check Type field");
        return c.getName();
    }
    
    public static String checkLong() {
        Class c = long.class;  
        return c.getName();
    }
    
    public static String checkFloat() {
        Class c = float.class;  
        return c.getName();
    }
    
    public static String checkDouble() {
        Class c = double.class;  
        return c.getName();
    }
    
    public static String checkIntArray() {
        Class c = boolean[].class;
        return c.getName();
    }
}
