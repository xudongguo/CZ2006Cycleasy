package com.example.cycleasy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class RouteFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_route, null);
        final TextView topsearchbar=(TextView) view.findViewById((R.id.route_topsearchhbar));
        final TextView botsearchbar=(TextView) view.findViewById((R.id.route_botsearchbar));
        topsearchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Call SearchableActivity to handle the search
                Intent intent = new Intent(view.getContext(), SearchableActivity.class);
                intent.putExtra("Sender", "topRouteSearchbar");
                startActivity(intent);
            }
        });

        botsearchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Call SearchableActivity to handle the search
                Intent intent = new Intent(view.getContext(), SearchableActivity.class);
                intent.putExtra("Sender", "botRouteSearchbar");
                startActivity(intent);
            }
        });

        FloatingActionButton favBut=(FloatingActionButton)view.findViewById(R.id.route_favBut);

        favBut.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO when favorite button is clicked
            public void onClick(View view) {
                Toast myToast=Toast.makeText(getContext(),"Added to favorite", Toast.LENGTH_LONG );
                myToast.show();

            }
        });
        return view;

    }

}