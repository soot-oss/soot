class C {
    int x;
}

public class FieldGets {

    public static void main(String[] args) {
        C c = new C();
        c.x = 3;
        int i = 7;
        int j = 8;
        c.x = i + j;
        System.out.println(c.x);
        System.out.println(c.x);
        System.out.println(c.x);
    }
}

