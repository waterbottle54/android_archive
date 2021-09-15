package com.cool.nfckiosk.ui.admin.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.nfckiosk.data.menu.Menu;
import com.cool.nfckiosk.databinding.OrderItemBinding;

import java.util.Map;

public class OrdersAdapter extends ListAdapter<Pair<String, Integer>, OrdersAdapter.OrderViewHolder> {

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private final OrderItemBinding binding;

        public OrderViewHolder(OrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Pair<String, Integer> model) {

            String menuId = model.first;
            int count = model.second;
            Menu menu = menuMap.get(menuId);

            binding.textViewMenuName.setText(menu != null ? menu.getName() : "-");
            binding.textViewMenuCount.setText(String.valueOf(count));
        }
    }


    private final Map<String, Menu> menuMap;


    public OrdersAdapter(Map<String, Menu> menuMap) {
        super(new DiffUtilCallback());
        this.menuMap = menuMap;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        OrderItemBinding binding = OrderItemBinding.inflate(layoutInflater, parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<Pair<String, Integer>> {

        @Override
        public boolean areItemsTheSame(@NonNull Pair<String, Integer> oldItem, @NonNull Pair<String, Integer> newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Pair<String, Integer> oldItem, @NonNull Pair<String, Integer> newItem) {
            return oldItem.equals(newItem);
        }
    }

}