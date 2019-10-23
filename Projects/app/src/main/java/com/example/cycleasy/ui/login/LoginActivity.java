package com.example.cycleasy.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.cycleasy.ContentActivity;
import com.example.cycleasy.R;
import com.example.cycleasy.SignupActivity;
import com.example.cycleasy.data.FirebaseDataSource;
import com.example.cycleasy.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding activityLoginBinding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new ViewModelFactory(this))
                .get(LoginViewModel.class);
        activityLoginBinding.setViewModel(loginViewModel);
        activityLoginBinding.setLifecycleOwner(this);

        activityLoginBinding.guestLoginBtn.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                loginViewModel.loginAnonymously(new FirebaseDataSource.OnCallBack() {
                    @Override
                    public void onSuccessful(FirebaseUser user) { updateUI(user); }
                    @Override
                    public void onError() {

                    }
                });
            }
        });
        activityLoginBinding.facebookLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViewModel.loginWithFacebook(new FirebaseDataSource.OnCallBack() {
                    @Override
                    public void onSuccessful(FirebaseUser user) { updateUI(user); }
                    @Override
                    public void onError() { }
                });
            }
        });
        activityLoginBinding.googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViewModel.loginWithGoogle(new FirebaseDataSource.OnCallBack() {
                    @Override
                    public void onSuccessful(FirebaseUser user) {
                        updateUI(user);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        });
        activityLoginBinding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginViewModel.dataIsValid()) {
                    loginViewModel.loginWithEmail(loginViewModel.getEmailAddress(),
                            loginViewModel.getPassword(), new FirebaseDataSource.OnCallBack() {
                                @Override
                                public void onSuccessful(FirebaseUser user) {
                                    updateUI(user);
                                }

                                @Override
                                public void onError() {

                                }
                            });
                } else {

                }
            }
        });
        activityLoginBinding.linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void updateUI(FirebaseUser firebaseUser) {
        Toast.makeText(this, "Welcome " + firebaseUser.getDisplayName(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ContentActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginViewModel.onActivityResult(requestCode, resultCode, data);
    }
}