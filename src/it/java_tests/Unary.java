public class Unary {

    
    public static void main (String [] args){
        int i = 0;
        i++;
        i--;
        ++i;
        --i;
        i = +i;
        i = -i;
        i = ~i;
        boolean j = false;
        boolean k = !j;

        int n = 0;

        for (int m = 0; m < 10; m++){
            n = 9+m;
        }

        int [] arr = new int [] {3, 4, 5};
        int h = 0;
        int x = arr[h++];
        h = 0;
        int y = arr[++h];
    }
}
