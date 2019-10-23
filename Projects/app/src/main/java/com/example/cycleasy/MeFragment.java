package com.example.cycleasy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class MeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_me, null);
        Button profilebutton= (Button)view.findViewById(R.id.editprofile);
        Button favbutton= (Button)view.findViewById(R.id.favpath);
        Button cychisbutton=(Button)view.findViewById(R.id.cychistory);

        profilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction frProfile=getFragmentManager().beginTransaction();
                frProfile.replace(R.id.fragment_container, new subFragment_profile());
                frProfile.commit();
            }
        });
        favbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction frFav=getFragmentManager().beginTransaction();
                frFav.replace(R.id.fragment_container, new subFragment_favpath());
                frFav.commit();
            }
        });
        cychisbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction frCyc=getFragmentManager().beginTransaction();
                frCyc.replace(R.id.fragment_container, new subFragment_cychis());
                frCyc.commit();
            }
        });

return view;
    }
    }

