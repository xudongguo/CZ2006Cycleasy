package com.example.cycleasy.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.cycleasy.MainActivity;
import com.example.cycleasy.R;
import com.example.cycleasy.data.LoginDataSource;
import com.example.cycleasy.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity class for activities in sign up page
 */
public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private SignupViewModel signupViewModel;
    private ActivitySignupBinding activitySignupBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignupBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        signupViewModel = ViewModelProviders.of(this, new ViewModelFactory(this))
                .get(SignupViewModel.class);
        activitySignupBinding.setViewModel(signupViewModel);
        activitySignupBinding.setLifecycleOwner(this);
        activitySignupBinding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = activitySignupBinding.email.getText().toString();
                String password = activitySignupBinding.password.getText().toString();
                if (signupViewModel.isDataValid()) {
                    signupViewModel.signupWithEmail(email, password, new LoginDataSource.OnCallBack() {
                        @Override
                        public void onSuccessful(FirebaseUser user) {
                            updateUI(user);
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(getApplicationContext(), "Account already exists.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    int error = signupViewModel.getError();
                    String binary = String.format("%6s", Integer.toBinaryString(error)).replace(' ', '0');
                    System.out.println(binary);
                    if (error < 8) {
                        if (binary.charAt(5) == '1') {
                            activitySignupBinding.email.setError("Email cannot be empty.");
                        }
                        if (binary.charAt(4) == '1') {
                            activitySignupBinding.password.setError("Password cannot be empty.");
                        }
                        if (binary.charAt(3) == '1') {
                            activitySignupBinding.passwordConfirm.setError("Confirm password cannot be empty.");
                        }
                    } else {
                        if (binary.charAt(2) == '1') {
                            activitySignupBinding.email.setError("Enter valid email.");
                        }
                        if (binary.charAt(1) == '1') {
                            activitySignupBinding.password.setError("Password must be >= 8 characters.");
                        }
                        if (binary.charAt(0) == '1') {
                            activitySignupBinding.passwordConfirm.setError("Passwords do not match.");
                        }
                    }
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
