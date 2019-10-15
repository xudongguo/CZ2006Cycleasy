package com.example.cycleasy;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class RacksFragment extends Fragment {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter adapter;




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_racks, null);
        final TextView searchbar = (TextView) view.findViewById((R.id.racks_searchbar));
        final ImageView rackbutton = (ImageView) view.findViewById(R.id.racks_racklocation);

        //show rack information bubble popup
        final BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(getContext()).inflate(R.layout.bubblelayout, null);
        final PopupWindow popupWindow = BubblePopupHelper.create(getContext(), bubbleLayout);
        final Random random = new Random();

        rackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int[] location = new int[2];

                bubbleLayout.setArrowDirection(ArrowDirection.BOTTOM);
                popupWindow.showAsDropDown(rackbutton,50,-250,Gravity.CENTER);
                //view.getLocationInWindow(location);
                //popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], view.getHeight() + location[1]);
            }
        });


        //Call SearchableActivity to handle the search
        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(view.getContext(), SearchableActivity.class);
                intent.putExtra("Sender", "RacksSearchBar");
                startActivityForResult(intent,1 );


            }
        });
        // SearchableActivity myActivity=(SearchableActivity)getActivity();
        // searchbar.setText(myActivity.getHintText());


        return view;

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1) {
            if (resultCode == RESULT_OK) {
                //display query text on searchbar
                String displaytxt = data.getExtras().getString("query");
                TextView searchbar = (TextView) getView().findViewById(R.id.racks_searchbar);
                searchbar.setText(displaytxt);
            }
            else if (resultCode== RESULT_CANCELED){
                //if activity closed abnormally
            }
        }
    }
}