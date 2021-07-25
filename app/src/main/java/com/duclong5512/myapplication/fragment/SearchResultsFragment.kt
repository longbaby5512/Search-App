package com.duclong5512.myapplication.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.duclong5512.myapplication.R
import com.duclong5512.myapplication.adapter.ResultAdapter
import com.duclong5512.myapplication.api.CustomSearchAPI
import com.duclong5512.myapplication.model.SearchResult


private const val ARG_QUERY = "QUERY"

class SearchResultsFragment : BaseFragment(), ResultAdapter.OnItemClickListener {
    private var query: String? = null

    private var adapter: ResultAdapter? = null
    private var rvResult: RecyclerView? = null
    private lateinit var searchApi: CustomSearchAPI
    private lateinit var progressBar: ProgressBar

    private var progress = false
    private var progressValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        results.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(TAG, "onCreateView")
        val view = inflater.inflate(R.layout.fragment_search_results, container, false)
        rvResult = view.findViewById(R.id.rv_result)
        progressBar = view.findViewById(R.id.progress_bar_result)

        searchApi = CustomSearchAPI(requireActivity())
        adapter = ResultAdapter(results, this)
        rvResult?.adapter = adapter
        return view
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
        arguments?.let {
            query = it.getString(ARG_QUERY)
        }
        if (query != null) {
            Log.d(TAG, query!!)
        } else {
            Log.d(TAG, "Query is null")
        }
        searchByQuery(query)
    }

    private fun searchByQuery(query: String?) {
        if (query != null) {
            progressBar.visibility = View.VISIBLE
        }
        progress = true
        requireActivity().runOnUiThread {
            while (progress && progressValue < 100) {
                progressValue++
                progressBar.progress = progressValue
            }
        }
        if (query != null) {
            searchApi.fetchResults(query)
                .addOnSuccessListener { showSearchResult(it) }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Error calling Custom API Search.", error)
                    showErrorResponse(error.localizedMessage)
                }
        }
    }

    private fun showErrorResponse(message: String?) {
        progressBar.visibility = View.GONE
        progress = false
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun showSearchResult(results: MutableList<SearchResult>) {
        progressBar.visibility = View.GONE
        Log.d(TAG, results.toString())
        progress = false
        if (query != null) {
            SearchResultsFragment.results.clear()
            SearchResultsFragment.results.addAll(results)
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rvResult?.adapter = null
        adapter = null
        rvResult = null
    }

    companion object {
        @JvmStatic
        fun newInstance(query: String?) =
            SearchResultsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_QUERY, query)
                }
            }
        private const val TAG = "SearchResultsFragment"
        private var results = mutableListOf<SearchResult>()
    }

    override fun setOnItemClickListener(position: Int) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragment, WebViewFragment.newInstance(results[position].link), "webview")
            ?.addToBackStack(null)
            ?.commit()
    }
}