package com.cool.nfckiosk.data.sale;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SalesRepository {

    private final CollectionReference salesCollection;

    @Inject
    public SalesRepository(FirebaseFirestore firestore) {
        salesCollection = firestore.collection("sales");
    }

    public void addSale(Sale sale, OnSuccessListener<Void> onSuccessListener) {
        salesCollection.document(sale.getId()).set(sale)
                .addOnSuccessListener(onSuccessListener);
    }

    public LiveData<List<Sale>> getDailySales(String adminId, LocalDateTime date) {

        LocalDateTime minDate = date.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime maxDate = minDate.plusDays(1);
        ZoneOffset zoneOffset = ZoneOffset.ofHours(9);
        long minMillis = minDate.toEpochSecond(zoneOffset) * 1000;
        long maxMillis = maxDate.toEpochSecond(zoneOffset) * 1000;

        return getSales(adminId, minMillis, maxMillis);
    }

    public LiveData<List<Sale>> getMonthlySales(String adminId, LocalDateTime date) {

        LocalDateTime minDate = date.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime maxDate = minDate.plusMonths(1);
        ZoneOffset zoneOffset = ZoneOffset.ofHours(9);
        long minMillis = minDate.toEpochSecond(zoneOffset) * 1000;
        long maxMillis = maxDate.toEpochSecond(zoneOffset) * 1000;

        return getSales(adminId, minMillis, maxMillis);
    }

    private LiveData<List<Sale>> getSales(String adminId, long minMillis, long maxMillis) {

        MutableLiveData<List<Sale>> sales = new MutableLiveData<>();

        salesCollection
                .whereEqualTo("adminId", adminId)
                .whereGreaterThanOrEqualTo("created", minMillis)
                .whereLessThan("created", maxMillis)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        sales.setValue(null);
                        return;
                    }
                    List<Sale> saleList = new ArrayList<>();
                    for (DocumentSnapshot snapshot : value) {
                        Sale sale = snapshot.toObject(Sale.class);
                        saleList.add(sale);
                    }
                    sales.setValue(saleList);
                });

        return sales;
    }

}
