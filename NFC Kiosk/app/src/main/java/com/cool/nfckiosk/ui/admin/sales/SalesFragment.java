package com.cool.nfckiosk.ui.admin.sales;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.cool.nfckiosk.R;
import com.cool.nfckiosk.databinding.FragmentSalesBinding;
import com.cool.nfckiosk.util.OnTabSelectedListener;
import com.cool.nfckiosk.util.ui.AuthDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SalesFragment extends AuthDialogFragment {

    private FragmentSalesBinding binding;
    private SalesViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentSalesBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(SalesViewModel.class);

        binding.tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.onTabSelected(tab.getPosition());
            }
        });

        viewModel.getTabTag().observe(getViewLifecycleOwner(), tabTag -> {
            if (tabTag == null) {
                return;
            }
            int month = LocalDate.now().getMonthValue();
            int dayOfMonth = LocalDate.now().getDayOfMonth();
            String strDate;
            if (tabTag == SalesViewModel.TAB_DAILY_SALES) {
                strDate = String.format(Locale.getDefault(),
                        getString(R.string.daily_sales_format),
                        month, dayOfMonth);
            } else {
                strDate = String.format(Locale.getDefault(),
                        getString(R.string.monthly_sales_format),
                        month);
            }
            binding.textViewTotalSalesIs.setText(strDate);
        });

        viewModel.getTotalSales().observe(getViewLifecycleOwner(), totalSales -> {
            if (totalSales == null) {
                return;
            }
            String strTotalSales = String.format(Locale.getDefault(),
                    getString(R.string.total_sales_format),
                    NumberFormat.getInstance().format(totalSales)
            );
            binding.textViewTotalSales.setText(strTotalSales);
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof SalesViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
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
