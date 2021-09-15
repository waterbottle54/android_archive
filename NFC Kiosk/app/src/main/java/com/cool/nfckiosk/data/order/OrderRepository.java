package com.cool.nfckiosk.data.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.cool.nfckiosk.data.menu.Menu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class OrderRepository {

    private final CollectionReference orderCollection;

    @Inject
    public OrderRepository(FirebaseFirestore firestore) {
        orderCollection = firestore.collection("orders");
    }

    public LiveData<List<Order>> getOrders(String userId) {
        MutableLiveData<List<Order>> orders = new MutableLiveData<>();
        orderCollection.whereEqualTo("adminId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        orders.setValue(null);
                        return;
                    }
                    List<Order> orderList = new ArrayList<>();
                    for (DocumentSnapshot snapshot : value) {
                        Order order = snapshot.toObject(Order.class);
                        orderList.add(order);
                    }
                    orders.setValue(orderList);
                });
        return orders;
    }

    public LiveData<Map<Integer, Order>> getOrderMap(String userId) {
        LiveData<Map<Integer, Order>> orderMap;
        orderMap = Transformations.map(getOrders(userId), orderList -> {
            Map<Integer, Order> map = new HashMap<>();
            for (Order order : orderList) {
                map.put(order.getTableNumber(), order);
            }
            return map;
        });
        return orderMap;
    }

    public void addOrder(Order order, OnSuccessListener<Void> onSuccessListener) {
        orderCollection.document(order.getId()).set(order)
                .addOnSuccessListener(onSuccessListener);
    }

    public void deleteOrder(Order order) {
        orderCollection.document(order.getId()).delete();
    }

}
