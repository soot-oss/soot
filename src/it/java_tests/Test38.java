
public class Test38 {

    public static void main(String [] args){
        Test38 t = new Test38();
        t.run();
    }

    public void run (){
        Inner1 in1 = new Inner1();
        in1.run();
    }

    public class Inner1 {
        
        public void run(){
            System.out.println("Smile from Inner1");
            Inner2 in2 = new Inner2();
            in2.run();
        }
    }
    
    public class Inner2 {
        
        public void run(){
            System.out.println("Smile from Inner2");
        }
    }
}
