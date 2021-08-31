package com.davidjo.greenworld.ui.add.category;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.davidjo.greenworld.data.category.Category;
import com.davidjo.greenworld.data.category.CategoryDao;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CategoryViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final CategoryDao categoryDao;

    private final LiveData<List<Category>> categories;


    @Inject
    public CategoryViewModel(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
        categories = categoryDao.getCategories();
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    // UI 이벤트 처리

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        }
    }

    public void onCategorySelected(Category category) {
        event.setValue(new Event.NavigateToAddScreen(category));
    }

    public void onCustomCategorySelected() {
        event.setValue(new Event.NavigateToAddScreen(null));
    }

    public void onCategorySwiped(Category category, int position) {
        if (!category.basic) {
            new Thread(() -> {
                category.visible = false;
                categoryDao.update(category);
                event.postValue(new Event.ShowCategoryDeletedMessage("카테고리가 삭제되었습니다", category));
            }).start();
        } else {
            event.setValue(new Event.ShowCannotDeleteBasicCategories("기본 카테고리는 삭제할 수 없습니다"));
            event.setValue(new Event.RedisplayCategory(position));
        }
    }

    public void onUndoClick(Category category) {
        new Thread(() -> {
            category.visible = true;
            categoryDao.update(category);
        }).start();
    }

    // 프래그먼트에 전송하는 이벤트

    public static class Event {

        public static class NavigateBack extends Event { }

        public static class NavigateToAddScreen extends Event {
            public final Category category;
            public NavigateToAddScreen(Category category) {
                this.category = category;
            }
        }

        public static class ShowCategoryDeletedMessage extends Event {
            public final String message;
            public final Category category;
            public ShowCategoryDeletedMessage(String message, Category category) {
                this.message = message;
                this.category = category;
            }
        }

        public static class ShowCannotDeleteBasicCategories extends Event {
            public final String message;
            public ShowCannotDeleteBasicCategories(String message) {
                this.message = message;
            }
        }

        public static class RedisplayCategory extends Event {
            public final int position;
            public RedisplayCategory(int position) {
                this.position = position;
            }
        }
    }

}





