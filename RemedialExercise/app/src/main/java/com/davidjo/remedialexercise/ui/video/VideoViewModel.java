package com.davidjo.remedialexercise.ui.video;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.davidjo.remedialexercise.data.BodyPart;
import com.davidjo.remedialexercise.data.exercise.Exercise;
import com.davidjo.remedialexercise.data.exercise.ExerciseDao;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class VideoViewModel extends ViewModel {

    private final ExerciseDao exerciseDao;

    @Inject
    public VideoViewModel(ExerciseDao exerciseDao) {
        this.exerciseDao = exerciseDao;
    }

    public LiveData<List<Exercise>> getExercises(BodyPart bodyPart) {
        return exerciseDao.getExercises(bodyPart);
    }

}
