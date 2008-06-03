class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}
public class Test84{

    public static void main(String [] args){
        Test84 t84 = new Test84();
        t84.run(5);
    }
    
    public void run(int x){
        new Helper(){
            int x = 9;
            public void action(){
                System.out.println("x: "+x);
            }
        }.action();
    }
}
