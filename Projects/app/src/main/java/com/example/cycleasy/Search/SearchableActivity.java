package com.example.cycleasy.Search;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.cycleasy.DirectionParser;
import com.example.cycleasy.R;
import com.example.cycleasy.RouteFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchableActivity extends AppCompatActivity //implements OnMapReadyCallback
{
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter adapter;

    private static final String TAG = "SearchableActivity";
    private GoogleMap mMap;
    ArrayList<LatLng> listPoints;
    private Boolean mLocationPermissionGranted = true;
    private static final float DEFAULT_ZOOM = 15f;
    private EditText mSearchText;
    private Address address;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlayout);
        SearchView searchView=findViewById(R.id.searchview);
        //mMapView=(MapView)findViewById(R.id.search_mapview);
        //set different queryhint texts for searches in different fragments
        //initGoogleMap(savedInstanceState);
        //mMapView.getMapAsync(this);
        final Intent thisIntent=getIntent();

        String sender=thisIntent.getExtras().getString("Sender");
        switch (sender){
            case "RacksSearchBar":
                searchView.setQueryHint("Where to park?");
                //For searching rack, display directly
                break;
            case "topRouteSearchbar":
                searchView.setQueryHint("Starting point?");
                // For searching route,
                // display and move camera to the startpt
                // then user is supposed to long click on the position he wants to start with
                break;
            case "botRouteSearchbar":
                searchView.setQueryHint("Destination?");
                //display and move camera to the endpt
                // then user is supposed to long click on the position he wants to end with
                break;
        }

        //force keyboard to popup automatically
        searchView.setIconified(false);
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);

        //RECEIVING QUERY
        //Listener for users' actions in the searchView


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            //when query is submitted
            public boolean onQueryTextSubmit(String query) {
                //TODO for search suggestions, currently not working...
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getBaseContext(),
                        SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
                suggestions.saveRecentQuery(query, null);
                //for performing real search in database or via API by doMySearch
                doMySearch(query);
                //pass query data back to fragment for display
                thisIntent.putExtra("query",query);
                setResult(RESULT_OK,thisIntent);
                finish();
                return false;
            }

            @Override
            //TODO when query is changed
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });



        //Intent intent = getIntent();
        //if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

        //String query = intent.getStringExtra(SearchManager.QUERY);
        //saving queries for recent suggestions in later searches

        //.}

        //TODO for presenting search result
        ListView listView=findViewById(R.id.listview);
        //list.add()
        //Adapter for search result presentation
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,list);
        listView.setAdapter(adapter);

    }


    protected Boolean doMySearch(String query){
        //TODO by backend, actual search in database
            //address = geoLocate(query);
        return true;
    }
/*
    // When map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            Toast.makeText(this, "get location",Toast.LENGTH_SHORT).show();
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
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
                        SearchableActivity.TaskRequestDirections taskRequestDirections = new SearchableActivity.TaskRequestDirections();
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

        String searchString = query;

        Geocoder geocoder = new Geocoder(SearchableActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString,1);
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
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void getDeviceLocation(){
        Toast.makeText(this, "get location",Toast.LENGTH_SHORT).show();
        Log.d(TAG,"getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
                            Toast.makeText(SearchableActivity.this,"unable to get current location",Toast.LENGTH_SHORT).show();
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
            SearchableActivity.TaskParser taskParser = new SearchableActivity.TaskParser();
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
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

*/

}