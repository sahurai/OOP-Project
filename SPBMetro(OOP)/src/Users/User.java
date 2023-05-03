package Users;

import Structure.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Class of default user.
 */
public class User implements UserInterface{
    /** User's login.*/
    private String login;
    /** User's password.*/
    private String password;
    /** User's requested routes.*/
    private List<List<Station>> storedRoutes;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.storedRoutes = new ArrayList<>();
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public List<List<Station>> getStoredRoutes() {
        return storedRoutes;
    }

    public boolean canSetNewAdmin(){
        return false;
    }

    public boolean canFindRoute() {
        return true;
    }

    public boolean canGetHistoryOfRoutes() {
        return true;
    }

    public boolean canSetStatusOfStation() {
        return false;
    }

    public boolean canShowMap() {
        return true;
    }

    public void setPassword(String newPassword){
        this.password = newPassword;
    }

    @Override
    public String toString() {
        return "Login: " + login + (canSetNewAdmin() ? " | Status: admin" : " | Status: user");
    }
}
