package exception;

/**
 * Created by Yauheni on 29.01.17.
 */
public class MatchNotFoundException extends IllegalArgumentException {
    public MatchNotFoundException() {
        super();
    }
    public MatchNotFoundException(String s) {
        super(s);
    }
}
