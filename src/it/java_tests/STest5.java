class Super1 {
    public int x = 9;
}

public class STest5 extends Super1{

    public int x = 8;

    public static void main(String [] args){
        STest5 st5 = new STest5();
        st5.run();
    }

    public void run(){
        System.out.println("this x: "+this.x);
        System.out.println("super x: "+super.x);
    }
}
