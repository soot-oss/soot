class Super4 {
    public void run(){
        System.out.println("running in super");
    }
}   

public class Test86 extends Super4 {
    
    public int x = 9;
    
    public static void main(String [] args){
        Test86 t86 = new Test86();
        t86.run();
    }

    public void run(){
        Inner in = new Inner();
        in.run();
    }

    class Inner {
        public void run(){
            System.out.println("Test86.super.run(): ");
            Test86.super.run();
            System.out.println("Test86.this.go(): ");
            Test86.this.go();
        }
    }

    public void go(){
        System.out.println("running in this.go()");
    }
}
