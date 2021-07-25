package com.duclong5512.myapplication.provider

import android.content.SearchRecentSuggestionsProvider

class RecentSearchProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.duclong5512.myapplication.RecentSearchProvider"
        const val MODE = DATABASE_MODE_QUERIES
    }
}