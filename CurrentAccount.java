import java.util.Locale;

/** A current account extends the shared Account base class. */
// INHERITANCE: CurrentAccount is a specialized Account.
public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double DEFAULT_OVERDRAFT_LIMIT = 10000.00;

    private double overdraftLimit;

    // CONSTRUCTOR OVERLOADING: uses the default overdraft limit of ₹10,000.
    public CurrentAccount(String accountNumber, Customer customer, double initialBalance)
            throws InvalidAmountException {
        this(accountNumber, customer, initialBalance, DEFAULT_OVERDRAFT_LIMIT);
    }

    // CONSTRUCTOR: permits a different overdraft limit when creating an account in code.
    public CurrentAccount(String accountNumber, Customer customer,
                          double initialBalance, double overdraftLimit)
            throws InvalidAmountException {
        super(accountNumber, customer, initialBalance);
        if (!Double.isFinite(overdraftLimit) || overdraftLimit < 0.0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative or invalid.");
        }
        this.overdraftLimit = roundMoney(overdraftLimit);
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    /** Current-account withdrawal availability includes the remaining overdraft. */
    public double getAvailableWithdrawalAmount() {
        return Math.max(0.0, roundMoney(getBalance() + overdraftLimit));
    }

    // POLYMORPHISM: current accounts override withdrawal to apply the overdraft rule.
    @Override
    public void withdraw(double amount)
            throws InvalidAmountException, InsufficientBalanceException {
        double validAmount = validatePositiveAmount(amount);
        double availableFunds = getAvailableWithdrawalAmount();
        if (validAmount > availableFunds + 0.000001) {
            throw new InsufficientBalanceException(getAccountNumber(), availableFunds, validAmount);
        }
        changeBalanceBy(-validAmount);
    }

    @Override
    public double calculateInterest() {
        // This simple project does not pay interest on current accounts.
        return 0.0;
    }

    @Override
    public String getAccountType() {
        return "Current Account";
    }

    @Override
    public String getAccountSpecificDetails() {
        return String.format(Locale.US, "Overdraft Limit: ₹%,.2f", overdraftLimit);
    }
}
