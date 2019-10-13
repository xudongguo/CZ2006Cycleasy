package com.example.cycleasy.data;

import com.example.cycleasy.data.model.LoggedInUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private FirebaseDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private FirebaseUser user;

    // private constructor : singleton access
    private LoginRepository(FirebaseDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(FirebaseDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(FirebaseUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

//    public Result<LoggedInUser> login(String username, String password) {
//        // handle login
//        Result<LoggedInUser> result = dataSource.login(username, password);
//        if (result instanceof Result.Success) {
//            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
//        }
//        return result;
//    }

    public void loginWithEmail(String email, String password) {
        dataSource.loginWithEmail(email, password);
    }

    public void loginWithCredentials(AuthCredential credential) {
        dataSource.loginWithCredentials(credential);
    }

    public FirebaseUser getUser() {
        return dataSource.getCurrentUser();
    }

    public void createNewUser(String email, String password) {
        dataSource.createUserWithEmailAndPassword(email, password);
    }
}
