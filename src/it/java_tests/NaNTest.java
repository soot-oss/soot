public class NaNTest {

    public float fx = Float.NaN;

    public static void main(String [] args){
        NaNTest n = new NaNTest();
        n.run();
    }

    public void run(){
        boolean b = 0 < fx;
        System.out.println(!b);
        System.out.println(fx);
    }
}
