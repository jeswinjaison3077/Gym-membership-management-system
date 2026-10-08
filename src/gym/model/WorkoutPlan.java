package gym.model;

import java.time.LocalDate;

/** Represents a workout plan a trainer assigns to a member. */
public class WorkoutPlan {
    private int workoutId;
    private int memberId;
    private int trainerId;
    private String title;
    private String description;
    private int daysPerWeek;
    private LocalDate createdDate;

    public WorkoutPlan(int workoutId, int memberId, int trainerId, String title,
                        String description, int daysPerWeek, LocalDate createdDate) {
        this.workoutId = workoutId;
        this.memberId = memberId;
        this.trainerId = trainerId;
        this.title = title;
        this.description = description;
        this.daysPerWeek = daysPerWeek;
        this.createdDate = createdDate;
    }

    public int getWorkoutId() { return workoutId; }
    public int getMemberId() { return memberId; }
    public int getTrainerId() { return trainerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDaysPerWeek() { return daysPerWeek; }
    public LocalDate getCreatedDate() { return createdDate; }

}
