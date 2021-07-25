package com.duclong5512.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.duclong5512.myapplication.R
import java.util.*

class HistoryAdapter(
    private val listHistory: MutableList<String>,
    private val listener: OnItemClickListener,
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>(), Filterable {

    private var listFilterHistory = mutableListOf<String>()

    init {
        listFilterHistory = listHistory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HistoryViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent, parent, false))

    override fun onBindViewHolder(holder: HistoryAdapter.HistoryViewHolder, position: Int) {
        holder.bind(listFilterHistory[position])
    }


    override fun getItemCount() = listFilterHistory.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                listFilterHistory = if (charSearch.isEmpty()) {
                    listHistory
                } else {
                    val resultList = mutableListOf<String>()
                    for (result in listHistory) {
                        if (result.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(result)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = listFilterHistory
                filterResults.count = listFilterHistory.size
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listFilterHistory = results?.values as MutableList<String>
                notifyDataSetChanged()
            }
        }
    }

    inner class HistoryViewHolder(itemVIew: View) : RecyclerView.ViewHolder(itemVIew),
        View.OnClickListener {
        private val tvHistory = itemVIew.findViewById<TextView>(R.id.tv_history)

        init {
            itemVIew.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            listener.setOnItemClickListener(bindingAdapterPosition)
        }

        fun bind(history: String) {
            tvHistory.text = history
        }
    }

    interface OnItemClickListener {
        fun setOnItemClickListener(position: Int)
    }
}