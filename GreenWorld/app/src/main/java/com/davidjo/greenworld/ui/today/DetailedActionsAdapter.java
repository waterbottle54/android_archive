package com.davidjo.greenworld.ui.today;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.davidjo.greenworld.R;
import com.davidjo.greenworld.data.detailedaction.DetailedAction;
import com.davidjo.greenworld.databinding.ActionItemBinding;

import java.util.Locale;

public class DetailedActionsAdapter extends ListAdapter<DetailedAction, DetailedActionsAdapter.IconActionViewHolder> {

    class IconActionViewHolder extends RecyclerView.ViewHolder {

        private final ActionItemBinding binding;

        public IconActionViewHolder(ActionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DetailedAction model) {

            binding.textViewActionName.setText(model.getActionName());

            String strRepetitions = String.format(Locale.getDefault(),
                    context.getString(R.string.repetitions_format), model.getRepetitions());
            binding.textViewActionRepetitions.setText(strRepetitions);

            binding.imageViewActionIcon.setBackgroundResource(model.getIcon());
        }
    }


    private final Context context;

    public DetailedActionsAdapter(Context context) {
        super(new DiffUtilCallback());
        this.context = context;
    }


    @NonNull
    @Override
    public IconActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ActionItemBinding binding = ActionItemBinding.inflate(layoutInflater, parent, false);
        return new IconActionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IconActionViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<DetailedAction> {

        @Override
        public boolean areItemsTheSame(@NonNull DetailedAction oldItem, @NonNull DetailedAction newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DetailedAction oldItem, @NonNull DetailedAction newItem) {
            return oldItem.equals(newItem);
        }
    }

}