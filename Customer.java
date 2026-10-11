import java.io.Serializable;

/** A bank customer. Private fields and validated accessors demonstrate encapsulation. */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    // ENCAPSULATION: customer data is private and can only be accessed through methods.
    private String customerId;
    private String name;
    private String phoneNumber;
    private String address;

    // CONSTRUCTOR: default constructor; setters can be used to fill the customer later.
    public Customer() {
        this.customerId = "";
        this.name = "";
        this.phoneNumber = "";
        this.address = "";
    }

    // CONSTRUCTOR: parameterized constructor initializes a complete customer object.
    public Customer(String customerId, String name, String phoneNumber, String address) {
        this();
        setCustomerId(customerId);
        setName(name);
        setPhoneNumber(phoneNumber);
        setAddress(address);
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = requireText(customerId, "Customer ID");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireText(name, "Customer name");
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    /** This student project validates a 10-digit Indian phone number. */
    public void setPhoneNumber(String phoneNumber) {
        String cleanedPhone = requireText(phoneNumber, "Phone number");
        if (!cleanedPhone.matches("[0-9]{10}")) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }
        this.phoneNumber = cleanedPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = requireText(address, "Address");
    }

    public String displayDetails() {
        return "Customer ID : " + customerId
                + "\nName        : " + name
                + "\nPhone       : " + phoneNumber
                + "\nAddress     : " + address;
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return name + " (" + customerId + ")";
    }
}
