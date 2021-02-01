package com.example.cycleasy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Fragment class for activities in favorite path page
 */
public class subFragment_favpath extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.subfragment_favpath, null);

        //find "take this path" button
        Button takepathBut=(Button) view.findViewById(R.id.goCylc_but);
        //get starting point of a favorite path
        TextView startptView=(TextView) view.findViewById(R.id.start_path);
        final String startpt=startptView.getText().toString();
        //get destination of a favorite path
        TextView endptView=(TextView) view.findViewById(R.id.end_path);
        final String endpt=endptView.getText().toString();

        takepathBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //pass starting point and destination to search in route page
                Bundle bundle=new Bundle();
                bundle.putString("Start Point", startpt);
                bundle.putString("End Point", endpt);
                //transit to route fragment
                RouteFragment routefrag=new RouteFragment();
                routefrag.setArguments(bundle);
                routefrag.setMessageSignal(true);
                FragmentTransaction tr_exercise=getFragmentManager().beginTransaction();
                tr_exercise.replace(R.id.fragment_container, routefrag, "ROUTE FRAGMENT").commit();
            }
        });


        return view;}

}

