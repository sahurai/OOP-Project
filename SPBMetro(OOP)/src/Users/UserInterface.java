package Users;

/**
 * Interface to simplify creating new types of users.
 */
public interface UserInterface {
    boolean canSetNewAdmin();
    boolean canSetStatusOfStation();
    boolean canGetHistoryOfRoutes();
    boolean canShowMap();
}
