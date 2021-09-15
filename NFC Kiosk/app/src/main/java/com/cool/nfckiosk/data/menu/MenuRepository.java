package com.cool.nfckiosk.data.menu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class MenuRepository {

    private final CollectionReference menuCollection;

    @Inject
    public MenuRepository(FirebaseFirestore firestore) {
        menuCollection = firestore.collection("menus");
    }

    public LiveData<List<Menu>> getMenus(String userId) {
        MutableLiveData<List<Menu>> menus = new MutableLiveData<>();
        menuCollection.whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        menus.setValue(null);
                        return;
                    }
                    List<Menu> menuList = new ArrayList<>();
                    for (DocumentSnapshot snapshot : value) {
                        Menu menu = snapshot.toObject(Menu.class);
                        menuList.add(menu);
                    }
                    menus.setValue(menuList);
                });
        return menus;
    }

    public LiveData<Map<String, Menu>> getMenuMap(String userId) {
        LiveData<Map<String, Menu>> menuMap;
        menuMap = Transformations.map(getMenus(userId), menuList -> {
            Map<String, Menu> map = new HashMap<>();
            for (Menu menu : menuList) {
                map.put(menu.getId(), menu);
            }
            return map;
        });
        return menuMap;
    }

    public void addMenu(Menu menu) {
        menuCollection.document(menu.getId()).set(menu);
    }

    public void deleteMenu(Menu menu) {
        menuCollection.document(menu.getId()).delete();
    }

}
