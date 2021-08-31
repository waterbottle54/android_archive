package com.davidjo.greenworld.ui.add.add;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.davidjo.greenworld.R;
import com.davidjo.greenworld.data.action.Action;
import com.davidjo.greenworld.data.category.Category;
import com.davidjo.greenworld.data.category.CategoryDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final Category category;

    private final MutableLiveData<String> actionName = new MutableLiveData<>();
    private final MutableLiveData<String> imageUrl = new MutableLiveData<>();
    private final MutableLiveData<Integer> repetitions = new MutableLiveData<>(1);

    private final CollectionReference actionCollection;
    private final CategoryDao categoryDao;

    private String userId;


    @Inject
    public AddViewModel(SavedStateHandle savedStateHandle, FirebaseFirestore firestore, CategoryDao categoryDao) {
        category = savedStateHandle.get("category");
        if (category != null) {
            actionName.setValue(category.name);
            imageUrl.setValue(category.imageUrl);
        } else {
            actionName.setValue(null);
            imageUrl.setValue(null);
        }

        actionCollection = firestore.collection("actions");
        this.categoryDao = categoryDao;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<String> getActionName() {
        return actionName;
    }

    public LiveData<String> getImageUrl() {
        return imageUrl;
    }

    public LiveData<Integer> getRepetitions() {
        return repetitions;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        } else {
            userId = firebaseAuth.getCurrentUser().getUid();
        }
    }

    public void onAddRepetitionsClick() {
        assert repetitions.getValue() != null;
        repetitions.setValue(repetitions.getValue() + 1);
    }

    public void onSubtractRepetitionsClick() {
        assert repetitions.getValue() != null;
        if (repetitions.getValue() > 1) {
            repetitions.setValue(repetitions.getValue() - 1);
        }
    }

    public void onSelectActionNameClick() {
        if (actionName.getValue() == null) {
            event.setValue(new Event.PromptCategoryName());
        }
    }

    public void onCategoryNameSelected(String name) {
        if (name.isEmpty()) {
            event.setValue(new Event.ShowShortCategoryNameMessage("1글자 이상 입력해주세요"));
            return;
        }
        actionName.setValue(name);
        event.setValue(new Event.ShowCategoryNameSelectedMessage("카테고리 이름이 설정되었습니다"));
    }

    public void onAddActionClick() {

        if (actionName.getValue() == null) {
            event.setValue(new Event.ShowCategoryNameNotSpecifiedMessage("카테고리 이름을 입력해주세요"));
            return;
        }

        if (userId == null) {
            return;
        }

        String actionId = Action.generateId(userId);

        assert repetitions.getValue() != null;
        Action action = new Action(actionId, userId, actionName.getValue(), repetitions.getValue());

        actionCollection.document(actionId).set(action)
                .addOnSuccessListener(command -> {
                    if (category != null) {
                        event.setValue(new Event.NavigateToHomeFragment(true));
                    } else {
                        event.setValue(new Event.PromptSaveCategory("카테고리도 저장하시겠습니까?"));
                    }
                })
                .addOnFailureListener(command -> event.setValue(new Event.NavigateToHomeFragment(false)));
    }

    public void onSaveCategoryClick() {

        if (actionName.getValue() == null) {
            return;
        }

        Category category = new Category(actionName.getValue(), 5, R.drawable.ic_nature, imageUrl.getValue(), false);

        new Thread(() -> {
            categoryDao.insert(category);
            event.postValue(new Event.NavigateToHomeFragment(true));
        }).start();
    }

    public void onImageClick() {
        if (category == null) {
            event.setValue(new Event.NavigateToPhotoFragment());
        }
    }

    public void onSearchImageClick() {
        onImageClick();
    }

    public void onPhotoResult(String photoUrl) {
        imageUrl.setValue(photoUrl);
    }


    public static class Event {

        public static class NavigateBack extends Event { }

        public static class PromptCategoryName extends Event {
        }

        public static class ShowShortCategoryNameMessage extends Event {
            public final String message;

            public ShowShortCategoryNameMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowCategoryNameSelectedMessage extends Event {
            public final String message;

            public ShowCategoryNameSelectedMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowCategoryNameNotSpecifiedMessage extends Event {
            public final String message;

            public ShowCategoryNameNotSpecifiedMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateToHomeFragment extends Event {
            public boolean success;
            public NavigateToHomeFragment(boolean success) {
                this.success = success;
            }
        }

        public static class PromptSaveCategory extends Event {
            public final String message;

            public PromptSaveCategory(String message) {
                this.message = message;
            }
        }

        public static class NavigateToPhotoFragment extends Event { }
    }

}
