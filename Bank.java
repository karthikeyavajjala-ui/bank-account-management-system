import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Business-logic layer for the application. Bank owns account and transaction collections;
 * BankGUI asks this class to perform operations instead of changing balances itself.
 */
public class Bank {
    private ArrayList<Account> accounts;
    private ArrayList<Transaction> transactions;
    private int accountSequence;
    private int customerSequence;
    private int transactionSequence;
    private final File dataFile;
    private String storageMessage;

    // CONSTRUCTOR: loads saved data if it exists; otherwise starts an empty bank.
    public Bank() {
        this("bank-data.dat");
    }

    // CONSTRUCTOR OVERLOADING: a file name can be supplied for tests or another demo folder.
    public Bank(String dataFileName) {
        if (dataFileName == null || dataFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("A data file name is required.");
        }
        this.accounts = new ArrayList<Account>();
        this.transactions = new ArrayList<Transaction>();
        this.accountSequence = 1000;
        this.customerSequence = 1000;
        this.transactionSequence = 0;
        this.dataFile = new File(dataFileName);
        this.storageMessage = "";
        loadData();
    }

    /** Creates a uniquely numbered SavingsAccount or CurrentAccount. */
    public Account createAccount(String holderName, String phoneNumber, String address,
                                String accountType, double initialDeposit)
            throws BankException {
        if (!Double.isFinite(initialDeposit) || initialDeposit < 0.0) {
            throw new InvalidAmountException("Initial deposit must be zero or a positive amount.");
        }
        if (accountType == null) {
            throw new BankException("Please select an account type.");
        }

        String normalizedType = accountType.trim().toLowerCase(Locale.ROOT);
        if (!normalizedType.equals("savings account")
                && !normalizedType.equals("current account")) {
            throw new BankException("Account type must be Savings Account or Current Account.");
        }

        int proposedCustomerSequence = customerSequence + 1;
        String customerId = String.format(Locale.ROOT, "CUST%04d", proposedCustomerSequence);
        // Customer validates the name, 10-digit phone number, and address.
        Customer customer = new Customer(customerId, holderName, phoneNumber, address);

        int proposedAccountSequence = accountSequence + 1;
        String accountNumber = formatAccountNumber(proposedAccountSequence);
        while (containsAccountNumber(accountNumber)) {
            proposedAccountSequence++;
            accountNumber = formatAccountNumber(proposedAccountSequence);
        }

        // POLYMORPHISM: the variable has the abstract Account type but refers to a child object.
        Account account;
        if (normalizedType.equals("savings account")) {
            account = new SavingsAccount(accountNumber, customer, initialDeposit);
        } else {
            account = new CurrentAccount(accountNumber, customer, initialDeposit);
        }

        if (containsAccountNumber(accountNumber)) {
            throw new DuplicateAccountException(accountNumber);
        }

        accountSequence = proposedAccountSequence;
        customerSequence = proposedCustomerSequence;
        accounts.add(account);
        addTransaction(accountNumber, "ACCOUNT CREATED", account.getBalance(),
                "New " + account.getAccountType());
        saveNow();
        return account;
    }

