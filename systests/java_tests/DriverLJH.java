// PR#192

public class DriverLJH {
    private static java.util.Vector v = new java.util.Vector();
    
    public static void main(String[] args) { test(); }

    public static void test() {
        DriverLJH temp = new DriverLJH();
        
        Inner inner = temp.new Inner();    
        
        if (inner.isInst)
          System.out.println("Inner instance flag is true");
    }
    
    class Inner {
        public boolean isInst = true;
    }
}
