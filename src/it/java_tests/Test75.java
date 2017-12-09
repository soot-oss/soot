public class Test75{

    public static void main(String [] args){
        Test75 t75 = new Test75();
        t75.run(5);
    }
    
    public void run(final int x){
        class MyHelper1{
        
            public void action(){
                
                class MyHelper2 {
                    public void action(){
                        System.out.println(x);

                        class MyHelper3{
                            public void action(){
                                System.out.println(x*x);
                                class MyHelper4{
                                    public void action(){
                                        System.out.println(x*x*x*x*x);
                                    }
                                };
                                MyHelper4 m4 = new MyHelper4();
                                m4.action();
                            }
                        };
                        MyHelper3 m3 = new MyHelper3();
                        m3.action();
                    }
                };
                MyHelper2 m2 = new MyHelper2();
                m2.action();
            }
        };
        MyHelper1 m1 = new MyHelper1();
        m1.action();
    }
}
