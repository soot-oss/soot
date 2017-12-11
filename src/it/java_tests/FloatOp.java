public class FloatOp {
    
    public static void main(String [] args){
        System.out.println(-0.0f);
        System.out.println((""+ Float.NEGATIVE_INFINITY) + -0.0f);
        float f = -0.0f + 0.0f;
        String s = "" + f;
        run(s);
        float y = f + 0;
    }
    
    public static void run(String s){
        System.out.println(s);
    }
}
