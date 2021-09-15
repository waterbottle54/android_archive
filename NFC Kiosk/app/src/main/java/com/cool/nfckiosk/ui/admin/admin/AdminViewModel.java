package com.cool.nfckiosk.ui.admin.admin;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.cool.nfckiosk.data.detailedTable.DetailedTable;
import com.cool.nfckiosk.data.detailedTable.DetailedTableRepository;
import com.cool.nfckiosk.data.menu.Menu;
import com.cool.nfckiosk.data.menu.MenuRepository;
import com.cool.nfckiosk.data.order.Order;
import com.cool.nfckiosk.data.order.OrderRepository;
import com.cool.nfckiosk.data.sale.Sale;
import com.cool.nfckiosk.data.sale.SalesRepository;
import com.cool.nfckiosk.data.store.StoreRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AdminViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> userId = new MutableLiveData<>();

    private final MutableLiveData<DetailedTable> selectedTable = new MutableLiveData<>();

    private final LiveData<List<DetailedTable>> detailedTables;

    private final LiveData<Map<String, Menu>> menuMap;

    private final LiveData<List<Order>> orders;
    private final Observer<List<Order>> ordersObserver;

    private final FirebaseAuth auth;
    private final SalesRepository salesRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    private int tableNumberNfcTarget = -1;
    private List<Order> recentOrderList;


    @Inject
    public AdminViewModel(FirebaseAuth auth,
                          DetailedTableRepository detailedTableRepository,
                          MenuRepository menuRepository,
                          SalesRepository salesRepository,
                          OrderRepository orderRepository,
                          StoreRepository storeRepository) {
        this.auth = auth;
        this.salesRepository = salesRepository;
        this.orderRepository = orderRepository;
        this.storeRepository = storeRepository;

        detailedTables = Transformations.switchMap(userId, detailedTableRepository::getDetailedTables);
        menuMap = Transformations.switchMap(userId, menuRepository::getMenuMap);

        orders = Transformations.switchMap(userId, orderRepository::getOrders);
        ordersObserver = orderList -> {
            if (orderList == null) {
                return;
            }

            if (recentOrderList != null && menuMap.getValue() != null) {
                Log.d("TAG", "AdminViewModel: " + orderList.size());
                Log.d("TAG", "AdminViewModel: " + recentOrderList.size());
                if (orderList.size() > recentOrderList.size()) {
                    List<Order> reducedList = new ArrayList<>(orderList);
                    reducedList.removeAll(recentOrderList);
                    Order newOrder = reducedList.get(0);
                    if (newOrder != null) {
                        event.setValue(new Event.ShowNewOrderMessage(newOrder, menuMap.getValue()));
                    }
                }
            }

            recentOrderList = orderList;
        };
        orders.observeForever(ordersObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        orders.removeObserver(ordersObserver);
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<DetailedTable>> getDetailedTables() {
        return detailedTables;
    }

    public LiveData<Map<String, Menu>> getMenuMap() {
        return menuMap;
    }

    public LiveData<DetailedTable> getSelectedTable() {
        return selectedTable;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new AdminViewModel.Event.NavigateBack());
        } else {
            userId.setValue(firebaseAuth.getCurrentUser().getUid());
        }
    }

    public void onBackClick() {
        if (selectedTable.getValue() == null) {
            event.setValue(new AdminViewModel.Event.ConfirmSignOut("로그아웃 하시겠습니까?"));
        } else {
            selectedTable.setValue(null);
        }
    }

    public void onSignOutConfirmed() {
        auth.signOut();
    }

    public void onTableClick(DetailedTable detailedTable) {

        if (userId.getValue() == null) {
            return;
        }

        if (!detailedTable.isActive()) {
            storeRepository.getStore(userId.getValue(), store -> {
                String textToWrite = String.format(Locale.getDefault(),
                        "%s#%d", store.getAdminNickname(), detailedTable.getNumber());
                tableNumberNfcTarget = detailedTable.getNumber();
                event.setValue(new Event.ShowNfcWriteScreen(textToWrite));
            });
        } else if (detailedTable.getOrder() == null) {
            event.setValue(new Event.ShowTableNotOrderedMessage("아직 주문이 들어오지 않은 테이블입니다"));
        } else {
            selectedTable.setValue(detailedTable);
        }

    }

    public void onNfcWriteResult(boolean success) {

        if (tableNumberNfcTarget == -1 || userId.getValue() == null) {
            return;
        }

        if (success) {
            int tableNumber = tableNumberNfcTarget;
            tableNumberNfcTarget = -1;
            storeRepository.activateTable(userId.getValue(), tableNumber, unused ->
                event.setValue(new Event.ShowTableActivatedMessage(tableNumber))
            );
        } else {
            event.setValue(new Event.ShowTableActivationFailureMessage("태그 인식에 실패했습니다"));
        }
    }

    public void onTableScreenClick() {
        selectedTable.setValue(null);
    }

    public void onEditMenuClick() {
        event.setValue(new Event.NavigateToMenuScreen());
    }

    public void onCompletePaymentClick() {
        if (selectedTable.getValue() == null) {
            event.setValue(new Event.ShowNoTableSelectedMessage("결제를 완료할 테이블을 선택해주세요"));
            return;
        }
        event.setValue(new Event.ConfirmCompletePayment("결제를 완료하시겠습니까?"));
    }

    public void onCompletePaymentConfirmed() {
        if (selectedTable.getValue() == null || userId.getValue() == null) {
            return;
        }
        Order order = selectedTable.getValue().getOrder();
        Sale sale = new Sale(userId.getValue(), order.getPrice());
        salesRepository.addSale(sale, unused -> {
            event.setValue(new Event.ShowPaymentCompletedMessage("결제가 완료되었습니다"));
            selectedTable.setValue(null);
            orderRepository.deleteOrder(order);
        });
    }

    public void onShowSalesClick() {
        event.setValue(new Event.NavigateToSalesFragment());
    }

    public void onEditTablesClick() {
        event.setValue(new Event.ShowEditTablesScreen());
    }

    public void onTableNumberSelected(int number) {

        if (userId.getValue() == null || number < 0 || detailedTables.getValue() == null) {
            return;
        }

        if (number == detailedTables.getValue().size()) {
            event.setValue(new Event.ShowTableNumberAdjustedMessage("동일한 테이블 수를 입력하셨습니다"));
            return;
        }

        storeRepository.adjustTableNumber(userId.getValue(), number,
                unused -> event.setValue(new Event.ShowTableNumberAdjustedMessage("테이블 수가 변경되었습니다"))
        );
    }


    public static class Event {

        public static class ConfirmSignOut extends Event {
            public final String message;

            public ConfirmSignOut(String message) {
                this.message = message;
            }
        }

        public static class NavigateBack extends Event {
        }

        public static class NavigateToMenuScreen extends Event {
        }

        public static class ConfirmCompletePayment extends Event {
            public final String message;

            public ConfirmCompletePayment(String message) {
                this.message = message;
            }
        }

        public static class ShowNoTableSelectedMessage extends Event {
            public final String message;

            public ShowNoTableSelectedMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowPaymentCompletedMessage extends Event {
            public final String message;

            public ShowPaymentCompletedMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowTableNotOrderedMessage extends Event {
            public final String message;

            public ShowTableNotOrderedMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateToSalesFragment extends Event {}

        public static class ShowEditTablesScreen extends Event {}

        public static class ShowTableNumberAdjustedMessage extends Event {
            public final String message;
            public ShowTableNumberAdjustedMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowNfcWriteScreen extends Event {
            public final String textToWrite;
            public ShowNfcWriteScreen(String textToWrite) {
                this.textToWrite = textToWrite;
            }
        }

        public static class ShowTableActivatedMessage extends Event {
            public final int tableNumber;
            public ShowTableActivatedMessage(int tableNumber) {
                this.tableNumber = tableNumber;
            }
        }

        public static class ShowTableActivationFailureMessage extends Event {
            public final String message;
            public ShowTableActivationFailureMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowNewOrderMessage extends Event {
            public final Order order;
            public final Map<String, Menu> menuMap;
            public ShowNewOrderMessage(Order order, Map<String, Menu> menuMap) {
                this.order = order;
                this.menuMap = menuMap;
            }
        }
    }

}
