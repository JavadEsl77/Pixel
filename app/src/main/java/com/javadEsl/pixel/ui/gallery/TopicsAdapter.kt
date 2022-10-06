package com.javadEsl.pixel.ui.gallery

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.search.convertedUrl
import com.javadEsl.pixel.data.topics.TopicsModelItem
import com.javadEsl.pixel.databinding.ItemPreviousSearchBinding
import com.javadEsl.pixel.databinding.ItemTopicsBinding
import com.javadEsl.pixel.hide
import com.javadEsl.pixel.show

class TopicsAdapter(
    private val listener: OnItemClickListener,
    var topicList: List<TopicsModelItem>,
    private val activity: Activity
) : RecyclerView.Adapter<TopicsAdapter.TodoViewHolder>() {
    var rowIndex = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding =
            ItemTopicsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return topicList.size
    }

    inner class TodoViewHolder(private val binding: ItemTopicsBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.root.setOnClickListener {
                topicList[bindingAdapterPosition].let { listener.onItemClick() }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun bind() {
            binding.apply {


                layoutTopicItem.setOnClickListener {
                    rowIndex = bindingAdapterPosition
                    notifyDataSetChanged()
                }

                if(rowIndex==bindingAdapterPosition){
                    textViewTopicItem.setTextColor(activity.resources.getColor(R.color.purple_200))
                    layoutLineTopicItem.show()
                    if (rowIndex == 0)
                    textViewTopicItem.setTypeface(textViewTopicItem.typeface, Typeface.BOLD)
                }
                else
                {
                    layoutLineTopicItem.visibility = View.INVISIBLE
                    textViewTopicItem.setTextColor(activity.resources.getColor(R.color.black))
                }


                textViewTopicItem.text = topicList[bindingAdapterPosition].title
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick()
    }
}