    /** Finds an account without exposing the modifiable ArrayList. */
    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new AccountNotFoundException(accountNumber);
        }
        String normalizedNumber = accountNumber.trim().toUpperCase(Locale.ROOT);
        if (!normalizedNumber.matches("ACC[0-9]{4,}")) {
            throw new AccountNotFoundException(normalizedNumber);
        }

        for (Account account : accounts) {
            if (account.getAccountNumber().equalsIgnoreCase(normalizedNumber)) {
                return account;
            }
        }
        throw new AccountNotFoundException(normalizedNumber);
    }

    /** Deposits money and records a transaction only after the deposit succeeds. */
    public Account deposit(String accountNumber, double amount) throws BankException {
        Account account = findAccount(accountNumber);
        account.deposit(amount);
        addTransaction(account.getAccountNumber(), "DEPOSIT", amount, "Cash deposit");
        saveNow();
        return account;
    }

    /**
     * POLYMORPHISM: Account.withdraw is dynamically dispatched here. For a CurrentAccount,
     * the overridden method applies its overdraft limit; SavingsAccount uses the base method.
     */
    public Account withdraw(String accountNumber, double amount) throws BankException {
        Account account = findAccount(accountNumber);
        account.withdraw(amount);
        addTransaction(account.getAccountNumber(), "WITHDRAWAL", amount, "Cash withdrawal");
        saveNow();
        return account;
    }

    /** Deletes an account after the GUI has asked the user to confirm. */
    public Account deleteAccount(String accountNumber) throws AccountNotFoundException {
        Account account = findAccount(accountNumber);
        accounts.remove(account);
        addTransaction(account.getAccountNumber(), "ACCOUNT DELETED", account.getBalance(),
                "Account closed; previous history retained");
        saveNow();
        return account;
    }

    /** Returns a read-only copy, so callers cannot add/remove bank accounts directly. */
    public List<Account> getAccounts() {
        return Collections.unmodifiableList(new ArrayList<Account>(accounts));
    }

    /** Returns the transaction history for one account number. */
    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        String normalizedNumber = accountNumber == null
                ? "" : accountNumber.trim().toUpperCase(Locale.ROOT);
        ArrayList<Transaction> matchingTransactions = new ArrayList<Transaction>();
        for (Transaction transaction : transactions) {
            if (transaction.getAccountNumber().equalsIgnoreCase(normalizedNumber)) {
                matchingTransactions.add(transaction);
            }
        }
        return Collections.unmodifiableList(matchingTransactions);
    }

    public int getAccountCount() {
        return accounts.size();
    }

    public int getTransactionCount() {
        return transactions.size();
    }

    public double getTotalBalance() {
        double total = 0.0;
        for (Account account : accounts) {
            total += account.checkBalance();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public String getStorageMessage() {
        return storageMessage;
    }

    public String getStorageFileName() {
        return dataFile.getPath();
    }

    /** Saves the current state; returns false and keeps a warning if disk writing fails. */
    public boolean saveNow() {
        File parent = dataFile.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        // try-with-resources closes the stream even if serialization throws an IOException.
        try (ObjectOutputStream output = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(dataFile)))) {
            output.writeObject(new SavedBankData(accounts, transactions,
                    accountSequence, customerSequence, transactionSequence));
            storageMessage = "";
            return true;
        } catch (IOException exception) {
            storageMessage = "Could not save " + dataFile.getPath() + ": " + exception.getMessage();
            return false;
        }
    }

    private void loadData() {
        if (!dataFile.exists()) {
            return;
        }

        try (ObjectInputStream input = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(dataFile)))) {
            Object savedObject = input.readObject();
            if (!(savedObject instanceof SavedBankData)) {
                throw new IOException("The file does not contain Bank Account Management data.");
            }

            SavedBankData savedData = (SavedBankData) savedObject;
            this.accounts = savedData.accounts == null
                    ? new ArrayList<Account>() : new ArrayList<Account>(savedData.accounts);
            this.transactions = savedData.transactions == null
                    ? new ArrayList<Transaction>() : new ArrayList<Transaction>(savedData.transactions);
            this.accountSequence = Math.max(1000, savedData.accountSequence);
            this.customerSequence = Math.max(1000, savedData.customerSequence);
            this.transactionSequence = Math.max(0, savedData.transactionSequence);
        } catch (IOException | ClassNotFoundException exception) {
            // EXCEPTION HANDLING: a bad/unreadable file does not stop the application from opening.
            accounts = new ArrayList<Account>();
            transactions = new ArrayList<Transaction>();
            accountSequence = 1000;
            customerSequence = 1000;
            transactionSequence = 0;
            storageMessage = "Saved data could not be loaded; the application started with an empty "
                    + "bank. Details: " + exception.getMessage();
        }
    }

    private void addTransaction(String accountNumber, String type,
                                double amount, String description) {
        transactionSequence++;
        String transactionId = String.format(Locale.ROOT, "TXN%04d", transactionSequence);
        transactions.add(new Transaction(transactionId, accountNumber, type, amount, description));
    }

    private boolean containsAccountNumber(String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                return true;
            }
        }
        return false;
    }

    private String formatAccountNumber(int sequence) {
        return String.format(Locale.ROOT, "ACC%04d", sequence);
    }

    /** Serializable snapshot written to the optional local data file. */
    private static final class SavedBankData implements Serializable {
        private static final long serialVersionUID = 1L;

        private final ArrayList<Account> accounts;
        private final ArrayList<Transaction> transactions;
        private final int accountSequence;
        private final int customerSequence;
        private final int transactionSequence;

        private SavedBankData(ArrayList<Account> accounts,
                              ArrayList<Transaction> transactions,
                              int accountSequence, int customerSequence,
                              int transactionSequence) {
            this.accounts = new ArrayList<Account>(accounts);
            this.transactions = new ArrayList<Transaction>(transactions);
            this.accountSequence = accountSequence;
            this.customerSequence = customerSequence;
            this.transactionSequence = transactionSequence;
        }
    }
}
