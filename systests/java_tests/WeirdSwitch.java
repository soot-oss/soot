public class WeirdSwitch {

    public static void main(String [] args) {
        switch (args.length + 0x7ffffff2) {
            case 0x7ffffff0:
            case 0x7ffffff1:
            case 0x7ffffff2:
            case 0x7ffffff3:
               System.out.print("OK");
               break;
            default:
               System.out.print("NOT_OK");
        }

    }
}
