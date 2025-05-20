package com.example.mybookapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mybookapplication.R
import com.example.mybookapplication.data.BookData


class BookAdapter (val bookList : ArrayList<BookData>) : RecyclerView.Adapter<BookAdapter.BookItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_book, parent, false)
        return BookItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        holder.tvBookName.text = "${(position+1)}. ${bookList[position].bookName}"
        holder.tvAuthorName.text = bookList[position].bookAuthor
        Glide.with(holder.itemView.context).load(bookList[position].bookImage).into(holder.ivBook)
    }

    // ViewHolder class
    class BookItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAuthorName: TextView
        val tvBookName: TextView
        val ivBook: ImageView

        init {
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvBookName = itemView.findViewById(R.id.tv_book_name);
            ivBook = itemView.findViewById(R.id.iv_book);
        }
    }


}