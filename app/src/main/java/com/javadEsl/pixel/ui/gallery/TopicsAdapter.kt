package com.javadEsl.pixel.ui.gallery

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.topics.TopicsModelItem
import com.javadEsl.pixel.databinding.ItemTopicsBinding
import com.javadEsl.pixel.fadeIn

class TopicsAdapter(
    private val listener: OnItemClickListener,
    private val topicList: List<TopicsModelItem>,
    private val lastPosition: Int,
) : RecyclerView.Adapter<TopicsAdapter.TodoViewHolder>() {

    private var lastSelectedPosition = lastPosition

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTopicsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    fun setSelect(position: Int) {
        topicList[lastSelectedPosition].isSelected = false
        notifyItemChanged(lastSelectedPosition)
        lastSelectedPosition = position
        topicList[lastSelectedPosition].isSelected = true
        notifyItemChanged(lastSelectedPosition)
    }


    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(topicList[position])
    }

    override fun getItemCount(): Int {
        return topicList.size
    }

    inner class TodoViewHolder(private val binding: ItemTopicsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = itemView.context

        init {
            binding.layoutTopicItem.setOnClickListener {
                val item = topicList[bindingAdapterPosition]
                listener.onTopicsItemClick(item, bindingAdapterPosition)
                setSelect(bindingAdapterPosition)
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun bind(item: TopicsModelItem) {
            binding.apply {
                textViewTopicItem.text = item.title
                if (lastSelectedPosition == bindingAdapterPosition) {
                    textViewTopicItem.setTextColor(getColor(R.color.purple_200))
                    layoutLineTopicItem.fadeIn()
                    textViewTopicItem.setTypeface(textViewTopicItem.typeface, Typeface.BOLD)
                } else {
                    layoutLineTopicItem.visibility = View.INVISIBLE
                    textViewTopicItem.setTextColor(getColor(R.color.black))
                    textViewTopicItem.setTypeface(textViewTopicItem.typeface, Typeface.NORMAL)
                }
            }
        }

        private fun getColor(@ColorRes id: Int) = ResourcesCompat.getColor(
            context.resources,
            id,
            null
        )
    }

    interface OnItemClickListener {
        fun onTopicsItemClick(topicsModelItem: TopicsModelItem, position: Int)
    }
}


