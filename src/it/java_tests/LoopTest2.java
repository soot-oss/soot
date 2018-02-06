public class LoopTest2 {

    int i = 9;

    public static void main(String [] args){
        LoopTest2 lt = new LoopTest2();        
        if (lt.sum(lt.i) > 10) return;
    }

    public int sum(int x){
        return x*x;
    }
}
