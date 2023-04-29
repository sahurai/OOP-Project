package Users;

/**
 * Class of admin user.
 */
public class Admin extends User{
    public Admin(String login, String password) {
        super(login, password);
    }

    @Override
    public boolean canSetStatusOfStation() {
        return true;
    }

    @Override
    public boolean canSetNewAdmin(){
        return true;
    }

}
