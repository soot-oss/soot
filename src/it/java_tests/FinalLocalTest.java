public class FinalLocalTest {

    public static void main(String [] args){
        FinalLocalTest flt = new FinalLocalTest();
        flt.run();
    }

    public void run(){
        final int i = 0;
        new Object() {
            public void run(){
                {
                    int i = 9;
                    System.out.println(i);
                }
                System.out.println(i);
            }
        }.run();
    }
}
