class Super2 {
    public int x = 8;
}

public class Test85 extends Super2 {
    
    public int x = 9;
    
    public static void main(String [] args){
        Test85 t85 = new Test85();
        t85.run();
    }

    public void run(){
        Inner in = new Inner();
        in.run();
    }

    class Inner {
        public void run(){
            System.out.println("Test85.super.x: "+Test85.super.x);
            System.out.println("Test85.this.x: "+Test85.this.x);
        }
    }
}
