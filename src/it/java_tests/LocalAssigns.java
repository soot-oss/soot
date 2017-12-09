public class LocalAssigns {

    private int x = 9;
    public static void main(String [] args){
        LocalAssigns la = new LocalAssigns();
        la.run();
    }

    public void run(){
        int y = 0;
        y = x;
        System.out.println(y);
        y += 9;
        System.out.println(y);
        y -= 9;
        System.out.println(y);
        y *= 9;
        System.out.println(y);
        y /= 9;
        System.out.println(y);
        y %= 9;
        System.out.println(y);
        y >>= 9;
        System.out.println(y);
        y >>>= 9;
        System.out.println(y);
        y <<= 9;
        System.out.println(y);
        y |= 9;
        System.out.println(y);
        y &= 9;
        System.out.println(y);
        y ^= 9;
        System.out.println(y);
    }
}
