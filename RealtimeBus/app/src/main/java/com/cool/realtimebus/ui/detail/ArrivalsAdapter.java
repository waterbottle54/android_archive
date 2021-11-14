package com.cool.realtimebus.ui.detail;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.realtimebus.data.arrival.Arrival;
import com.cool.realtimebus.databinding.ArrivalItemBinding;

import java.util.Locale;

public class ArrivalsAdapter extends ListAdapter<Arrival, ArrivalsAdapter.ArrivalViewHolder> {

    class ArrivalViewHolder extends RecyclerView.ViewHolder {

        private final ArrivalItemBinding binding;

        public ArrivalViewHolder(ArrivalItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position);
                }
            });
        }

        public void bind(Arrival model) {

            binding.textViewArrivalRouteTitle.setText(model.getRouteTitle());

            String strInfo = String.format(Locale.getDefault(),
                    "%d분(%d번째 전)",
                    model.getSecondsLeft()/60,
                    model.getStationsLeft()
            );
            binding.textViewArrivalInfo.setText(strInfo);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;


    public ArrivalsAdapter() {
        super(new DiffUtilCallback());
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public ArrivalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ArrivalItemBinding binding = ArrivalItemBinding.inflate(layoutInflater, parent, false);
        return new ArrivalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArrivalViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<Arrival> {

        @Override
        public boolean areItemsTheSame(@NonNull Arrival oldItem, @NonNull Arrival newItem) {
            return oldItem.getRouteId().equals(newItem.getRouteId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Arrival oldItem, @NonNull Arrival newItem) {
            return oldItem.equals(newItem);
        }
    }

}