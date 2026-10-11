# Mini Project Report

## 1. Title

**Bank Account Management System**  
*An OOP-Based Java AWT Application*

## 2. Introduction

The Bank Account Management System is a desktop application for creating and maintaining a small set of bank accounts. It supports Savings and Current Accounts, deposits, withdrawals, balance enquiries, account details, account listing, transaction history, and account deletion. The graphical interface is written with Java AWT and communicates with a separate `Bank` business-logic class.

The project is intentionally sized for a first- or second-year OOP course. It uses standard Java only and makes its OOP concepts visible in the class structure and comments.

## 3. Problem Statement

Keeping account details and transaction records manually makes it easy to lose information or calculate balances incorrectly. This project provides a simple desktop interface that validates user input, centralizes account rules, records transactions, and saves the demo data locally between runs.

## 4. Objectives

- Create and manage Savings and Current Account objects.
- Apply deposits and withdrawals through validated methods.
- Demonstrate encapsulation, inheritance, abstraction, and runtime polymorphism.
- Use custom checked exceptions to report banking errors without crashing the GUI.
- Use `ArrayList` collections for accounts and transactions.
- Build an event-driven interface using Java AWT.
- Persist demonstration data with standard Java file/object streams.

## 5. Technologies Used

| Technology | Use |
|---|---|
| Java (JDK 8+) | Classes, inheritance, exceptions, collections, date/time, serialization |
| Java AWT | Desktop windows, forms, buttons, text areas, dialogs, layout managers |
| AWT event handling | `ActionListener`, `ActionEvent`, `WindowAdapter` |
| Java Collections | `ArrayList<Account>` and `ArrayList<Transaction>` |
| Standard file I/O | `ObjectInputStream` and `ObjectOutputStream` for `bank-data.dat` |

No Swing, JavaFX, external libraries, database, or web framework is used.

## 6. OOP Concepts Used

| OOP concept | Exact implementation in this project |
|---|---|
| Class | `Account`, `SavingsAccount`, `CurrentAccount`, `Customer`, `Transaction`, `Bank`, and `BankGUI` define object types. |
| Object | `Bank.createAccount()` creates a `Customer` and a Savings or Current Account object. Each successful operation works with those objects. |
| Constructor | Parameterized constructors initialize customers, accounts, and transactions. `Customer` also has a no-argument constructor. Savings and Current Accounts have overloaded constructors with default and configurable rates/limits. |
| Encapsulation | `Account.balance`, account number, customer reference, and all `Customer` fields are private. Balance changes only through `deposit()` and `withdraw()`. Getters expose read-only values. |
| Inheritance | `SavingsAccount extends Account` and `CurrentAccount extends Account`. |
| Abstraction | `Account` is abstract. It declares `getAccountType()`, `calculateInterest()`, and `getAccountSpecificDetails()` for each concrete account type to implement. |
| Polymorphism | `Bank` stores and returns accounts using the `Account` type. `Bank.withdraw()` calls `account.withdraw(amount)`; Java dynamically invokes `CurrentAccount.withdraw()` for a current account and the inherited method for a savings account. `displayAccountDetails()` also calls overridden account methods. |
| Method overriding | `CurrentAccount` overrides `withdraw()`. Both subclasses override account type, interest, and account-specific details. |
| Method overloading | `Customer()` and `Customer(id, name, phone, address)` are overloaded constructors. Savings/Current constructors also have overloads. |
| Exception handling | Business methods declare custom checked exceptions. GUI event handling uses `try`, `catch`, and `finally`; `Bank` uses try-with-resources and catches file errors. |
| Collections and loops | `Bank` stores data in `ArrayList`s; `for` loops find accounts, total balances, list accounts, and filter transactions. |
| Conditional logic | Account type selection, input validation, overdraft checking, and confirmation decisions use `if`/`else` and `switch`. |
| File handling | `Bank` saves/loads a serializable snapshot in `bank-data.dat`. |
| Event handling | `BankGUI implements ActionListener`; button clicks are routed through `actionPerformed(ActionEvent)`. A `WindowAdapter` handles the close control. |

