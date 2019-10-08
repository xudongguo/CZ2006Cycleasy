package com.example.cycleasy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RacksFragment extends Fragment {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_racks, null);
        final TextView searchbar = (TextView) view.findViewById((R.id.racks_searchbar));
        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Call SearchableActivity to handle the search
                Intent intent = new Intent(view.getContext(), SearchableActivity.class);
                intent.putExtra("Sender", "RacksSearchBar");
                startActivity(intent);


            }
        });


        // SearchableActivity myActivity=(SearchableActivity)getActivity();
        // searchbar.setText(myActivity.getHintText());


        return view;

    }

}