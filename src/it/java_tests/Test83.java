class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}
public class Test83{

    public static void main(String [] args){
        Test83 t83 = new Test83();
        t83.run(5);
    }
    
    public void run(final int x){
        new Helper(){
        
            public void action(){
                int x = 9;
                System.out.println("x: "+x);
            }
        }.action();
    }
}
