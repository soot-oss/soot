public class NameTests {
    public static void main(String[] args) {
        NameTests nt = new NameTests();
        nt.run(new NameTests());
    }
    
    private void run(NameTests nameTests) { NameTests n = new NameTests(); }
    
}
