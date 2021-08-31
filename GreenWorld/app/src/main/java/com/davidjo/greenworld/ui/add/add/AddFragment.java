package com.davidjo.greenworld.ui.add.add;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.davidjo.greenworld.R;
import com.davidjo.greenworld.databinding.DialogCategoryNameBinding;
import com.davidjo.greenworld.databinding.FragmentAddBinding;
import com.davidjo.greenworld.ui.home.HomeFragment;
import com.davidjo.greenworld.util.ui.AuthFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddFragment extends AuthFragment {

    public static final String REQUEST_PHOTO = "com.davidjo.greenworld.request_photo";
    public static final String RESULT_PHOTO_URL = "com.davidjo.greendworld.result_photo_url";


    private FragmentAddBinding binding;
    private AddViewModel viewModel;


    public AddFragment() {
        super(R.layout.fragment_add);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentAddBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(AddViewModel.class);

        binding.textViewSelectActionName.setOnClickListener(v -> viewModel.onSelectActionNameClick());
        binding.imageViewCategoryImage.setOnClickListener(v -> viewModel.onImageClick());
        binding.imageViewSearchImage.setOnClickListener(v -> viewModel.onSearchImageClick());
        binding.buttonAddRepetitions.setOnClickListener(v -> viewModel.onAddRepetitionsClick());
        binding.buttonSubtractRepetitions.setOnClickListener(v -> viewModel.onSubtractRepetitionsClick());
        binding.fabAddAction.setOnClickListener(v -> viewModel.onAddActionClick());

        // 선택된 카테고리명을 텍스트뷰에 띄우기
        viewModel.getActionName().observe(getViewLifecycleOwner(), categoryName -> {
            if (categoryName != null) {
                binding.textViewActionName.setText(categoryName);
            }
            binding.textViewActionName.setVisibility(categoryName != null ? View.VISIBLE : View.INVISIBLE);
            binding.textViewSelectActionName.setVisibility(categoryName == null ? View.VISIBLE : View.INVISIBLE);
            binding.textViewSelectActionName.setClickable(categoryName == null);
            binding.imageViewSearchImage.setVisibility(categoryName == null ? View.VISIBLE : View.INVISIBLE);
            binding.imageViewSearchImage.setClickable(categoryName == null);
            binding.imageViewCategoryImage.setClickable(categoryName == null);
        });

        // 선택된 이미지 url 을 이미지뷰에 띄우기
        viewModel.getImageUrl().observe(getViewLifecycleOwner(), imageUrl -> {
            if (imageUrl != null) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(binding.imageViewCategoryImage);
            } else {
                binding.imageViewCategoryImage.setBackgroundResource(R.drawable.image_icon);
            }
        });

        // 반복 횟수를 텍스트뷰에 표시
        viewModel.getRepetitions().observe(getViewLifecycleOwner(), repetitions ->
                binding.textViewActionRepetitions.setText(String.valueOf(repetitions))
        );

        // 뷰모델에서 전송한 이벤트 처리
        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof AddViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof AddViewModel.Event.PromptCategoryName) {
                showCategoryNameDialog();
            } else if (event instanceof AddViewModel.Event.ShowShortCategoryNameMessage) {
                String message = ((AddViewModel.Event.ShowShortCategoryNameMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof AddViewModel.Event.ShowCategoryNameSelectedMessage) {
                String message = ((AddViewModel.Event.ShowCategoryNameSelectedMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof AddViewModel.Event.ShowCategoryNameNotSpecifiedMessage) {
                String message = ((AddViewModel.Event.ShowCategoryNameNotSpecifiedMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof AddViewModel.Event.NavigateToHomeFragment) {
                boolean success = ((AddViewModel.Event.NavigateToHomeFragment) event).success;
                Bundle result = new Bundle();
                result.putBoolean(HomeFragment.RESULT_ADD_SUCCESS, success);
                getParentFragmentManager().setFragmentResult(HomeFragment.REQUEST_ADD, result);
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof AddViewModel.Event.PromptSaveCategory) {
                String message = ((AddViewModel.Event.PromptSaveCategory) event).message;
                showSaveCategoryDialog(message);
            } else if (event instanceof AddViewModel.Event.NavigateToPhotoFragment) {
                NavDirections action = AddFragmentDirections.actionAddFragmentToPhotoFragment();
                Navigation.findNavController(requireView()).navigate(action);
            }
        });

        // PhotoFragment 에서 선택된 결과값 처리
        getParentFragmentManager().setFragmentResultListener(REQUEST_PHOTO, getViewLifecycleOwner(), (requestKey, result) ->
                viewModel.onPhotoResult(result.getString(RESULT_PHOTO_URL))
        );
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

    private void showCategoryNameDialog() {
        DialogCategoryNameBinding dialogBinding = DialogCategoryNameBinding.inflate(getLayoutInflater());
        new AlertDialog.Builder(context)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("확인", (dialog, which) ->
                        viewModel.onCategoryNameSelected(dialogBinding.editTextCategoryName.getText().toString()))
                .setNegativeButton("취소", null)
                .show();
    }

    private void showSaveCategoryDialog(String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("네", (dialog, which) -> viewModel.onSaveCategoryClick())
                .setNegativeButton("아니오", null)
                .show();
    }

}
