public class AssignUsed {

    public int j = 4;
    public int y = j * 4;
    
    public static void main(String [] args){
        AssignUsed au = new AssignUsed();
        au.run();
    }

    public void run(){
        int i = 8;
        int x = i * i;

        System.out.println(x = y);
        System.out.println(y = x);
    }
}
