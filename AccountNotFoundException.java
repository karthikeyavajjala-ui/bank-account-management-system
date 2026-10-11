/** Thrown when an account number is malformed or is not in the bank. */
public class AccountNotFoundException extends BankException {
    private static final long serialVersionUID = 1L;

    // CONSTRUCTOR
    public AccountNotFoundException(String accountNumber) {
        super("Account not found. Please check account number: "
                + (accountNumber == null || accountNumber.trim().isEmpty()
                ? "(empty)" : accountNumber.trim()));
    }
}
