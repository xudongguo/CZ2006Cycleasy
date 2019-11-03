package com.example.cycleasy;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment class for activities in exercise report page
 */
public class ExeReportFragment extends Fragment {

    private String timetxt,distxt,speedtxt, datetxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_exe_report, container, false);
        ImageButton gobackBut=(ImageButton)view.findViewById(R.id.rep_gobackbutton);
        final TextView cycdist=view.findViewById(R.id.rep_distnum);
        TextView cyctime=view.findViewById(R.id.rep_durationnum);
        TextView cycspeed=view.findViewById(R.id.rep_speednum);
        TextView reportdate=view.findViewById(R.id.reportDate);

        //get the cycling metrics from exercise fragment
        timetxt=getArguments().getString("cycling time");
        distxt=getArguments().getString("cycling distance");
        speedtxt=getArguments().getString("cycling speed");
        datetxt = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());



        //set the report information
        cycdist.setText(distxt);
        cyctime.setText(timetxt);
        cycspeed.setText(speedtxt);
        reportdate.setText(datetxt);

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
                Bundle bundle=new Bundle();
                bundle.putString("cycling distance", distxt);
                bundle.putString("cycling time", timetxt );
                bundle.putString("cycling speed",speedtxt);
                bundle.putString("report date", datetxt);
                FragmentTransaction tr_cychist=getFragmentManager().beginTransaction();
                subFragment_cychis cychisFrag=new subFragment_cychis();
                cychisFrag.setArguments(bundle);
                cychisFrag.setMessageStatus(true);
                tr_cychist.addToBackStack(null);
                tr_cychist.replace(R.id.fragment_container,cychisFrag, "CYCLING HISTORY FRAGMENT").commit();
            }
        });
        return view;
    }

}
