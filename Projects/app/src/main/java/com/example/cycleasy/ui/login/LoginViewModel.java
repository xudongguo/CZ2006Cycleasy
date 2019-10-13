package com.example.cycleasy.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.example.cycleasy.data.LoginRepository;
import com.example.cycleasy.data.Result;
import com.example.cycleasy.data.model.LoggedInUser;
import com.example.cycleasy.R;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginViewModel extends ViewModel {

    private static final String TAG = "LoginViewModel";

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Log.d(TAG, "login");

        loginRepository.loginWithEmail(username, password);
        FirebaseUser user = loginRepository.getUser();
    }

    public void loginWithFacebook(AccessToken accessToken) {
        Log.d(TAG, "loginWithFacebook:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        loginRepository.loginWithCredentials(credential);
        FirebaseUser user = loginRepository.getUser();
    }

    public void loginWithGoogle(GoogleSignInAccount acc) {
      Log.d(TAG, "loginWithGoogle:" + acc.getId());

      AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
      loginRepository.loginWithCredentials(credential);
      FirebaseUser user = loginRepository.getUser();
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
