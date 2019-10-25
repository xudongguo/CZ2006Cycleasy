package com.example.cycleasy.ui.login;

import android.util.Patterns;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cycleasy.data.LoginDataSource;
import com.example.cycleasy.data.UserRepository;

public class SignupViewModel extends ViewModel {

    private UserRepository userRepository;
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> passwordConfirm = new MutableLiveData<>();
    public MutableLiveData<Integer> loading;

    public SignupViewModel(UserRepository userRepository) { this.userRepository = userRepository; }

    public boolean isDataValid() {
        String email = this.email.getValue();
        String password = this.password.getValue();
        String passwordConfirm = this.passwordConfirm.getValue();

        if (email == null || password == null || passwordConfirm == null || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                || password.length()< 8 || !passwordConfirm.equals(password)) {
            return false;
        }
        return true;
    }

    public void signupWithEmail(String email, String password, LoginDataSource.OnCallBack onCallBack) {
        getLoading().setValue(View.VISIBLE);
        userRepository.signupWithEmail(email, password, onCallBack);
        getLoading().setValue(View.GONE);
    }

    private MutableLiveData<Integer> getLoading() {
        if (loading == null) {
            loading = new MutableLiveData<>();
            loading.setValue(View.GONE);
        }
        return loading;
    }

    public int getError() {
        int error = 0;
        String email = this.email.getValue();
        String password = this.password.getValue();
        String passwordConfirm = this.passwordConfirm.getValue();

        if (email == null || password == null || passwordConfirm == null) {
            if (email == null) { error += 1; }
            if (password == null) { error += 2; }
            if (passwordConfirm == null) { error += 4; }
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { error += 8; }
            if (password.length() < 8) { error += 16; }
            else if (!passwordConfirm.equals(password)) { error += 32; }
        }

        return error;
    }
}
