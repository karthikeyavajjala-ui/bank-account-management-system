/** Thrown when a banking amount is zero, negative, or not a valid number. */
public class InvalidAmountException extends BankException {
    private static final long serialVersionUID = 1L;

    // CONSTRUCTOR
    public InvalidAmountException(String message) {
        super(message);
    }
}
