public class ClassTests {

    public static void main(String [] args) {
        ClassTests ct = new ClassTests();
        ct.run();
    }

    private void run() {
        //System.out.println(this.getClass().getName());
        System.out.println(ClassTests.class.getName());
        System.out.println(String.class.getName());
    }
}