### Encapsulation in plain words

The balance is private, so code outside `Account` cannot write `account.balance = ...`. It must call a controlled operation such as `deposit()` or `withdraw()`. Those methods validate the amount and enforce the account's balance/overdraft rules before changing the balance. `Bank.getAccounts()` also returns a read-only copy rather than exposing its modifiable collection.

## 7. System Features

- Dashboard with account count, total account balance, and transaction count.
- Create Savings or Current Accounts with automatically generated account/customer IDs.
- Input validation for blank fields, 10-digit phone number, and invalid amounts.
- Deposit and withdrawal with custom error dialogs.
- Current Account overdraft support (default ₹10,000).
- Savings Account annual interest estimate (default 4%; display only, not credited).
- Balance check and full account details.
- All-accounts list and per-account transaction history.
- Confirmation dialog before deleting an account.
- Account-created, deposit, withdrawal, and account-deleted transaction records.
- Automatic local persistence using `bank-data.dat`.
- Friendly AWT dialogs for success, invalid input, missing accounts, and insufficient funds.

## 8. System Architecture

```text
┌─────────────────────────────────────────┐
│              BankGUI (AWT)              │
│ forms, dashboard, dialogs,ActionListener│
└────────────────────┬────────────────────┘
                     │ calls business methods
                     ▼
┌─────────────────────────────────────────┐
│                  Bank                   │
│ account lookup, create/delete, deposits,│
│ withdrawals, collections, persistence   │
└──────────────┬─────────────────┬────────┘
               │                 │
               ▼                 ▼
┌────────────────────────┐  ┌─────────────────────┐
│ Account (abstract)     │  │ Transaction         │
│ balance and common     │  │ immutable history   │
│ account operations     │  └─────────────────────┘
└────────────┬───────────┘
             │ extends
       ┌─────┴──────────┐
       ▼                ▼
┌──────────────┐  ┌───────────────┐
│SavingsAccount│  │CurrentAccount │
└──────────────┘  └───────────────┘
             │ has a customer
             ▼
       ┌──────────┐
       │ Customer │
       └──────────┘

Bank ── saves/loads ──> bank-data.dat
```

The GUI does not implement balance rules. It collects text-field values, asks `Bank` to perform an operation, and displays the result or exception message.

## 9. Class Diagram

```text
                         ┌───────────────────────┐
                         │         Bank          │
                         │ - accounts: ArrayList │
                         │ - transactions: List  │
                         │ + createAccount()     │
                         │ + findAccount()       │
                         │ + deposit()/withdraw()│
                         └───────┬────────┬──────┘
                                 │        │
                  manages 0..*   │        │ manages 0..*
                                 ▼        ▼
                     ┌────────────────┐  ┌────────────────┐
                     │ Account        │  │ Transaction    │
                     │ <<abstract>>   │  └────────────────┘
                     │ - accountNumber│
                     │ - balance      │
                     │ - customer     │
                     │ + deposit()    │
                     │ + withdraw()   │
                     │ + calculate... │
                     └───┬────────┬───┘
                         │        │
               extends   │        │   extends
              ┌──────────┘        └───────────┐
              ▼                               ▼
    ┌──────────────────┐             ┌──────────────────┐
    │ SavingsAccount   │             │ CurrentAccount   │
    │ - interestRate   │             │ - overdraftLimit │
    │ + calculate...   │             │ + withdraw()     │
    └──────────────────┘             └──────────────────┘
                         Account has one Customer
                                  │
                                  ▼
                          ┌──────────────┐
                          │ Customer     │
                          │ - customerId │
                          │ - name       │
                          │ - phone      │
                          │ - address    │
                          └──────────────┘

    BankGUI ──uses──> Bank
```

## 10. Exception Handling

