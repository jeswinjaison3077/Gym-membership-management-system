package gym.model;

/**
 * Abstract base class representing any human entity in the gym system.
 * Demonstrates ABSTRACTION: it defines the common contract (fields + the
 * abstract method displayDetails()) but cannot be instantiated itself.
 * Member and Trainer extend this class -> INHERITANCE.
 */
public abstract class Person {

    // Private fields -> ENCAPSULATION (accessed only through getters/setters)
    private int id;
    private String name;
    private String phone;
    private String email;

    public Person(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Getters and setters (encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * Abstract method -> every subclass MUST provide its own version.
     * This is the hook for POLYMORPHISM: calling displayDetails() on a
     * Person reference will run the subclass's specific implementation
     * at runtime (dynamic method dispatch).
     */
    public abstract void displayDetails();
}
