class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}
public class Test101{

    public static void main(String [] args){
        Test101 t101 = new Test101();
        t101.run(5);
    }
    
    public void run(final int x){
        new Helper(){
        
            public void action(){
                
                class MyHelper1 {
                    public void action(){
                        System.out.println(x);

                        new Helper(){
                            public void run(final int y){
                                System.out.println(x*x);
                                class MyHelper2{
                                    public void action(){
                                        System.out.println(x*y*x*x*x);
                                    }
                                };
                                MyHelper2 m2 = new MyHelper2();
                                m2.action();
                                class MyHelper3 extends MyHelper2{
                                    public void action(){
                                        super.action();
                                    }
                                };
                                new MyHelper3().action();
                                class MyHelper4 extends MyHelper3{
                                    public void action(){
                                        super.action();
                                    }
                                };
                                new MyHelper4().action();
                                
                            }
                           
                        }.run(7);
                    }
                };
                MyHelper1 m1 = new MyHelper1();
                m1.action();
            }
        }.action();
    }
}
