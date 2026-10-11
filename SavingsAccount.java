import java.util.Locale;

/** A savings account reuses the common Account implementation. */
// INHERITANCE: SavingsAccount is a specialized Account.
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double DEFAULT_INTEREST_RATE = 4.0;

    private double interestRate;

    // CONSTRUCTOR OVERLOADING: uses the default annual interest rate.
    public SavingsAccount(String accountNumber, Customer customer, double initialBalance)
            throws InvalidAmountException {
        this(accountNumber, customer, initialBalance, DEFAULT_INTEREST_RATE);
    }

    // CONSTRUCTOR: parameterized constructor permits a different rate when needed.
    public SavingsAccount(String accountNumber, Customer customer,
                          double initialBalance, double interestRate)
            throws InvalidAmountException {
        super(accountNumber, customer, initialBalance);
        if (!Double.isFinite(interestRate) || interestRate < 0.0) {
            throw new IllegalArgumentException("Interest rate cannot be negative or invalid.");
        }
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    // POLYMORPHISM: this overridden method calculates savings-account interest.
    @Override
    public double calculateInterest() {
        return roundMoney(getBalance() * interestRate / 100.0);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public String getAccountSpecificDetails() {
        return String.format(Locale.US, "Interest Rate  : %.2f%% per year", interestRate);
    }
}
