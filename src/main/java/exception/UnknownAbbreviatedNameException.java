package exception;

/**
 * Created by Yauheni on 29.01.17.
 */
public class UnknownAbbreviatedNameException extends IllegalArgumentException {
    public UnknownAbbreviatedNameException() {
        super();
    }
    public UnknownAbbreviatedNameException(String s) {
        super(s);
    }
}
