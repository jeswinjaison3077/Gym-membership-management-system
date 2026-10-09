package gym.model;

/**
 * ============================================================================
 * [CONCEPT: ABSTRACTION & INHERITANCE BASE]
 * Abstract base class representing any human entity in the gym system.
 * 
 * Demonstrates:
 * 1. Abstraction: Cannot be instantiated directly.
 * 2. Encapsulation: Private instance variables accessed via getters/setters.
 * 3. Inheritance Base: Parent class for Member and Trainer.
 * 4. Abstract Method: Subclasses MUST override displayDetails().
 * ============================================================================
 */
public abstract class Person {

    // Private fields -> ENCAPSULATION
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
     * [CONCEPT: POLYMORPHISM - DYNAMIC METHOD DISPATCH]
     * Hook for dynamic method dispatch.
     */
    public abstract void displayDetails();
}
