package com.example.cycleasy.data;

import android.content.Intent;

import com.google.firebase.auth.FirebaseUser;

public interface LoginDataSource {
    interface OnCallBack {
        void onSuccessful(FirebaseUser user);
        void onError();
    }

    void loginAnonymously(OnCallBack onCallBack);
    void loginWithEmail(String email, String password, OnCallBack onCallBack);
    void loginWithFacebook(OnCallBack onCallBack);
    void loginWithGoogle(OnCallBack onCallBack);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
