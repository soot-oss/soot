public class MethodIntroductions2 {
    public static void main(String[] args) {
        new MethodIntroductions2().realMain(args);
    }
    public void realMain(String[] args) {
        AbstractSuperNoIntro as0n = new AbstractSuperNoIntro() { public
        int foo() { return 7; } };
        System.out.println(as0n.foo());
    }

}

interface I {
    public int foo();
}

abstract class AbstractSuperNoIntro implements I {}

