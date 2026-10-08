package gym.model;

/**
 * Represents a gym trainer. Extends Person -> INHERITANCE.
 * Overrides displayDetails() differently from Member -> POLYMORPHISM.
 */
public class Trainer extends Person {

    private String specialization;
    private int experienceYears;

    public Trainer(int id, String name, String phone, String email,
                    String specialization, int experienceYears) {
        super(id, name, phone, email);
        this.specialization = specialization;
        this.experienceYears = experienceYears;
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    @Override
    public void displayDetails() {
        System.out.println("---------------------------------------------");
        System.out.println("Trainer ID     : " + getId());
        System.out.println("Name           : " + getName());
        System.out.println("Phone          : " + getPhone());
        System.out.println("Email          : " + getEmail());
        System.out.println("Specialization : " + specialization);
        System.out.println("Experience     : " + experienceYears + " years");
        System.out.println("---------------------------------------------");
    }
}
