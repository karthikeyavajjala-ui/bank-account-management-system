import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**

* Represents a banking transaction.
* Demonstrates classes, objects, constructors and encapsulation.
  */
  public class Transaction implements Serializable {

  private static final long serialVersionUID = 1L;

  private String transactionId;
  private String accountNumber;
  private String transactionType;
  private double amount;
  private String description;
  private String dateTime;

  // Parameterized constructor
  public Transaction(String transactionId, String accountNumber,
  String transactionType, double amount,
  String description) {


   this.transactionId = transactionId;
   this.accountNumber = accountNumber;
   this.transactionType = transactionType;
   this.amount = amount;
   this.description = description;

   DateTimeFormatter formatter =
           DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

   this.dateTime = LocalDateTime.now().format(formatter);


  }

  // Getter methods
  public String getTransactionId() {
  return transactionId;
  }

  public String getAccountNumber() {
  return accountNumber;
  }

  public String getTransactionType() {
  return transactionType;
  }

  public double getAmount() {
  return amount;
  }

  public String getDescription() {
  return description;
  }

  public String getDateTime() {
  return dateTime;
  }

  // Display transaction details
  @Override
  public String toString() {
  return "Transaction ID: " + transactionId
  + "\nAccount Number: " + accountNumber
  + "\nType: " + transactionType
  + "\nAmount: ₹" + String.format("%.2f", amount)
  + "\nDescription: " + description
  + "\nDate and Time: " + dateTime;
  }
  }
