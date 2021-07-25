package com.duclong5512.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duclong5512.myapplication.R
import com.duclong5512.myapplication.model.SearchResult

class ResultAdapter(
    private val searchResults: MutableList<SearchResult>,
    private val listener: OnItemClickListener,
) :
    RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ResultViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_result_search, parent, false), )

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(searchResults[position])
    }

    override fun getItemCount() = searchResults.size

    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)

        }

        fun bind(searchResult: SearchResult) {
            with(itemView) {
                findViewById<TextView>(R.id.tv_result_short_link).text = searchResult.displayLink
                findViewById<TextView>(R.id.tv_result_description).text = searchResult.description
                findViewById<TextView>(R.id.tv_result_title).text = searchResult.title
                if (searchResult.imageUrl?.isNotEmpty() == true) {
                    Glide.with(itemView).load(searchResult.imageUrl)
                        .into(findViewById(R.id.iv_result_image))
                } else {
                    findViewById<ImageView>(R.id.iv_result_image).setImageDrawable(getDrawable(
                        context,
                        R.drawable.ic_delete))
                }
            }
        }

        override fun onClick(v: View?) {
            listener.setOnItemClickListener(bindingAdapterPosition)
        }
    }

    interface OnItemClickListener {
        fun setOnItemClickListener(position: Int)
    }
}