public class FinalStaticTest {

    public static final int x = 9;
    public static final long y = 982L;
    public static final double z = 9.0;
    public static final float a = 9.23e3f;
    public static final char c = 'c';
    public static final byte by = 1;
    public static final short sh = 2;
    
    public static final String b = "Jennifer";
    public static final String q = "string with \" quotes";
    public static final int [] arr = new int [] {1, 1, 2, 3, 5, 8, 13, 21};
    public static final Object o = new Object();
    
    public static void main(String [] args){
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);
        System.out.println(a);
        System.out.println(b);
        System.out.println("quotes \" in string");
    }
}
