package com.example.mybookapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.mybookapplication.data.BookData


class BookAdapter (val bookList : ArrayList<BookData>) : RecyclerView.Adapter<BookAdapter.BookItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_book, null)
        return BookItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        holder.tvBookName.text = bookList[position].bookName
        holder.tvAuthorName.text = bookList[position].bookAuthor
    }

    // ViewHolder class
    class BookItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAuthorName: TextView
        var tvBookName: TextView

        init {
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvBookName = itemView.findViewById(R.id.tv_book_name);

        }
    }


}