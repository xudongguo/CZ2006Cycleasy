package com.example.cycleasy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class RouteFragment extends Fragment {
    //true if there is message sent from other fragment, false if otherwise
    private boolean messagepending=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, null);
        final TextView topsearchbar = (TextView) view.findViewById((R.id.route_topsearchhbar));
        final TextView botsearchbar = (TextView) view.findViewById((R.id.route_botsearchbar));

        //if there is message passed from subfragments, set the search information based on the message.
        if(messagepending){
        String startpt = getArguments().getString("Start Point");
        String endpt = getArguments().getString("End Point");
        if (!startpt.isEmpty() && !endpt.isEmpty()) {
            topsearchbar.setText(startpt);
            botsearchbar.setText(endpt);
        }}


        //top search bar activity
        topsearchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Call SearchableActivity to handle the search
                Intent intent = new Intent(view.getContext(), SearchableActivity.class);
                intent.putExtra("Sender", "topRouteSearchbar");
                startActivityForResult(intent, 1);
            }
        });

        //bottom search bar activity
        botsearchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Call SearchableActivity to handle the search
                Intent intent = new Intent(view.getContext(), SearchableActivity.class);
                intent.putExtra("Sender", "botRouteSearchbar");
                startActivityForResult(intent, 2);
            }
        });

        //share button activity
        final FloatingActionButton shareBut = (FloatingActionButton) view.findViewById(R.id.route_shareBut);
        shareBut.hide();
        shareBut.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO when share button is clicked
            public void onClick(View view) {

            }
        });

        //favorite button activity
        final FloatingActionButton favBut = (FloatingActionButton) view.findViewById(R.id.route_favBut);
        favBut.hide();
        favBut.setOnClickListener(new View.OnClickListener() {
            boolean favButflag = true;

            @Override

            //TODO when favorite button is clicked or unclicked
            public void onClick(View view) {


                if (favButflag) {

                    favBut.setImageDrawable(getResources().getDrawable(R.drawable.icon_favfilled));
                    favButflag = false;
                    Toast myToast = Toast.makeText(getContext(), "Added to favorite", Toast.LENGTH_LONG);
                    myToast.show();

                } else if (!favButflag) {

                    favBut.setImageDrawable(getResources().getDrawable(R.drawable.icon_favempty));
                    favButflag = true;
                    Toast myToast = Toast.makeText(getContext(), "Removed from favorite", Toast.LENGTH_LONG);
                    myToast.show();
                }
            }
        });


        //Find button activity
        FloatingActionButton findBut = (FloatingActionButton) view.findViewById(R.id.route_findbutton);
        findBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO for when "FIND" BUTTON IS CLICKED
                favBut.show();
                shareBut.show();
            }
        });


        return view;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request sent from topsearchbar
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //display query text on top searchbar
                String displaytxt = data.getExtras().getString("query");
                TextView topsearchbar = (TextView) getView().findViewById(R.id.route_topsearchhbar);
                topsearchbar.setText(displaytxt);
            } else if (resultCode == RESULT_CANCELED) {
                //if activity closed abnormally
            }
        }
        //request sent from bottomsearchbar
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                //display query text on bottom searchbar
                String displaytxt = data.getExtras().getString("query");
                TextView botsearchbar = (TextView) getView().findViewById(R.id.route_botsearchbar);
                botsearchbar.setText(displaytxt);
            } else if (resultCode == RESULT_CANCELED) {
                //if activity closed abnormally
            }
        }
    }


    //set the state of messagepending
    public void setMessageSignal(boolean signal){
        messagepending=signal;
    }
}