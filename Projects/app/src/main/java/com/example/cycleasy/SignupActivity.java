package com.example.cycleasy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cycleasy.data.DatabaseHelper;
import com.example.cycleasy.ui.login.LoginActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Activity class for activities in sign up page
 */
public class SignupActivity extends AppCompatActivity {

    private EditText Name, Email, Password, CPassword;
    private Button Signup;
    private TextView Login;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setupUIViews();

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Name.getText().toString();
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                String cpassword = CPassword.getText().toString();

                Boolean checkmail = db.checkemail(email);
                    if (checkmail == true) {
                        if (!isValidPassword(password)) {
                            Password.setError("Password must contain a digit, lowercase, uppercase and special character (@#$%^&+=)!");
                        } else {
                            if (isValidPassword(password)) {
                                if (password.equals(cpassword)) {
                                    Boolean insert = db.insert(email, name, password);
                                    if (insert == true) {
                                        Toast.makeText(getApplicationContext(), "Registered successfully!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    }
                                } else {
                                    CPassword.setError("Password do not match!");
                                }
                            }
                        }
                    }else {
                        Email.setError("Email already exist!");
                        }
                    }
                });

                Login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = (new Intent(SignupActivity.this,LoginActivity.class));
                        startActivity(intent);
                    }
                });
            }

            private TextWatcher loginTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String nameInput = Name.getText().toString().trim();
                    String emailInput = Email.getText().toString().trim();
                    String passwordInput = Password.getText().toString().trim();
                    String cpasswordInput = CPassword.getText().toString().trim();

                    Signup.setEnabled(!nameInput.isEmpty() && !emailInput.isEmpty() && !passwordInput.isEmpty() &&
                            !cpasswordInput.isEmpty());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            private void setupUIViews(){
                Name = (EditText)findViewById(R.id.regname);
                Email = (EditText)findViewById(R.id.regusername);
                Password = (EditText)findViewById(R.id.regpassword);
                CPassword = (EditText)findViewById(R.id.regconfirmpassword);
                Signup = (Button)findViewById(R.id.regsignup);
                Login = (TextView)findViewById(R.id.reglogin);
                db = new DatabaseHelper(this);

                Name.addTextChangedListener(loginTextWatcher);
                Email.addTextChangedListener(loginTextWatcher);
                Password.addTextChangedListener(loginTextWatcher);
                CPassword.addTextChangedListener(loginTextWatcher);
            }

            public boolean isValidPassword(final String password) {

            Pattern pattern;
            Matcher matcher;

            //Password must contain a number, lowercase, uppercase and special character
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password);

            return matcher.matches();
            }
        }
