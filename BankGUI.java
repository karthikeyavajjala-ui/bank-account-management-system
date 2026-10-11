import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Locale;

/**
 * Main desktop interface. This class uses Java AWT components only (no Swing).
 * It handles display and user events; banking rules remain in Bank and Account classes.
 */
// AWT EVENT HANDLING: BankGUI registers as an ActionListener for its buttons.
public class BankGUI extends Frame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final Color NAVY = new Color(23, 39, 68);
    private static final Color SIDEBAR = new Color(31, 49, 81);
    private static final Color BUTTON_BLUE = new Color(39, 105, 181);
    private static final Color BUTTON_GREEN = new Color(31, 133, 91);
    private static final Color BUTTON_RED = new Color(178, 62, 62);
    private static final Color BACKGROUND = new Color(242, 246, 250);
    private static final Color TEXT = new Color(42, 53, 69);
    private static final Color MUTED = new Color(104, 117, 134);
    private static final Color WHITE = Color.WHITE;

    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    private static final Font PAGE_TITLE_FONT = new Font("SansSerif", Font.BOLD, 23);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 13);
    private static final Font MONO_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 13);

    private final Bank bank;
    private final CardLayout pageLayout;
    private final Panel pageCards;

    // Create-account form components.
    private final TextField createNameField = new TextField(28);
    private final TextField createPhoneField = new TextField(28);
    private final TextField createAddressField = new TextField(28);
    private final Choice createTypeChoice = new Choice();
    private final TextField createInitialDepositField = new TextField("0", 28);

    // Deposit and withdrawal form components.
    private final TextField depositAccountField = new TextField(28);
    private final TextField depositAmountField = new TextField(28);
    private final TextField withdrawAccountField = new TextField(28);
    private final TextField withdrawAmountField = new TextField(28);

    // Lookup and delete form components.
    private final TextField detailsAccountField = new TextField(28);
    private final TextField balanceAccountField = new TextField(28);
    private final TextField historyAccountField = new TextField(28);
    private final TextField deleteAccountField = new TextField(28);

    private final TextArea detailsOutput = createOutputArea();
    private final TextArea balanceOutput = createOutputArea();
    private final TextArea historyOutput = createOutputArea();
    private final TextArea allAccountsOutput = createOutputArea();
    private final TextArea deleteOutput = createOutputArea();

    private final Label accountCountValue = new Label("0", Label.CENTER);
    private final Label totalBalanceValue = new Label("₹0.00", Label.CENTER);
    private final Label transactionCountValue = new Label("0", Label.CENTER);
    private final Label storageInfoLabel = new Label("", Label.CENTER);

    // CONSTRUCTOR: creates the AWT frame and all screens in the CardLayout.
    public BankGUI() {
        super("Bank Account Management System");
        bank = new Bank();
        pageLayout = new CardLayout();
        pageCards = new Panel(pageLayout);
        pageCards.setBackground(BACKGROUND);

        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildNavigation(), BorderLayout.WEST);

        pageCards.add(buildDashboardPage(), "DASHBOARD");
        pageCards.add(buildCreateAccountPage(), "CREATE");
        pageCards.add(buildAccountDetailsPage(), "DETAILS");
        pageCards.add(buildDepositPage(), "DEPOSIT");
        pageCards.add(buildWithdrawPage(), "WITHDRAW");
        pageCards.add(buildBalancePage(), "BALANCE");
        pageCards.add(buildTransactionHistoryPage(), "TRANSACTIONS");
        pageCards.add(buildAllAccountsPage(), "ALL");
        pageCards.add(buildDeleteAccountPage(), "DELETE");
        add(pageCards, BorderLayout.CENTER);

        setSize(1160, 760);
        setMinimumSize(new Dimension(980, 650));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                handleExit();
            }
        });

        refreshDashboard();
        if (!bank.getStorageMessage().isEmpty()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    showMessage("Saved data warning", bank.getStorageMessage(), true);
                }
            });
        }
    }

    private Panel buildHeader() {
        Panel header = new Panel(new BorderLayout(18, 0));
        header.setBackground(NAVY);
        header.setPreferredSize(new Dimension(900, 82));

        Panel titlePanel = new Panel(new GridLayout(2, 1, 0, 4));
        titlePanel.setBackground(NAVY);
        Label title = new Label("BANK ACCOUNT MANAGEMENT SYSTEM");
        title.setFont(TITLE_FONT);
        title.setForeground(WHITE);
        Label subtitle = new Label("An OOP-Based Java AWT Application");
        subtitle.setFont(SUBTITLE_FONT);
        subtitle.setForeground(new Color(204, 216, 231));
        titlePanel.add(title);
        titlePanel.add(subtitle);
        header.add(titlePanel, BorderLayout.CENTER);

        Label tag = new Label("JAVA AWT  |  LOCAL DEMO", Label.RIGHT);
        tag.setFont(new Font("SansSerif", Font.BOLD, 12));
        tag.setForeground(new Color(204, 216, 231));
        header.add(tag, BorderLayout.EAST);
        return header;
    }

    private Panel buildNavigation() {
        Panel navigation = new Panel(new BorderLayout(0, 14));
        navigation.setBackground(SIDEBAR);
        navigation.setPreferredSize(new Dimension(218, 600));

        Panel navTitlePanel = new Panel(new GridLayout(2, 1, 0, 4));
        navTitlePanel.setBackground(SIDEBAR);
        Label navTitle = new Label("  BANKING MENU");
        navTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        navTitle.setForeground(new Color(179, 196, 218));
        Label navHint = new Label("  Choose an operation");
        navHint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        navHint.setForeground(new Color(179, 196, 218));
        navTitlePanel.add(navTitle);
        navTitlePanel.add(navHint);
        navigation.add(navTitlePanel, BorderLayout.NORTH);

        Panel navButtons = new Panel(new GridLayout(0, 1, 0, 6));
        navButtons.setBackground(SIDEBAR);
        addNavigationButton(navButtons, "Dashboard", "DASHBOARD");
        addNavigationButton(navButtons, "Create Account", "CREATE");
        addNavigationButton(navButtons, "Account Details", "DETAILS");
        addNavigationButton(navButtons, "Deposit Money", "DEPOSIT");
        addNavigationButton(navButtons, "Withdraw Money", "WITHDRAW");
        addNavigationButton(navButtons, "Check Balance", "BALANCE");
        addNavigationButton(navButtons, "Transaction History", "TRANSACTIONS");
        addNavigationButton(navButtons, "All Accounts", "ALL");
        addNavigationButton(navButtons, "Delete Account", "DELETE");
        Button exitButton = new Button("Exit Application");
        exitButton.setActionCommand("EXIT");
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        exitButton.setBackground(new Color(112, 55, 61));
        exitButton.setForeground(WHITE);
        exitButton.addActionListener(this);
        navButtons.add(exitButton);
        navigation.add(navButtons, BorderLayout.CENTER);

        Panel navFooter = new Panel(new GridLayout(2, 1, 0, 4));
        navFooter.setBackground(SIDEBAR);
        Label footer1 = new Label("  OOP MINI PROJECT");
        footer1.setFont(new Font("SansSerif", Font.BOLD, 11));
        footer1.setForeground(new Color(179, 196, 218));
        Label footer2 = new Label("  Data auto-saves locally");
        footer2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        footer2.setForeground(new Color(179, 196, 218));
        navFooter.add(footer1);
        navFooter.add(footer2);
        navigation.add(navFooter, BorderLayout.SOUTH);
        return navigation;
    }

    private void addNavigationButton(Panel parent, String text, String pageName) {
        Button button = new Button(text);
        button.setActionCommand("PAGE:" + pageName);
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setBackground(new Color(43, 63, 98));
        button.setForeground(WHITE);
        button.addActionListener(this);
        parent.add(button);
    }

    private Panel buildDashboardPage() {
        Panel page = createPage("Dashboard", "A simple control centre for account and transaction management.");
        Panel content = new Panel(new BorderLayout(0, 18));
        content.setBackground(BACKGROUND);

        Panel statistics = new Panel(new GridLayout(1, 3, 14, 0));
        statistics.setBackground(BACKGROUND);
        statistics.add(createStatisticCard("TOTAL ACCOUNTS", accountCountValue));
        statistics.add(createStatisticCard("TOTAL ACCOUNT BALANCE", totalBalanceValue));
        statistics.add(createStatisticCard("TRANSACTIONS", transactionCountValue));
        content.add(statistics, BorderLayout.NORTH);

        Panel middle = new Panel(new GridLayout(1, 2, 14, 0));
        middle.setBackground(BACKGROUND);
        Panel welcomeCard = createWhiteCard();
        welcomeCard.setLayout(new BorderLayout(8, 8));
        Label welcomeTitle = new Label("Welcome to the Bank Console");
        welcomeTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        welcomeTitle.setForeground(NAVY);
        Label welcomeText = new Label("Create accounts, process transactions, and review account records.");
        welcomeText.setFont(BODY_FONT);
        welcomeText.setForeground(TEXT);
        welcomeCard.add(welcomeTitle, BorderLayout.NORTH);
        welcomeCard.add(welcomeText, BorderLayout.CENTER);

        Panel tipCard = createWhiteCard();
        tipCard.setLayout(new GridLayout(3, 1, 0, 7));
        Label tipTitle = new Label("Demo notes");
        tipTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        tipTitle.setForeground(NAVY);
        Label tip1 = new Label("Savings: 4% annual interest estimate (not credited automatically).");
        tip1.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tip1.setForeground(TEXT);
        Label tip2 = new Label("Current: withdrawals may use the ₹10,000 overdraft limit.");
        tip2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tip2.setForeground(TEXT);
        tipCard.add(tipTitle);
        tipCard.add(tip1);
        tipCard.add(tip2);
        middle.add(welcomeCard);
        middle.add(tipCard);
        content.add(middle, BorderLayout.CENTER);

        Panel quickActions = createWhiteCard();
        quickActions.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        Label quickLabel = new Label("Quick actions:");
        quickLabel.setFont(LABEL_FONT);
        quickLabel.setForeground(TEXT);
        quickActions.add(quickLabel);
        quickActions.add(createActionButton("Create Account", "PAGE:CREATE", BUTTON_BLUE));
        quickActions.add(createActionButton("Deposit", "PAGE:DEPOSIT", BUTTON_GREEN));
        quickActions.add(createActionButton("All Accounts", "PAGE:ALL", BUTTON_BLUE));
        content.add(quickActions, BorderLayout.SOUTH);

        Panel pageBody = new Panel(new BorderLayout(0, 12));
        pageBody.setBackground(BACKGROUND);
        pageBody.add(content, BorderLayout.CENTER);
        storageInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        pageBody.add(storageInfoLabel, BorderLayout.SOUTH);
        page.add(pageBody, BorderLayout.CENTER);
        return page;
    }

    private Panel createStatisticCard(String heading, Label value) {
        Panel card = createWhiteCard();
        card.setLayout(new GridLayout(2, 1, 0, 7));
        Label title = new Label(heading, Label.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 11));
        title.setForeground(MUTED);
        value.setFont(new Font("SansSerif", Font.BOLD, 22));
        value.setForeground(BUTTON_BLUE);
        card.add(title);
        card.add(value);
        return card;
    }

    private Panel buildCreateAccountPage() {
        Panel page = createPage("Create Account", "Enter customer details and choose an account type.");
        Panel card = createWhiteCard();
        Panel form = createFormGrid();
        createTypeChoice.removeAll();
        createTypeChoice.add("Savings Account");
        createTypeChoice.add("Current Account");
        addFormRow(form, "Account holder name", createNameField);
        addFormRow(form, "Phone number (10 digits)", createPhoneField);
        addFormRow(form, "Address", createAddressField);
        addFormRow(form, "Account type", createTypeChoice);
        addFormRow(form, "Initial deposit (₹; 0 allowed)", createInitialDepositField);

        Panel actionRow = createActionRow();
        actionRow.add(createActionButton("Create Account", "CREATE_ACCOUNT", BUTTON_GREEN));
        Panel cardBody = new Panel(new BorderLayout(0, 14));
        cardBody.setBackground(WHITE);
        // Keep input rows at their natural height instead of stretching TextFields vertically.
        cardBody.add(form, BorderLayout.NORTH);
        cardBody.add(actionRow, BorderLayout.CENTER);
        card.add(cardBody, BorderLayout.NORTH);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    private Panel buildDepositPage() {
        Panel page = createPage("Deposit Money", "Deposit a positive amount into an existing account.");
        Panel form = createFormGrid();
        addFormRow(form, "Account number", depositAccountField);
        addFormRow(form, "Deposit amount (₹)", depositAmountField);
        Panel actionRow = createActionRow();
        actionRow.add(createActionButton("Deposit", "DEPOSIT_MONEY", BUTTON_GREEN));
        page.add(createFormCard(form, actionRow), BorderLayout.CENTER);
        return page;
    }

    private Panel buildWithdrawPage() {
        Panel page = createPage("Withdraw Money", "Savings accounts use their balance; current accounts also use the overdraft limit.");
        Panel form = createFormGrid();
        addFormRow(form, "Account number", withdrawAccountField);
        addFormRow(form, "Withdrawal amount (₹)", withdrawAmountField);
        Panel actionRow = createActionRow();
        actionRow.add(createActionButton("Withdraw", "WITHDRAW_MONEY", BUTTON_BLUE));
        page.add(createFormCard(form, actionRow), BorderLayout.CENTER);
        return page;
    }

    private Panel buildAccountDetailsPage() {
        return buildLookupPage("Account Details", "View the customer's contact details and account-specific information.",
                "Account number", detailsAccountField, "Show Account Details", "SHOW_DETAILS",
                detailsOutput, "Enter an account number and choose Show Account Details.");
    }

    private Panel buildBalancePage() {
        return buildLookupPage("Check Balance", "Look up an account and its current balance.",
                "Account number", balanceAccountField, "Check Balance", "CHECK_BALANCE",
                balanceOutput, "Enter an account number and choose Check Balance.");
    }

    private Panel buildTransactionHistoryPage() {
        return buildLookupPage("Transaction History", "Review account creation, deposits, withdrawals, and account closure.",
                "Account number", historyAccountField, "Show History", "SHOW_HISTORY",
                historyOutput, "Enter an account number and choose Show History. Deleted-account history is retained.");
    }

    private Panel buildLookupPage(String title, String subtitle, String accountFieldLabel,
                                  TextField accountField, String buttonText, String command,
                                  TextArea output, String initialMessage) {
        Panel page = createPage(title, subtitle);
        Panel card = createWhiteCard();
        Panel form = createFormGrid();
        addFormRow(form, accountFieldLabel, accountField);

        Panel inputAndButton = new Panel(new BorderLayout(0, 8));
        inputAndButton.setBackground(WHITE);
        inputAndButton.add(form, BorderLayout.CENTER);
        Panel actionRow = createActionRow();
        actionRow.add(createActionButton(buttonText, command, BUTTON_BLUE));
        inputAndButton.add(actionRow, BorderLayout.SOUTH);
        card.add(inputAndButton, BorderLayout.NORTH);

        configureOutputArea(output);
        output.setText(initialMessage);
        card.add(output, BorderLayout.CENTER);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    private Panel buildAllAccountsPage() {
        Panel page = createPage("All Accounts", "A live list of the accounts currently held by the bank.");
        Panel card = createWhiteCard();
        configureOutputArea(allAccountsOutput);
        card.add(allAccountsOutput, BorderLayout.CENTER);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    private Panel buildDeleteAccountPage() {
        Panel page = createPage("Delete Account", "Account deletion requires confirmation. Previous transaction history is retained.");
        Panel card = createWhiteCard();
        Panel form = createFormGrid();
        addFormRow(form, "Account number", deleteAccountField);
        Panel actionRow = createActionRow();
        actionRow.add(createActionButton("Delete Account", "DELETE_ACCOUNT", BUTTON_RED));

        Panel top = new Panel(new BorderLayout(0, 8));
        top.setBackground(WHITE);
        top.add(form, BorderLayout.CENTER);
        top.add(actionRow, BorderLayout.SOUTH);
        card.add(top, BorderLayout.NORTH);
        configureOutputArea(deleteOutput);
        deleteOutput.setText("Deleted-account confirmation will appear here.");
        card.add(deleteOutput, BorderLayout.CENTER);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    private Panel createPage(String title, String subtitle) {
        Panel page = new Panel(new BorderLayout(0, 16));
        page.setBackground(BACKGROUND);
        Panel heading = new Panel(new GridLayout(2, 1, 0, 5));
        heading.setBackground(BACKGROUND);
        Label titleLabel = new Label(title);
        titleLabel.setFont(PAGE_TITLE_FONT);
        titleLabel.setForeground(NAVY);
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(MUTED);
        heading.add(titleLabel);
        heading.add(subtitleLabel);
        page.add(heading, BorderLayout.NORTH);
        return page;
    }

    private Panel createWhiteCard() {
        Panel card = new Panel(new BorderLayout(12, 12));
        card.setBackground(WHITE);
        return card;
    }

    private Panel createFormGrid() {
        Panel form = new Panel(new GridLayout(0, 1, 0, 12));
        form.setBackground(WHITE);
        return form;
    }

    private void addFormRow(Panel form, String labelText, Component input) {
        Panel row = new Panel(new BorderLayout(12, 0));
        row.setBackground(WHITE);
        Label label = new Label(labelText, Label.LEFT);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT);
        label.setPreferredSize(new Dimension(205, 36));
        input.setFont(BODY_FONT);
        input.setPreferredSize(new Dimension(360, 36));
        if (input instanceof TextField) {
            input.setBackground(new Color(250, 252, 254));
        }
        row.add(label, BorderLayout.WEST);
        row.add(input, BorderLayout.CENTER);
        form.add(row);
    }

    private Panel createActionRow() {
        Panel row = new Panel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        row.setBackground(WHITE);
        return row;
    }

    private Panel createFormCard(Panel form, Panel actionRow) {
        Panel card = createWhiteCard();
        Panel body = new Panel(new BorderLayout(0, 14));
        body.setBackground(WHITE);
        // Natural-sized rows keep AWT TextFields compact in the larger page card.
        body.add(form, BorderLayout.NORTH);
        body.add(actionRow, BorderLayout.CENTER);
        card.add(body, BorderLayout.NORTH);
        return card;
    }

    private Button createActionButton(String text, String command, Color color) {
        Button button = new Button(text);
        button.setActionCommand(command);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(WHITE);
        button.addActionListener(this);
        return button;
    }

    private static TextArea createOutputArea() {
        TextArea output = new TextArea("", 14, 68, TextArea.SCROLLBARS_VERTICAL_ONLY);
        output.setEditable(false);
        output.setFont(MONO_FONT);
        output.setBackground(WHITE);
        output.setForeground(TEXT);
        return output;
    }

    private void configureOutputArea(TextArea output) {
        output.setEditable(false);
        output.setFont(MONO_FONT);
        output.setBackground(new Color(250, 252, 254));
        output.setForeground(TEXT);
    }

    // AWT EVENT HANDLING: every navigation and operation button is routed through this listener.
    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.startsWith("PAGE:")) {
            showPage(command.substring("PAGE:".length()));
            return;
        }
        if ("EXIT".equals(command)) {
            handleExit();
            return;
        }

        try {
            switch (command) {
                case "CREATE_ACCOUNT":
                    handleCreateAccount();
                    break;
                case "DEPOSIT_MONEY":
                    handleDeposit();
                    break;
                case "WITHDRAW_MONEY":
                    handleWithdraw();
                    break;
                case "SHOW_DETAILS":
                    handleAccountDetails();
                    break;
                case "CHECK_BALANCE":
                    handleBalanceCheck();
                    break;
                case "SHOW_HISTORY":
                    handleTransactionHistory();
                    break;
                case "DELETE_ACCOUNT":
                    handleDeleteAccount();
                    break;
                default:
                    showMessage("Unknown action", "This button action is not configured.", true);
                    break;
            }
        } catch (BankException exception) {
            showMessage("Banking operation could not be completed", exception.getMessage(), true);
        } catch (IllegalArgumentException exception) {
            showMessage("Please check the entered details", exception.getMessage(), true);
        } catch (Exception exception) {
            exception.printStackTrace();
            showMessage("Unexpected error", "The operation could not be completed.\n"
                    + exception.getMessage(), true);
        } finally {
            // EXCEPTION HANDLING: refresh the summary whether an operation succeeds or fails.
            refreshDashboard();
        }
    }

    private void handleCreateAccount() throws BankException {
        String name = requireText(createNameField.getText(), "Account holder name");
        String phone = requireText(createPhoneField.getText(), "Phone number");
        String address = requireText(createAddressField.getText(), "Address");
        double initialDeposit = parseAmount(createInitialDepositField, "Initial deposit", true);

        Account account = bank.createAccount(name, phone, address,
                createTypeChoice.getSelectedItem(), initialDeposit);
        String message = "Account created successfully!\n\n"
                + "Account Number: " + account.getAccountNumber() + "\n"
                + "Customer ID: " + account.getCustomerId() + "\n"
                + "Account Holder: " + account.getAccountHolderName() + "\n"
                + "Account Type: " + account.getAccountType() + "\n"
                + "Initial Balance: " + money(account.getBalance());
        showSuccess("Account Created", message);
        createNameField.setText("");
        createPhoneField.setText("");
        createAddressField.setText("");
        createInitialDepositField.setText("0");
    }

    private void handleDeposit() throws BankException {
        String accountNumber = requireText(depositAccountField.getText(), "Account number");
        double amount = parseAmount(depositAmountField, "Deposit amount", false);
        Account account = bank.deposit(accountNumber, amount);
        showSuccess("Deposit Successful", "Amount deposited: " + money(amount)
                + "\nAccount: " + account.getAccountNumber()
                + "\nNew balance: " + money(account.getBalance()));
        depositAmountField.setText("");
    }

    private void handleWithdraw() throws BankException {
        String accountNumber = requireText(withdrawAccountField.getText(), "Account number");
        double amount = parseAmount(withdrawAmountField, "Withdrawal amount", false);
        Account account = bank.withdraw(accountNumber, amount);
        showSuccess("Withdrawal Successful", "Amount withdrawn: " + money(amount)
                + "\nAccount: " + account.getAccountNumber()
                + "\nNew balance: " + money(account.getBalance()));
        withdrawAmountField.setText("");
    }

    private void handleAccountDetails() throws AccountNotFoundException {
        String accountNumber = requireText(detailsAccountField.getText(), "Account number");
        Account account = bank.findAccount(accountNumber);
        detailsOutput.setText(account.displayAccountDetails());
    }

    private void handleBalanceCheck() throws AccountNotFoundException {
        String accountNumber = requireText(balanceAccountField.getText(), "Account number");
        Account account = bank.findAccount(accountNumber);
        StringBuilder text = new StringBuilder();
        text.append("ACCOUNT BALANCE\n");
        text.append("----------------------------------------\n");
        text.append("Account Number : ").append(account.getAccountNumber()).append('\n');
        text.append("Account Holder : ").append(account.getAccountHolderName()).append('\n');
        text.append("Account Type   : ").append(account.getAccountType()).append('\n');
        text.append("Current Balance: ").append(money(account.checkBalance())).append('\n');
        if (account instanceof CurrentAccount) {
            CurrentAccount current = (CurrentAccount) account;
            text.append("Available to withdraw (incl. overdraft): ")
                    .append(money(current.getAvailableWithdrawalAmount())).append('\n');
        }
        text.append("----------------------------------------");
        balanceOutput.setText(text.toString());
    }

    private void handleTransactionHistory() throws AccountNotFoundException {
        String accountNumber = requireText(historyAccountField.getText(), "Account number");
        List<Transaction> records = bank.getTransactionsForAccount(accountNumber);
        String normalizedNumber = accountNumber.trim().toUpperCase(Locale.ROOT);
        String accountHolder = "Deleted account (history retained)";
        try {
            Account account = bank.findAccount(normalizedNumber);
            accountHolder = account.getAccountHolderName();
        } catch (AccountNotFoundException exception) {
            // A deleted account is still valid for history lookup if it has saved transactions.
            if (records.isEmpty()) {
                throw exception;
            }
        }

        StringBuilder text = new StringBuilder();
        text.append("TRANSACTION HISTORY\n");
        text.append("Account: ").append(normalizedNumber).append("  |  ")
                .append(accountHolder).append('\n');
        text.append("============================================================\n");
        if (records.isEmpty()) {
            text.append("No transactions have been recorded for this account.");
        } else {
            int rowNumber = 1;
            for (Transaction transaction : records) {
                text.append(rowNumber++).append(". ").append(transaction.toString()).append('\n');
            }
        }
        historyOutput.setText(text.toString());
    }

    private void handleDeleteAccount() throws AccountNotFoundException {
        String accountNumber = requireText(deleteAccountField.getText(), "Account number");
        Account account = bank.findAccount(accountNumber);
        boolean confirmed = confirm("Delete account " + account.getAccountNumber() + "?\n"
                + "This removes it from the active account list. Its transaction history is retained.");
        if (!confirmed) {
            return;
        }

        Account deletedAccount = bank.deleteAccount(account.getAccountNumber());
        String message = "Account deleted successfully.\n"
                + "Account Number: " + deletedAccount.getAccountNumber() + "\n"
                + "Account Holder: " + deletedAccount.getAccountHolderName() + "\n"
                + "Final Balance: " + money(deletedAccount.getBalance());
        deleteOutput.setText(message);
        showSuccess("Account Deleted", message);
        deleteAccountField.setText("");
    }

    private void showPage(String pageName) {
        pageLayout.show(pageCards, pageName);
        if ("DASHBOARD".equals(pageName)) {
            refreshDashboard();
        } else if ("ALL".equals(pageName)) {
            refreshAllAccounts();
        }
    }

    private void refreshDashboard() {
        accountCountValue.setText(String.valueOf(bank.getAccountCount()));
        totalBalanceValue.setText(money(bank.getTotalBalance()));
        transactionCountValue.setText(String.valueOf(bank.getTransactionCount()));
        if (bank.getStorageMessage().isEmpty()) {
            storageInfoLabel.setForeground(MUTED);
            storageInfoLabel.setText("Local file storage: " + bank.getStorageFileName()
                    + "  |  changes are saved automatically");
        } else {
            storageInfoLabel.setForeground(BUTTON_RED);
            storageInfoLabel.setText("Storage warning: " + bank.getStorageMessage());
        }
    }

    private void refreshAllAccounts() {
        List<Account> accounts = bank.getAccounts();
        if (accounts.isEmpty()) {
            allAccountsOutput.setText("No accounts have been created yet. Use Create Account to add one.");
            return;
        }

        StringBuilder text = new StringBuilder();
        text.append(String.format(Locale.US, "%-12s %-23s %-19s %15s%n",
                "ACCOUNT NO.", "ACCOUNT HOLDER", "ACCOUNT TYPE", "BALANCE"));
        text.append("--------------------------------------------------------------------------\n");
        for (Account account : accounts) {
            text.append(String.format(Locale.US, "%-12s %-23s %-19s %15s%n",
                    fitColumn(account.getAccountNumber(), 12),
                    fitColumn(account.getAccountHolderName(), 23),
                    fitColumn(account.getAccountType(), 19),
                    money(account.getBalance())));
        }
        allAccountsOutput.setText(text.toString());
    }

    private String fitColumn(String value, int width) {
        if (value.length() <= width) {
            return value;
        }
        return value.substring(0, Math.max(0, width - 3)) + "...";
    }

    private String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " cannot be empty.");
        }
        return value.trim();
    }

    private double parseAmount(TextField field, String label, boolean zeroAllowed)
            throws InvalidAmountException {
        String rawAmount = field.getText().trim().replace(",", "");
        if (rawAmount.isEmpty()) {
            throw new InvalidAmountException(label + " is required.");
        }
        final double amount;
        try {
            amount = Double.parseDouble(rawAmount);
        } catch (NumberFormatException exception) {
            throw new InvalidAmountException(label + " must be a valid number, such as 500 or 1250.50.");
        }
        if (!Double.isFinite(amount) || (zeroAllowed ? amount < 0.0 : amount <= 0.0)) {
            throw new InvalidAmountException(zeroAllowed
                    ? label + " cannot be negative. Enter 0 or a positive amount."
                    : label + " must be greater than ₹0.00.");
        }
        return amount;
    }

    private String money(double amount) {
        return String.format(Locale.US, "₹%,.2f", amount);
    }

    private void showSuccess(String title, String message) {
        String warning = bank.getStorageMessage();
        if (!warning.isEmpty()) {
            message += "\n\nStorage warning: " + warning;
        }
        showMessage(title, message, false);
    }

    /** AWT modal dialog used for all success and validation messages. */
    private void showMessage(String title, String message, boolean isError) {
        final Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(BACKGROUND);

        TextArea text = new TextArea(message == null ? "" : message, 6, 52,
                TextArea.SCROLLBARS_VERTICAL_ONLY);
        text.setEditable(false);
        text.setFont(BODY_FONT);
        text.setForeground(isError ? new Color(145, 41, 41) : TEXT);
        text.setBackground(WHITE);
        dialog.add(text, BorderLayout.CENTER);

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
        Button okButton = new Button("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dialog.dispose();
            }
        });
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                dialog.dispose();
            }
        });

        int lineCount = message == null ? 3 : message.split("\n", -1).length;
        dialog.setSize(540, Math.min(430, 150 + lineCount * 24));
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /** AWT confirmation dialog; closing the dialog is treated as No. */
    private boolean confirm(String message) {
        final boolean[] confirmed = new boolean[] { false };
        final Dialog dialog = new Dialog(this, "Please Confirm", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(BACKGROUND);

        TextArea text = new TextArea(message, 4, 48, TextArea.SCROLLBARS_VERTICAL_ONLY);
        text.setEditable(false);
        text.setFont(BODY_FONT);
        text.setBackground(WHITE);
        dialog.add(text, BorderLayout.CENTER);

        Panel buttons = new Panel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        Button yesButton = new Button("Yes, Delete");
        Button noButton = new Button("Cancel");
        yesButton.setBackground(BUTTON_RED);
        yesButton.setForeground(WHITE);
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                confirmed[0] = true;
                dialog.dispose();
            }
        });
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dialog.dispose();
            }
        });
        buttons.add(yesButton);
        buttons.add(noButton);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                dialog.dispose();
            }
        });
        dialog.setSize(520, 220);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return confirmed[0];
    }

    private void handleExit() {
        if (!confirm("Exit the Bank Account Management System?\nAll successful changes have been saved locally.")) {
            return;
        }
        boolean saved = bank.saveNow();
        if (!saved) {
            showMessage("Could not save data", bank.getStorageMessage()
                    + "\nThe application will now close.", true);
        }
        dispose();
        System.exit(0);
    }
}
