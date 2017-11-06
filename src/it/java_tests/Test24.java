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
public class Test24 {

    public static void main (String [] args){
        Test24 t24 = new Test24();    
    }

    public void run(final int j){
        class MyHelper1 extends HelperWithParams {

            public MyHelper1(){
            }
            public MyHelper1(int x, int y){
            }
            public void action(){
                System.out.println("Smile Anon: "+j);
            }
        };
        new MyHelper1(2, 3).action();
    }
}
