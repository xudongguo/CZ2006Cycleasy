package com.example.cycleasy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cycleasy.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Fragment class for activities in profile page
 */
public class subFragment_profile extends Fragment {
        //.onBackPressed();
        private Button logoutButton;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.subfragment_profile, null);
            logoutButton = view.findViewById(R.id.logoutBtn);
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
