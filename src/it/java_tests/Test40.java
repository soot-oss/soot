
public class Test40 {

    public static void main(String [] args){
        Test40 t = new Test40();
        t.run();
    }

    public void run (){
        Inner in = new Inner();
        in.run();
    }

    public class Inner {
        
        public Inner(){
            this(4);
        }

        public Inner(int x){
            run();
        }
        
        public void run(){
            System.out.println("Smile");
        }
    }
}
