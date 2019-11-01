package com.example.cycleasy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.Loader;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Fragment class for activities in racks page
 */
public class RacksFragment extends Fragment implements OnMapReadyCallback {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter adapter;
    private static final String TAG = "RacksActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    //true if there is message sent from other fragment, false if otherwise
    private boolean messagepending=false;
    private MapView mMapView;
    private GoogleMap mMap;
    private String startpt,endpt;
    private Location targetLocation=new Location(""), currentLocation;
    private Address currentadd;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionGranted = true;
    private Boolean isParsed=false;
    private static final float DEFAULT_ZOOM = 15f;
    private ArrayList<String> rackinfo;
    private Context thiscontext;




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_racks, null);
        thiscontext = container.getContext();
        final TextView searchbar = (TextView) view.findViewById((R.id.racks_searchbar));
        mMapView = (MapView) view.findViewById(R.id.racks_mapview);

        //show rack information bubble popup
        final BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(getContext()).inflate(R.layout.bubblelayout, null);
        final PopupWindow popupWindow = BubblePopupHelper.create(getContext(), bubbleLayout);
        final FloatingActionButton mylocatBUT=(FloatingActionButton) view.findViewById(R.id.racks_locationBut);
        final FloatingActionButton findbutton=view.findViewById(R.id.racks_findbutton);


        findbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayRackMarker();
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
        if (!isParsed){
            //get the ita-bicycle-rack-kml file and parse the rack info
            rackinfo=getRackInfo();
            isParsed=true;}

        initGoogleMap(savedInstanceState);
        //mMapView.getMapAsync(this);



        return view;

    }



    private ArrayList<String> getRackInfo(){

        InputStream rackinfoStream=thiscontext.getResources().openRawResource(R.raw.ltarackkml);
        ArrayList<String> rackinfo=new ArrayList<>();
        XmlParser rackParser=new XmlParser();
        try {
            rackinfo=rackParser.parseFile(rackinfoStream);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return rackinfo;
    }

    /**
     * Method to be called when SearchableActivity finished
     * @param requestCode request code to be validated with SearchableAvtivity
     * @param resultCode result code to be validated with SearchableActivity
     * @param data data received from the previous intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1) {
            if (resultCode == RESULT_OK) {
                //display query text on searchbar
                String displaytxt = data.getExtras().getString("query");
                TextView searchbar = (TextView) getView().findViewById(R.id.racks_searchbar);
                searchbar.setText(displaytxt);
                currentadd=geoLocate(displaytxt);
                if(currentadd!=null)
                //set target location address
                {targetLocation.setLatitude(currentadd.getLatitude());
                targetLocation.setLongitude(currentadd.getLongitude());}
                else
                    targetLocation=currentLocation;

            }
            else if (resultCode== RESULT_CANCELED){
                //if activity closed abnormally
            }
        }
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


            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(thiscontext);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(thiscontext);
                    title.setTextColor(Color.WHITE);
                    title.setGravity(Gravity.CENTER);
                    title.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(thiscontext);
                    snippet.setTextColor(Color.GRAY);
                    //snippet.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
        }
    }
    //display markers for racks
    public void displayRackMarker(){
       //create an location object for target address using currentadd

        for (int temp = 0; temp < rackinfo.size(); temp++) {
            String[] tempinfo = rackinfo.get(temp).split("[|]");
            double racklat = Double.valueOf(tempinfo[0]);
            double racklong = Double.valueOf(tempinfo[1]);
            Location racklocation=new Location("");
            racklocation.setLatitude(racklat);
            racklocation.setLongitude(racklong);

            //find the distance between current location and rack location
            if(racklocation.distanceTo(targetLocation)<500)
            {
                String racktype = tempinfo[2];
                String rackcount = tempinfo[3];
                String issheltered = tempinfo[4];

                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.rackiconbitmap);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
                //get lat lng of rack
                LatLng rackpos = new LatLng(racklat, racklong);
                Marker rackMarker = mMap.addMarker(new MarkerOptions()

                        .position(rackpos)
                        .title("rack" + temp)
                        .snippet("type: " + racktype + " deck" + "\n" + "number of racks: " + rackcount + "\n" + "sheltered: " + issheltered)
                        //.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_racklocation)))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                );
            }
        }

    }
    /**
     * locating a geographical location from a location name of type String, and move the camera to that location
     * @param query query text containing the name of the location
     * @return Address of the location
     */
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
                            currentLocation = (Location) task.getResult();
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