package com.example.cycleasy.ui.login;

import android.content.Intent;
import android.util.Patterns;
import android.view.View;

import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cycleasy.data.LoginDataSource;
import com.example.cycleasy.data.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel implements LoginDataSource {

    private static final String TAG = "LoginViewModel";
    private UserRepository userRepository;
    public MutableLiveData<String> emailAddress = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public ObservableBoolean loading;

    public LoginViewModel(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public void loginAnonymously(OnCallBack onCallBack) {
        getLoading().set(true);
        userRepository.loginAnonymously(onCallBack);
        getLoading().set(false);
    }

    @Override
    public void loginWithEmail(String email, String password, OnCallBack onCallBack) {
        getLoading().set(true);
        userRepository.loginWithEmail(email, password, onCallBack);
        getLoading().set(false);
    }

    @Override
    public void loginWithFacebook(OnCallBack onCallBack) {
        getLoading().set(true);
        userRepository.loginWithFacebook(onCallBack);
        getLoading().set(false);
    }

    @Override
    public void loginWithGoogle(OnCallBack onCallBack) {
        getLoading().set(true);
        userRepository.loginWithGoogle(onCallBack);
        getLoading().set(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        userRepository.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    private ObservableBoolean getLoading() {
        if (loading == null) {
            loading = new ObservableBoolean();
            loading.set(false);
        }
        return loading;
    }

    public boolean dataIsValid() {
        String email = emailAddress.getValue();
        String pass = password.getValue();
        if (email == null || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            System.out.println(email);
            return false;
        }

        if (pass == null || pass.isEmpty() || pass.length() < 8) {
            System.out.println(pass);
            return false;
        }

        return true;
    }
}
