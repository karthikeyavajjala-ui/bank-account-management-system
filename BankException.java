/** Base checked exception for expected banking and validation errors. */
public class BankException extends Exception {
    private static final long serialVersionUID = 1L;

    // CONSTRUCTOR
    public BankException(String message) {
        super(message);
    }
}
