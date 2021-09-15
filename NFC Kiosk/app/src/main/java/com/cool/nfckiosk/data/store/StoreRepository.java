package com.cool.nfckiosk.data.store;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import javax.inject.Inject;

public class StoreRepository {

    private final CollectionReference storeCollection;

    @Inject
    public StoreRepository(FirebaseFirestore firestore) {
        storeCollection = firestore.collection("stores");
    }

    public LiveData<Store> getStore(String userId) {
        MutableLiveData<Store> store = new MutableLiveData<>();
        storeCollection.whereEqualTo("adminId", userId)
                .addSnapshotListener((value, error) -> {
                    if (value == null || error != null || value.isEmpty()) {
                        store.setValue(null);
                        return;
                    }
                    DocumentSnapshot snapshot = value.getDocuments().get(0);
                    store.setValue(snapshot.toObject(Store.class));
                });
        return store;
    }

    public void getStoreByNickname(String nickname, OnSuccessListener<Store> onSuccessListener) {
        storeCollection.whereEqualTo("adminNickname", nickname).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        Store store = queryDocumentSnapshots.getDocuments().get(0).toObject(Store.class);
                        onSuccessListener.onSuccess(store);
                    } else {
                        onSuccessListener.onSuccess(null);
                    }
                });
    }

    public void getStore(String userId, OnSuccessListener<Store> onSuccessListener) {
        storeCollection.whereEqualTo("adminId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        Store store = queryDocumentSnapshots.getDocuments().get(0).toObject(Store.class);
                        onSuccessListener.onSuccess(store);
                    } else {
                        onSuccessListener.onSuccess(null);
                    }
                });
    }

    public void adjustTableNumber(String userId, int number, OnSuccessListener<Void> listener) {

        getStore(userId, store -> {
            if (store == null) {
                return;
            }
            List<Store.Table> tables = store.getTables();
            if (number < tables.size()) {
                tables = tables.subList(0, number);
            } else if (number > tables.size()) {
                int oldNumber = tables.size();
                for (int i = 0; i < number - oldNumber; i++) {
                    tables.add(new Store.Table(tables.size() + 1, false));
                }
            } else {
                listener.onSuccess(null);
                return;
            }
            storeCollection.document(store.getId())
                    .update("tables", tables)
                    .addOnSuccessListener(unused -> listener.onSuccess(null));
        });
    }

    public void activateTable(String userId, int tableNumber, OnSuccessListener<Void> listener) {

        getStore(userId, store -> {
            if (store == null || tableNumber > store.getTables().size()) {
                return;
            }
            List<Store.Table> tables = store.getTables();
            tables.get(tableNumber - 1).setActive(true);
            storeCollection.document(store.getId())
                    .update("tables", tables)
                    .addOnSuccessListener(unused -> listener.onSuccess(null));
        });
    }

}



