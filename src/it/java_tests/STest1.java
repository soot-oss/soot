public class STest1 {
    
    private int x = 9;
    
    public static void main(String [] args){
        STest1 st1 = new STest1();
        st1.run();
    }

    
    public void run(){
        System.out.println(this.x);
        this.go();
    }

    public void go(){
        System.out.println("going");
    }
}
