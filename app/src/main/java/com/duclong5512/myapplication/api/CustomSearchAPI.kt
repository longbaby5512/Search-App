package com.duclong5512.myapplication.api

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.duclong5512.myapplication.model.SearchResult
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import org.json.JSONObject

class CustomSearchAPI(context: Context) {
    companion object {
        private const val TAG = "CustomSearchAPI"
        private const val KEY = "AIzaSyA_R5zNOkgBg99_EXfjL82FbbqVP6_KWV8"
        private const val ID = "bff349ffabda48635"
    }

    private val requestQueue = Volley.newRequestQueue(context)

    fun fetchResults(query: String): Task<MutableList<SearchResult>> {
        val apiSource = TaskCompletionSource<MutableList<SearchResult>>()
        val apiTask = apiSource.task
        val urlString = "https://www.googleapis.com/customsearch/v1?key=${KEY}&cx=${ID}&q=$query"
        val request = object: JsonObjectRequest(
            Method.GET,
            urlString,
            null,
            { response ->
                val searchResults = mutableListOf<SearchResult>()

                val items = response.getJSONArray("items")
                for (i in 1 until items.length()) {
                    val json = items[i] as JSONObject
                    val title = json.getString("title")
                    val description = json.getString("snippet")
                    val displayLink = json.getString("displayLink")
                    val link = json.getString("link")
                    val pagemap = json.getJSONObject("pagemap")
                    var thumbnailLink = ""
                    if (pagemap.has("cse_thumbnail")) {
                        thumbnailLink += (pagemap.getJSONArray("cse_thumbnail")[0] as JSONObject).getString("src")
                    }
                    val searchResult = SearchResult(title, description, thumbnailLink, displayLink, link)
//                    Log.d(TAG, searchResult.toString())
                    searchResults.add(searchResult)
                }

                apiSource.setResult(searchResults)

            },
            { error -> apiSource.setException(error) }
        ) {
            override fun getBodyContentType() = "application/json"
        }.apply {
            setShouldCache(false)
        }

        requestQueue.add(request)
        return apiTask
    }

}