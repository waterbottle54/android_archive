package com.holy.interiortalk.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.holy.interiortalk.R
import com.holy.interiortalk.models.Qna

class QnaAdapter(
    private val context: Context,
    options: FirestoreRecyclerOptions<Qna>
) : FirestoreRecyclerAdapter<Qna, QnaAdapter.ViewHolder>(options) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val questionText: TextView = itemView.findViewById(R.id.txt_qna_question)
        private val answerText: TextView = itemView.findViewById(R.id.txt_qna_answer)
        private val answerCard: CardView = itemView.findViewById(R.id.card_answer)

        fun bind(model: Qna, listener: OnItemClickListener?) {

            questionText.text = model.question

            if (model.answer != null) {
                answerText.text = model.answer
                answerCard.visibility = View.VISIBLE
            } else {
                answerCard.visibility = View.GONE
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(model)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.item_qna, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Qna) {
        holder.bind(model, onItemClickListener)
    }

    interface OnItemClickListener {
        fun onItemClick(qna: Qna)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

}
