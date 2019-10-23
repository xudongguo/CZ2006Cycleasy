package com.example.cycleasy.ui.login;

import androidx.lifecycle.ViewModel;

import com.example.cycleasy.data.LoginDataSource;
import com.example.cycleasy.data.UserRepository;

public class SignupViewModel extends ViewModel {

    private UserRepository userRepository;


    public SignupViewModel(UserRepository userRepository) { this.userRepository = userRepository; }
    public void signupWithEmail(String email, String password, LoginDataSource.OnCallBack onCallBack) {
        userRepository.signupWithEmail(email, password, onCallBack);
    }
}
