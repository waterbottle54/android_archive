package com.davidjo.remedialexercise.ui.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.data.BodyPart;
import com.davidjo.remedialexercise.data.exercise.Exercise;
import com.davidjo.remedialexercise.databinding.FragmentVideoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VideoFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        VideoViewModel viewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        BodyPart bodyPart = BodyPart.NECK;
        if (getArguments() != null) {
            bodyPart = VideoFragmentArgs.fromBundle(getArguments()).getBodyPart();
        }

        FragmentVideoBinding binding = FragmentVideoBinding.bind(view);
        getLifecycle().addObserver(binding.youtubePlayer);

        viewModel.getExercises(bodyPart).observe(getViewLifecycleOwner(), exercises -> {
            if (!exercises.isEmpty()) {
                Exercise exercise = exercises.get(0);
                binding.textViewExerciseName.setText(exercise.getName());
                binding.textViewExerciseDescription.setText(exercise.getDescription());
                binding.textViewExerciseProvider.setText(exercise.getProvider());
                binding.youtubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.loadVideo(exercise.getYoutubeVideoId(), 0);
                    }
                });
            }
        });
    }

}




