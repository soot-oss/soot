public class Hello {
    private int i;
    
    public static void main(String[] args) {
        System.out.println("Hello");
        Hello h = new Hello();
        h.run();
    }
    
    private void run() { for (this.i = 0; this.i < 10; this.i++) { System.out.println(this.i * this.i); } }
    
}
