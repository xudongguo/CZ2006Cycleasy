package com.example.cycleasy.Search;

import android.content.SearchRecentSuggestionsProvider;
/**
 * Provider class for providing search hint
 */
public class SearchSuggestionsProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY="com.example.cycleasy.Search.SearchSuggestionsProvider";
    public final static int MODE=DATABASE_MODE_QUERIES;

    public SearchSuggestionsProvider(){
        setupSuggestions(AUTHORITY,MODE);

    }
}
