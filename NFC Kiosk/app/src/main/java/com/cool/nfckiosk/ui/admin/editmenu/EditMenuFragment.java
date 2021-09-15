package com.cool.nfckiosk.ui.admin.editmenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.nfckiosk.R;
import com.cool.nfckiosk.data.menu.Menu;
import com.cool.nfckiosk.databinding.FragmentMenuBinding;
import com.cool.nfckiosk.util.OnTextChangedListener;
import com.cool.nfckiosk.util.ui.AuthDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditMenuFragment extends AuthDialogFragment {

    private FragmentMenuBinding binding;
    private EditMenuViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentMenuBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(EditMenuViewModel.class);

        binding.editTextMenuName.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onMenuNameChanged(text);
            }
        });
        binding.editTextMenuPrice.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onMenuPriceChanged(text);
            }
        });
        binding.imageButtonAddMenu.setOnClickListener(v -> viewModel.onAddMenuClick());

        MenusAdapter adapter = new MenusAdapter();
        binding.recyclerMenu.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Menu menu = adapter.getCurrentList().get(viewHolder.getAdapterPosition());
                viewModel.onMenuSwiped(menu);
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.recyclerMenu);

        viewModel.getMenus().observe(getViewLifecycleOwner(), menus -> {
            if (menus != null) {
                adapter.submitList(menus);
            }
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof EditMenuViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof EditMenuViewModel.Event.ShowInputUnspecifiedMessage) {
                String message = ((EditMenuViewModel.Event.ShowInputUnspecifiedMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof EditMenuViewModel.Event.ShowMenuAddedMessage) {
                String message = ((EditMenuViewModel.Event.ShowMenuAddedMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
                binding.editTextMenuName.setText("");
                binding.editTextMenuPrice.setText("");
            } else if (event instanceof EditMenuViewModel.Event.ShowMenuDeletedMessage) {
                String message = ((EditMenuViewModel.Event.ShowMenuDeletedMessage) event).message;
                Menu menu = ((EditMenuViewModel.Event.ShowMenuDeletedMessage) event).menu;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
                        .setAction("취소", v -> viewModel.onUndoDeleteClick(menu))
                        .show();
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
