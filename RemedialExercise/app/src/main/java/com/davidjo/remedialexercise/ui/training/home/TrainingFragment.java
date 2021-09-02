package com.davidjo.remedialexercise.ui.training.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.data.exercise.Exercise;
import com.davidjo.remedialexercise.services.TrainingService;
import com.davidjo.remedialexercise.databinding.FragmentTrainingBinding;
import com.davidjo.remedialexercise.util.TimeUtils;
import com.google.android.material.snackbar.Snackbar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrainingFragment extends Fragment implements ServiceConnection {

    private static final String TAG = "TrainingFragment";

    private Context context;
    private FragmentTrainingBinding binding;
    private TrainingViewModel viewModel;
    private TrainingService trainingService;
    private final MutableLiveData<Boolean> isBound = new MutableLiveData<>(false);
    private YouTubePlayer youTubePlayer;

    public TrainingFragment() {
        super(R.layout.fragment_training);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentTrainingBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(TrainingViewModel.class);

        binding.fabDiscardPlan.setOnClickListener(v -> viewModel.onDiscardPlanClick());
        binding.fabCompletePlan.setOnClickListener(v -> viewModel.onCompletePlanClick());

        getLifecycle().addObserver(binding.youtubePlayer);
        binding.youtubePlayer.getYouTubePlayerWhenReady(youTubePlayer -> {
            this.youTubePlayer = youTubePlayer;
            Log.d(TAG, "getYouTubePlayerWhenReady: ");
        });

        viewModel.getDayPlus().observe(getViewLifecycleOwner(), dayPlus -> {
            if (dayPlus != null) {
                String strDayPlus = String.format(Locale.getDefault(), getString(R.string.day_plus),
                        dayPlus >= 0 ? "+" : "", dayPlus);
                binding.textViewDayPlus.setText(strDayPlus);
            } else {
                binding.textViewDayPlus.setText("");
            }
        });

        viewModel.isStartable().observe(getViewLifecycleOwner(), isStartable -> {
            binding.fabStartPauseRepetition.setEnabled(isStartable);
            binding.fabStartPauseRepetition.setClickable(isStartable);
        });

        viewModel.getDaysLeft().observe(getViewLifecycleOwner(), daysLeft -> {
            if (daysLeft != null) {
                String strDaysLeft = String.format(Locale.getDefault(), getString(R.string.days_left), daysLeft);
                binding.textViewDaysLeft.setText(strDaysLeft);
            } else {
                binding.textViewDaysLeft.setText("");
            }
            binding.fabCompletePlan.setEnabled(daysLeft != null && daysLeft == 0);
        });

        viewModel.getPlan().observe(getViewLifecycleOwner(), plan ->
                binding.fabDiscardPlan.setEnabled(plan != null));

        viewModel.getTotalRepetition().observe(getViewLifecycleOwner(), totalRepetition -> {
            if (totalRepetition != null) {
                binding.textViewRepetitionTotal.setText(String.valueOf(totalRepetition));
            } else {
                binding.textViewRepetitionTotal.setText("");
            }
        });

        viewModel.getCurrentRepetition().observe(getViewLifecycleOwner(), currentRepetition -> {
            if (currentRepetition != null) {
                binding.textViewRepetitionDone.setText(String.valueOf(currentRepetition));
            } else {
                binding.textViewRepetitionDone.setText("");
            }
        });

        viewModel.getMinutesPerRepetition().observe(getViewLifecycleOwner(), minutes -> {
            if (minutes != null) {
                binding.textViewTimeRemaining.setText(TimeUtils.formatMinutesSeconds(minutes * 60));
            } else {
                binding.textViewTimeRemaining.setText("-:-");
            }
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof TrainingViewModel.Event.ShowNoPlanMessage) {
                TrainingViewModel.Event.ShowNoPlanMessage showNoPlanMessage =
                        (TrainingViewModel.Event.ShowNoPlanMessage) event;
                new AlertDialog.Builder(context)
                        .setMessage(showNoPlanMessage.message)
                        .setPositiveButton("확인", (dialog, which) ->
                                Navigation.findNavController(requireView()).popBackStack()).show();
            } else if (event instanceof TrainingViewModel.Event.ShowDiscardPlanConfirmMessage) {
                TrainingViewModel.Event.ShowDiscardPlanConfirmMessage showDiscardPlanConfirmMessage =
                        (TrainingViewModel.Event.ShowDiscardPlanConfirmMessage) event;
                Snackbar.make(requireView(), showDiscardPlanConfirmMessage.message, Snackbar.LENGTH_SHORT)
                        .setAction("포기", v -> viewModel.onDiscardPlanConfirm())
                        .show();
            } else if (event instanceof TrainingViewModel.Event.NavigateToSurveyScreen) {
                NavDirections action = TrainingFragmentDirections.actionTrainingFragmentToSurveyFragment();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof TrainingViewModel.Event.StartTrainingService) {
                if (!TrainingService.isServiceStarted) {
                    startTrainingService();
                }
            }
        });

        isBound.observe(getViewLifecycleOwner(), isBound -> {
            if (isBound) {
                binding.fabStartPauseRepetition.setOnClickListener(v -> trainingService.toggleCountdownTimer());
                binding.fabStopRepetition.setEnabled(true);
                binding.fabStopRepetition.setOnClickListener(v -> trainingService.stopCountdownTimer());
                binding.groupPlayer.setVisibility(View.VISIBLE);

                trainingService.getSeconds().observe(getViewLifecycleOwner(), seconds ->
                        binding.textViewTimeRemaining.setText(TimeUtils.formatMinutesSeconds(seconds))
                );
                trainingService.isPaused().observe(getViewLifecycleOwner(), isPaused ->
                        binding.fabStartPauseRepetition.setImageDrawable(
                                ContextCompat.getDrawable(context, isPaused ? R.drawable.ic_start : R.drawable.ic_pause))
                );

                List<Exercise> exercises = viewModel.getExercises();
                if (exercises != null && !exercises.isEmpty()) {
                    Exercise exercise = exercises.get(0);
                    if (youTubePlayer == null) {
                        binding.youtubePlayer.getYouTubePlayerWhenReady(youTubePlayer -> {
                            this.youTubePlayer = youTubePlayer;
                            youTubePlayer.loadVideo(exercise.getYoutubeVideoId(), 0);
                        });
                    } else {
                        youTubePlayer.loadVideo(exercise.getYoutubeVideoId(), 0);
                    }
                }

            } else {
                binding.fabStartPauseRepetition.setOnClickListener(v -> viewModel.onStartRepetitionClick());
                binding.fabStartPauseRepetition.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_start));
                binding.fabStopRepetition.setEnabled(false);
                binding.fabStopRepetition.setClickable(false);
                binding.groupPlayer.setVisibility(View.GONE);

                Integer minutes = viewModel.getMinutesPerRepetition().getValue();
                if (minutes != null) {
                    binding.textViewTimeRemaining.setText(TimeUtils.formatMinutesSeconds(minutes * 60));
                }

                if (youTubePlayer != null) {
                    youTubePlayer.pause();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (TrainingService.isServiceStarted) {
            Intent intent = new Intent(context, TrainingService.class);
            context.bindService(intent, this, Activity.BIND_AUTO_CREATE);
        } else {
            isBound.setValue(false);
        }

        context.registerReceiver(finishedReceiver, new IntentFilter(TrainingService.BR_FINISHED));
    }

    @Override
    public void onPause() {
        super.onPause();

        if (TrainingService.isServiceStarted) {
            context.unbindService(this);
        }

        context.unregisterReceiver(finishedReceiver);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        trainingService = ((TrainingService.TrainingBinder) service).getService();
        isBound.setValue(true);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        onServiceUnbound();
    }

    private void onServiceUnbound() {
        trainingService = null;
        isBound.setValue(false);
    }

    public void startTrainingService() {

        if (viewModel.getPlan().getValue() != null) {
            Intent intent = new Intent(context, TrainingService.class);
            intent.putExtra(TrainingService.EXTRA_PLAN, viewModel.getPlan().getValue());
            context.startForegroundService(intent);
            context.bindService(intent, this, Activity.BIND_AUTO_CREATE);
        }
    }


    private final BroadcastReceiver finishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TrainingFragment.this.context.unbindService(TrainingFragment.this);
            onServiceUnbound();
        }
    };

}
