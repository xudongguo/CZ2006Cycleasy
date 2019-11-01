package com.example.cycleasy;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.LocalSocketAddress;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Fragment class for activities in route page
 */
public class RouteFragment extends Fragment implements OnMapReadyCallback {


    //true if there is message sent from other fragment, false if otherwise
    private boolean messagepending = false;
    private MapView mMapView;
    private static GoogleMap mMap;
    ArrayList<LatLng> listPoints = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Boolean mLocationPermissionGranted = false, gps_enabled=false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String TAG = "RouteActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final float DEFAULT_ZOOM = 15f;
    private static Context thiscontext;
    private String startpt, endpt;
    private Address startadd, endadd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, null);
        thiscontext = container.getContext();
        final TextView topsearchbar = (TextView) view.findViewById((R.id.route_topsearchhbar));
        final TextView botsearchbar = (TextView) view.findViewById((R.id.route_botsearchbar));
        mMapView = (MapView) view.findViewById(R.id.route_mapview);
        getLocationPermission();
        //check if location is enabled
        LocationManager lm = (LocationManager)thiscontext.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gps_enabled) {
            //prompt user to enable gps in settings
            showSettingsAlert();
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        initGoogleMap(savedInstanceState);
        //if there is message passed from subfragments, set the search information based on the message.
        if (messagepending) {
            Log.d(TAG, "onmessagepending: message pending");
            startpt = getArguments().getString("Start Point");
            endpt = getArguments().getString("End Point");
            if (!startpt.isEmpty() && !endpt.isEmpty()) {
                topsearchbar.setText(startpt);
                botsearchbar.setText(endpt);
                messagepending = false;

            }


        }


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

                favBut.show();
                shareBut.show();
                //TODO for when "FIND" BUTTON IS CLICKED
                if (listPoints.size() == 2) {
                    listPoints.clear();
                    mMap.clear();
                }
                if (startadd == null)
                    Toast.makeText(thiscontext, "starting point not entered", Toast.LENGTH_SHORT).show();
                else if (endadd == null)
                    Toast.makeText(thiscontext, "destination not entered", Toast.LENGTH_SHORT).show();
                else {
                    //Reset marker when already 2

                    //Save first point selected
                    LatLng startLL = new LatLng(startadd.getLatitude(), startadd.getLongitude());
                    LatLng endLL = new LatLng(endadd.getLatitude(), endadd.getLongitude());
                    listPoints.add(startLL);
                    listPoints.add(endLL);
                    moveCamera(endLL,DEFAULT_ZOOM,"destination");
                    //Create marker
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(endLL);

                 if (listPoints.size() == 1) {
                    //Add first marker to the map
                    Log.d(TAG, "onClickFindButton: adding first point");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    //Add second market to the map
                    Log.d(TAG, "onClickFindButton: adding second point");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                    mMap.addMarker(markerOptions);
                    //request get direction code bellow
                    if (listPoints.size() == 2) {
                        Log.d(TAG, "onClickFindButton: Searching now");
                        String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                        RouteFragment.TaskRequestDirections taskRequestDirections = new RouteFragment.TaskRequestDirections();
                        taskRequestDirections.execute(url);
                    }

                }
            }
        });


        //mylocation button activity
        FloatingActionButton mylocatBut = (FloatingActionButton) view.findViewById(R.id.route_mylocationBut);
        mylocatBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();


            }
        });


        return view;
    }

    /**
     * Method to be called when SearchableActivity finished
     * @param requestCode request code to be validated with SearchableAvtivity
     * @param resultCode result code to be validated with SearchableActivity
     * @param data data received from the previous intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request sent from topsearchbar
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //display query text on top searchbar
                String displaytxt = data.getExtras().getString("query");
                TextView topsearchbar = (TextView) getView().findViewById(R.id.route_topsearchhbar);
                topsearchbar.setText(displaytxt);
                startpt = displaytxt;
                startadd = geoLocate(displaytxt);
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
                endpt = displaytxt;
                endadd = geoLocate(displaytxt);

            } else if (resultCode == RESULT_CANCELED) {
                //if activity closed abnormally
                Log.d(TAG, "resultcode: result cancelled, messaage receive failure");
            }
        }
    }

    /**
     * Set the mesasagesignal to a boolean value
     * @param signal true if there is message sent and pending, false otherwise
     */
    //set the state of messagepending
    public void setMessageSignal(boolean signal) {
        messagepending = signal;
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

    private void initGoogleMap(Bundle savedInstanceState) {
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getContext(), "Map is Ready", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted  && gps_enabled) {
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
           /* mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
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
                        RouteFragment.TaskRequestDirections taskRequestDirections = new RouteFragment.TaskRequestDirections();
                        taskRequestDirections.execute(url);
                    }
                }
            });*/
        }
    }

    // get request about starting and ending points and convert to url, and use url to request from google map api
    private String getRequestUrl(LatLng origin, LatLng dest) {
        // value of origin
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        // value of destination
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // set value anble the sensor
        //String sensor = "sensor=false";
        // Mode for finding direction
        String mode = "mode=bicycle";
        // Build the full param
        String api_key = "&key=AIzaSyDW_vO8Zofe8at0AwHE-91_Pa1ZQFTijr8";

        String param = str_org + "&" + str_dest + "&" + mode + api_key;
        // Output format
        String output = "json";
        // Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        Log.d(TAG, "getRequestUrl: " + url);
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

    /**
     * locating a geographical location from a location name of type String, and move the camera to that location
     * @param query query text containing the name of the location
     * @return Address of the location
     */
    private Address geoLocate(String query) {
        Log.d(TAG, "geoLocate: geolocating");

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(query, 1);
        } catch (IOException e) {
            Log.e(TAG, "geolocate: IOException" + e.getMessage());
        }

        if (list.size() > 0) {

            Log.d(TAG, "geoLocate: found something");
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this,address.toString(),Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));

            return address;
        }
        return null;
    }

    /**
     * move camera towards a point with latitude and longtitude parsed in LatLng, and camera zoom of zoom and location name title
     * @param latLng LatLng object containing latitude and longtitude of an address
     * @param zoom camera zoom value
     * @param title location name
     */
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        Log.d(TAG, "hideSoftKeyboard: hiding");
        // this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



    /**
     * Get the location of this device
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(thiscontext);

        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: foundLocation!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }

    /**
     * Get permission to use location on the current device
     */
    private void getLocationPermission() {
        //Log.d(TAG, "getLocationPermission: ");
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(thiscontext,
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "haspermission1: getting location permissions");
            if (ContextCompat.checkSelfPermission(thiscontext,
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "haspermission2: getting location permissions");
                mLocationPermissionGranted = true;

            } else {
                requestPermissions(

                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
                Log.d(TAG, "nopermission1: getting location permissions");
            }
        } else {
            requestPermissions(

                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
            Log.d(TAG, "nopermission2: getting location permissions");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        //super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            showExitReasonDialogue();
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");

                    mLocationPermissionGranted = true;
                    //initialize the map
                }

                else {
                    showExitReasonDialogue();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }


            }
        }
    }

    private void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(thiscontext,R.style.AlertDialogTheme);
        // Setting Dialog Title
        alertDialog.setTitle("GPS not enabled");
        // Setting Dialog Message
        alertDialog.setMessage("To continue using this application,  do you want to enable GPS?\n" +
                " Cancel will exit the application");
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                thiscontext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                System.exit(0);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    private void showExitReasonDialogue(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(thiscontext,R.style.AlertDialogTheme);

        // Setting Dialog Title
        alertDialog.setTitle("GPS permission not granted");

        // Setting Dialog Message
        alertDialog.setMessage("To continue using this application, do you want to grant GPS permission?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                getLocationPermission();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                System.exit(0);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    // get direction, using httpurlconnection
    private static String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
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
    public static class TaskRequestDirections extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            Log.d(TAG, "onPostExecute: calling taskparser");
            RouteFragment.TaskParser taskParser = new RouteFragment.TaskParser();
            taskParser.execute(s);

        }
    }

    public static class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

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
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //super.onPostExecute(lists);
            // Get list route and display it into the map
            super.onPostExecute(lists);

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                Log.d(TAG, "onPostExecute: drawing route");
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
            }
            else
                Toast.makeText(thiscontext, "no route available", Toast.LENGTH_SHORT).show();
        }
    }

}



