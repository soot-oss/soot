public class Game implements GameConstants {

    public static void main(String [] args) {
        System.out.println("Playing from: "+LOW+" to "+HIGH);
    }
}

interface GameConstants {

    public static int HIGH = 100;
    public static int LOW = 1;

}

