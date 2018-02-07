package Monopoly.init;


public class BadResourceException extends RuntimeException {

    public BadResourceException() {
        this("Error reading resource. Check that resource path is valid and resource exists.");
    }

    public BadResourceException(String error) {
        super(error);
    }

}
