public class UnaryInc2 {

    int count = 0;
    
    public static void main(String [] args){
        UnaryInc2 u = new UnaryInc2();
        u.run();
    }

    public void run(){
        new Runnable(){
            public void run(){
                count++;
            }
        }.run();
    }


}
