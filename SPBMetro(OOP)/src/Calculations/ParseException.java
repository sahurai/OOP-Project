package Calculations;

/**
 * Parse exception.
 */
public class ParseException extends Exception{
    // Конструктор без параметров
    public ParseException() {
        super();
    }

    // Конструктор с сообщением об ошибке
    public ParseException(String message) {
        super(message);
    }
}