| Exception | When it is used |
|---|---|
| `BankException` | Checked base exception for expected banking errors. |
| `AccountNotFoundException` | A blank, malformed, or unknown account number is requested. |
| `InsufficientBalanceException` | A withdrawal is greater than available funds (including a current account's remaining overdraft). The message includes available and requested amounts. |
| `InvalidAmountException` | A deposit/withdrawal is zero, negative, not numeric, or otherwise invalid. An initial deposit may be zero but cannot be negative. |
| `DuplicateAccountException` | Defensive check in case a generated account number collides with an existing one. |

`BankGUI.actionPerformed()` catches expected banking/validation exceptions and displays an AWT dialog. A `finally` block refreshes the dashboard whether the request succeeds or fails. File-load/save errors are also caught so an unreadable local data file does not crash the interface.

## 11. Screenshots to Capture for Submission

Take screenshots from the running application on a desktop and add them to your college report/presentation:

1. Main Dashboard
2. Create Account form
3. Successful Account Creation dialog showing generated number
4. Deposit form and success message
5. Withdrawal form and success message
6. Insufficient Balance error dialog
7. Account Details result
8. Transaction History result
9. All Accounts list
10. Optional: Delete confirmation dialog

Screenshots are not bundled because they need to be captured from the application running on your computer.

## 12. Testing

The business logic can be tested without a display; the AWT screens should be manually demonstrated on a computer with a graphical desktop.

| Test case | Input | Expected result | Status |
|---|---|---|---|
| Create Savings Account | Valid name, 10-digit phone, address, ₹5,000 | Account is created with generated `ACC...` number and creation transaction | Pass — logic smoke-tested |
| Deposit | Existing account, ₹2,000 | Balance increases and a deposit transaction is recorded | Pass — logic smoke-tested |
| Withdraw | Existing account with enough balance, ₹500 | Balance decreases and a withdrawal transaction is recorded | Pass — logic smoke-tested |
| Insufficient funds | Savings balance ₹500; withdraw ₹700 | `InsufficientBalanceException`; balance stays unchanged | Pass — logic smoke-tested |
| Invalid amount | Deposit ₹0 / negative amount | `InvalidAmountException`; no transaction is recorded | Pass — logic smoke-tested |
| Missing account | Search `ACC9999` when absent | `AccountNotFoundException`; GUI shows a message dialog | Pass — logic smoke-tested |
| Current overdraft | Current account; withdraw within then beyond ₹10,000 available | First withdrawal succeeds; excess request is rejected | Pass — logic smoke-tested |
| Persistence | Save data, construct a new `Bank` from the same file | Accounts and transactions reload | Pass — logic smoke-tested |
| AWT navigation and dialogs | Click each menu/form button on a desktop | Correct card/action/dialog appears | Run manually on desktop |

The project includes the report/test checklist; for final college submission, run the GUI and capture your own screenshots.

## 13. Conclusion

The project implements a functional AWT bank-account demo with separate GUI and business-logic layers. It demonstrates private state and validated methods, an abstract superclass with two account subclasses, overridden behavior at runtime, custom checked exceptions, collections, event handling, and local file persistence. The design is intentionally small enough to explain method by method during a viva.

## 14. Future Enhancements

- Login and role-based access for a real multi-user application.
- Database connectivity and secure transaction storage.
- Interest posting, configurable fees, and richer bank policies.
- Exportable PDF or CSV statements.
- Improved phone/address validation and internationalization.
- Audit logging and authentication/security controls.
- ATM or online-banking integration (outside the scope of this desktop mini project).

## Viva Preparation: Java OOP Questions and Answers

1. **What is a class?** — A class is a blueprint that defines data and behavior for objects. `Account` and `Customer` are examples.
2. **What is an object?** — An object is an instance of a class with its own state. `ACC1001` is represented by an account object.
3. **Where did you create objects in this project?** — `Bank.createAccount()` creates a `Customer` and a `SavingsAccount` or `CurrentAccount`; the GUI and bank also create AWT and transaction objects.
4. **What is a constructor?** — A special method called when an object is created; it initializes that object's fields.
5. **Which constructors did you use?** — `Customer` has default and parameterized constructors. Savings/Current accounts have overloaded constructors. `Bank` has default and file-name constructors.
6. **What is encapsulation?** — Keeping data private and exposing safe operations through methods.
7. **Where is encapsulation implemented?** — `Account.balance` and `Customer` fields are private. Balance changes through `deposit()` and `withdraw()`.
8. **Why are account variables private?** — To prevent uncontrolled changes and enforce validation and balance rules.
9. **What is inheritance?** — A child class reuses and extends a parent class's state and behavior.
10. **Where did you use inheritance?** — `SavingsAccount` and `CurrentAccount` extend `Account`.
11. **What is polymorphism?** — One parent-type reference can call behavior belonging to different child object types.
12. **Where is method overriding used?** — `CurrentAccount` overrides `withdraw()`. Both subclasses override `calculateInterest()` and account-type methods.
13. **What is abstraction?** — Showing common operations while leaving implementation details to subclasses. `Account` is abstract and declares account-specific methods.
14. **What is exception handling?** — Handling an error condition so the program can report it and continue instead of terminating unexpectedly.
15. **Why did you create custom exceptions?** — To distinguish banking errors such as missing accounts, invalid amounts, and insufficient funds.
16. **Difference between `throw` and `throws`?** — `throw` raises an exception at a point in the code; `throws` declares that a method may pass an exception to its caller.
17. **What is `try-catch`?** — `try` contains code that may fail; `catch` handles a matching exception.
18. **Why did you use `ArrayList`?** — The number of accounts and transactions can grow or shrink, and `ArrayList` resizes dynamically.
19. **What is AWT?** — The Abstract Window Toolkit is Java's standard set of GUI components and event APIs.
20. **What is an event listener?** — An object that waits for and responds to a GUI event such as a button click.
21. **What does `ActionListener` do?** — It receives button/action events through `actionPerformed(ActionEvent)`.
22. **What happens when Deposit is clicked?** — The GUI validates the text, calls `Bank.deposit()`, the account balance changes through `Account.deposit()`, a transaction is recorded and saved, and the GUI shows the result.
23. **How is an account number generated?** — `Bank` increments a sequence and formats it as `ACC1001`, `ACC1002`, and so on; the sequence is saved.
24. **How is insufficient balance handled?** — The withdrawal method throws `InsufficientBalanceException`; the GUI catches it and shows its message in an AWT dialog.
25. **Why separate GUI and business logic?** — It keeps display/event code separate from banking rules, making the logic easier to test and explain.
26. **Why use multiple classes instead of one?** — Each class has one clear responsibility: account, customer, transaction, bank operations, exceptions, or GUI.
27. **What are access modifiers?** — Keywords controlling visibility. This project uses `private`, `protected`, and `public`.
28. **What is method overloading?** — Same method name with different parameter lists; this project demonstrates it with constructors.
29. **What is method overriding?** — A subclass provides its own implementation of an inherited method with the same signature.
30. **What improvements could be made?** — Add secure login, database storage, exported statements, configurable interest/fees, and stronger security and audit controls.

## Recommended 5–10 Minute Live Demonstration

1. Start the app and explain the dashboard and its three totals.
2. Create a Savings Account with a test name, 10-digit phone, address, and ₹5,000 initial deposit.
3. Point out the generated account number and customer ID in the success dialog.
4. Open Account Details and show the private data through getters/display methods plus the 4% interest estimate.
5. Deposit ₹2,000 and check the new balance.
6. Withdraw ₹1,000 and check the updated balance.
7. Attempt to withdraw more than the remaining Savings balance; explain the custom exception dialog and show that the balance did not change.
8. Show Transaction History and All Accounts.
9. Briefly explain `Account` inheritance, `CurrentAccount.withdraw()` overriding, `ArrayList`, and the GUI-to-`Bank` call flow.
10. Optionally create a Current Account and show that its overdraft rule differs; exit and confirm.
