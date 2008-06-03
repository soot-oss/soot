class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}
public class Test44{

    public static void main(String [] args){
        Test44 t44 = new Test44();
        t44.run(8);
    }
    
    public void run(final int x){
        new Helper(){
        
            public void action(){
                
                new Helper() {
                    public void action(){
                        System.out.println(x);
                    }
                }.action();
            }
        }.action();
    }
}
