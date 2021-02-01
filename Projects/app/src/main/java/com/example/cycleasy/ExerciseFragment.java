package com.example.cycleasy;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment class for activities in exercise page
 */
public class ExerciseFragment extends Fragment implements OnMapReadyCallback{
    private static final String TAG = "ExerciseActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    //true if there is message sent from other fragment, false if otherwise
    private boolean messagepending=false, bound=false, running=false;
    private long pauseoffset;
    private double  cyctime, cycdist=0,cycspeed=0;
    private MapView mMapView;
    private GoogleMap mMap;
    private ArrayList<LatLng> listPoints;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ServiceConnection mServiceConnection;
    private DistanceTraveledService mDistanceTraveledService;
    private Boolean mLocationPermissionGranted = true;
    private static final float DEFAULT_ZOOM = 15f;
    private Context thiscontext;
    private TextView distancetxt,speedtxt;
    private Chronometer mychrono;
    private String startpt,endpt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_exercise, null);
        thiscontext = container.getContext();
        FloatingActionButton startBut=(FloatingActionButton) view.findViewById(R.id.exe_startBut);
        FloatingActionButton stopBut=(FloatingActionButton)view.findViewById(R.id.exe_stopBut);
        FloatingActionButton mylocatBut=(FloatingActionButton)view.findViewById(R.id.exe_loactionBut);
        distancetxt=(TextView)view.findViewById(R.id.distnum);
        speedtxt=(TextView)view.findViewById(R.id.speednumber);
        mychrono=(Chronometer)view.findViewById(R.id.exe_chronometer);
        mMapView = (MapView) view.findViewById(R.id.exe_mapview);


        //Service connection for distance tracker
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DistanceTraveledService.DistanceTravelBinder distanceTravelBinder = (DistanceTraveledService.DistanceTravelBinder)service;
                mDistanceTraveledService = distanceTravelBinder.getBinder();
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        };

        //Start exercise button activity
        startBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!running){
                    mychrono.setBase(SystemClock.elapsedRealtime()-pauseoffset);
                    mychrono.start();
                    displayMetrics();
                    running=true;}
                else{
                    mychrono.stop();
                    pauseoffset=SystemClock.elapsedRealtime()-mychrono.getBase();
                    running=false;
            }

        }}
        );
        stopBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(thiscontext,"hold to end exercise",Toast.LENGTH_LONG).show();
            }
        });
        //stop exercise button activity
        stopBut.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //send cycling metrics to exercise report fragment for display
                Bundle bundle=new Bundle();
                bundle.putString("cycling distance", String.format("%.2fKM",cycdist));
                bundle.putString("cycling time", mychrono.getText().toString());
                bundle.putString("cycling speed",String.format("%.2fKM/H",cycspeed));
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                ExeReportFragment reportFragment=new ExeReportFragment();
                reportFragment.setArguments(bundle);
                transaction.setCustomAnimations(R.anim.slide_in_bott, R.anim.slide_out_bott);
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment_container, reportFragment, "EXERCISE REPORT").commit();

                //reset the metrics displayed
                distancetxt.setText(getResources().getString(R.string.defaultdistance));
                speedtxt.setText(getResources().getString(R.string.defaultspeed));
                mychrono.setBase(SystemClock.elapsedRealtime());
                pauseoffset=0;

                return false;
            }
        });


        mylocatBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
        // SearchableActivity myActivity=(SearchableActivity)getActivity();
        // searchbar.setText(myActivity.getHintText());

        //initiate google map
        initGoogleMap(savedInstanceState);
        mMapView.getMapAsync(this);


        return view;
    }

    //display exercise metrics
    private void displayMetrics() {
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mDistanceTraveledService != null){
                        cycdist = mDistanceTraveledService.getDistanceTraveled()/100000;
                    }
                    cyctime=(SystemClock.elapsedRealtime()-mychrono.getBase())/1000;
                    cycspeed=cycdist/(cyctime/3600);
                    speedtxt.setText(String.format("%.2fKM/H",cycspeed));
                    distancetxt.setText(String.format("%.2fKM",cycdist));
                    handler.postDelayed(this, 1000);
                    Log.d("time", String.valueOf(cyctime)+"S");
                    Log.d("displaydistance", String.valueOf(cycdist)+"KM");
                    Log.d("speed",String.valueOf(cycspeed)+"KM/H");


                }
            });

        }



    @Override

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        Intent intent = new Intent (thiscontext, DistanceTraveledService.class);
        thiscontext.bindService(intent,mServiceConnection,Context.BIND_AUTO_CREATE);
        Log.d("onStart", "binded");
    }


    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        if(bound){
            thiscontext.unbindService(mServiceConnection);
            Log.d("onStop", "unbind");
           bound = false;
        }
    }


    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }


    /**
     * called when map is initialized and ready for geolocating and route searching
     * @param googleMap non null googleMap instance passed in for performing map functions
     */
    // When map is ready
    // When map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getContext(), "Map is Ready", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(thiscontext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(thiscontext,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            //Fx: find a location.


        }
    }

    /**
     * move camera towards a point with latitude and longtitude parsed in LatLng, and camera zoom of zoom and location name title
     * @param latLng LatLng object containing latitude and longtitude of an address
     * @param zoom camera zoom value
     * @param title location name
     */
    private void moveCamera(LatLng latLng, float zoom,String title){
        Log.d(TAG, "moveCamera: moving the camera to: "+latLng.latitude +", lng: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        if(!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard(){
        Log.d(TAG, "hideSoftKeyboard: hiding");
        // this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Get the location of this device
     */
    private void getDeviceLocation(){
        Log.d(TAG,"getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(thiscontext);

        try{
            if(mLocationPermissionGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: foundLocation!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(),"unable to get current location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException"+e.getMessage());
        }
    }

}
