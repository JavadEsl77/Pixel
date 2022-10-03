package com.javadEsl.pixel.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javadEsl.pixel.databinding.ItemCalssificationBinding
import com.javadEsl.pixel.databinding.ItemUnsplashUserPhotoBinding

class ClassificationAdapter(
    var classificationList: List<String>
) : RecyclerView.Adapter<ClassificationAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding =
            ItemCalssificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return classificationList.size
    }

    inner class TodoViewHolder(private val binding: ItemCalssificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                textViewClassification.text = classificationList[bindingAdapterPosition]
            }
        }
    }
}


