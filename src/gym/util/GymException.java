package gym.util;

/**
 * Custom checked exception used for business-rule violations
 * (e.g. duplicate member, invalid plan chosen).
 * Demonstrates custom EXCEPTION HANDLING, a common viva question.
 */
public class GymException extends Exception {
    public GymException(String message) {
        super(message);
    }
}
