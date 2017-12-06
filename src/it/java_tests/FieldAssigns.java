public class FieldAssigns {

    private int x;
    public static void main(String [] args){
        FieldAssigns fa = new FieldAssigns();
        fa.run();
    }

    public void run(){
        x = 9;
        System.out.println(x);
        x += 9;
        System.out.println(x);
        x -= 9;
        System.out.println(x);
        x *= 9;
        System.out.println(x);
        x /= 9;
        System.out.println(x);
        x %= 9;
        System.out.println(x);
        x >>= 9;
        System.out.println(x);
        x >>>= 9;
        System.out.println(x);
        x <<= 9;
        System.out.println(x);
        x |= 9;
        System.out.println(x);
        x &= 9;
        System.out.println(x);
        x ^= 9;
        System.out.println(x);
    }
}
