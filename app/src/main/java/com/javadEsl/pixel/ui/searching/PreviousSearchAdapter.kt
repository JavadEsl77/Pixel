package com.javadEsl.pixel.ui.searching

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javadEsl.pixel.databinding.ItemCalssificationBinding
import com.javadEsl.pixel.databinding.ItemPreviousSearchBinding
import com.javadEsl.pixel.databinding.ItemUnsplashUserPhotoBinding

class PreviousSearchAdapter(
    var previousSearchList: List<String>
) : RecyclerView.Adapter<PreviousSearchAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding =
            ItemPreviousSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return previousSearchList.size
    }

    inner class TodoViewHolder(private val binding: ItemPreviousSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                textViewClassification.text = previousSearchList[bindingAdapterPosition]
            }
        }
    }
}


