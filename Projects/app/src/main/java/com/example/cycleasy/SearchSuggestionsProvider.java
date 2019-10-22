package com.example.cycleasy;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionsProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY="com.example.cycleasy.SearchSuggestionsProvider";
    public final static int MODE=DATABASE_MODE_QUERIES;

    public SearchSuggestionsProvider(){
        setupSuggestions(AUTHORITY,MODE);

    }
}
