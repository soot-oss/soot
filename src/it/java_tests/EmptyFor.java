public class EmptyFor {

    public static void main(String [] args) {
        int i = 0;
        for ( ; i< 100000; i += 12345){
            System.out.println(i);
            if (i < 100000) continue;
        }
        System.out.println(i);
        
    }
}
