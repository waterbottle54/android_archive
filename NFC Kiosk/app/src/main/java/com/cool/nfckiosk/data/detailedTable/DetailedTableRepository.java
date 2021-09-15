package com.cool.nfckiosk.data.detailedTable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.cool.nfckiosk.data.order.Order;
import com.cool.nfckiosk.data.order.OrderRepository;
import com.cool.nfckiosk.data.store.Store;
import com.cool.nfckiosk.data.store.StoreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class DetailedTableRepository {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;

    @Inject
    public DetailedTableRepository(StoreRepository storeRepository, OrderRepository orderRepository) {
        this.storeRepository = storeRepository;
        this.orderRepository = orderRepository;
    }

    public LiveData<List<DetailedTable>> getDetailedTables(String adminId) {

        LiveData<Store> store = storeRepository.getStore(adminId);
        LiveData<Map<Integer, Order>> orderMap = orderRepository.getOrderMap(adminId);

        return Transformations.switchMap(store, storeValue ->
                Transformations.map(orderMap, orderMapValue -> {
                    List<DetailedTable> detailedTableList = new ArrayList<>();
                    for (int i = 0; i < storeValue.getTables().size(); i++) {
                        Store.Table table = storeValue.getTables().get(i);
                        Order order = orderMapValue.get(table.getNumber());
                        detailedTableList.add(new DetailedTable(table, order));
                    }
                    return detailedTableList;
                })
        );
    }

    public LiveData<DetailedTable> getDetailedTable(String adminId, int tableNumber) {

        LiveData<Store> store = storeRepository.getStore(adminId);
        LiveData<Map<Integer, Order>> orderMap = orderRepository.getOrderMap(adminId);

        return Transformations.switchMap(store, storeValue ->
                Transformations.map(orderMap, orderMapValue -> {
                    Store.Table table = storeValue.getTables().get(tableNumber - 1);
                    Order order = orderMapValue.get(tableNumber);
                    return table == null ? null : new DetailedTable(table, order);
                })
        );
    }

}
