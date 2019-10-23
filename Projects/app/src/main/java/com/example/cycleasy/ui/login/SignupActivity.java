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

                        }

                        public void onError(String errMsg) {

                        }
                    });
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
}
