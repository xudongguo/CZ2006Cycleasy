package com.example.cycleasy.data;

import android.content.Context;
import android.content.Intent;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

public class UserRepository implements LoginDataSource{

    private static final String TAG = "UserRepository";
    private static UserRepository userRepository;
    private FirebaseDataSource firebaseDataSource;

    private UserRepository(Context context) { firebaseDataSource = FirebaseDataSource.getInstance(context); }

    public static UserRepository getInstance(Context context) {
        if (userRepository == null) {
            userRepository = new UserRepository(context);
        }
        return userRepository;
    }

    @Override
    public void loginAnonymously(OnCallBack onCallBack) {
        firebaseDataSource.loginAnonymously(onCallBack);
    }

    @Override
    public void loginWithEmail(String email, String password, OnCallBack onCallBack) {
        firebaseDataSource.loginWithEmail(email, password, onCallBack);
    }

    @Override
    public void loginWithFacebook(OnCallBack onCallBack) {
        firebaseDataSource.loginWithFacebook(onCallBack);
    }

    @Override
    public void loginWithGoogle(OnCallBack onCallBack) {
        firebaseDataSource.loginWithGoogle(onCallBack);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        firebaseDataSource.onActivityResult(requestCode, resultCode, data);
    }

    public void signupWithEmail(String email, String password, OnCallBack onCallBack) {
        firebaseDataSource.signupWithEmail(email, password, onCallBack);
    }
}
