package gym.model;

/**
 * Interface -> pure ABSTRACTION contract.
 * Any class that involves a monetary transaction implements this,
 * guaranteeing it can calculate dues and generate a receipt.
 * Demonstrates INTERFACE-based abstraction + POLYMORPHISM
 * (different classes could implement this differently).
 */
public interface Payable {
    double calculateAmountDue();
    String generateReceipt();
}
