package soot.asm;

import javax.annotation.Nullable;

public class ScopeFinderTarget {

    public Object field;

    public static Object static_field;

    static {
        static_field = new Object();
    }

    public ScopeFinderTarget() {
        field = new Object();
    }

    @Nullable
    public void method() {
        System.out.println("in method");
    }

    public static class Inner {

        public Object field;

        public static Object static_field;

        static {
            static_field = new Object();
        }

        public Inner() {
            field = new Object();
        }

        public void method() {
            System.out.println("in method");
        }

        public class InnerInner {
            public Object field;

            public InnerInner() {
                field = new Object();
            }

            public void method() {
                System.out.println("in method");
            }
        }
    }

    public ScopeFinderTarget(Object param) {
        field = param;
    }


    public void methodPara(String p1){
        System.out.println(p1);
    }

    public void methodPara(String p1, String p2){
        System.out.println(p1);
        System.out.println(p2);
    }

}
