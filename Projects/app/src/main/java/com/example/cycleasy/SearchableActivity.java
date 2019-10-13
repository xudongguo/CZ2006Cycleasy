package com.example.cycleasy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlayout);
        SearchView searchView=findViewById(R.id.searchview);

        //set different queryhint texts for searches in different fragments
        final Intent thisIntent=getIntent();
        String sender=thisIntent.getExtras().getString("Sender");
        switch (sender){
            case "RacksSearchBar":
                searchView.setQueryHint("Where to park?");
                break;
            case "topRouteSearchbar":
                searchView.setQueryHint("Starting point?");
                break;
            case "botRouteSearchbar":
                searchView.setQueryHint("Destination?");
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
                Toast toast=Toast.makeText(getApplicationContext(),"query submitted",Toast.LENGTH_SHORT);
                toast.show();
                //TODO for search suggestions, currently not working...
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getBaseContext(),
                        SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
                suggestions.saveRecentQuery(query, null);
                //for performing real search in database or via API by doMySearch
                doMySearch(query);
                //pass query data back to fragment for display
                thisIntent.putExtra("query",query);
                setResult(RESULT_OK,thisIntent);
                //finish search activity
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
        return true;
    }


   /* public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.searchview).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }*/

}