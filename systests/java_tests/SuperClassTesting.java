public class SuperClassTesting {

    int x = 9;

    public static void main(String [] args) {
        SuperClassTesting sc = new SuperClassTesting();
        sc.run();
    }

    public void run(){
        Integer int1 = new Integer(8);
        work(int1);
    }

    public void work(Object o) {
        System.out.println("Object method used");
    }

    public void work(Number n) {
        System.out.println("Number method used");
    }
   
}
