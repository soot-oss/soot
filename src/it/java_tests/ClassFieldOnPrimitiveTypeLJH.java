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
