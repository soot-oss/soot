interface TestsInterface {

    public static int staticVariable = 3;
}

public class TestsClass implements TestsInterface {

    public static void main(String [] args){
        TestsClass tc = new TestsClass();
        tc.run();
    }
    
    public void run(){
        int x = staticVariable;    
    }
}
