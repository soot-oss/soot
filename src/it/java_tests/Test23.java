class HelperWithParams {

    public static void main(String [] args){
    }
    public HelperWithParams(int x, int y){
        
    }
    
    public HelperWithParams(){
        
    }
    
    public void action(){
        System.out.println("Helper");
    }
}
public class Test23 {

    public static void main (String [] args){
        Test23 t23 = new Test23();
        t23.run();
    }

    public void run(){
        class MyHelper1 extends HelperWithParams {

            public MyHelper1(){
            }
            public MyHelper1(int x, int y){
            }
            public void action(){
                System.out.println("Smile Anon");
            }
        };
        new MyHelper1(2, 3).action();
    }
}
