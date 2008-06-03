public class WhileTestBreak {
    public static void main(String [] args) {
        int i = 4;
        outer:while (i < 10 ) {
            System.out.println(i);
            if (i == 8) break outer;
            i = i + 2;
        }
        
    }
}
