package com.cool.nfckiosk.ui.admin.editmenu;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.cool.nfckiosk.data.menu.Menu;
import com.cool.nfckiosk.data.menu.MenuRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditMenuViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> userId = new MutableLiveData<>();

    private final MenuRepository menuRepository;
    private final LiveData<List<Menu>> menus;

    private String menuName;
    private String menuPrice;


    @Inject
    public EditMenuViewModel(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
        menus = Transformations.switchMap(userId, menuRepository::getMenus);
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Menu>> getMenus() {
        return menus;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        } else {
            userId.setValue(firebaseAuth.getCurrentUser().getUid());
        }
    }

    public void onMenuNameChanged(String name) {
        this.menuName = name;
    }

    public void onMenuPriceChanged(String price) {
        this.menuPrice = price;
    }

    public void onAddMenuClick() {
        if (menuName == null || menuName.trim().isEmpty()) {
            event.setValue(new Event.ShowInputUnspecifiedMessage("메뉴 이름을 지정해주세요"));
            return;
        }
        if (menuPrice == null || menuPrice.trim().isEmpty()) {
            event.setValue(new Event.ShowInputUnspecifiedMessage("가격을 지정해주세요"));
            return;
        }
        if (userId.getValue() == null) {
            return;
        }
        int price = Integer.parseInt(menuPrice);
        Menu menu = new Menu(userId.getValue(), menuName, price, null);
        menuRepository.addMenu(menu);
        event.setValue(new Event.ShowMenuAddedMessage("메뉴가 추가되었습니다"));
    }

    public void onMenuSwiped(Menu menu) {
        menuRepository.deleteMenu(menu);
        event.setValue(new Event.ShowMenuDeletedMessage("메뉴가 삭제되었습니다", menu));
    }

    public void onUndoDeleteClick(Menu menu) {
        menuRepository.addMenu(menu);
    }


    public static class Event {
        public static class NavigateBack extends Event {}
        public static class ShowInputUnspecifiedMessage extends Event {
            public final String message;
            public ShowInputUnspecifiedMessage(String message) {
                this.message = message;
            }
        }
        public static class ShowMenuAddedMessage extends Event {
            public final String message;
            public ShowMenuAddedMessage(String message) {
                this.message = message;
            }
        }
        public static class ShowMenuDeletedMessage extends Event {
            public final String message;
            public final Menu menu;
            public ShowMenuDeletedMessage(String message, Menu menu) {
                this.message = message;
                this.menu = menu;
            }
        }
    }

}
