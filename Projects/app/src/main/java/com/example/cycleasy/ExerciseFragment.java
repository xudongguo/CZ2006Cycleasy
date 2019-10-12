package com.example.cycleasy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExerciseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_exercise, null);
        FloatingActionButton startBut=(FloatingActionButton) view.findViewById(R.id.exe_startBut);
        FloatingActionButton stopBut=(FloatingActionButton)view.findViewById(R.id.exe_stopBut);


        startBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO for timer and tracker when start button is clicked
            }
        });

        stopBut.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //TODO for timer and tracker when stop button is long clicked
                //show exercise report in exercise report fragment
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                ExeReportFragment reportFragment=new ExeReportFragment();
                transaction.setCustomAnimations(R.anim.slide_in_bott, R.anim.slide_out_bott);
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment_container, reportFragment, "EXERCISE REPORT").commit();
                return false;
            }
        });

        return view;
    }
}
