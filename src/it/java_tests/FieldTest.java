public class FieldTest {

    public String f = new String("k");

    public static void main(String [] args){

        FieldTest ft = new FieldTest();
        ft.run();
    }

    public void run(){
        int i;
        System.out.println("my field: "+f);       
    }
}
