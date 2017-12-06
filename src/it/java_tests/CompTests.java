public class CompTests {

    public static void main(String [] args) {
        CompTests ct = new CompTests();
        ct.run(10L);
    }

    private void run(long time){
        boolean neg;

        neg = time < 0;
        if (neg) time = -time;
    }
} 
