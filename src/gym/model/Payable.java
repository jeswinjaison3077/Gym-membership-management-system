package gym.model;

/**
 * ============================================================================
 * [CONCEPT: INTERFACE & ABSTRACTION]
 * Pure abstraction contract for financial billing and receipt generation.
 * 
 * Demonstrates:
 * 1. Interface abstraction: Declares abstract method contracts.
 * 2. Interface polymorphism: Multiple classes can implement this interface.
 * ============================================================================
 */
public interface Payable {
    double calculateAmountDue();
    String generateReceipt();
}
