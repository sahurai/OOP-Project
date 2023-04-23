package Users;

/**
 * Class of guest user.
 */
public class Guest extends User {
    public Guest() {
        super("guest", "guest");
    }

    @Override
    public boolean canGetHistoryOfRoutes() {
        return false;
    }

    @Override
    public boolean canSetNewAdmin(){
        return false;
    }

}
