public class PrivateAccessFieldSets {
    
    private String s = "hi";

    public static void main(String [] args){
        PrivateAccessFieldSets pafs = new PrivateAccessFieldSets();
        pafs.run();
    }

    public void run(){
        new Object() {
            public void run(){
                s = "hello";
                System.out.println(s);
            }
        }.run();
        new Object() {
            public void run(){
                s += " there";
                System.out.println(s);
            }
        }.run();
        new Object() {
            public void run(){
                int x = s.length();
                System.out.println(x);
            }
        }.run();
    }
}
