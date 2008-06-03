import java.util.*;

public class Simple2 {
    private int x;
    
    public static void main(String[] args) {
        int j = 1;
        int i = 2;
        ;
        if (i + j > 1) { System.out.println("Hello" + i); }
        Simple2.add(1, 2);
        Simple2 simple = new Simple2();
        System.out.println(Simple2.add(2, 3));
        simple.run();
    }
    
    public static String getString() { return "Hello"; }
    
    public static int add(int i, int j) { return i + j; }
    
    public Simple2() { this(8); }
    
    public Simple2(int y) {
        super();
        this.x = y;
        System.out.println(this.x);
    }
    
    public void run() { int[] arr = { 9, 0, 8 }; }
    
}
