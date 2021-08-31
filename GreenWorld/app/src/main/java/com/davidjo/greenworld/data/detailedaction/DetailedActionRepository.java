package com.davidjo.greenworld.data.detailedaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.davidjo.greenworld.data.action.Action;
import com.davidjo.greenworld.data.category.Category;
import com.davidjo.greenworld.data.category.CategoryDao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DetailedActionRepository {

    private final CategoryDao categoryDao;


    @Inject
    public DetailedActionRepository(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public LiveData<List<DetailedAction>> getDetailedActions(List<Action> actions) {

        // Action 클래스들로부터 DetailedAction 클래스를 가져옴

        MutableLiveData<List<DetailedAction>> iconActions = new MutableLiveData<>();

        new Thread(() -> {
            List<DetailedAction> detailedActionList = new ArrayList<>();
            for (Action action : actions) {
                Category category = categoryDao.findCategory(action.getActionName());
                if (category != null) {
                    detailedActionList.add(new DetailedAction(action, category));
                }
            }
            iconActions.postValue(detailedActionList);
        }).start();

        return iconActions;
    }


}
