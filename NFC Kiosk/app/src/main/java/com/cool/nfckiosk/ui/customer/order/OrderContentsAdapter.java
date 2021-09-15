package com.cool.nfckiosk.ui.customer.order;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.nfckiosk.R;
import com.cool.nfckiosk.data.menu.Menu;
import com.cool.nfckiosk.databinding.OrderPriceItemBinding;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderContentsAdapter extends ListAdapter<Pair<Menu, Integer>, OrderContentsAdapter.OrderViewHolder> {

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private final OrderPriceItemBinding binding;

        public OrderViewHolder(OrderPriceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position);
                }
            });
        }

        public void bind(Pair<Menu, Integer> model) {

            binding.textViewMenuName.setText(model.first.getName());

            String strCount = String.format(Locale.getDefault(),
                    resources.getString(R.string.menu_count),
                    model.second
            );
            binding.textViewMenuCount.setText(strCount);

            int totalPrice = model.first.getPrice() * model.second;
            String strPrice = NumberFormat.getNumberInstance().format(totalPrice);
            binding.textViewOrderPrice.setText(strPrice);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;
    private Resources resources;


    public OrderContentsAdapter(Resources resources) {
        super(new DiffUtilCallback());
        this.resources = resources;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        OrderPriceItemBinding binding = OrderPriceItemBinding.inflate(layoutInflater, parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<Pair<Menu, Integer>> {

        @Override
        public boolean areItemsTheSame(@NonNull Pair<Menu, Integer> oldItem, @NonNull Pair<Menu, Integer> newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Pair<Menu, Integer> oldItem, @NonNull Pair<Menu, Integer> newItem) {
            return oldItem.equals(newItem);
        }
    }

}