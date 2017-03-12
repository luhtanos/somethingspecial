package exception;

/**
 * Created by Yauheni on 29.01.17.
 */
public class UnknownTeamException extends IllegalArgumentException {
    public UnknownTeamException() {
        super();
    }
    public UnknownTeamException(String s) {
        super(s);
    }
}
