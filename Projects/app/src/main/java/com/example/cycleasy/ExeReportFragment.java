package com.example.cycleasy;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Fragment class for activities in exercise report page
 */
public class ExeReportFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_exe_report, container, false);
        ImageButton gobackBut=(ImageButton)view.findViewById(R.id.rep_gobackbutton);

        //go back to exercise fragment
        gobackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction tr_exercise=getFragmentManager().beginTransaction();
                ExerciseFragment exerciseFragment=new ExerciseFragment();
                tr_exercise.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
                tr_exercise.addToBackStack(null);
                tr_exercise.replace(R.id.fragment_container, exerciseFragment, "EXERCISE FRAGMENT").commit();
            }
        });

        TextView notiText=(TextView)view.findViewById(R.id.savednotification);

        //go to cycling history fragment
        notiText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction tr_cychist=getFragmentManager().beginTransaction();
                subFragment_cychis cychisFrag=new subFragment_cychis();
                tr_cychist.addToBackStack(null);
                tr_cychist.replace(R.id.fragment_container,cychisFrag, "CYCLING HISTORY FRAGMENT").commit();
            }
        });
        return view;
    }

}
