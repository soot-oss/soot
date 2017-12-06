public class STest2 extends SuperB {
    
    private int x = 9;
    
    public static void main(String [] args){
        STest2 st2 = new STest2();
        st2.run();
    }

    
    public void run(){
        System.out.println(super.z);
        super.go();
    }

    public void go(){
        System.out.println("going");
    }
}

class SuperB extends SuperA {
    public int z = 6;
    protected int w = 7;
    public void go(){
        System.out.println("go from SuperB");
    }
    public void go1(){
        System.out.println("go1 from SuperB");
    }
    protected void going1(){
        System.out.println("going1 from SuperB");
    }
}
class SuperA {
    public int x = 9;
    protected int y = 8;
    public void go(){
        System.out.println("go from SuperA");
    }
    protected void going(){
        System.out.println("going from SuperA");
    }
}

