package com.example.cycleasy;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main activity to govern fragment transitions
 */
public class MainActivity extends AppCompatActivity

    implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static int CONTENT_TIMEOUT = 3000;
    private boolean singleBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        navigation.setOnNavigationItemSelectedListener(this);
        loadfragment(new RouteFragment());


    }

    /**
     * To load a particular fragment
     * @param fragment fragment to be loaded
     * @return return false if fragment is null, true otherwise
     */
    private boolean loadfragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    /**
     * Method to select one tab from the bottom menu
     * @param item
     * @return return loadfragment(fragment)
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.menu_exercise:
                fragment = new ExerciseFragment();
                break;
            case R.id.menu_route:
                fragment = new RouteFragment();
                break;
            case R.id.menu_racks:
                fragment = new RacksFragment();
                break;
            case R.id.menu_me:
                fragment = new MeFragment();
                break;
        }
        return loadfragment(fragment);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (singleBackPressed) {
                super.onBackPressed();
            }

            singleBackPressed = true;
            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() { singleBackPressed = false;
                }
            }, 2000);
        }
    }
}