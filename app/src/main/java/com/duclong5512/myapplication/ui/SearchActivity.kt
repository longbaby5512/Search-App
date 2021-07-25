package com.duclong5512.myapplication.ui

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.duclong5512.myapplication.R
import com.duclong5512.myapplication.adapter.HistoryAdapter
import com.duclong5512.myapplication.provider.RecentSearchProvider

class SearchActivity : AppCompatActivity(), HistoryAdapter.OnItemClickListener {

    private lateinit var adapter: HistoryAdapter
    private var listHistory = mutableListOf<String>()

    private lateinit var toolbar: Toolbar
    private lateinit var btnClearHistory: TextView
    private lateinit var svSearchGoogle: SearchView
    private lateinit var rvHistory: RecyclerView
    private lateinit var btnBack: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initView()

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
        }
        toolbar.title = ""

        btnClearHistory.setOnClickListener { clearHistory() }
        btnBack.setOnClickListener { finish() }


        handleIntent(intent)
        setupSearchView(svSearchGoogle)

        initList()

    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        btnClearHistory = findViewById(R.id.btn_clear_history)
        svSearchGoogle = findViewById(R.id.sv_search_google)
        rvHistory = findViewById(R.id.rv_history)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun setupSearchView(searchView: SearchView) {
        searchView.setIconifiedByDefault(false)
        searchView.setSearchableInfo(
            (getSystemService(Context.SEARCH_SERVICE) as? SearchManager)?.getSearchableInfo(
                componentName
            )
        )
        searchView.isQueryRefinementEnabled = true
        searchView.requestFocus()
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.isFocusable = true
        searchView.isIconified = false
        searchView.requestFocusFromTouch()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY).also { query ->
                if (query != null) {
                    doMySearch(query)
                }
            }
        }
    }


    private fun initList() {
        val contentUri = "content://${RecentSearchProvider.AUTHORITY}/suggestions"
        val uri = Uri.parse(contentUri)
        val cursor: Cursor? =
            applicationContext?.contentResolver?.query(uri, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                listHistory.add(cursor.getString(cursor.getColumnIndex("query")))
                cursor.moveToNext()
            }
        }
        adapter = HistoryAdapter(listHistory, this)
        adapter.notifyDataSetChanged()
        rvHistory.adapter = adapter
        cursor.close()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun doMySearch(query: String) {
        SearchRecentSuggestions(
            applicationContext,
            RecentSearchProvider.AUTHORITY,
            RecentSearchProvider.MODE
        ).saveRecentQuery(query, null)
        svSearchGoogle.setQuery("", false)
        if (query !in listHistory) {
            listHistory.add(query)
            adapter.notifyDataSetChanged()
        }
        setResult(Activity.RESULT_OK, intent.putExtra("QUERY", query))
        Log.d(TAG, query)
        finish()
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, SearchActivity::class.java)
        private const val TAG = "SearchActivity"

    }

    override fun setOnItemClickListener(position: Int) {
        doMySearch(listHistory[position])
    }

    private fun clearHistory() {
        SearchRecentSuggestions(applicationContext, RecentSearchProvider.AUTHORITY, RecentSearchProvider.MODE)
            .clearHistory()
        listHistory.clear()
        adapter.notifyDataSetChanged()
    }

}