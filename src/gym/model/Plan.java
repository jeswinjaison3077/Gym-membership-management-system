package gym.model;

/** Represents a subscription plan type, e.g. Basic / Premium / Platinum. */
public class Plan {
    private int planId;
    private String planName;
    private int durationMonths;
    private double price;

    public Plan(int planId, String planName, int durationMonths, double price) {
        this.planId = planId;
        this.planName = planName;
        this.durationMonths = durationMonths;
        this.price = price;
    }

    public int getPlanId() { return planId; }
    public String getPlanName() { return planName; }
    public int getDurationMonths() { return durationMonths; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return planId + ". " + planName + " - " + durationMonths + " month(s) - Rs." + price;
    }
}
