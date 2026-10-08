package gym.model;

import java.time.LocalDate;

/**
 * Represents a gym member. Extends Person -> INHERITANCE.
 * Overrides displayDetails() -> POLYMORPHISM (method overriding).
 */
public class Member extends Person {

    private int age;
    private String gender;
    private LocalDate joinDate;
    private Integer trainerId;   // nullable: member may not have a trainer yet
    private String trainerName;  // convenience field filled in by DAO joins (not stored again in DB)

    public Member(int id, String name, String phone, String email,
                  int age, String gender, LocalDate joinDate, Integer trainerId) {
        super(id, name, phone, email);   // calling parent constructor
        this.age = age;
        this.gender = gender;
        this.joinDate = joinDate;
        this.trainerId = trainerId;
    }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public Integer getTrainerId() { return trainerId; }
    public void setTrainerId(Integer trainerId) { this.trainerId = trainerId; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    // Overridden (Runtime Polymorphism)
    @Override
    public void displayDetails() {
        System.out.println("---------------------------------------------");
        System.out.println("Member ID   : " + getId());
        System.out.println("Name        : " + getName());
        System.out.println("Phone       : " + getPhone());
        System.out.println("Email       : " + getEmail());
        System.out.println("Age/Gender  : " + age + " / " + gender);
        System.out.println("Join Date   : " + joinDate);
        System.out.println("Trainer     : " + (trainerName != null ? trainerName : "Not Assigned"));
        System.out.println("---------------------------------------------");
    }
}
