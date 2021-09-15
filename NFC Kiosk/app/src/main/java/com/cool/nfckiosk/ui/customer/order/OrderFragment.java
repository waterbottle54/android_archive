package com.cool.nfckiosk.ui.customer.order;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cool.nfckiosk.R;
import com.cool.nfckiosk.data.menu.Menu;
import com.cool.nfckiosk.databinding.FragmentOrderBinding;
import com.cool.nfckiosk.databinding.RequestMessageDialogBinding;
import com.cool.nfckiosk.util.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;
import io.grpc.okhttp.internal.Util;

@AndroidEntryPoint
public class OrderFragment extends Fragment {

    private Context context;
    private FragmentOrderBinding binding;
    private OrderViewModel viewModel;


    public OrderFragment() {
        super(R.layout.fragment_order);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.onBackClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentOrderBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        binding.buttonSubmit.setOnClickListener(v -> viewModel.onSubmitClick());
        binding.imageViewDeleteOrder.setOnClickListener(v -> viewModel.onDeleteOrderClick());
        binding.textViewTableNumber.setText(String.format(Locale.getDefault(),
                getString(R.string.table_number_format), viewModel.getTableNumber()
        ));

        MenusAdapter menusAdapter = new MenusAdapter();
        menusAdapter.setOnItemSelectedListener(position -> {
            Menu menu = menusAdapter.getCurrentList().get(position);
            viewModel.onMenuClick(menu);
        });

        binding.recyclerMenu.post(() -> {
            int spanCount = Utils.getRecyclerSpanCount(getResources(), binding.recyclerMenu, 108);
            binding.recyclerMenu.setAdapter(menusAdapter);
            binding.recyclerMenu.setLayoutManager(new GridLayoutManager(context, Math.max(spanCount, 1)));
        });

        OrderContentsAdapter orderContentsAdapter = new OrderContentsAdapter(getResources());
        binding.recyclerOrder.setAdapter(orderContentsAdapter);

        viewModel.getMenus().observe(getViewLifecycleOwner(), menus -> {
            if (menus != null) {
                menusAdapter.submitList(menus);
            }
        });

        viewModel.getOrderContents().observe(getViewLifecycleOwner(), contentsMap -> {
            List<Pair<Menu, Integer>> contentsList = contentsMap.entrySet()
                    .stream()
                    .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
            orderContentsAdapter.submitList(contentsList);

            binding.textViewNoOrders.setVisibility(contentsMap.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            binding.groupTotalPrice.setVisibility(contentsMap.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            binding.buttonSubmit.setEnabled(!contentsMap.isEmpty());
            binding.imageViewDeleteOrder.setVisibility(!contentsMap.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        });

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> {
            if (totalPrice != null) {
                String strPrice = NumberFormat.getInstance().format(totalPrice);
                binding.textViewTotalPrice.setText(strPrice);
            }
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof OrderViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof OrderViewModel.Event.ShowNoContentsMessage) {
                String message = ((OrderViewModel.Event.ShowNoContentsMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof OrderViewModel.Event.PromptRequestMessage) {
                String message = ((OrderViewModel.Event.PromptRequestMessage) event).message;
                showRequestMessageDialog(message);
            } else if (event instanceof OrderViewModel.Event.ShowOrderCompletedMessage) {
                String message = ((OrderViewModel.Event.ShowOrderCompletedMessage) event).message;
                showOrderCompletedDialog(message);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showRequestMessageDialog(String text) {

        RequestMessageDialogBinding messageDialogBinding =
                RequestMessageDialogBinding.inflate(getLayoutInflater());

        messageDialogBinding.textViewMessage.setText(text);

        new AlertDialog.Builder(context)
                .setView(messageDialogBinding.getRoot())
                .setPositiveButton(R.string.order, (dialogInterface, i) -> {
                    String requestMessage = messageDialogBinding.editTextRequestMessage.getText().toString().trim();
                    viewModel.onRequestMessageSubmit(requestMessage);
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void showOrderCompletedDialog(String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("확인", (dialogInterface, i) -> viewModel.onOrderCompletedClick())
                .show();
    }

}
