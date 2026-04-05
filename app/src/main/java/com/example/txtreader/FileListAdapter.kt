package com.example.txtreader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FileListAdapter(
    private val onItemClick: (FileItem) -> Unit
) : RecyclerView.Adapter<FileListAdapter.FileViewHolder>() {

    private val items = mutableListOf<FileItem>()

    fun submitList(newItems: List<FileItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return FileViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class FileViewHolder(
        itemView: View,
        private val onItemClick: (FileItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(item: FileItem) {
            title.text = item.name
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}
