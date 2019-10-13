package com.example.cycleasy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.cycleasy.ui.SignupViewModel;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private SignupViewModel signupViewModel;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager facebookCallbackManager;

    private EditText emailText;
    private EditText passwordText;
    private EditText confirmPwdText;
    private Button signupBtn;
    private SignInButton googleSigninBtn;
    private LoginButton facebookSigninBtn;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupViewModel = ViewModelProviders.of(this, new SignupViewModel.Factory(getApplicationContext())).get(SignupViewModel.class);
        firebaseAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        facebookCallbackManager = CallbackManager.Factory.create();

        emailText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);
        confirmPwdText = (EditText) findViewById(R.id.confirm_password);
        signupBtn = (Button) findViewById(R.id.signup);
        googleSigninBtn = findViewById(R.id.google_signin_btn);
        facebookSigninBtn = findViewById(R.id.facebook_login_btn);


    }
}
