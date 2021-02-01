package com.example.cycleasy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cycleasy.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Fragment class for activities in profile page
 */
public class subFragment_profile extends Fragment {
        //.onBackPressed();
        private Button logoutButton;
        public static final String LAST_TEXT = "Your username";
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.subfragment_profile, null);
            logoutButton = view.findViewById(R.id.logoutBtn);
            EditText usernameField= view.findViewById(R.id.username);


            //save username
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            usernameField.setText(pref.getString(LAST_TEXT, "Your username"));
            usernameField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    pref.edit().putString(LAST_TEXT, s.toString()).apply();



                }
            });

            //send username to me page for display
            initView();
            return  view;
        }

        private void initView() {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }
    //return view;}

    }
