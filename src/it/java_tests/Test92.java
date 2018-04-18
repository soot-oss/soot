public class Test92 {
    public static void main(String [] args){
        Test92 t92 = new Test92();
        int j = (new Integer(8)).intValue();
        t92.run(j);
    }

    public void run(final int x){
        
        new Object() {
            int y = x * 7;
            public void run(){
                System.out.println(y);
            }
        }.run();
    }
}
