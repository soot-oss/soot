class Aaa {
        class Ccc {}
        class Bbb {}
        public class Ddd{}
        
        Ccc ccc;

        public Aaa() {
            ccc = new Ccc();
       }
}

public class Test30 {

        public static void main(String[] args) {
            Aaa aaa = new Aaa();
        }

}

