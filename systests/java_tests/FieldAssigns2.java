public class FieldAssigns2 {

    private int x = 7;
    private int y = 8;
    public static void main(String [] args){
        FieldAssigns2 fa2 = new FieldAssigns2();
        fa2.run();
    }

    public void run(){
        x = y;
        System.out.println(x);
        x += y;
        System.out.println(x);
        x -= y;
        System.out.println(x);
        x *= y;
        System.out.println(x);
        x /= y;
        System.out.println(x);
        x %= y;
        System.out.println(x);
        x >>= y;
        System.out.println(x);
        x >>>= y;
        System.out.println(x);
        x <<= y;
        System.out.println(x);
        x |= y;
        System.out.println(x);
        x &= y;
        System.out.println(x);
        x ^= y;
        System.out.println(x);
    }
}
