public class ReachingDefsTest1 {

    public static void main (String [] args) {
        ReachingDefsTest1 rdt1 = new ReachingDefsTest1();
        rdt1.m(8);
    }

    public void m(int i){
        int x = 4;
        int y = 3;
        if (i < 10) {
            x = 5;
        }
        else if (i == 10){
            x = 6;
        }
        else {
            x = 7;
        }
        int j = x * y;
        
    }
    
    public void n(int i){
        int x = 4;
        if (i < 10){
            x = 5;
        }
        else if (x == 10){
            x = 6;
        }
        
        System.out.println(x);
    }
}
