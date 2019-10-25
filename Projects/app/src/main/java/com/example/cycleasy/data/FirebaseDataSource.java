package com.example.cycleasy.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cycleasy.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class FirebaseDataSource implements LoginDataSource{

    private static final String TAG = "FirebaseDataSource";
    private static FirebaseDataSource firebaseDataSource = null;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private OnCallBack facebookOnCallBack, googleOnCallBack;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;

    private FirebaseDataSource(Context context) {
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("" + R.string.google_web_client_id)
                .requestServerAuthCode("" + R.string.google_web_client_id)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
    }

    public static FirebaseDataSource getInstance(Context context) {
        if (firebaseDataSource == null) {
            firebaseDataSource = new FirebaseDataSource(context);
        }
        return firebaseDataSource;
    }

    @Override
    public void loginAnonymously(final OnCallBack onCallBack) {
        firebaseAuth.signInAnonymously().addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "loginAnonymously:success");
                    sendMessage("Anonymous authentication success.");
                    onCallBack.onSuccessful(firebaseAuth.getCurrentUser());
                } else {
                    Log.w(TAG, "loginAnonymously:failure", task.getException());
                    sendMessage("Anonymous authentication failed.");
                }
            }
        });
    }

    @Override
    public void loginWithEmail(String email, String password, final OnCallBack onCallBack) {

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "loginWithEmail:success");
                    sendMessage("Email authentication success.");
                    onCallBack.onSuccessful(firebaseAuth.getCurrentUser());
                } else {
                    Log.w(TAG, "loginWithEmail:failure", task.getException());
                    sendMessage("Email authentication failed.");
                }
            }
        });
    }

    @Override
    public void loginWithFacebook(OnCallBack onCallBack) {
        facebookOnCallBack = onCallBack;
        checkAccessToken();
        loginManager.logInWithReadPermissions((Activity) context, Arrays.asList("public_profile", "email"));
    }

    @Override
    public void loginWithGoogle(OnCallBack onCallBack) {
        googleOnCallBack = onCallBack;
        Intent intent = googleSignInClient.getSignInIntent();
        ((Activity)context).startActivityForResult(intent, 9001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "loginWithGoogle:failed", e);
                sendMessage("Google authentication failed");
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void signupWithEmail(String email, String password, final OnCallBack onCallBack) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signupWithEmail:success");
                    sendMessage("Account creation successful.");
                    onCallBack.onSuccessful(firebaseAuth.getCurrentUser());
                } else {
                    Log.w(TAG, "signupWithEmail:failure", task.getException());
                    sendMessage("Account creation failed.");
                    onCallBack.onError();
                }
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "firebaseAuthWithGoogle:success");
                    sendMessage("Google authentication success.");
                    googleOnCallBack.onSuccessful(firebaseAuth.getCurrentUser());
                } else {
                    Log.w(TAG, "firebaseAuthWithGoogle:failure", task.getException());
                    sendMessage("Google authentication failed.");
                }
            }
        });
    }

    private void checkAccessToken() {
        loginManager = LoginManager.getInstance();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && accessToken.isExpired()) {
            loginManager.logOut();
        }
        loginManager.registerCallback(callbackManager, new LoginResultFacebookCallback());
    }

    private void firebaseAuthWithFacebook(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "firebaseAuthWithFacebook:success");
                            sendMessage("Facebook authentication success.");
                            facebookOnCallBack.onSuccessful(firebaseAuth.getCurrentUser());
                        } else {
                            Log.w(TAG, "firebaseAuthWithFacebook:failure", task.getException());
                            sendMessage("Facebook authentication failed.");
                        }
                    }
                });
    }

    private void sendMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    private class LoginResultFacebookCallback implements com.facebook.FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(final LoginResult loginResult) {
            GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    if (response.getError() != null) {
                        String errorMessage = response.getError().getErrorMessage();
                        Log.e(TAG, errorMessage);
                        if (facebookOnCallBack != null) {
                            facebookOnCallBack.onError();
                        }
                    }
                    firebaseAuthWithFacebook(loginResult.getAccessToken());
                }
            });

            Bundle bundle = new Bundle();
            bundle.putString("fields", "picture.type(large),quotes,email,id,name,link,age_range,first_name,last_name,gender,locale,timezone,verified,updated_time");
            graphRequest.setParameters(bundle);
            graphRequest.executeAsync();
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "Facebook login cancelled.");
            if (facebookOnCallBack != null) {
                facebookOnCallBack.onError();
            }
        }

        @Override
        public void onError(FacebookException error) {
            Log.d(TAG, "Facebook login error: " + error.getMessage());
            if (facebookOnCallBack != null) {
                facebookOnCallBack.onError();
            }
        }
    }
}
