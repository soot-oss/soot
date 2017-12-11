
public class Test62 {

    public static void main(String [] args){
        Test62 t = new Test62();
        t.run();
    }

    public void run (){
        Inner in = new Inner();
        in.run();
    }

    public class Inner {
        
        public void run(){
            System.out.println("Inner - level1");
            Inner2 in2 = new Inner2();
            in2.run();
        }

        public class Inner2 {
            public void run(){
                System.out.println("Inner - level2");
                Inner3 in3 = new Inner3();
                in3.run();
            }
            
            public class Inner3 {
                public void run(){
                    System.out.println("Inner - level3");
                }
                
            }
        }
    }
}
