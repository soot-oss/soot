class IfTest5 {
    public static void main(String[] args) {
        int x,y;
                 
        x=1;
        y=2;
        if( x > 2 ) {
            x = y;
            if (y > 1) {
                x = x+1;
            }
            else
                x = x-1;
        }
        else
           y = x;      
    }
}

