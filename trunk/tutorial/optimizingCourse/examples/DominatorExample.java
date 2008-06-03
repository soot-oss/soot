public class DominatorExample {

    public static void main (String [] args){
        
        int x = 9;
        
        for (int i = 9; i < 10; i++){
            x++;    
        }

        for (;;){
            if (x < 4) break;
            x--;
        }

        do {
            x += 2;
        }while(x < 15);

        while (x > 10){
            x -= 3;
        }

        while (true){
            if (x > 47) break;
            x *= 3;
        }

        while (x > 4) {
            x--;
            if (x % 3 != 0) continue;
            System.out.println(x);
        }

        int [] arr = new int[9];
        for (int m = 0; m < 10; m++){
            x = 4;
            arr[4] = 8;
            arr[4] = m;
            arr[x] = 8;
        }
    }
}
