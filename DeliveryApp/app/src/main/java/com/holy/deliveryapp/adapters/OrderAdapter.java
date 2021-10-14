package com.holy.deliveryapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.deliveryapp.R;
import com.holy.deliveryapp.models.Order;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<Order> list;
    private OnItemClickListener onItemClickListener;

    public OrderAdapter(List<Order> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView localNameText;
        private final TextView orderListText;
        private final TextView orderDateText;
        private final TextView totalPriceText;

        public ViewHolder(View itemView) {
            super(itemView);

            localNameText = itemView.findViewById(R.id.txtLocalName);
            orderListText = itemView.findViewById(R.id.txtOrderList);
            orderDateText = itemView.findViewById(R.id.txtOrderDate);
            totalPriceText = itemView.findViewById(R.id.txtTotalPrice);
        }

        public void bind(Order model, OnItemClickListener listener) {

            localNameText.setText(model.getLocalName());

            List<String> orderList = model.getOrderList();
            StringBuilder strOrderList = new StringBuilder();
            for (int i = 0; i < orderList.size(); i++) {
                strOrderList.append(orderList.get(i));
                if (i < orderList.size() - 1) {
                    strOrderList.append(", ");
                }
            }
            orderListText.setText(strOrderList);

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(model.getDate());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            String strOrderDate = String.format(Locale.getDefault(),
                    "%d-%02d-%02d",
                    year, month, dayOfMonth);
            orderDateText.setText(strOrderDate);

            String strTotalPrice = NumberFormat.getInstance().format(model.getTotalPrice());
            totalPriceText.setText(strTotalPrice);

            if (listener != null) {
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Order item = list.get(position);
        holder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}