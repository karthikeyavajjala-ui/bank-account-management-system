import java.io.Serializable;
import java.util.Locale;

/**
 * Common abstract base for all bank account types.
 * ENCAPSULATION: the account number, customer reference, and balance are private.
 * ABSTRACTION: subclasses supply their account type, interest behavior, and account rules.
 */
// ABSTRACTION: Account defines common operations and leaves account-specific behavior to subclasses.
public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    // ENCAPSULATION: balance cannot be assigned directly from the GUI or Bank class.
    private String accountNumber;
    private Customer customer;
    private double balance;

    // CONSTRUCTOR: shared initialization for SavingsAccount and CurrentAccount.
    protected Account(String accountNumber, Customer customer, double initialBalance)
            throws InvalidAmountException {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("A customer is required for an account.");
        }
        if (!Double.isFinite(initialBalance) || initialBalance < 0.0) {
            throw new InvalidAmountException("Initial deposit cannot be negative or invalid.");
        }

        this.accountNumber = accountNumber.trim().toUpperCase(Locale.ROOT);
        // Store a copy so a caller cannot change this account by retaining a Customer reference.
        this.customer = new Customer(customer.getCustomerId(), customer.getName(),
                customer.getPhoneNumber(), customer.getAddress());
        this.balance = roundMoney(initialBalance);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return customer.getName();
    }

    public String getCustomerId() {
        return customer.getCustomerId();
    }

    public String getPhoneNumber() {
        return customer.getPhoneNumber();
    }

    public String getAddress() {
        return customer.getAddress();
    }

    public double getBalance() {
        return balance;
    }

    /** Named checkBalance for a simple, easy-to-explain account query. */
    public double checkBalance() {
        return balance;
    }

    // Banking changes to the private balance are made only through these controlled methods.
    public void deposit(double amount) throws InvalidAmountException {
        double validAmount = validatePositiveAmount(amount);
        if (!Double.isFinite(balance + validAmount)) {
            throw new InvalidAmountException("The resulting balance is too large.");
        }
        changeBalanceBy(validAmount);
    }

    /** Savings accounts use this implementation; CurrentAccount overrides it. */
    public void withdraw(double amount)
            throws InvalidAmountException, InsufficientBalanceException {
        double validAmount = validatePositiveAmount(amount);
        double availableFunds = Math.max(0.0, balance);
        if (validAmount > availableFunds + 0.000001) {
            throw new InsufficientBalanceException(accountNumber, availableFunds, validAmount);
        }
        changeBalanceBy(-validAmount);
    }

    /** Validates a transaction and rounds it to the nearest paisa. */
    protected static double validatePositiveAmount(double amount) throws InvalidAmountException {
        if (!Double.isFinite(amount) || amount <= 0.0) {
            throw new InvalidAmountException("Amount must be a valid number greater than ₹0.00.");
        }
        double roundedAmount = roundMoney(amount);
        if (roundedAmount <= 0.0) {
            throw new InvalidAmountException("Amount must be at least ₹0.01.");
        }
        return roundedAmount;
    }

    /**
     * Protected balance helper: subclasses can perform controlled transactions,
     * but unrelated classes cannot directly modify the private balance field.
     */
    protected final void changeBalanceBy(double amount) {
        double updatedBalance = roundMoney(balance + amount);
        if (!Double.isFinite(updatedBalance)) {
            throw new ArithmeticException("Balance arithmetic overflow.");
        }
        balance = updatedBalance;
    }

    protected static double roundMoney(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    // POLYMORPHISM: these methods are implemented differently by account subclasses.
    public abstract String getAccountType();

    /** Returns a simple one-year interest estimate; it does not credit the balance. */
    public abstract double calculateInterest();

    public abstract String getAccountSpecificDetails();

    /** Builds a readable account report using overridden subclass methods at runtime. */
    public String displayAccountDetails() {
        StringBuilder details = new StringBuilder();
        details.append("ACCOUNT DETAILS\n");
        details.append("----------------------------------------\n");
        details.append("Account Number : ").append(accountNumber).append('\n');
        details.append("Customer ID    : ").append(customer.getCustomerId()).append('\n');
        details.append("Customer Name  : ").append(customer.getName()).append('\n');
        details.append("Phone Number   : ").append(customer.getPhoneNumber()).append('\n');
        details.append("Address        : ").append(customer.getAddress()).append('\n');
        details.append("Account Type   : ").append(getAccountType()).append('\n');
        details.append(String.format(Locale.US, "Current Balance: ₹%,.2f%n", balance));
        details.append(String.format(Locale.US, "Annual Interest Estimate: ₹%,.2f%n",
                calculateInterest()));
        details.append(getAccountSpecificDetails()).append('\n');
        details.append("----------------------------------------");
        return details.toString();
    }
}
