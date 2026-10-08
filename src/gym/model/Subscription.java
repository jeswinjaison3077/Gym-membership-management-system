package gym.model;

import java.time.LocalDate;

/** Represents a member's subscription to a particular plan. */
public class Subscription {
    private int subscriptionId;
    private int memberId;
    private int planId;
    private String planName;   // filled by DAO join, for display convenience
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;     // ACTIVE / EXPIRED / CANCELLED

    public Subscription(int subscriptionId, int memberId, int planId, String planName,
                         LocalDate startDate, LocalDate endDate, String status) {
        this.subscriptionId = subscriptionId;
        this.memberId = memberId;
        this.planId = planId;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getSubscriptionId() { return subscriptionId; }
    public int getMemberId() { return memberId; }
    public int getPlanId() { return planId; }
    public String getPlanName() { return planName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /** Business logic: is this subscription currently valid? */
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status) && !LocalDate.now().isAfter(endDate);
    }

    @Override
    public String toString() {
        return "Sub#" + subscriptionId + " | " + planName + " | " + startDate + " to " + endDate + " | " + status;
    }
}
