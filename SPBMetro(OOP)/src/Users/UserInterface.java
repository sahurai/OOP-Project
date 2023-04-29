package Users;

public interface UserInterface {
    boolean canSetNewAdmin();
    boolean canSetStatusOfStation();
    boolean canGetHistoryOfRoutes();
    boolean canShowMap();
}
