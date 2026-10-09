package gym.util;

/**
 * ============================================================================
 * [CONCEPT: EXCEPTION HANDLING - CUSTOM EXCEPTION]
 * Custom application-specific exception class.
 * 
 * Demonstrates:
 * 1. Extending Exception class to create a custom checked exception.
 * 2. Exception propagation and handling via try-catch blocks.
 * ============================================================================
 */
public class GymException extends Exception {
    
    public GymException(String message) {
        super(message);
    }
    
    public GymException(String message, Throwable cause) {
        super(message, cause);
    }
}
