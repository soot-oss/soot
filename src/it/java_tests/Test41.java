
public class Test41 {

    public static void main(String [] args){
        Test41 t = new Test41();
        t.run();
    }

    public Test41(){
        System.out.println("init for Test41");
    }
    
    public void run (){
        Inner in = new Inner();
        in.run();
    }

    public class Inner extends Test41{
        
        public Inner(){
            this(4);
        }

        public Inner(int x){
            super();
            System.out.println("Second Init Inner");
            run();
        }
        
        public void run(){
            System.out.println("Smile");
        }
    }
}
