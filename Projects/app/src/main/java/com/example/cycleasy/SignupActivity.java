package com.example.cycleasy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cycleasy.data.DatabaseHelper;
import com.example.cycleasy.ui.login.LoginActivity;

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
                if(name.equals("") || email.equals("") ||password.equals("")||cpassword.equals("")){
                    Toast.makeText(getApplicationContext(),"Fields are empty",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(password.equals(cpassword)){
                        Boolean checkmail = db.checkemail(email);
                        if(checkmail==true) {
                            Boolean insert = db.insert(email, name, password);
                            if (insert == true) {
                                Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Email Already exists",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "Password do not match", Toast.LENGTH_SHORT).show();
                    }
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

    private void setupUIViews(){
        Name = (EditText)findViewById(R.id.regname);
        Email = (EditText)findViewById(R.id.regusername);
        Password = (EditText)findViewById(R.id.regpassword);
        CPassword = (EditText)findViewById(R.id.regconfirmpassword);
        Signup = (Button)findViewById(R.id.regsignup);
        Login = (TextView)findViewById(R.id.reglogin);
        db = new DatabaseHelper(this);
    }
}
