package soot.toolkits.graph.targets;

public class TestException {

    public static void main(String[] args) {
        int a = 0;
        int b = 0;

        try {
            a = a / b;
        }
        catch (Exception e) {
            a = Math.abs(-1);
        }
        finally {
            b = 0;
        }
    }

}