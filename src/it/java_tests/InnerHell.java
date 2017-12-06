import javax.swing.*;

public class InnerHell {
    public static void main(String[] args) {
        AbstractButton b = new MyButton();
    }
}

class MyButton extends AbstractButton {
    class AccessibleMyButton extends AccessibleJComponent { 
    }
}

