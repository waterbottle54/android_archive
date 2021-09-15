package com.cool.nfckiosk.ui.admin.admin;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.nfckiosk.R;
import com.cool.nfckiosk.data.detailedTable.DetailedTable;
import com.cool.nfckiosk.data.menu.Menu;
import com.cool.nfckiosk.data.order.Order;
import com.cool.nfckiosk.data.store.Store;
import com.cool.nfckiosk.databinding.TableItemBinding;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DetailedTablesAdapter extends ListAdapter<DetailedTable, DetailedTablesAdapter.TableViewHolder> {

    class TableViewHolder extends RecyclerView.ViewHolder {

        private final TableItemBinding binding;

        public TableViewHolder(TableItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position);
                }
            });
        }

        public void bind(DetailedTable model) {

            Order order = model.getOrder();

            // 테이블 색상
            int colorTableInactive = resources.getColor(R.color.colorTableInactive, null);
            int colorTableOrdered = resources.getColor(R.color.colorTableOrdered, null);
            int colorTableNotOrdered = resources.getColor(R.color.colorTableNotOrdered, null);

            if (!model.isActive()) {
                binding.getRoot().setCardBackgroundColor(colorTableInactive);
            } else {
                binding.getRoot().setCardBackgroundColor(order != null ? colorTableOrdered : colorTableNotOrdered);
            }

            // 테이블 번호
            binding.textViewTableNumber.setText(String.valueOf(model.getNumber()));

            if (order != null) {
                // 테이블 주문 내역
                OrdersAdapter adapter = new OrdersAdapter(menuMap);
                binding.recyclerOrder.setAdapter(adapter);
                binding.recyclerOrder.setHasFixedSize(true);

                List<Pair<String, Integer>> contentList = order.getContents().entrySet()
                        .stream()
                        .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());
                adapter.submitList(contentList);
            } else {
                binding.recyclerOrder.setAdapter(null);
            }
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;
    private final Resources resources;
    private final Map<String, Menu> menuMap;

    public DetailedTablesAdapter(Resources resources, Map<String, Menu> menuMap) {
        super(new DiffUtilCallback());
        this.resources = resources;
        this.menuMap = menuMap;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TableItemBinding binding = TableItemBinding.inflate(layoutInflater, parent, false);
        return new TableViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<DetailedTable> {

        @Override
        public boolean areItemsTheSame(@NonNull DetailedTable oldItem, @NonNull DetailedTable newItem) {
            return oldItem.getNumber() == newItem.getNumber();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DetailedTable oldItem, @NonNull DetailedTable newItem) {
            return oldItem.equals(newItem);
        }
    }

}