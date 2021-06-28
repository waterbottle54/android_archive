package com.holy.exercise;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holy.exercise.adapters.ExerciseAdapter;
import com.holy.exercise.models.Exercise;

import java.util.List;


public class ExerciseFragment extends Fragment {

    private App mApplication;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        if (getActivity() != null) {
            mApplication = (App) (getActivity().getApplication());
        }

        buildUpperExerciseRecycler(view);
        buildMiddleExerciseRecycler(view);
        buildLowerExerciseRecycler(view);

        return view;
    }

    private void buildUpperExerciseRecycler(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerUpperExercise);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<Exercise> list = mApplication.getUpperExerciseList();
        ExerciseAdapter adapter = new ExerciseAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getContext(), ExerciseActivity.class);
            intent.putExtra(ExerciseActivity.EXTRA_EXERCISE, list.get(position));
            startActivity(intent);
        });
    }

    private void buildMiddleExerciseRecycler(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerMiddleExercise);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<Exercise> list = mApplication.getMiddleExerciseList();
        ExerciseAdapter adapter = new ExerciseAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getContext(), ExerciseActivity.class);
            intent.putExtra(ExerciseActivity.EXTRA_EXERCISE, list.get(position));
            startActivity(intent);
        });
    }

    private void buildLowerExerciseRecycler(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerLowerExercise);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<Exercise> list = mApplication.getLowerExerciseList();
        ExerciseAdapter adapter = new ExerciseAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getContext(), ExerciseActivity.class);
            intent.putExtra(ExerciseActivity.EXTRA_EXERCISE, list.get(position));
            startActivity(intent);
        });
    }
}