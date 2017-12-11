public class UnaryInc {

    private int x = 9;

    public static void main(String [] args){
        UnaryInc u = new UnaryInc();
        u.run();
    }

    public void run(){
        Inner i = new Inner();
        i.run();
    }
    
    public class Inner {
        
        public void run(){
            x++;
            System.out.println(x);
        }
    }
}
