class SuperTest {
    
    public void go(){
        System.out.println("hello from super");
    }
    
}

public class SubClassTest extends SuperTest {
    public void go(){
        System.out.println("hello from sub");
    }

    public static void main(String [] args){
        SubClassTest sct = new SubClassTest();
        sct.run();
    }

    public void run(){
        this.go();
        super.go();
        new SubSubClass().new SubSubSubClass().run();
    }

    class SubSubClass {
        class SubSubSubClass{
            public void run(){
                SubClassTest.super.go();
            }
        }
    }
}
