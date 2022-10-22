package com.javadEsl.pixel.ui.gallery

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.javadEsl.pixel.*
import com.javadEsl.pixel.databinding.ItemTopicsBinding
import com.javadEsl.pixel.helper.extensions.show

class TopicsAdapter(
    private val listener: OnItemClickListener,
    private val topicList: List<com.javadEsl.pixel.data.model.topics.TopicsModelItem>,
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
        fun bind(item: com.javadEsl.pixel.data.model.topics.TopicsModelItem) {
            val textBuilder = SpannableStringBuilder()
            binding.apply {

                if (lastSelectedPosition == bindingAdapterPosition) {
                    textViewTopicItem.textSize = 13f
                    layoutLineTopicItem.show()
                    textViewTopicItem.text = textBuilder.color(getColor(R.color.purple_200)) {
                        bold { bold { append(item.title) } }
                    }

                } else {
                    textViewTopicItem.textSize = 12f
                    layoutLineTopicItem.visibility = View.INVISIBLE
                    textViewTopicItem.text = textBuilder.color(getColor(R.color.color_hint_edittext)) {
                        append(item.title)
                    }

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
        fun onTopicsItemClick(topicsModelItem: com.javadEsl.pixel.data.model.topics.TopicsModelItem, position: Int)
    }
}


