class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}
public class Test57{

    public static void main(String [] args){
        Test57 t57 = new Test57();
        t57.run(5);
    }
    
    public void run(final int x){
        new Helper(){
        
            public void action(){
                
                new Helper() {
                    public void action(){
                        System.out.println(x);

                        new Helper(){
                            public void action(){
                                System.out.println(x*x);
                            }
                        }.action();
                    }
                }.action();
            }
        }.action();
    }
}
