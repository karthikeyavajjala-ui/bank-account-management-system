import java.util.Locale;

/** Thrown when an account cannot cover a requested withdrawal. */
public class InsufficientBalanceException extends BankException {
    private static final long serialVersionUID = 1L;

    private final double availableFunds;
    private final double requestedAmount;

    // CONSTRUCTOR
    public InsufficientBalanceException(String accountNumber,
                                        double availableFunds,
                                        double requestedAmount) {
        super("Insufficient balance!\nAccount: " + accountNumber
                + "\nAvailable funds: ₹" + String.format(Locale.US, "%,.2f", availableFunds)
                + "\nRequested amount: ₹" + String.format(Locale.US, "%,.2f", requestedAmount));
        this.availableFunds = availableFunds;
        this.requestedAmount = requestedAmount;
    }

    public double getAvailableFunds() {
        return availableFunds;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }
}
