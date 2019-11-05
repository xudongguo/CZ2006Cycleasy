package com.example.cycleasy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cycleasy.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Fragment class for activities in me page
 */
public class MeFragment extends Fragment{

    private Fragment fragment;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, null);

        TextView usernameText = view.findViewById(R.id.textView2);
        String s;
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser().getDisplayName() == null) {
            s = "Hi Guest!";
        } else {
            s = "Hi " + firebaseAuth.getCurrentUser().getDisplayName() + "!";
        }
        usernameText.setText(s);

        cychisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadfragment(new subFragment_cychis());
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadfragment(new subFragment_profile());
            }
        });
        favpathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadfragment(new subFragment_favpath());
            }
        });
        return view;

    }

    public static class favpath_subFragment extends Fragment{
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_favpath, null);
        }
    }

    public static class profile_subFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_profile, null);
        }
    }

    public static class cyclinghistory_subFragment {
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_cyclinghistory, null);
        }
    }
    }

