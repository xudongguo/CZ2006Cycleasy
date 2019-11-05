package com.example.cycleasy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cycleasy.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

/**
 * Fragment class for activities in me page
 */
public class MeFragment extends Fragment{

    private Fragment fragment;
    private static boolean hasmessage=false;
    private static String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        Button cychisButton =view.findViewById(R.id.cychistorybtn);
        Button profileButton = view.findViewById(R.id.editprofilebtn);
        Button favpathButton =view.findViewById(R.id.favpathbtn);
        TextView usernamefield=view.findViewById(R.id.Me_username);

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

        //TODO set username if is has been changed

        return view;
    }

    /**
     * To load a particular fragment
     * @param fragment fragment to be loaded
     * @return return false if fragment is null, true otherwise
     */
    private boolean loadfragment(Fragment fragment) {
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    public void setMessageStatus(boolean status){
        hasmessage=status;
    }
}

