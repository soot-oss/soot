public class LocalClassTest {

    public static void main(String [] args){
        LocalClassTest lct = new LocalClassTest();
        lct.run();
    }

    public void run(){
        final int i = 8;
        class MClass {
            public void run(){
                System.out.println("class m: "+i);
            }
        }
        class KClass {
            public void run(){
                System.out.println("class k");
            }
        }
        class JClass {
            public void run(){
                System.out.println("class j");
                new KClass();
            }
        }
    }
}
