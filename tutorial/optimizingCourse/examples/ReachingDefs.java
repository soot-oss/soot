public class ReachingDefs {
    public static void main (String [] args) {
        ReachingDefs rdt1 = new ReachingDefs();
        rdt1.m(8);
        rdt1.n();
    }
    public void m(int i){
        int x = 4;
        int y = 3;
        if (i < 10) {
            x = 5;
        }
        else {
            x = 7;
            y = 18;
        }
        int j = x * y;   
    }
    public void n(){
    	int x = 9;
    	int y = 0;
    	while (x < 10){
    		y = x + 3;
    		x = x + 1;
    	}
    	int z = x + 2;
    }
}
