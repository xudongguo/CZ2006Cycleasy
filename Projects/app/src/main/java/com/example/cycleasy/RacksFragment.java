package com.example.cycleasy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;
import com.example.cycleasy.Search.SearchableActivity;
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
import java.util.Random;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class RacksFragment extends Fragment implements OnMapReadyCallback {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter adapter;
    private static final String TAG = "RacksActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    //true if there is message sent from other fragment, false if otherwise
    private boolean messagepending=false;
    private MapView mMapView;
    private GoogleMap mMap;
    ArrayList<LatLng> listPoints;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionGranted = true;
    private static final float DEFAULT_ZOOM = 15f;
    Context thiscontext;
    private String startpt,endpt;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_racks, null);
        thiscontext = container.getContext();
        final TextView searchbar = (TextView) view.findViewById((R.id.racks_searchbar));
        final ImageView rackbutton = (ImageView) view.findViewById(R.id.racks_racklocation);
        mMapView = (MapView) view.findViewById(R.id.racks_mapview);

        //show rack information bubble popup
        final BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(getContext()).inflate(R.layout.bubblelayout, null);
        final PopupWindow popupWindow = BubblePopupHelper.create(getContext(), bubbleLayout);
        final FloatingActionButton mylocatBUT=(FloatingActionButton) view.findViewById(R.id.racks_locationBut);

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

        mylocatBUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
        // SearchableActivity myActivity=(SearchableActivity)getActivity();
        // searchbar.setText(myActivity.getHintText());

        initGoogleMap(savedInstanceState);
        mMapView.getMapAsync(this);


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
                geoLocate(displaytxt);
            }
            else if (resultCode== RESULT_CANCELED){
                //if activity closed abnormally
            }
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServiceOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if (available == ConnectionResult.SUCCESS) {
            //fine
            Log.d(TAG, "isServicesOK:");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //
            Log.d(TAG, "isServicesOK: ");
            //Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getContext(), available, ERROR_DIALOG_REQUEST);
            //dialog.show();
        } else {
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
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
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
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

            //Fx: find bicycling directions between 2 points
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    //Reset marker when already 2
                    if (listPoints.size() == 2) {
                        listPoints.clear();
                        mMap.clear();
                    }
                    //Save first point selected
                    listPoints.add(latLng);
                    //Create marker
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    if (listPoints.size() == 1) {
                        //Add first marker to the map
                        Log.d(TAG, "onMapLongClick: adding first point");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else {
                        //Add second market to the map
                        Log.d(TAG, "onMapLongClick: adding second point");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                    mMap.addMarker(markerOptions);
                    //request get direction code bellow
                    if (listPoints.size() == 2) {
                        Log.d(TAG, "onMapLongClick: Searching now");
                        String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                        RacksFragment.TaskRequestDirections taskRequestDirections = new RacksFragment.TaskRequestDirections();
                        taskRequestDirections.execute(url);
                    }
                }
            });
        }
    }
    // get request about starting and ending points and convert to url, and use url to request from google map api
    private String getRequestUrl(LatLng origin,LatLng dest){
        // value of origin
        String str_org = "origin="+origin.latitude+","+origin.longitude;
        // value of destination
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        // set value anble the sensor
        //String sensor = "sensor=false";
        // Mode for finding direction
        String mode = "mode=bicycling";
        // Build the full param
        String api_key = "&key=AIzaSyDW_vO8Zofe8at0AwHE-91_Pa1ZQFTijr8";

        String param = str_org +"&"+str_dest+"&"+mode+api_key;
        // Output format
        String output = "json";
        // Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param;
        Log.d(TAG, "getRequestUrl: "+url);
        return url;
    }

//    private void init(String query){
//        Log.d(TAG, "init: initializing");
//
//        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                        ||actionId == EditorInfo.IME_ACTION_DONE
//                        ||keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        ||keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
//
//                    //execute our method for searching
//                    geoLocate(query);
//                    hideSoftKeyboard();
//                }
//                return false;
//            }
//        });
//    }

    private Address geoLocate(String query){
        Log.d(TAG, "geoLocate: geolocating");

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(query,1);
        }catch(IOException e){
            Log.e(TAG,"geolocate: IOException"+e.getMessage());
        }

        if(list.size()>0){

            Log.d(TAG, "geoLocate: found something");
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: "+address.toString());
            //Toast.makeText(this,address.toString(),Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));

            return address;
        }
        return null;
    }

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

    // get direction, using httpurlconnection
    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }
    // create a AsyncTask to call request direction
    public class TaskRequestDirections extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings){
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            //Parse json here
            Log.d(TAG, "onPostExecute: calling taskparser");
            RacksFragment.TaskParser taskParser = new RacksFragment.TaskParser();
            taskParser.execute(s);

        }
    }

    public class TaskParser extends AsyncTask<String,Void,List<List<HashMap<String,String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: calling json");
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionParser directionParser = new DirectionParser();
                routes = directionParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //super.onPostExecute(lists);
            // Get list route and display it into the map
            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    Log.d(TAG, "onPostExecute: " + lat + " " + lon);
                    points.add(new LatLng(lat, lon));
                }

                Log.d(TAG, "onPostExecute: drawing line now");
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Log.d(TAG, "onPostExecute: Direction not found!");
                Toast.makeText(getContext(), "Direction not found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}