package com.cool.nfckiosk.ui.admin.sales;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.cool.nfckiosk.data.sale.Sale;
import com.cool.nfckiosk.data.sale.SalesRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SalesViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    public static final int TAB_DAILY_SALES = 0;
    public static final int TAB_MONTHLY_SALES = 1;

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> userId = new MutableLiveData<>();

    private final MutableLiveData<Integer> tabTag = new MutableLiveData<>(TAB_DAILY_SALES);

    private final LiveData<Integer> totalSales;


    @Inject
    public SalesViewModel(SalesRepository salesRepository) {

        LiveData<List<Sale>> sales = Transformations.switchMap(userId, id ->
                Transformations.switchMap(tabTag, tag -> {
                    if (tag == TAB_DAILY_SALES) {
                        return salesRepository.getDailySales(id, LocalDateTime.now());
                    } else {
                        return salesRepository.getMonthlySales(id, LocalDateTime.now());
                    }
                }));

        totalSales = Transformations.map(sales, saleList -> {
            if (saleList == null) {
                return null;
            }
            int sum = 0;
            for (Sale sale : saleList) {
                sum += sale.getPrice();
            }
            return sum;
        });
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<Integer> getTotalSales() {
        return totalSales;
    }

    public LiveData<Integer> getTabTag() {
        return tabTag;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        } else {
            userId.setValue(firebaseAuth.getCurrentUser().getUid());
        }
    }

    public void onTabSelected(int position) {
        if (position == 0) {
            tabTag.setValue(TAB_DAILY_SALES);
        } else if (position == 1) {
            tabTag.setValue(TAB_MONTHLY_SALES);
        }
    }


    public static class Event {

        public static class NavigateBack extends Event {
        }
    }

}
