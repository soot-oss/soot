public class Test {
    public static String bar() { return "bar"; }

    public static void main(String[] args) {
        String s="foo";
        s+=bar();
    }

}

