package ingage.ingage20;

/**
 * Created by Davis on 4/9/2017.
 */

public class SignInProvider {

    String cUsername;
    String cPassword;

    //IF user is signed in or not
    boolean signInState;

    public void setcUsername(String username){
        cUsername = username;
    }

    public void setcPassword(String password){
        cPassword = password;
    }

    String getcUsername(){
        return cUsername;
    }

    String getcPassword(){
        return cPassword;
    }

    public void setSignInState(boolean signInState){
        signInState = signInState;
    }
}
