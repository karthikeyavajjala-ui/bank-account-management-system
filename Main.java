import java.awt.EventQueue;

/** Starts the Java AWT Bank Account Management System. */
public class Main {
    // CONSTRUCTOR: private because this class is only an application entry point.
    private Main() {
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BankGUI window = new BankGUI();
                window.setVisible(true);
            }
        });
    }
}
