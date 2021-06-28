package com.holy.interiortalk.adapters;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holy.interiortalk.R
import com.holy.interiortalk.models.FurnitureLabel

class LabelAdapter(
    private val context: Context,
    private val list: List<FurnitureLabel>
) : RecyclerView.Adapter<LabelAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val numberText: TextView = itemView.findViewById(R.id.txt_label_number)
        private val titleText: TextView = itemView.findViewById(R.id.txt_label_title)
        private val purchaseUrlText: TextView = itemView.findViewById(R.id.txt_purchase_url)

        fun bind(model: FurnitureLabel, listener: OnItemClickListener?) {

            numberText.text = "${1 + adapterPosition}"
            titleText.text = model.title
            purchaseUrlText.text = model.purchaseUrl

            purchaseUrlText.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onPurchaseUrlClick(model.purchaseUrl)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.item_label, parent, false)

        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(list[position], onItemClickListener)
    }

    override fun getItemCount(): Int {

        return list.size
    }


    interface OnItemClickListener {
        fun onPurchaseUrlClick(url: String?)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener;
    }

}
