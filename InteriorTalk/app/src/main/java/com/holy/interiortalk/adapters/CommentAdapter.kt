package com.holy.interiortalk.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holy.interiortalk.R
import com.holy.interiortalk.models.Comment
import java.time.LocalDateTime
import java.util.*

class CommentAdapter(
    private val context: Context,
    private val list: List<Comment>
    ) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val writerText: TextView = itemView.findViewById(R.id.txt_comment_writer)
        private val commentText: TextView = itemView.findViewById(R.id.txt_comment_text)
        private val timeText: TextView = itemView.findViewById(R.id.txt_comment_time)

        fun bind(model: Comment) {

            writerText.text = model.writer
            commentText.text = model.text

            // 작성시간 표시
            val time = LocalDateTime.parse(model.time)
            with (time) {
                timeText.text = String.format(
                    Locale.getDefault(),
                    "%d-%02d-%02d %02d:%02d:%02d",
                    year, monthValue, dayOfMonth, hour, minute, second)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.item_comment, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
