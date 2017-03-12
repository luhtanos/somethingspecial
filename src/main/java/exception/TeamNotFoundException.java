package exception;

/**
 * Created by Yauheni on 06.02.17.
 */
public class TeamNotFoundException extends IllegalArgumentException {
    public TeamNotFoundException() {
        super();
    }
    public TeamNotFoundException(String s) {
        super(s);
    }
}
