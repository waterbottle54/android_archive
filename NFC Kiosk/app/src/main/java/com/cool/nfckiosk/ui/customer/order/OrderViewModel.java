package com.cool.nfckiosk.ui.customer.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.cool.nfckiosk.data.menu.Menu;
import com.cool.nfckiosk.data.menu.MenuRepository;
import com.cool.nfckiosk.data.order.Order;
import com.cool.nfckiosk.data.order.OrderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OrderViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final OrderRepository orderRepository;

    private final int tableNumber;
    private final String adminId;

    private final LiveData<List<Menu>> menus;
    private final MutableLiveData<Map<Menu, Integer>> contents = new MutableLiveData<>();
    private final LiveData<Integer> totalPrice;


    @Inject
    public OrderViewModel(SavedStateHandle savedStateHandle,
                          MenuRepository menuRepository,
                          OrderRepository orderRepository) {

        this.orderRepository = orderRepository;

        Integer tableNum = savedStateHandle.get("tableNumber");
        assert tableNum != null;
        tableNumber = tableNum;
        adminId = savedStateHandle.get("adminId");

        menus = menuRepository.getMenus(adminId);

        totalPrice = Transformations.map(contents, contentsMap -> {
            int sum = 0;
            for (Map.Entry<Menu, Integer> entry : contentsMap.entrySet()) {
                Menu menu = entry.getKey();
                int count = entry.getValue();
                sum += (count * menu.getPrice());
            }
            return sum;
        });
        contents.setValue(new HashMap<>());
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Menu>> getMenus() {
        return menus;
    }

    public LiveData<Map<Menu, Integer>> getOrderContents() {
        return contents;
    }

    public LiveData<Integer> getTotalPrice() {
        return totalPrice;
    }

    public int getTableNumber() {
        return tableNumber;
    }


    public void onBackClick() {
        event.setValue(new Event.NavigateBack());
    }

    public void onMenuClick(Menu menu) {

        assert contents.getValue() != null;
        Map<Menu, Integer> contentsMap = new HashMap<>(contents.getValue());
        contentsMap.merge(menu, 1, Integer::sum);
        contents.setValue(contentsMap);
    }

    public void onSubmitClick() {

        assert contents.getValue() != null;
        if (contents.getValue().isEmpty()) {
            event.setValue(new Event.ShowNoContentsMessage("메뉴를 하나 이상 선택해주세요"));
            return;
        }

        event.setValue(new Event.PromptRequestMessage("주문 시 요청사항을 말씀해주세요"));
    }

    public void onRequestMessageSubmit(String requestMessage) {

        requestMessage = !requestMessage.isEmpty() ? requestMessage : null;

        assert contents.getValue() != null;
        Map<String, Integer> contentsMap = new HashMap<>();
        for (Map.Entry<Menu, Integer> entry : contents.getValue().entrySet()) {
            contentsMap.put(entry.getKey().getId(), entry.getValue());
        }

        assert totalPrice.getValue() != null;
        Order order = new Order(
                adminId,
                tableNumber,
                contentsMap,
                totalPrice.getValue(),
                requestMessage
        );
        orderRepository.addOrder(order, unused ->
                event.setValue(new Event.ShowOrderCompletedMessage("주문이 완료되었습니다"))
        );
    }

    public void onOrderCompletedClick() {
        event.setValue(new Event.NavigateBack());
    }

    public void onDeleteOrderClick() {
        contents.setValue(new HashMap<>());
    }


    public static class Event {

        public static class NavigateBack extends Event { }

        public static class ShowNoContentsMessage extends Event {
            public final String message;
            public ShowNoContentsMessage(String message) {
                this.message = message;
            }
        }
        
        public static class PromptRequestMessage extends Event {
            public final String message;
            public PromptRequestMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowOrderCompletedMessage extends Event {
            public final String message;
            public ShowOrderCompletedMessage(String message) {
                this.message = message;
            }
        }

    }

}
