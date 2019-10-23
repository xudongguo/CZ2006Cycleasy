package com.example.cycleasy.ui.login;

import android.content.Intent;
import android.util.Patterns;
import android.view.View;

import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cycleasy.data.LoginDataSource;
import com.example.cycleasy.data.UserRepository;

public class LoginViewModel extends ViewModel implements LoginDataSource {

    private static final String TAG = "LoginViewModel";
    private UserRepository userRepository;
    public MutableLiveData<String> emailAddress = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<Integer> loading;

    public LoginViewModel(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public void loginAnonymously(OnCallBack onCallBack) {
        getLoading().setValue(View.VISIBLE);
        userRepository.loginAnonymously(onCallBack);
        getLoading().setValue(View.GONE);
    }

    @Override
    public void loginWithEmail(String email, String password, OnCallBack onCallBack) {
        getLoading().setValue(View.VISIBLE);
        userRepository.loginWithEmail(email, password, onCallBack);
        getLoading().setValue(View.GONE);
    }

    @Override
    public void loginWithFacebook(OnCallBack onCallBack) {
        getLoading().setValue(View.VISIBLE);
        userRepository.loginWithFacebook(onCallBack);
        getLoading().setValue(View.GONE);
    }

    @Override
    public void loginWithGoogle(OnCallBack onCallBack) {
        getLoading().setValue(View.VISIBLE);
        userRepository.loginWithGoogle(onCallBack);
        getLoading().setValue(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        userRepository.onActivityResult(requestCode, resultCode, data);
    }

    private MutableLiveData<Integer> getLoading() {
        if (loading == null) {
            loading = new MutableLiveData<>();
            loading.setValue(View.GONE);
        }
        return loading;
    }

    boolean dataIsValid() {
        String email = emailAddress.getValue();
        String pass = password.getValue();
        if (email == null || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            System.out.println(email);
            return false;
        }

        if (pass == null || pass.isEmpty() || pass.length() < 8 || pass.length() > 15) {
            System.out.println(pass);
            return false;
        }

        return true;
    }

    public String getEmailAddress() {
        return emailAddress.getValue();
    }

    public String getPassword() {
        return password.getValue();
    }
}
