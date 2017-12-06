public class OrderOfTypes {
    public static void main(String[] args) {}
}

class Root {
    class RootInner {}
}

class Second extends First {
    class SecondInner extends RootInner {}
}

class First extends Root {
}
