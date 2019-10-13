package com.example.cycleasy.data;

import android.content.Context;
import android.net.wifi.hotspot2.pps.Credential;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cycleasy.data.model.LoggedInUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class FirebaseDataSource {

    private static final String TAG = "FirebaseDataSource";
    FirebaseAuth firebaseAuth;
//    Context context;

    public FirebaseDataSource() {
        firebaseAuth = FirebaseAuth.getInstance();
//        this.context = context;
    }

    public void loginWithEmail(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "loginWithEmail:success");
                    FirebaseUser user = getCurrentUser();
                } else {
                    Log.w(TAG, "loginWithEmail:fail", task.getException());
//                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loginWithCredentials(AuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "loginWithCredential:success");
                    // TODO: handle successful login

                } else {
                    Log.w(TAG, "loginWithCredential:fail", task.getException());
//                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void createUserWithEmailAndPassword(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                } else {
                    Log.w(TAG, "createUser failed.", task.getException());
//                    Toast.makeText(context, "Failed to create new user.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void logout() {
        // TODO: revoke authentication
        firebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
}
