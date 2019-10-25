package com.example.cycleasy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cycleasy.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;

/**
 * Fragment class for activities in me page
 */
public class MeFragment extends Fragment implements View.OnClickListener {

    Button profileButton;
    private Fragment fragment;

    /**
     * To load a particular fragment
     * @param fragment fragment to be loaded
     * @return return false if fragment is null, true otherwise
     */
    private boolean loadfragment(Fragment fragment) {
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        profileButton = view.findViewById(R.id.editprofilebtn);
        InitView();
        return view;
    }

    private void InitView() {
        profileButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editprofilebtn:
                loadfragment(new profile_subFragment());
                break;
        }
    }

    public static class favpath_subFragment extends Fragment{
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_favpath, null);
        }
    }

    public static class profile_subFragment extends Fragment {

        Button logoutButton;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.subfragment_profile, null);
            logoutButton = view.findViewById(R.id.logoutBtn);
            initView();
            return view;
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

    }

    public static class cyclinghistory_subFragment {
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_cyclinghistory, null);
        }
    }

    void onBackPressed() {

    }
}

