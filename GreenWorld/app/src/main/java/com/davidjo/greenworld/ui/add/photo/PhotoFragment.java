package com.davidjo.greenworld.ui.add.photo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.davidjo.greenworld.R;
import com.davidjo.greenworld.databinding.FragmentPhotoBinding;
import com.davidjo.greenworld.ui.add.add.AddFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PhotoFragment extends BottomSheetDialogFragment {

    // BottomSheetDialogFragment 는 화면 아래에서 올라오는 sheet 형태의 프래그먼트

    private Context context;
    private FragmentPhotoBinding binding;
    private PhotoViewModel viewModel;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentPhotoBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(PhotoViewModel.class);

        // 서치뷰 검색 버튼 처리
        binding.searchViewPhotoQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.onSearchClick(query);
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.textViewNoSearchResult.setVisibility(View.INVISIBLE);
                hideKeyboard(requireView());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.imageViewPixabayLogo.setOnClickListener(v -> navigateToPixabay());

        // 사진 리사이클러뷰에 연결되는 어댑터
        PhotosAdapter adapter = new PhotosAdapter(Glide.with(this));
        adapter.setOnItemClickListener(position ->
                viewModel.onPhotoClick(adapter.getCurrentList().get(position))
        );
        binding.recyclerPhoto.setAdapter(adapter);
        binding.recyclerPhoto.setHasFixedSize(true);

        // 검색된 사진들을 리사이클러뷰에 띄움
        viewModel.getPhotos().observe(getViewLifecycleOwner(), photos -> {
            if (photos != null) {
                adapter.submitList(photos);
                binding.recyclerPhoto.scrollToPosition(0);
                binding.textViewPhotoCount.setText(String.format(Locale.getDefault(),
                        getString(R.string.search_results_count), photos.size()));
                binding.textViewNoSearchResult.setVisibility(photos.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                binding.textViewPhotoCount.setVisibility(!photos.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            }
            binding.progressBar.setVisibility(View.INVISIBLE);
        });

        // 뷰모델이 전송한 이벤트 처리
        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof PhotoViewModel.Event.ShowSearchFailureMessage) {
                String message = ((PhotoViewModel.Event.ShowSearchFailureMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("다시 시도", v ->
                                viewModel.onSearchClick(binding.searchViewPhotoQuery.getQuery().toString()))
                        .show();
            } else if (event instanceof PhotoViewModel.Event.ShowNoResultMessage) {
                String message = ((PhotoViewModel.Event.ShowNoResultMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof PhotoViewModel.Event.NavigateBackWithResult) {
                String photoUrl = ((PhotoViewModel.Event.NavigateBackWithResult) event).photoUrl;
                Bundle bundle = new Bundle();
                bundle.putString(AddFragment.RESULT_PHOTO_URL, photoUrl);
                getParentFragmentManager().setFragmentResult(AddFragment.REQUEST_PHOTO, bundle);
                NavHostFragment.findNavController(this).popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    private void navigateToPixabay() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pixabay.com/ko/"));
        startActivity(intent);
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
