public class FieldBaseTest {

    Point p1 = new Point();
    Point p2 = new Point();

    public static void main(String [] args){
        FieldBaseTest f = new FieldBaseTest();
        f.run();
    }
    
    public void run(){
        int i = 3;

        p1.x = 9;
        p1.y = 8;

        p2.x = i;
        p2.y = 4;

        if ((p1.x - p2.x) > (p1.y - p2.y)){
            p1.x = p1.y;
        }
    }

    public void test(Point p){
        if (p.x > 3){
            p.y = 3;
        } 
    }
}

class Point {

    public int x;
    public int y;
}
