package com.holy.interiortalk.adapters;

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.StorageReference
import com.holy.interiortalk.R
import com.holy.interiortalk.models.Post

class PostAdapter(
    private val context: Context,
    private val picturesRef: StorageReference,
    options: FirestoreRecyclerOptions<Post>
) : FirestoreRecyclerAdapter<Post, PostAdapter.ViewHolder>(options) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val postImage: ImageView = itemView.findViewById(R.id.img_post)
        private val titleText: TextView = itemView.findViewById(R.id.txt_post_title)
        private val descriptionText: TextView = itemView.findViewById(R.id.txt_post_description)
        private val writerText: TextView = itemView.findViewById(R.id.txt_post_writer)
        private val likesText: TextView = itemView.findViewById(R.id.txt_post_likes)

        fun bind(model: Post, picturesRef: StorageReference, listener: OnItemClickListener?) {

            titleText.text = model.title
            descriptionText.text = model.description
            writerText.text = model.writer

            // 이미지를 불러온다
            val megaBytes = (1024 * 1024).toLong()
            picturesRef.child("${model.id}.jpg")
                .getBytes(10 * megaBytes)
                .addOnSuccessListener {  bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    postImage.setImageBitmap(bitmap)
                }
                .addOnFailureListener{
                }

            // 좋아요 수를 표시한다
            val strLikes = "+ ${model.likes}"
            likesText.text = strLikes

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(model)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.item_post, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Post) {

        holder.bind(model, picturesRef, onItemClickListener)
    }


    interface OnItemClickListener {
        fun onItemClick(post: Post)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener;
    }

}
