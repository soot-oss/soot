class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}
public class Test74{

    public static void main(String [] args){
        Test74 t74 = new Test74();
        t74.run(5);
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
                                new Helper(){
                                    public void action(){
                                        System.out.println(x*x*x*x*x);
                                    }
                                }.action();
                            }
                        }.action();
                    }
                }.action();
            }
        }.action();
    }
}
