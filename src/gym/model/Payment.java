package gym.model;

import java.time.LocalDate;

/**
 * ============================================================================
 * [CONCEPT: INTERFACE IMPLEMENTATION & POLYMORPHISM]
 * Represents a payment transaction made by a member.
 * 
 * Demonstrates:
 * 1. Interface Implementation: Implements Payable interface.
 * 2. Polymorphism: Provides concrete implementation for calculateAmountDue() and generateReceipt().
 * ============================================================================
 */
public class Payment implements Payable {

    private int paymentId;
    private int memberId;
    private Integer subscriptionId;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMode;  // CASH / CARD / UPI
    private String status;       // PAID / DUE

    public Payment(int paymentId, int memberId, Integer subscriptionId, double amount,
                    LocalDate paymentDate, String paymentMode, String status) {
        this.paymentId = paymentId;
        this.memberId = memberId;
        this.subscriptionId = subscriptionId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMode = paymentMode;
        this.status = status;
    }

    public int getPaymentId() { return paymentId; }
    public int getMemberId() { return memberId; }
    public Integer getSubscriptionId() { return subscriptionId; }
    public double getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getPaymentMode() { return paymentMode; }
    public String getStatus() { return status; }

    // [CONCEPT: INTERFACE METHOD IMPLEMENTATION]
    @Override
    public double calculateAmountDue() {
        return "DUE".equalsIgnoreCase(status) ? amount : 0.0;
    }

    @Override
    public String generateReceipt() {
        return String.format(
            "RECEIPT #%d | Member ID: %d | Amount: Rs.%.2f | Mode: %s | Date: %s | Status: %s",
            paymentId, memberId, amount, paymentMode, paymentDate, status);
    }
}
