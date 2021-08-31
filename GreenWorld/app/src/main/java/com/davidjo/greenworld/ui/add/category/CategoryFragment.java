package com.davidjo.greenworld.ui.add.category;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.davidjo.greenworld.R;
import com.davidjo.greenworld.data.category.Category;
import com.davidjo.greenworld.databinding.FragmentCategoryBinding;
import com.davidjo.greenworld.util.ui.AuthFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryFragment extends AuthFragment {

    private FragmentCategoryBinding binding;
    private CategoryViewModel viewModel;


    public CategoryFragment() {
        super(R.layout.fragment_category);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentCategoryBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        binding.cardViewCategoryCustom.setOnClickListener(v -> viewModel.onCustomCategorySelected());
        binding.progressBar.setVisibility(View.VISIBLE);

        // 카테고리 리사이클러뷰에 연결될 어댑터
        CategoriesAdapter adapter = new CategoriesAdapter();
        adapter.setOnItemSelectedListener(position ->
                viewModel.onCategorySelected(adapter.getCurrentList().get(position))
        );
        binding.recyclerCategory.setAdapter(adapter);
        binding.recyclerCategory.setHasFixedSize(true);

        // 카테고리 리사이클러뷰에 밀어서 삭제하는 기능을 제공하는 객체
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Category category = adapter.getCurrentList().get(viewHolder.getAdapterPosition());
                viewModel.onCategorySwiped(category, viewHolder.getAdapterPosition());
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.recyclerCategory);

        // DB 의 카테고리들을 리사이클러뷰에 표시함
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            List<Category> visibleCategories = categories.stream().filter(category -> category.visible).collect(Collectors.toList());
            adapter.submitList(visibleCategories);
            binding.progressBar.setVisibility(View.INVISIBLE);
        });

        // 뷰모델에서 전송한 이벤트 처리
        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof CategoryViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof CategoryViewModel.Event.NavigateToAddScreen) {
                Category category = ((CategoryViewModel.Event.NavigateToAddScreen) event).category;
                NavDirections action = CategoryFragmentDirections.actionCategoryFragmentToAddFragment(category);
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof CategoryViewModel.Event.ShowCategoryDeletedMessage) {
                CategoryViewModel.Event.ShowCategoryDeletedMessage showCategoryDeletedMessage =
                        (CategoryViewModel.Event.ShowCategoryDeletedMessage) event;
                Snackbar.make(requireView(), showCategoryDeletedMessage.message, Snackbar.LENGTH_LONG)
                        .setAction("취소하기", v -> viewModel.onUndoClick(showCategoryDeletedMessage.category))
                        .show();
            } else if (event instanceof CategoryViewModel.Event.ShowCannotDeleteBasicCategories) {
                String message = ((CategoryViewModel.Event.ShowCannotDeleteBasicCategories) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof CategoryViewModel.Event.RedisplayCategory) {
                int position = ((CategoryViewModel.Event.RedisplayCategory) event).position;
                adapter.notifyItemChanged(position);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        viewModel.onAuthStateChanged(firebaseAuth);
    }

}
