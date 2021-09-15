package com.cool.nfckiosk.ui.admin.admin;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cool.nfckiosk.R;
import com.cool.nfckiosk.data.detailedTable.DetailedTable;
import com.cool.nfckiosk.data.menu.Menu;
import com.cool.nfckiosk.data.order.Order;
import com.cool.nfckiosk.databinding.EditTablesDialogBinding;
import com.cool.nfckiosk.databinding.FragmentAdminBinding;
import com.cool.nfckiosk.databinding.NewOrderDialogBinding;
import com.cool.nfckiosk.ui.admin.table.TableFragment;
import com.cool.nfckiosk.ui.nfc.NfcDialogFragment;
import com.cool.nfckiosk.util.Utils;
import com.cool.nfckiosk.util.ui.AuthFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminFragment extends AuthFragment {

    private FragmentAdminBinding binding;
    private AdminViewModel viewModel;

    private DetailedTablesAdapter detailedTablesAdapter;

    private TableFragment tableFragment;

    private EditTablesDialogBinding editTablesBinding;
    private AlertDialog editTablesDialog;

    private boolean rotated;


    public AdminFragment() {
        super(R.layout.fragment_admin);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentAdminBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        tableFragment = (TableFragment) getChildFragmentManager().findFragmentById(R.id.fragment_table);

        editTablesBinding = EditTablesDialogBinding.inflate(getLayoutInflater());
        editTablesDialog = new AlertDialog.Builder(context)
                .setMessage("테이블 수를 입력해주세요")
                .setView(editTablesBinding.getRoot())
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    String strNumber = editTablesBinding.editTextTableNumber.getText().toString();
                    if (!strNumber.isEmpty()) {
                        viewModel.onTableNumberSelected(Integer.parseInt(strNumber));
                    }
                })
                .setNegativeButton("취소", null)
                .create();

        binding.buttonEditMenu.setOnClickListener(v -> viewModel.onEditMenuClick());
        binding.buttonCompletePayment.setOnClickListener(v -> viewModel.onCompletePaymentClick());
        binding.buttonShowSales.setOnClickListener(v -> viewModel.onShowSalesClick());
        binding.buttonEditTables.setOnClickListener(v -> viewModel.onEditTablesClick());
        binding.fragmentTable.setOnClickListener(v -> viewModel.onTableScreenClick());

        viewModel.getMenuMap().observe(getViewLifecycleOwner(), map -> {
            if (map != null) {
                detailedTablesAdapter = new DetailedTablesAdapter(getResources(), map);
                binding.recyclerTable.setAdapter(detailedTablesAdapter);

                int spanCount = Utils.getRecyclerSpanCount(getResources(), binding.recyclerTable, 128);
                binding.recyclerTable.setLayoutManager(new GridLayoutManager(context, Math.max(spanCount, 1)));
                detailedTablesAdapter.setOnItemSelectedListener(position -> {
                    DetailedTable detailedTable = detailedTablesAdapter.getCurrentList().get(position);
                    viewModel.onTableClick(detailedTable);
                });

                List<DetailedTable> detailedTableList = viewModel.getDetailedTables().getValue();
                if (detailedTableList != null) {
                    detailedTablesAdapter.submitList(detailedTableList);
                }
            }
        });

        viewModel.getDetailedTables().observe(getViewLifecycleOwner(), detailedTableList -> {
            if (detailedTableList == null) {
                return;
            }
            if (detailedTablesAdapter != null) {
                detailedTablesAdapter.submitList(detailedTableList);
                editTablesBinding.editTextTableNumber.setText(String.valueOf(detailedTableList.size()));
            }
        });

        viewModel.getSelectedTable().observe(getViewLifecycleOwner(), selectedTable -> {
            if (selectedTable != null) {
                tableFragment.setDetailedTable(selectedTable);
            }
            binding.recyclerTable.setVisibility(selectedTable == null ? View.VISIBLE : View.INVISIBLE);
            binding.fragmentTable.setVisibility(selectedTable != null ? View.VISIBLE : View.INVISIBLE);
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof AdminViewModel.Event.ConfirmSignOut) {
                String message = ((AdminViewModel.Event.ConfirmSignOut) event).message;
                showConfirmSignOutDialog(message);
            } else if (event instanceof AdminViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof AdminViewModel.Event.NavigateToMenuScreen) {
                NavDirections action = AdminFragmentDirections.actionAdminFragmentToMenuFragment();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof AdminViewModel.Event.ConfirmCompletePayment) {
                String message = ((AdminViewModel.Event.ConfirmCompletePayment) event).message;
                showConfirmCompletePaymentDialog(message);
            } else if (event instanceof AdminViewModel.Event.ShowNoTableSelectedMessage) {
                String message = ((AdminViewModel.Event.ShowNoTableSelectedMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof AdminViewModel.Event.ShowPaymentCompletedMessage) {
                String message = ((AdminViewModel.Event.ShowPaymentCompletedMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof AdminViewModel.Event.ShowTableNotOrderedMessage) {
                String message = ((AdminViewModel.Event.ShowTableNotOrderedMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof AdminViewModel.Event.NavigateToSalesFragment) {
                NavDirections action = AdminFragmentDirections.actionAdminFragmentToSalesFragment();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof AdminViewModel.Event.ShowEditTablesScreen) {
                showTableNumberDialog();
            } else if (event instanceof AdminViewModel.Event.ShowTableNumberAdjustedMessage) {
                String message = ((AdminViewModel.Event.ShowTableNumberAdjustedMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof AdminViewModel.Event.ShowNfcWriteScreen) {
                String textToWrite = ((AdminViewModel.Event.ShowNfcWriteScreen) event).textToWrite;
                NavDirections action = AdminFragmentDirections.actionGlobalNfcDialogFragment(textToWrite);
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof AdminViewModel.Event.ShowTableActivatedMessage) {
                int tableNumber = ((AdminViewModel.Event.ShowTableActivatedMessage) event).tableNumber;
                String message = String.format(Locale.getDefault(),
                        getString(R.string.table_activated_format), tableNumber);
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof AdminViewModel.Event.ShowTableActivationFailureMessage) {
                String message = ((AdminViewModel.Event.ShowTableActivationFailureMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof AdminViewModel.Event.ShowNewOrderMessage) {
                Order order = ((AdminViewModel.Event.ShowNewOrderMessage) event).order;
                Map<String, Menu> menuMap = ((AdminViewModel.Event.ShowNewOrderMessage) event).menuMap;
                showNewOrderDialog(order, menuMap);
            }
        });

        getParentFragmentManager().setFragmentResultListener(NfcDialogFragment.NFC_REQUEST, getViewLifecycleOwner(),
                (requestKey, result) -> {
                    boolean writeSuccess = result.getBoolean(NfcDialogFragment.NFC_RESULT_WRITE_SUCCESS);
                    viewModel.onNfcWriteResult(writeSuccess);
                });

        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        editTablesBinding = null;

        if (rotated) {
            assert getActivity() != null;
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        rotate();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        viewModel.onAuthStateChanged(firebaseAuth);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            viewModel.onBackClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showConfirmSignOutDialog(String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("로그아웃", (dialog, which) -> viewModel.onSignOutConfirmed())
                .setNegativeButton("취소", null)
                .show();
    }

    public void showConfirmCompletePaymentDialog(String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("결제 완료", (dialog, which) -> viewModel.onCompletePaymentConfirmed())
                .setNegativeButton("취소", null)
                .show();
    }

    public void showTableNumberDialog() {

        editTablesBinding.editTextTableNumber.selectAll();
        editTablesDialog.show();
    }

    public void showNewOrderDialog(Order order, Map<String, Menu> menuMap) {

        NewOrderDialogBinding dialogBinding = NewOrderDialogBinding.inflate(getLayoutInflater());

        String strTableNumber = String.format(Locale.getDefault(),
                getString(R.string.table_number_format_2),
                order.getTableNumber());
        dialogBinding.textViewTableNumber.setText(strTableNumber);

        String requestMessage = order.getMessage();
        if (requestMessage != null && !requestMessage.isEmpty()) {
            dialogBinding.textViewRequestMessage.setText(requestMessage);
        } else {
            dialogBinding.groupRequestMessage.setVisibility(View.GONE);
        }

        OrdersAdapter adapter = new OrdersAdapter(menuMap);
        dialogBinding.recyclerOrder.setAdapter(adapter);
        adapter.submitList(order.getContents().entrySet()
                .stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList())
        );

        new AlertDialog.Builder(context)
                .setTitle(R.string.new_order)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("확인", null)
                .show();
    }

    private void rotate() {

        Activity activity = getActivity();
        assert activity != null;

        int currentOrientation = activity.getResources()
                .getConfiguration().orientation;

        if (currentOrientation != Configuration.ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            rotated = true;
        }
    }

}
