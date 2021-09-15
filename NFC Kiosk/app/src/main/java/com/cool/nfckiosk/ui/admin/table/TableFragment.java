package com.cool.nfckiosk.ui.admin.table;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.cool.nfckiosk.R;
import com.cool.nfckiosk.data.detailedTable.DetailedTable;
import com.cool.nfckiosk.databinding.FragmentMenuBinding;
import com.cool.nfckiosk.databinding.FragmentTableBinding;
import com.cool.nfckiosk.ui.customer.order.OrderContentsAdapter;
import com.cool.nfckiosk.util.ui.AuthFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TableFragment extends AuthFragment {

    private FragmentTableBinding binding;
    private TableViewModel viewModel;


    public static TableFragment newInstance(DetailedTable detailedTable) {
        TableFragment fragment = new TableFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("detailed_table", detailedTable);
        fragment.setArguments(bundle);
        return fragment;
    }

    public TableFragment() {
        super(R.layout.fragment_table);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentTableBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(TableViewModel.class);
        if (getArguments() != null) {
            DetailedTable detailedTable = (DetailedTable) getArguments().getSerializable("detailed_table");
            viewModel.setDetailedTable(detailedTable);
        }

        OrderContentsAdapter adapter = new OrderContentsAdapter(getResources());
        binding.recyclerOrderPrice.setAdapter(adapter);

        viewModel.getTableNumber().observe(getViewLifecycleOwner(), number -> {
            String strTable = String.format(Locale.getDefault(),
                    getString(R.string.table_order_contents), number
            );
            binding.textViewTableNumber.setText(strTable);
        });

        viewModel.getContentsList().observe(getViewLifecycleOwner(), adapter::submitList);

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), price -> {
            String strPrice = NumberFormat.getInstance().format(price);
            binding.textViewTotalPrice.setText(strPrice);
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

    public void setDetailedTable(DetailedTable detailedTable) {
        if (viewModel != null) {
            viewModel.setDetailedTable(detailedTable);
        }
    }

}