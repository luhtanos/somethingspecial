package exception;

public class ShitException extends IllegalStateException {
    public static final String SHIT = "shit";
    public ShitException() {
        super();
    }
    public ShitException(String s) {
        super(s);
    }
}
