package com.davidjo.greenworld.ui.add.photo;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.davidjo.greenworld.data.photo.PixabayPhoto;
import com.davidjo.greenworld.databinding.PhotoItemBinding;

public class PhotosAdapter extends ListAdapter<PixabayPhoto, PhotosAdapter.PixabayPhotoViewHolder> {

    class PixabayPhotoViewHolder extends RecyclerView.ViewHolder {

        private final PhotoItemBinding binding;

        public PixabayPhotoViewHolder(PhotoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(position);
                }
            });
        }

        public void bind(PixabayPhoto model) {
            binding.imageViewPhoto.setImageDrawable(null);
            glide.load(model.getPreviewURL())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.imageViewPhoto);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    private final RequestManager glide;

    private OnItemClickListener listener;

    public PhotosAdapter(RequestManager glide) {
        super(new DiffUtilCallback());
        this.glide = glide;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public PixabayPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PhotoItemBinding binding = PhotoItemBinding.inflate(layoutInflater, parent, false);
        return new PixabayPhotoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PixabayPhotoViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<PixabayPhoto> {

        @Override
        public boolean areItemsTheSame(@NonNull PixabayPhoto oldItem, @NonNull PixabayPhoto newItem) {
            return oldItem.getPreviewURL().equals(newItem.getPreviewURL());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PixabayPhoto oldItem, @NonNull PixabayPhoto newItem) {
            return oldItem.equals(newItem);
        }
    }

}