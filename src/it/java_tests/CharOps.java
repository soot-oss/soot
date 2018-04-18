public class CharOps {

    public static void main(String [] agrs){
        char c1 = 'j';
        System.out.println(charOp("" + (char) 0, (char) 0));
        System.out.println( ("" + (char) 0) + (char) 0);
        System.out.println((short)0);
        System.out.println("Max Char: "+Character.MAX_VALUE);
        System.out.println(Character.MAX_VALUE);
        System.out.println(charOp("" + Character.MAX_VALUE, (char) 0));
        System.out.println(("" + Character.MAX_VALUE) + (char) 0);
        System.out.println(charOp(""+(char)1 , Character.MAX_VALUE));
        System.out.println((""+(char)1) + Character.MAX_VALUE);

        System.out.println();
        System.out.println(charOp("" + Character.MAX_VALUE, (char) 0));
        System.out.println("" + Character.MAX_VALUE + (char) 0);     
    }
    

    static String charOp(String x, char y) { return x + y; }
}
