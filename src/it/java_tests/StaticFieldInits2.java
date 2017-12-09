public class StaticFieldInits2 {

    static int x = 9;
    static int y = 10;
    
    static boolean b1 = x > y;

    public static void main(String [] args){
        System.out.println(x);
        System.out.println(y);
        System.out.println(b1);
    }
}
