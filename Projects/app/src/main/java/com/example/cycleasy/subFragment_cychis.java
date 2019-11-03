package com.example.cycleasy;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.core.view.MarginLayoutParamsCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment class for activities in cycling history page
 */
public class subFragment_cychis extends Fragment {

    private String timetxt,distxt,speedtxt, datetxt;
    private static boolean hasmessage=false;
    private Context thiscontext=getContext();
    private LinearLayout scrolllayout;
    private static ArrayList<String> metriclist=new ArrayList<String>();
    private static int listcount=0;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.subfragment_cyclinghistory, null);
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.hist_scroller);
        scrolllayout=(LinearLayout) view.findViewById(R.id.hist_scroll_layout);
        //XmlPullParser parser = getResources().getXml();
        //AttributeSet attributes = Xml.asAttributeSet(parser);

        //check if there is message, add the message to the arraylist
        if (hasmessage){
            timetxt=getArguments().getString("cycling time");
            distxt=getArguments().getString("cycling distance");
            speedtxt=getArguments().getString("cycling speed");
            datetxt =getArguments().getString("report date");
            metriclist.add(timetxt+"|"+distxt+"|"+speedtxt+"|"+datetxt);

            hasmessage=false;

        }
        //create record for each metric sent
        for (int temp=0;temp<metriclist.size();temp++){
            String[] tempinfo = metriclist.get(temp).split("[|]");
            String temptime=tempinfo[0];
            String tempdist=tempinfo[1];
            String tempspeed=tempinfo[2];
            String tempdate=tempinfo[3];
            createRecord(temptime,tempdist,tempspeed, tempdate);
        }


        return view;
    }

    protected void setMessageStatus(boolean status){
        hasmessage=status;
    }


    //create record based on exercise record
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void createRecord(String time, String distance, String speed, String date){
        // new record layout
        LinearLayout newrecord= new LinearLayout(getActivity());
        newrecord.setOrientation(LinearLayout.VERTICAL);
        newrecord.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin=5;
        params.bottomMargin=20;

        newrecord.setLayoutParams(params);

        //date row
        Button datebtn=new Button(getActivity());
        datebtn.setText(date);
        datebtn.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        ViewGroup.LayoutParams btnparams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        datebtn.setLayoutParams(btnparams);

        //distance row
        LinearLayout distRec=new LinearLayout(getActivity());
        distRec.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams metricparams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        metricparams.leftMargin=20;
        metricparams.topMargin=30;
        distRec.setLayoutParams(metricparams);


        ImageView distimg=new ImageView(getActivity());
        LinearLayout.LayoutParams imgparam=new LinearLayout.LayoutParams(80,80);
        distimg.setBackground(getResources().getDrawable(R.drawable.icon_distancecycled));
        imgparam.leftMargin=15;
        imgparam.gravity=Gravity.CENTER;
        distimg.setLayoutParams(imgparam);

        TextView disttitle=new TextView(getActivity());
        disttitle.setText(getResources().getString(R.string.exe_disttext));
        LinearLayout.LayoutParams txtparam=new LinearLayout.LayoutParams(100,80);
        txtparam.gravity=Gravity.CENTER_VERTICAL;
        txtparam.weight=1;
        disttitle.setPadding(10,10,5,5);
        disttitle.setTextAlignment(txtparam.gravity);
        disttitle.setTextColor(Color.BLACK);
        disttitle.setTextSize(15);
        disttitle.setLayoutParams(txtparam);

        TextView distnum=new TextView(getActivity());
        LinearLayout.LayoutParams numparam=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        numparam.weight=1;
        distnum.setText(distance);
        distnum.setPadding(5,10,5,5);
        distnum.setTextColor(Color.BLACK);
        distnum.setTextSize(15);
        distnum.setLayoutParams(numparam);


        distRec.addView(distimg);
        distRec.addView(disttitle);
        distRec.addView(distnum);

        //time row
        LinearLayout timeRec=new LinearLayout(getActivity());
        timeRec.setOrientation(LinearLayout.HORIZONTAL);
        timeRec.setLayoutParams(metricparams);


        ImageView timeimg=new ImageView(getActivity());
        timeimg.setBackground(getResources().getDrawable(R.drawable.time_icon));
        timeimg.setLayoutParams(imgparam);

        TextView timetitle=new TextView(getActivity());
        timetitle.setText(getResources().getString(R.string.exe_durationcycled));
        timetitle.setPadding(10,10,5,5);
        timetitle.setTextAlignment(txtparam.gravity);
        timetitle.setTextColor(Color.BLACK);
        timetitle.setTextSize(15);
        timetitle.setLayoutParams(txtparam);

        TextView timenum=new TextView(getActivity());
        timenum.setText(time);
        timenum.setPadding(5,10,5,5);
        timenum.setTextColor(Color.BLACK);
        timenum.setTextSize(15);
        timenum.setLayoutParams(numparam);


        timeRec.addView(timeimg);
        timeRec.addView(timetitle);
        timeRec.addView(timenum);

        //speed row
        LinearLayout speedRec=new LinearLayout(getActivity());
        timeRec.setOrientation(LinearLayout.HORIZONTAL);
        timeRec.setLayoutParams(metricparams);


        ImageView speedimg=new ImageView(getActivity());
        speedimg.setBackground(getResources().getDrawable(R.drawable.icon_speedcycled));
        LinearLayout.LayoutParams speedimgparam=new LinearLayout.LayoutParams(80,80);
        speedimgparam.leftMargin=32;
        speedimgparam.topMargin=10;
        speedimgparam.gravity=Gravity.CENTER;
        speedimg.setLayoutParams(speedimgparam);

        TextView speedtitle=new TextView(getActivity());
        speedtitle.setText(getResources().getString(R.string.exe_avespeed));
        speedtitle.setPadding(12,20,5,5);
        speedtitle.setTextAlignment(txtparam.gravity);
        speedtitle.setTextColor(Color.BLACK);
        speedtitle.setTextSize(15);
        speedtitle.setLayoutParams(txtparam);

        TextView speednum=new TextView(getActivity());
        speednum.setText(speed);
        speednum.setPadding(5,20,5,5);
        speednum.setTextColor(Color.BLACK);
        speednum.setTextSize(15);
        speednum.setLayoutParams(numparam);


        speedRec.addView(speedimg);
        speedRec.addView(speedtitle);
        speedRec.addView(speednum);

        newrecord.addView(datebtn);
        newrecord.addView(distRec);
        newrecord.addView(timeRec);
        newrecord.addView(speedRec);
        scrolllayout.addView(newrecord);



        }
}

