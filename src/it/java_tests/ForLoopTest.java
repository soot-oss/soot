public class ForLoopTest {
    
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++)
            System.out.print(i + " ");
        for (int i = 10; i > 0; i--)
            System.out.print(i + " ");
        System.out.println();
        
        int i;
        class Local {
            {
                for (int i = 0; i < 10; i++)
                System.out.println(i);
            }
        }
        new Local(); 
    }

}
