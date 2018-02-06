public class Example {
    public static final void main( String[] args ) {
        int x,y,z;
        z = 1;
        x = 2;
        y = 3;
        if( x <= y ) {
            do {
            	
                y = y - 1;
            } while( y > 0 );
        } else {
            x = x + 1;
        }
        System.out.println( z );
    }
}
