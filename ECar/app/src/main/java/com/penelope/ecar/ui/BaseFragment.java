package com.penelope.ecar.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseFragment <B, VM> extends Fragment {

    protected VM viewModel;
    protected B binding;


    public BaseFragment(int layout) {
        super(layout);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = BindingUti;
        viewModel = new ViewModelProvider(this).get(getViewModelClass());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected abstract Class<VM> getViewModelClass();

    protected abstract B getBinding();

}
