package com.example.cycleasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.util.Log;
import android.app.Dialog;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static int CONTENT_TIMEOUT=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent ContentIntent=new Intent(MainActivity.this, ContentActivity.class);
//                startActivity(ContentIntent);
//                finish();
//            }
//        },CONTENT_TIMEOUT);

        if(isServicesOK()){
            init();
        }
}

    private void init(){
        Button btnMap = (Button)findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
    }
    public boolean isServicesOK() {
        Log.d(TAG, "isServiceOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //fine
            Log.d(TAG,"isServicesOK:");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //
            Log.d(TAG,"isServicesOK: ");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"You can't make map requests",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
