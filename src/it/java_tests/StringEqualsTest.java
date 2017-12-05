public class StringEqualsTest {

    public static void main(String [] args){
        String l1 = "the" + "re";
        String l2 = "ther" + "e";


        if (l1 == l2) {
            System.out.println("== works");
        }

        if (l1.equals(l2)){
            System.out.println(".equals works");
        }
        
    }
}
