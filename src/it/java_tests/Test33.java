
public class Test33 {

    public static void main(String [] args){
        Test33 t = new Test33();
        t.run();
    }

    public void run (){
        Inner in = new Inner();
        in.run();
    }

    public class Inner {
        
        public void run(){
            System.out.println("Smile");
        }
    }
}
