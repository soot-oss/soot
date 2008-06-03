class Super5 {
    public void run(String s){
        System.out.println("running in super "+s);
    }
}   

public class Test90 extends Super5 {
    
    public int x = 9;
    
    public static void main(String [] args){
        Test90 t90 = new Test90();
        t90.run();
    }

    public void run(){
        Inner in = new Inner();
        in.run();
    }

    class Inner {
        public void run(){
            System.out.println("Test90.super.run(): ");
            Test90.super.run(getMyString());
        }

        public String getMyString(){
            return "hi";
        }
    }

}
