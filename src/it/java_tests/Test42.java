
public class Test42 {

    public static void main(String [] args){
        Test42 t = new Test42();
        t.run();
    }

    public Test42(){
        System.out.println("init for Test42");
    }
    
    public void run (){
        Inner in = new Inner();
        in.run();
    }

    public class Inner extends Test42{
        
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

    public class Inner2 extends Inner {
        public Inner2(){
            super();
            System.out.println("Init called for Inner2");
        }
    }
}
