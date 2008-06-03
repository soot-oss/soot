public class Test29 {

    public static void main(String [] args){
        Test29 t = new Test29();
        t.run();
    }
    
    public void run(){
        TL1 tl1 = new TL1();
        tl1.run();
        TL2 tl2 = new TL2();
        tl2.run();
    }
}

class TL1{
    public void run(){
        System.out.println("Top-level 1");
    }
}

class TL2{
    public void run(){
        System.out.println("Top-level 2");
    }
}
