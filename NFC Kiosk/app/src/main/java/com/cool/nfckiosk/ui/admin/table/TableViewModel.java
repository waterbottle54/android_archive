package com.cool.nfckiosk.ui.admin.table;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.cool.nfckiosk.data.detailedTable.DetailedTable;
import com.cool.nfckiosk.data.menu.Menu;
import com.cool.nfckiosk.data.menu.MenuRepository;
import com.cool.nfckiosk.data.store.Store;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TableViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> userId = new MutableLiveData<>();

    private final MutableLiveData<DetailedTable> detailedTable = new MutableLiveData<>();
    private final LiveData<Integer> tableNumber;
    private final LiveData<Integer> totalPrice;
    private final LiveData<List<Pair<Menu, Integer>>> contentsList;

    @Inject
    public TableViewModel(MenuRepository menuRepository) {

        tableNumber = Transformations.map(detailedTable, Store.Table::getNumber);
        totalPrice = Transformations.map(detailedTable, table -> table.getOrder().getPrice());

        LiveData<Map<String, Menu>> menuMap = Transformations.switchMap(userId, menuRepository::getMenuMap);
        contentsList = Transformations.switchMap(detailedTable, table ->
                Transformations.map(menuMap, map ->
                    table.getOrder().getContents().entrySet()
                            .stream()
                            .map(entry -> new Pair<>(map.get(entry.getKey()), entry.getValue()))
                            .collect(Collectors.toList())
                )
        );
    }

    public void setDetailedTable(DetailedTable detailedTable) {
        this.detailedTable.setValue(detailedTable);
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<Integer> getTableNumber() {
        return tableNumber;
    }

    public LiveData<Integer> getTotalPrice() {
        return totalPrice;
    }

    public LiveData<List<Pair<Menu, Integer>>> getContentsList() {
        return contentsList;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        } else {
            userId.setValue(firebaseAuth.getCurrentUser().getUid());
        }
    }


    public static class Event {
        public static class NavigateBack extends Event {
        }
    }

}